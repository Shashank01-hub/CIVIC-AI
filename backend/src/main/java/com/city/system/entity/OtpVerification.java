package com.city.system.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "otp_verifications")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OtpVerification {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(nullable=false) private String email;
  @Column(nullable=false) private String purpose;
  @Column(name="code_hash",nullable=false) private String codeHash;
  @Column(name="expires_at",nullable=false) private Instant expiresAt;
  @Builder.Default @Column(nullable=false) private boolean used=false;
  @Column(name="created_at",nullable=false,updatable=false) @Builder.Default private Instant createdAt=Instant.now();
}
