package com.city.system.ai;
import com.city.system.dto.IssueDtos.AiAnalysisResult;
import com.city.system.entity.Issue;
import com.city.system.entity.Department;
import com.city.system.repository.DepartmentRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.*;
@Service @RequiredArgsConstructor
public class AiAnalysisService {
  private final DepartmentRepository departments;
  private final ObjectMapper mapper = new ObjectMapper();
  private final RestTemplate restTemplate = new RestTemplate();
  @Value("${app.gemini-key:}") private String geminiKey;
  public AiAnalysisResult analyze(String title,String description,String location){
    if(geminiKey!=null&&!geminiKey.isBlank()) {
      try {
        String text=(title+" "+description+" "+location).trim();
        String prompt="Analyze this civic issue and return ONLY valid JSON: {\"category\":\"string\",\"priority\":\"LOW|MEDIUM|HIGH|CRITICAL\",\"summary\":\"short text\",\"suggestedDepartment\":\"one of Roads & Infrastructure|Sanitation & Waste|Electricity|Water Supply|Public Safety|General Administration\"}. Issue: "+text;
        Map<String,Object> part=Map.of("text",prompt);
        Map<String,Object> body=Map.of("contents",List.of(Map.of("parts",List.of(part))));
        String url="https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key="+geminiKey;
        HttpHeaders h=new HttpHeaders(); h.setContentType(MediaType.APPLICATION_JSON);
        JsonNode root=restTemplate.postForObject(url,new HttpEntity<>(body,h),JsonNode.class);
        String raw=root.path("candidates").path(0).path("content").path("parts").path(0).path("text").asText("");
        raw=raw.replaceAll("(?s)^```(?:json)?\\s*","").replaceAll("\\s*```$","");
        JsonNode json=mapper.readTree(raw);
        String category=json.path("category").asText("General");
        String p=json.path("priority").asText("MEDIUM").toUpperCase();
        Issue.Priority priority=Arrays.stream(Issue.Priority.values()).anyMatch(v->v.name().equals(p))?Issue.Priority.valueOf(p):Issue.Priority.MEDIUM;
        String deptName=json.path("suggestedDepartment").asText("General Administration");
        Department dept=findDepartment(deptName);
        return new AiAnalysisResult(category,priority,json.path("summary").asText("City issue requiring review"),dept.getId(),dept.getName());
      } catch(Exception ignored) {}
    }
    return fallback(title+" "+description+" "+location);
  }
  public AiAnalysisResult fallback(String text){
    String s=text.toLowerCase(Locale.ROOT);
    String department="General Administration";
    if(s.contains("pothole")||s.contains("road")||s.contains("street")||s.contains("bridge")) department="Roads & Infrastructure";
    else if(s.contains("garbage")||s.contains("waste")||s.contains("trash")||s.contains("dump")) department="Sanitation & Waste";
    else if(s.contains("streetlight")||s.contains("electric")||s.contains("wire")||s.contains("power")) department="Electricity";
    else if(s.contains("water")||s.contains("leak")||s.contains("pipe")||s.contains("drain")) department="Water Supply";
    else if(s.contains("crime")||s.contains("accident")||s.contains("danger")||s.contains("safety")) department="Public Safety";
    String category=department.replace(" & "," ").replace(" Roads","Road").replace("General Administration","General");
    Issue.Priority priority=Issue.Priority.MEDIUM;
    if(s.contains("accident")||s.contains("danger")||s.contains("flood")||s.contains("fire")||s.contains("collapse")||s.contains("live wire")) priority=Issue.Priority.CRITICAL;
    else if(s.contains("urgent")||s.contains("injury")||s.contains("blocked")||s.contains("major")) priority=Issue.Priority.HIGH;
    else if(s.contains("minor")||s.contains("small")) priority=Issue.Priority.LOW;
    Department dept=findDepartment(department);
    String summary=text.length()>120?text.substring(0,120)+"...":text;
    return new AiAnalysisResult(category,priority,summary.isBlank()?"Civic issue requiring review":summary,dept.getId(),department);
  }
  private Department findDepartment(String name){ return departments.findByName(name).orElseGet(()->departments.findAll().stream().filter(d->d.getName().contains("General")).findFirst().orElseThrow()); }
}
