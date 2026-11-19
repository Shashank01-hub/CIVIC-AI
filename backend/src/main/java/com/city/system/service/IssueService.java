package com.city.system.service;
import com.city.system.ai.AiAnalysisService;
import com.city.system.dto.IssueDtos.*;
import com.city.system.entity.*;
import com.city.system.exception.ApiException;
import com.city.system.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.util.*;
@Service @RequiredArgsConstructor
public class IssueService {
  private final IssueRepository issues; private final IssueUpdateRepository updates; private final DepartmentRepository departments;
  private final AiAnalysisService ai; private final StorageService storage;
  @Transactional
  public IssueResponse create(User user,String title,String description,String locationText,Double latitude,Double longitude,MultipartFile image){
    String imageUrl=image==null||image.isEmpty()?null:storage.store(image);
    AiAnalysisResult analysis=ai.analyze(title,description,locationText);
    Department suggested=departments.findById(analysis.getSuggestedDepartmentId()).orElseGet(()->departments.findAll().stream().findFirst().orElseThrow());
    Issue issue=Issue.builder().user(user).title(title).description(description).locationText(locationText).latitude(latitude).longitude(longitude).imageUrl(imageUrl)
      .aiCategory(analysis.getCategory()).aiPriority(analysis.getPriority()).aiSummary(analysis.getSummary()).aiSuggestedDepartment(suggested).build();
    issue=issues.save(issue);
    return toResponse(issue,true);
  }
  public List<IssueResponse> myIssues(User user){return issues.findByUserOrderByCreatedAtDesc(user).stream().map(i->toResponse(i,true)).toList();}
  public IssueResponse get(Long id,User requester){
    Issue i=issues.findById(id).orElseThrow();
    if(requester.getRole()!=User.Role.ADMIN&&!i.getUser().getId().equals(requester.getId())) throw new ApiException("Not authorized to view this issue");
    return toResponse(i,true);
  }
  public Page<IssueResponse> adminList(String status,String priority,String category,Long department,int page,int size,String sort){
    Specification<Issue> spec=Specification.where(null);
    if(status!=null&&!status.isBlank()) spec=spec.and((r,q,c)->c.equal(r.get("status"),Issue.Status.valueOf(status.toUpperCase())));
    if(priority!=null&&!priority.isBlank()) spec=spec.and((r,q,c)->c.equal(r.get("aiPriority"),Issue.Priority.valueOf(priority.toUpperCase())));
    if(category!=null&&!category.isBlank()) spec=spec.and((r,q,c)->c.like(c.lower(r.get("aiCategory")), "%"+category.toLowerCase()+"%"));
    if(department!=null) spec=spec.and((r,q,c)->c.or(c.equal(r.get("assignedDepartment").get("id"),department),c.equal(r.get("aiSuggestedDepartment").get("id"),department)));
    Sort sorting=sort.startsWith("-")?Sort.by(sort.substring(1)).descending():Sort.by(sort).ascending();
    return issues.findAll(spec,PageRequest.of(Math.max(page,0),Math.min(size,100),sorting)).map(i->toResponse(i,false));
  }
  @Transactional public IssueResponse verify(Long id,User admin){
    Issue i=issues.findById(id).orElseThrow();
    if(i.getStatus()!=Issue.Status.SUBMITTED) throw new ApiException("Only submitted issues can be verified");
    return changeStatus(i,Issue.Status.VERIFIED,admin,"Issue verified by administrator");
  }
  @Transactional public IssueResponse assign(Long id,Long departmentId,User admin){
    Issue i=issues.findById(id).orElseThrow(); Department d=departments.findById(departmentId).orElseThrow();
    Issue.Status oldStatus=i.getStatus();
    i.setAssignedDepartment(d);
    if(i.getStatus()==Issue.Status.SUBMITTED||i.getStatus()==Issue.Status.VERIFIED) i.setStatus(Issue.Status.ASSIGNED);
    i=issues.save(i);
    updates.save(IssueUpdate.builder().issue(i).updatedBy(admin).oldStatus(oldStatus).newStatus(i.getStatus()).comment("Assigned to "+d.getName()).build());
    return toResponse(i,true);
  }
  @Transactional public IssueResponse status(Long id,StatusRequest r,User admin){
    Issue i=issues.findById(id).orElseThrow();
    if(r.getStatus()==null) throw new ApiException("Status is required");
    return changeStatus(i,r.getStatus(),admin,r.getComment()==null||r.getComment().isBlank()?"Status changed":r.getComment());
  }
  private IssueResponse changeStatus(Issue i,Issue.Status newStatus,User admin,String comment){
    Issue.Status old=i.getStatus(); i.setStatus(newStatus); i=issues.save(i);
    updates.save(IssueUpdate.builder().issue(i).updatedBy(admin).oldStatus(old).newStatus(newStatus).comment(comment).build());
    return toResponse(i,true);
  }
  public List<Department> departments(){return departments.findAll();}
  public Map<String,Object> stats(){
    List<Issue> all=issues.findAll();
    Map<String,Long> statusCount=new LinkedHashMap<>(),priorityCount=new LinkedHashMap<>(),categoryCount=new LinkedHashMap<>(),departmentCount=new LinkedHashMap<>();
    for(Issue.Status s:Issue.Status.values()) statusCount.put(s.name(),all.stream().filter(i->i.getStatus()==s).count());
    for(Issue.Priority p:Issue.Priority.values()) priorityCount.put(p.name(),all.stream().filter(i->i.getAiPriority()==p).count());
    all.forEach(i->categoryCount.merge(i.getAiCategory()==null?"General":i.getAiCategory(),1L,Long::sum));
    all.forEach(i->{String n=i.getAssignedDepartment()!=null?i.getAssignedDepartment().getName():i.getAiSuggestedDepartment()!=null?i.getAiSuggestedDepartment().getName(): "Unassigned";departmentCount.merge(n,1L,Long::sum);});
    Map<String,Object> r=new LinkedHashMap<>(); r.put("totals",Map.of("issues",all.size(),"resolved",statusCount.get("RESOLVED"),"pending",statusCount.get("SUBMITTED")+statusCount.get("VERIFIED")+statusCount.get("ASSIGNED")+statusCount.get("IN_PROGRESS")));
    r.put("byStatus",statusCount);r.put("byCategory",categoryCount);r.put("byPriority",priorityCount);r.put("byDepartment",departmentCount);
    r.put("recent",all.stream().sorted(Comparator.comparing(Issue::getCreatedAt).reversed()).limit(10).map(i->toResponse(i,false)).toList());
    return r;
  }
  private IssueResponse toResponse(Issue i,boolean history){
    IssueResponse.IssueResponseBuilder b=IssueResponse.builder().id(i.getId()).title(i.getTitle()).description(i.getDescription()).locationText(i.getLocationText()).latitude(i.getLatitude()).longitude(i.getLongitude()).imageUrl(i.getImageUrl()).aiCategory(i.getAiCategory()).aiPriority(i.getAiPriority()).aiSummary(i.getAiSummary()).status(i.getStatus()).createdAt(i.getCreatedAt()).updatedAt(i.getUpdatedAt());
    if(i.getAiSuggestedDepartment()!=null)b.aiSuggestedDepartment(department(i.getAiSuggestedDepartment()));
    if(i.getAssignedDepartment()!=null)b.assignedDepartment(department(i.getAssignedDepartment()));
    if(i.getUser()!=null)b.reporter(com.city.system.dto.AuthDtos.UserResponse.builder().id(i.getUser().getId()).name(i.getUser().getName()).email(i.getUser().getEmail()).role(i.getUser().getRole().name()).build());
    if(history)b.history(updates.findByIssueIdOrderByCreatedAtAsc(i.getId()).stream().map(u->new IssueUpdateResponse(u.getId(),u.getOldStatus().name(),u.getNewStatus().name(),u.getComment(),u.getCreatedAt(),u.getUpdatedBy().getName())).toList());
    return b.build();
  }
  private DepartmentResponse department(Department d){return DepartmentResponse.builder().id(d.getId()).name(d.getName()).description(d.getDescription()).build();}
}
