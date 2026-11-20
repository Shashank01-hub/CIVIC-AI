package com.city.system.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class OtpMailConfig {
  @Bean(name="otpMailSender")
  public JavaMailSender otpMailSender(
      @Value("${SMTP_HOST:}") String host,
      @Value("${SMTP_PORT:587}") int port,
      @Value("${SMTP_USERNAME:}") String username,
      @Value("${SMTP_PASSWORD:}") String password,
      @Value("${SMTP_AUTH:true}") boolean auth,
      @Value("${SMTP_STARTTLS:true}") boolean startTls,
      @Value("${SMTP_SSL_ENABLE:false}") boolean sslEnable) {
    JavaMailSenderImpl sender=new JavaMailSenderImpl();
    sender.setHost(host);
    sender.setPort(port);
    sender.setUsername(username);
    sender.setPassword(password==null?"":password.replaceAll("\\s+",""));
    Properties properties=sender.getJavaMailProperties();
    properties.put("mail.transport.protocol","smtp");
    properties.put("mail.smtp.auth",String.valueOf(auth));
    properties.put("mail.smtp.starttls.enable",String.valueOf(startTls));
    properties.put("mail.smtp.starttls.required",String.valueOf(startTls));
    properties.put("mail.smtp.ssl.enable",String.valueOf(sslEnable));
    properties.put("mail.smtp.connectiontimeout","10000");
    properties.put("mail.smtp.timeout","10000");
    properties.put("mail.smtp.writetimeout","10000");
    return sender;
  }
}
