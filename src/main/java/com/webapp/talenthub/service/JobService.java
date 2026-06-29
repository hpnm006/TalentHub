package com.webapp.talenthub.service;

import com.webapp.talenthub.entity.Job;
import com.webapp.talenthub.entity.JobStatus;
import com.webapp.talenthub.entity.User;
import com.webapp.talenthub.repository.ApplicationRepository;
import com.webapp.talenthub.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class JobService {

    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;

    @Autowired
    public JobService(JobRepository jobRepository, ApplicationRepository applicationRepository) {
        this.jobRepository = jobRepository;
        this.applicationRepository = applicationRepository;
    }

    public List<Job> getActiveJobs() {
        return jobRepository.findByStatus(JobStatus.ACTIVE);
    }

    public List<Job> getActiveJobsFiltered(String department, String location) {
        if (department == null) department = "";
        if (location == null) location = "";
        return jobRepository.findByStatusAndDepartmentContainingIgnoreCaseAndLocationContainingIgnoreCase(
                JobStatus.ACTIVE, department, location);
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

    public List<Job> searchJobs(JobStatus status, String keyword, String department, Long createdById) {
        return jobRepository.searchJobs(status, keyword, department, createdById);
    }

    public long countByStatus(JobStatus status) {
        return jobRepository.countByStatus(status);
    }

    public long countByStatusAndCreator(JobStatus status, Long creatorId) {
        return jobRepository.countByStatusAndCreatedById(status, creatorId);
    }

    public List<Job> getJobsByCreator(Long creatorId) {
        return jobRepository.findByCreatedById(creatorId);
    }

    public List<Job> getJobsByStatusAndCreator(JobStatus status, Long creatorId) {
        return jobRepository.findByStatusAndCreatedById(status, creatorId);
    }

    public Job saveJob(Job job, User user) {
        // Validation: deadline must be today or in the future
        if (job.getDeadline() != null) {
            LocalDateTime startOfToday = LocalDateTime.now().toLocalDate().atStartOfDay();
            if (job.getDeadline().isBefore(startOfToday)) {
                throw new IllegalArgumentException("Hạn nộp hồ sơ phải là ngày hiện tại hoặc tương lai.");
            }
        }

        if (job.getId() != null) {
            // Edit Mode
            Job existing = getJobById(job.getId());
            if (existing.getStatus() == JobStatus.CLOSED) {
                throw new IllegalStateException("Không thể chỉnh sửa bài đăng đã đóng.");
            }
            
            existing.setTitle(job.getTitle());
            existing.setDescription(job.getDescription());
            existing.setDepartment(job.getDepartment());
            existing.setLocation(job.getLocation());
            existing.setRequirements(job.getRequirements());
            existing.setSalaryRange(job.getSalaryRange());
            existing.setDeadline(job.getDeadline());
            
            // Allow status update if sent (e.g. from form)
            if (job.getStatus() != null) {
                existing.setStatus(job.getStatus());
            }
            
            return jobRepository.save(existing);
        } else {
            // Create Mode
            job.setCreatedBy(user);
            if (job.getStatus() == null) {
                job.setStatus(JobStatus.DRAFT);
            }
            return jobRepository.save(job);
        }
    }

    public Job publishJob(Long id, User user) {
        Job job = getJobById(id);
        if (job.getStatus() != JobStatus.DRAFT) {
            throw new IllegalStateException("Chỉ bài đăng DRAFT mới có thể đăng tuyển.");
        }
        job.setStatus(JobStatus.ACTIVE);
        return jobRepository.save(job);
    }

    public Job closeJob(Long id, User user) {
        Job job = getJobById(id);
        if (job.getStatus() != JobStatus.ACTIVE) {
            throw new IllegalStateException("Chỉ bài đăng ACTIVE mới có thể đóng.");
        }
        job.setStatus(JobStatus.CLOSED);
        return jobRepository.save(job);
    }

    public void deleteJob(Long id, User user) {
        Job job = getJobById(id);
        if (job.getStatus() != JobStatus.DRAFT) {
            throw new IllegalStateException("Chỉ được phép xóa bài đăng nháp (DRAFT).");
        }
        
        // Check if candidates have already applied
        if (!applicationRepository.findByJobId(id).isEmpty()) {
            throw new IllegalStateException("Không thể xóa bài đăng nháp đã có ứng viên nộp hồ sơ.");
        }
        
        jobRepository.delete(job);
    }
}
