package com.webapp.talenthub.service;

import com.webapp.talenthub.entity.Job;
import com.webapp.talenthub.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobService {

    private final JobRepository jobRepository;

    @Autowired
    public JobService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    public List<Job> getActiveJobs() {
        return jobRepository.findByStatus("ACTIVE");
    }

    public List<Job> getActiveJobsFiltered(String department, String location) {
        if (department == null) department = "";
        if (location == null) location = "";
        return jobRepository.findByStatusAndDepartmentContainingIgnoreCaseAndLocationContainingIgnoreCase("ACTIVE", department, location);
    }

    public List<String> getDistinctDepartments() {
        return jobRepository.findDistinctDepartmentsByActiveStatus();
    }

    public List<String> getDistinctLocations() {
        return jobRepository.findDistinctLocationsByActiveStatus();
    }

    public Job getJobById(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found with id: " + id));
    }
}
