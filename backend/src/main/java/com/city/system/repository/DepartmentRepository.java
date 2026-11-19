package com.city.system.repository;
import com.city.system.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface DepartmentRepository extends JpaRepository<Department,Long> { Optional<Department> findByName(String name); }
