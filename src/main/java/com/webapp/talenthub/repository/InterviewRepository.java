package com.webapp.talenthub.repository;

import com.webapp.talenthub.entity.Interview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InterviewRepository extends JpaRepository<Interview, Long> {
    List<Interview> findByApplicationId(Long applicationId);
    
    List<Interview> findByInterviewerId(Long interviewerId);

    @Query("SELECT COUNT(i) FROM Interview i WHERE " +
           "(i.application.job.createdBy.id = :hrId OR i.interviewer.id = :hrId) " +
           "AND i.scheduledAt BETWEEN :start AND :end")
    long countUpcomingInterviewsForHr(
            @Param("hrId") Long hrId, 
            @Param("start") LocalDateTime start, 
            @Param("end") LocalDateTime end);
}
