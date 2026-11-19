package com.city.system.repository;
import com.city.system.entity.Issue;
import com.city.system.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.List;
public interface IssueRepository extends JpaRepository<Issue,Long>, JpaSpecificationExecutor<Issue> { List<Issue> findByUserOrderByCreatedAtDesc(User user); long countByStatus(Issue.Status status); }
