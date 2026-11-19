package com.city.system.repository;
import com.city.system.entity.IssueUpdate;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface IssueUpdateRepository extends JpaRepository<IssueUpdate,Long> { List<IssueUpdate> findByIssueIdOrderByCreatedAtAsc(Long issueId); }
