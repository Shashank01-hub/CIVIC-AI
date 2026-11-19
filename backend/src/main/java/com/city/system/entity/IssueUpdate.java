package com.city.system.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
@Entity @Table(name="issue_updates")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class IssueUpdate {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @ManyToOne(optional=false) @JoinColumn(name="issue_id") private Issue issue;
  @ManyToOne(optional=false) @JoinColumn(name="updated_by") private User updatedBy;
  @Enumerated(EnumType.STRING) @Column(name="old_status",nullable=false) private Issue.Status oldStatus;
  @Enumerated(EnumType.STRING) @Column(name="new_status",nullable=false) private Issue.Status newStatus;
  @Column(columnDefinition="TEXT") private String comment;
  @Column(name="created_at",nullable=false,updatable=false) @Builder.Default private Instant createdAt=Instant.now();
}
