package com.city.system.controller;
import com.city.system.entity.Department;
import com.city.system.service.IssueService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/departments") @RequiredArgsConstructor
public class DepartmentController {
  private final IssueService issues;
  @GetMapping public List<Department> all(){return issues.departments();}
}
