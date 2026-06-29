package com.webapp.talenthub.repository;

import com.webapp.talenthub.entity.Job;
import com.webapp.talenthub.entity.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
    List<Job> findByStatus(JobStatus status);
    
    List<Job> findByCreatedById(Long userId);
    
    List<Job> findByStatusAndCreatedById(JobStatus status, Long userId);
    
    long countByStatus(JobStatus status);
    
    long countByStatusAndCreatedById(JobStatus status, Long userId);
    
    // Supports filtering active jobs by department and location
    List<Job> findByStatusAndDepartmentContainingIgnoreCaseAndLocationContainingIgnoreCase(
            JobStatus status, String department, String location);
            
    @Query("SELECT DISTINCT j.department FROM Job j WHERE j.status = com.webapp.talenthub.entity.JobStatus.ACTIVE AND j.department IS NOT NULL")
    List<String> findDistinctDepartmentsByActiveStatus();

    @Query("SELECT DISTINCT j.location FROM Job j WHERE j.status = com.webapp.talenthub.entity.JobStatus.ACTIVE AND j.location IS NOT NULL")
    List<String> findDistinctLocationsByActiveStatus();

    @Query("SELECT j FROM Job j WHERE " +
           "(:status IS NULL OR j.status = :status) AND " +
           "(:keyword IS NULL OR :keyword = '' OR LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:department IS NULL OR :department = '' OR j.department = :department) AND " +
           "(:createdById IS NULL OR j.createdBy.id = :createdById)")
    List<Job> searchJobs(@Param("status") JobStatus status,
                         @Param("keyword") String keyword,
                         @Param("department") String department,
                         @Param("createdById") Long createdById);
}
