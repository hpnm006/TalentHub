package com.webapp.talenthub.repository;

import com.webapp.talenthub.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByJobId(Long jobId);
    
    // Find applications submitted by a specific candidate user
    List<Application> findByCandidateUserId(Long userId);
    
    // Check if a candidate has already applied to a specific job
    Optional<Application> findByJobIdAndCandidateUserId(Long jobId, Long userId);
}
