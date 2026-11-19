package com.city.system.service;

import com.city.system.entity.OtpVerification;
import com.city.system.exception.ApiException;
import com.city.system.repository.OtpVerificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.SecureRandom;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class OtpService {
  private final OtpVerificationRepository otps;
  private final PasswordEncoder passwordEncoder;
  private final JavaMailSender mailSender;
  @Value("${app.smtp-from:no-reply@smartcity.local}") private String smtpFrom;
  @Value("${spring.mail.host:}") private String smtpHost;

  public String send(String email,String purpose) {
    String code=String.format("%06d",new SecureRandom().nextInt(1_000_000));
    otps.save(OtpVerification.builder().email(email.toLowerCase()).purpose(purpose).codeHash(passwordEncoder.encode(code)).expiresAt(Instant.now().plusSeconds(600)).build());
    if(smtpHost!=null&&!smtpHost.isBlank()){
      SimpleMailMessage message=new SimpleMailMessage();
      message.setFrom(smtpFrom);
      message.setTo(email);
      message.setSubject("Your AI Smart City verification code");
      message.setText("Your verification code is "+code+". It expires in 10 minutes.");
      mailSender.send(message);
    }
    return smtpHost==null||smtpHost.isBlank()?code:null;
  }

  public void verify(String email,String purpose,String code) {
    if(code==null||code.isBlank()) throw new ApiException("OTP is required");
    OtpVerification otp=otps.findTopByEmailAndPurposeAndUsedFalseOrderByCreatedAtDesc(email.toLowerCase(),purpose).orElseThrow(()->new ApiException("OTP not found or expired"));
    if(otp.getExpiresAt().isBefore(Instant.now())) throw new ApiException("OTP has expired");
    if(!passwordEncoder.matches(code,otp.getCodeHash())) throw new ApiException("Invalid OTP");
    otp.setUsed(true);
    otps.save(otp);
  }
}
