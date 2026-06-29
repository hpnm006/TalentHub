package com.webapp.talenthub.repository;

import com.webapp.talenthub.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByJobId(Long jobId);
    
    List<Application> findByCandidateUserId(Long userId);
    
    Optional<Application> findByJobIdAndCandidateUserId(Long jobId, Long userId);

    @Query("SELECT COUNT(a) FROM Application a WHERE a.job.createdBy.id = :hrId AND a.status = com.webapp.talenthub.entity.ApplicationStatus.APPLIED")
    long countNewApplicationsForHr(@Param("hrId") Long hrId);
}
