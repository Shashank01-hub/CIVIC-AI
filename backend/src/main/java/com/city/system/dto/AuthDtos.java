package com.city.system.dto;
import jakarta.validation.constraints.*;
import lombok.*;
public class AuthDtos {
  @Data public static class RegisterRequest { @NotBlank private String name; @Email @NotBlank private String email; @Size(min=6,max=100) private String password; @Size(min=6,max=100) private String confirmPassword; private String adminCode; private String otp; }
  @Data public static class LoginRequest { @Email @NotBlank private String email; @NotBlank private String password; @Size(min=6,max=100) private String confirmPassword; private String otp; }
  @Data public static class OtpRequest { @Email @NotBlank private String email; private String purpose; }
  @Data public static class OtpVerifyRequest { @Email @NotBlank private String email; @NotBlank private String otp; private String purpose; }
  @Data public static class ForgotPasswordRequest { @Email @NotBlank private String email; }
  @Data public static class ResetPasswordRequest { @Email @NotBlank private String email; @NotBlank private String otp; @Size(min=6,max=100) private String password; @Size(min=6,max=100) private String confirmPassword; }
  @Data @Builder @NoArgsConstructor @AllArgsConstructor public static class AuthResponse { private String token; private Long id; private String name; private String email; private String role; }
  @Data @Builder @NoArgsConstructor @AllArgsConstructor public static class UserResponse { private Long id; private String name; private String email; private String role; }
}
