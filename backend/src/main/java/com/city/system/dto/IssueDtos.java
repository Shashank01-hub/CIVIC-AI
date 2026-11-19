package com.city.system.dto;
import com.city.system.entity.Issue;
import lombok.*;
import java.time.Instant;
import java.util.List;
public class IssueDtos {
  @Data @Builder @NoArgsConstructor @AllArgsConstructor public static class IssueResponse {
    private Long id; private String title; private String description; private String locationText; private Double latitude; private Double longitude; private String imageUrl;
    private String aiCategory; private Issue.Priority aiPriority; private String aiSummary; private DepartmentResponse aiSuggestedDepartment;
    private DepartmentResponse assignedDepartment; private Issue.Status status; private Instant createdAt; private Instant updatedAt;
    private AuthDtos.UserResponse reporter; private List<IssueUpdateResponse> history;
  }
  @Data @Builder @NoArgsConstructor @AllArgsConstructor public static class DepartmentResponse { private Long id; private String name; private String description; }
  @Data @Builder @NoArgsConstructor @AllArgsConstructor public static class IssueUpdateResponse { private Long id; private String oldStatus; private String newStatus; private String comment; private Instant createdAt; private String updatedBy; }
  @Data @Builder @NoArgsConstructor @AllArgsConstructor public static class AiAnalysisResult { private String category; private Issue.Priority priority; private String summary; private Long suggestedDepartmentId; private String suggestedDepartment; }
  @Data @NoArgsConstructor @AllArgsConstructor public static class StatusRequest { private Issue.Status status; private String comment; }
  @Data @NoArgsConstructor @AllArgsConstructor public static class AssignRequest { private Long departmentId; }
}
