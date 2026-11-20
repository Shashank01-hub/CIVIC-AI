package com.city.system.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class OtpEmailSender {
  private final JavaMailSender mailSender;
  @Value("${app.smtp-from:no-reply@smartcity.local}") private String smtpFrom;

  public OtpEmailSender(JavaMailSender mailSender) {
    this.mailSender=mailSender;
  }

  @Async
  public void sendAsync(String email,String code){
    for(int attempt=1;attempt<=3;attempt++){
      try{
        SimpleMailMessage message=new SimpleMailMessage();
        message.setFrom(smtpFrom);
        message.setTo(email);
        message.setSubject("Your AI Smart City verification code");
        message.setText("Your verification code is "+code+". It expires in 10 minutes.");
        mailSender.send(message);
        System.out.println("OTP email delivered to "+email);
        return;
      }catch(Exception e){
        System.err.println("OTP email attempt "+attempt+" failed for "+email+": "+e.getMessage());
        if(attempt<3){
          try{Thread.sleep(1000L*attempt);}catch(InterruptedException interrupted){Thread.currentThread().interrupt();return;}
        }
      }
    }
  }
}
