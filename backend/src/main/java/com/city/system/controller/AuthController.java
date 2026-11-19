package com.city.system.controller;
import com.city.system.dto.AuthDtos.*;
import com.city.system.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import com.city.system.service.OtpService;
import com.city.system.entity.User;
import com.city.system.exception.ApiException;
import com.city.system.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
@RestController @RequestMapping("/api/auth") @RequiredArgsConstructor
public class AuthController {
  private final AuthService auth;
  private final OtpService otpService;
  private final UserRepository users;
  private final PasswordEncoder passwordEncoder;
  @PostMapping("/register") public AuthResponse register(@Valid @RequestBody RegisterRequest r){return auth.register(r);}
  @PostMapping("/login") public AuthResponse login(@Valid @RequestBody LoginRequest r){return auth.login(r);}
  @PostMapping("/otp/send") public Map<String,Object> sendOtp(@Valid @RequestBody OtpRequest r){
    String purpose=r.getPurpose()==null?"REGISTER":r.getPurpose().toUpperCase();
    if(!purpose.equals("REGISTER")&&!purpose.equals("LOGIN")&&!purpose.equals("FORGOT_PASSWORD")) throw new ApiException("Invalid OTP purpose");
    if(purpose.equals("LOGIN")||purpose.equals("FORGOT_PASSWORD")) users.findByEmail(r.getEmail().toLowerCase()).orElseThrow(()->new ApiException("Account not found"));
    String devCode=otpService.send(r.getEmail(),purpose);
    return devCode==null?Map.of("message","OTP sent") : Map.of("message","OTP generated","developmentOtp",devCode);
  }
  @PostMapping("/otp/verify") public Map<String,Object> verifyOtp(@Valid @RequestBody OtpVerifyRequest r){
    String purpose=r.getPurpose()==null?"REGISTER":r.getPurpose().toUpperCase();
    otpService.verify(r.getEmail(),purpose,r.getOtp());
    return Map.of("verified",true);
  }
  @PostMapping("/forgot-password") public Map<String,Object> forgotPassword(@Valid @RequestBody ForgotPasswordRequest r){
    String email=r.getEmail().trim().toLowerCase();
    users.findByEmail(email).orElseThrow(()->new ApiException("Account not found"));
    String devCode=otpService.send(email,"FORGOT_PASSWORD");
    return devCode==null?Map.of("message","Password reset OTP sent") : Map.of("message","Password reset OTP generated","developmentOtp",devCode);
  }
  @PostMapping("/reset-password")
  @Transactional
  public Map<String,Object> resetPassword(@Valid @RequestBody ResetPasswordRequest r){
    if(!r.getPassword().equals(r.getConfirmPassword())) throw new ApiException("Passwords do not match");
    String email=r.getEmail().trim().toLowerCase();
    otpService.verify(email,"FORGOT_PASSWORD",r.getOtp());
    User u=users.findByEmail(email).orElseThrow(()->new ApiException("Account not found"));
    u.setPasswordHash(passwordEncoder.encode(r.getPassword()));
    users.saveAndFlush(u);
    return Map.of("message","Password reset successful");
  }
}
