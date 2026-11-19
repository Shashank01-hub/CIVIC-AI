package com.city.system.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
@Entity @Table(name="issues")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Issue {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @ManyToOne(optional=false) @JoinColumn(name="user_id") private User user;
  @Column(nullable=false) private String title;
  @Column(nullable=false,columnDefinition="TEXT") private String description;
  @Column(name="location_text",nullable=false) private String locationText;
  private Double latitude;
  private Double longitude;
  @Column(name="image_url") private String imageUrl;
  @Column(name="ai_category") private String aiCategory;
  @Enumerated(EnumType.STRING) @Column(name="ai_priority") private Priority aiPriority;
  @Column(name="ai_summary",columnDefinition="TEXT") private String aiSummary;
  @ManyToOne @JoinColumn(name="ai_suggested_department_id") private Department aiSuggestedDepartment;
  @ManyToOne @JoinColumn(name="assigned_department_id") private Department assignedDepartment;
  @Enumerated(EnumType.STRING) @Column(nullable=false) @Builder.Default private Status status=Status.SUBMITTED;
  @Column(name="created_at",nullable=false,updatable=false) @Builder.Default private Instant createdAt=Instant.now();
  @Column(name="updated_at",nullable=false) @Builder.Default private Instant updatedAt=Instant.now();
  public enum Priority { LOW, MEDIUM, HIGH, CRITICAL }
  public enum Status { SUBMITTED, VERIFIED, ASSIGNED, IN_PROGRESS, RESOLVED, REJECTED }
  @PreUpdate public void touch(){ updatedAt=Instant.now(); }
}
