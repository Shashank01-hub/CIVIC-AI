package com.city.system.service;
import com.city.system.dto.AuthDtos.*;
import com.city.system.entity.User;
import com.city.system.exception.ApiException;
import com.city.system.repository.UserRepository;
import com.city.system.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
@Service @RequiredArgsConstructor
public class AuthService {
  private final UserRepository users; private final PasswordEncoder encoder; private final JwtService jwt;
  private final OtpService otpService;
  @org.springframework.beans.factory.annotation.Value("${app.admin-registration-code:}") private String adminRegistrationCode;
  public AuthResponse register(RegisterRequest r){
    if(r.getConfirmPassword()==null||!r.getPassword().equals(r.getConfirmPassword())) throw new ApiException("Passwords do not match");
    if(r.getEmail()==null||r.getEmail().isBlank()) throw new ApiException("Email is required");
    otpService.verify(r.getEmail(),"REGISTER",r.getOtp());
    if(users.existsByEmail(r.getEmail().toLowerCase())) throw new ApiException("Email is already registered");
    boolean correctAdminCode=adminRegistrationCode!=null&&!adminRegistrationCode.isBlank()&&r.getAdminCode()!=null&&r.getAdminCode().equals(adminRegistrationCode);
    User u=User.builder().name(r.getName()).email(r.getEmail().toLowerCase()).passwordHash(encoder.encode(r.getPassword())).role(correctAdminCode?User.Role.ADMIN:User.Role.CITIZEN).build();
    u=users.save(u);
    return AuthResponse.builder().token(jwt.generate(u)).id(u.getId()).name(u.getName()).email(u.getEmail()).role(u.getRole().name()).build();
  }
  public AuthResponse login(LoginRequest r){
    if(r.getConfirmPassword()==null||!r.getPassword().equals(r.getConfirmPassword())) throw new ApiException("Passwords do not match");
    String email=r.getEmail().trim().toLowerCase();
    User u=users.findByEmail(email).orElseThrow(()->new ApiException("Invalid email or password"));
    if(!encoder.matches(r.getPassword(),u.getPasswordHash())) throw new ApiException("Invalid email or password");
    return AuthResponse.builder().token(jwt.generate(u)).id(u.getId()).name(u.getName()).email(u.getEmail()).role(u.getRole().name()).build();
  }
}
