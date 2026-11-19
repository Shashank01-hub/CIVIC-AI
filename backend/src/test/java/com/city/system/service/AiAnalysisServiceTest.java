package com.city.system.service;
import com.city.system.ai.AiAnalysisService;
import com.city.system.entity.Department;
import com.city.system.entity.Issue;
import com.city.system.repository.DepartmentRepository;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
class AiAnalysisServiceTest {
  @Test void classifiesPotholeAndCriticalHazard(){
    DepartmentRepository r=mock(DepartmentRepository.class);
    when(r.findByName(anyString())).thenAnswer(i->java.util.Optional.of(Department.builder().id(1L).name(i.getArgument(0)).build()));
    AiAnalysisService ai=new AiAnalysisService(r);
    var road=ai.fallback("Large pothole on main road");
    assertEquals(Issue.Priority.MEDIUM,road.getPriority()); assertTrue(road.getSuggestedDepartment().contains("Road"));
    var danger=ai.fallback("Live wire is causing danger and flood");
    assertEquals(Issue.Priority.CRITICAL,danger.getPriority()); assertEquals("Electricity",danger.getSuggestedDepartment());
  }
}
