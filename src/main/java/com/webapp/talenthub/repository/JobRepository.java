package com.webapp.talenthub.repository;

import com.webapp.talenthub.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
    List<Job> findByStatus(String status);
    
    // Supports filtering active jobs by department and location (case-insensitive contains)
    List<Job> findByStatusAndDepartmentContainingIgnoreCaseAndLocationContainingIgnoreCase(
            String status, String department, String location);
            
    @Query("SELECT DISTINCT j.department FROM Job j WHERE j.status = 'ACTIVE' AND j.department IS NOT NULL")
    List<String> findDistinctDepartmentsByActiveStatus();

    @Query("SELECT DISTINCT j.location FROM Job j WHERE j.status = 'ACTIVE' AND j.location IS NOT NULL")
    List<String> findDistinctLocationsByActiveStatus();
}
