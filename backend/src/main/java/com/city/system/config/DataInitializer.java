package com.city.system.config;
import com.city.system.entity.*;
import com.city.system.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
@Component @Profile("!test") @RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
  private final DepartmentRepository departments; private final UserRepository users; private final IssueRepository issues; private final PasswordEncoder encoder;
  public void run(String... args){
    if(departments.count()==0) departments.saveAll(java.util.List.of(
      Department.builder().name("Roads & Infrastructure").description("Road and infrastructure repairs").build(),
      Department.builder().name("Sanitation & Waste").description("Waste collection and sanitation").build(),
      Department.builder().name("Electricity").description("Streetlights and electrical hazards").build(),
      Department.builder().name("Water Supply").description("Water pipelines and supply").build(),
      Department.builder().name("Public Safety").description("Public safety incidents").build(),
      Department.builder().name("General Administration").description("General municipal issues").build()));
    if(users.count()==0){
      users.save(User.builder().name("City Admin").email("admin@city.com").passwordHash(encoder.encode("Admin@123")).role(User.Role.ADMIN).build());
      users.save(User.builder().name("Demo Citizen").email("citizen@city.com").passwordHash(encoder.encode("Citizen@123")).role(User.Role.CITIZEN).build());
    }
  }
}
