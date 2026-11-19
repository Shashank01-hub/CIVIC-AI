package com.city.system.controller;
import com.city.system.dto.IssueDtos.*;
import com.city.system.entity.User;
import com.city.system.service.IssueService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.List;
import com.city.system.repository.UserRepository;
import com.city.system.repository.IssueRepository;
import com.city.system.exception.ApiException;
import org.springframework.transaction.annotation.Transactional;
import com.city.system.dto.AuthDtos;
import java.time.Instant;
@RestController @RequestMapping("/api/admin") @RequiredArgsConstructor
public class AdminController {
  private final IssueService issues;
  private final UserRepository users;
  private final IssueRepository issueRepository;
  @GetMapping("/issues")
  public Page<IssueResponse> list(@RequestParam(required=false) String status,@RequestParam(required=false) String priority,@RequestParam(required=false) String category,@RequestParam(required=false) Long department,@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="10") int size,@RequestParam(defaultValue="-createdAt") String sort){return issues.adminList(status,priority,category,department,page,size,sort);}
  @PutMapping("/issues/{id}/verify") public IssueResponse verify(@PathVariable Long id,@AuthenticationPrincipal User admin){return issues.verify(id,admin);}
  @PutMapping("/issues/{id}/assign") public IssueResponse assign(@PathVariable Long id,@RequestBody AssignRequest r,@AuthenticationPrincipal User admin){return issues.assign(id,r.getDepartmentId(),admin);}
  @PutMapping("/issues/{id}/status") public IssueResponse status(@PathVariable Long id,@RequestBody StatusRequest r,@AuthenticationPrincipal User admin){return issues.status(id,r,admin);}
  @GetMapping("/stats") public Map<String,Object> stats(){return issues.stats();}

  public record UserManagementResponse(Long id,String name,String email,String role,Instant createdAt,long issueCount){}
  public record UserRoleRequest(String role){}

  @GetMapping("/users")
  public List<UserManagementResponse> users(){
    return users.findAll(org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC,"createdAt")).stream()
      .map(u->new UserManagementResponse(u.getId(),u.getName(),u.getEmail(),u.getRole().name(),u.getCreatedAt(),
        issueRepository.findAll().stream().filter(i->i.getUser()!=null&&i.getUser().getId().equals(u.getId())).count()))
      .toList();
  }

  @PutMapping("/users/{id}/role")
  @Transactional
  public UserManagementResponse changeUserRole(@PathVariable Long id,@RequestBody UserRoleRequest request,@AuthenticationPrincipal User admin){
    if(id.equals(admin.getId())) throw new ApiException("You cannot change your own role");
    User u=users.findById(id).orElseThrow();
    if(request.role()==null) throw new ApiException("Role is required");
    try { u.setRole(User.Role.valueOf(request.role().toUpperCase())); }
    catch(IllegalArgumentException e){ throw new ApiException("Role must be CITIZEN or ADMIN"); }
    u=users.save(u);
    Long savedId=u.getId();
    long count=issueRepository.findAll().stream().filter(i->i.getUser()!=null&&i.getUser().getId().equals(savedId)).count();
    return new UserManagementResponse(u.getId(),u.getName(),u.getEmail(),u.getRole().name(),u.getCreatedAt(),count);
  }

  @DeleteMapping("/users/{id}")
  @Transactional
  public Map<String,Object> deleteUser(@PathVariable Long id,@AuthenticationPrincipal User admin){
    if(id.equals(admin.getId())) throw new ApiException("You cannot delete your own account");
    User u=users.findById(id).orElseThrow();
    boolean hasIssues=issueRepository.findAll().stream().anyMatch(i->i.getUser()!=null&&i.getUser().getId().equals(id));
    if(hasIssues) throw new ApiException("Cannot delete a user who has reported issues");
    users.delete(u);
    return Map.of("message","User deleted");
  }
}
