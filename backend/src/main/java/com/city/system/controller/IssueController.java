package com.city.system.controller;
import com.city.system.dto.IssueDtos.*;
import com.city.system.entity.User;
import com.city.system.service.IssueService;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
@RestController @RequestMapping("/api/issues") @RequiredArgsConstructor
public class IssueController {
  private final IssueService issues;
  @PostMapping(consumes="multipart/form-data")
  public IssueResponse create(@AuthenticationPrincipal User user,@RequestParam @NotBlank String title,@RequestParam @NotBlank String description,@RequestParam("location") @NotBlank String locationText,@RequestParam(required=false) Double lat,@RequestParam(required=false) Double lng,@RequestParam(required=false) MultipartFile image){
    return issues.create(user,title,description,locationText,lat,lng,image);
  }
  @GetMapping("/my") public List<IssueResponse> my(@AuthenticationPrincipal User user){return issues.myIssues(user);}
  @GetMapping("/{id}") public IssueResponse get(@PathVariable Long id,@AuthenticationPrincipal User user){return issues.get(id,user);}
}
