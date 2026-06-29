package com.webapp.talenthub.service;

import com.webapp.talenthub.entity.Application;
import com.webapp.talenthub.entity.ApplicationStatus;
import com.webapp.talenthub.entity.CandidateProfile;
import com.webapp.talenthub.entity.Job;
import com.webapp.talenthub.entity.User;
import com.webapp.talenthub.repository.ApplicationRepository;
import com.webapp.talenthub.repository.CandidateProfileRepository;
import com.webapp.talenthub.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final CandidateProfileRepository candidateProfileRepository;
    
    // Directory outside target/ where CVs are stored securely
    private final String uploadDir = "uploads/cv/";

    @Autowired
    public ApplicationService(ApplicationRepository applicationRepository,
                              JobRepository jobRepository,
                              CandidateProfileRepository candidateProfileRepository) {
        this.applicationRepository = applicationRepository;
        this.jobRepository = jobRepository;
        this.candidateProfileRepository = candidateProfileRepository;
    }

    public List<Application> getApplicationsByJob(Long jobId) {
        return applicationRepository.findByJobId(jobId);
    }

    public Application getApplicationById(Long id) {
        return applicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found"));
    }

    @Transactional
    public Application updateStatus(Long id, ApplicationStatus newStatus) {
        Application application = getApplicationById(id);
        application.setStatus(newStatus);
        return applicationRepository.save(application);
    }

    public List<Application> getAllApplications() {
        return applicationRepository.findAll();
    }

    // Get applications submitted by a specific candidate
    public List<Application> getApplicationsByCandidate(Long userId) {
        return applicationRepository.findByCandidateUserId(userId);
    }

    // Check if candidate has already applied for this job
    public boolean hasApplied(Long jobId, Long userId) {
        return applicationRepository.findByJobIdAndCandidateUserId(jobId, userId).isPresent();
    }

    // Submit new job application with file upload
    @Transactional
    public Application applyJob(Long jobId, User candidateUser, String coverLetter, MultipartFile cvFile) throws IOException {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        if (!"ACTIVE".equals(job.getStatus())) {
            throw new RuntimeException("Job is not active and cannot be applied to.");
        }

        if (hasApplied(jobId, candidateUser.getId())) {
            throw new RuntimeException("You have already applied for this job.");
        }

        // Get or create candidate profile dynamically
        CandidateProfile profile = candidateProfileRepository.findByUserId(candidateUser.getId())
                .orElseGet(() -> {
                    CandidateProfile newProfile = new CandidateProfile();
                    newProfile.setUser(candidateUser);
                    return candidateProfileRepository.save(newProfile);
                });

        // Save CV file
        String fileName = saveCvFile(cvFile);

        Application application = new Application();
        application.setJob(job);
        application.setCandidate(profile);
        application.setStatus(ApplicationStatus.APPLIED);
        application.setCoverLetter(coverLetter);
        application.setCvUrl(fileName); // Stores the safe file name

        return applicationRepository.save(application);
    }

    // Withdraw application (only allowed in APPLIED or SCREENING status)
    @Transactional
    public Application withdrawApplication(Long applicationId, User candidateUser) {
        Application application = getApplicationById(applicationId);

        if (!application.getCandidate().getUser().getId().equals(candidateUser.getId())) {
            throw new RuntimeException("Unauthorized: You do not own this application.");
        }

        ApplicationStatus currentStatus = application.getStatus();
        if (currentStatus != ApplicationStatus.APPLIED && currentStatus != ApplicationStatus.SCREENING) {
            throw new RuntimeException("Cannot withdraw application that has advanced past screening.");
        }

        application.setStatus(ApplicationStatus.WITHDRAWN);
        return applicationRepository.save(application);
    }

    // Helper to store the uploaded CV securely
    private String saveCvFile(MultipartFile cvFile) throws IOException {
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String fileExtension = "";
        String originalFilename = cvFile.getOriginalFilename();
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        
        String fileName = System.currentTimeMillis() + "_" + candidateProfileRepository.hashCode() + fileExtension;
        Path filePath = Paths.get(uploadDir, fileName);
        Files.copy(cvFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        return fileName;
    }

    public Path getCvPath(String fileName) {
        return Paths.get(uploadDir, fileName);
    }
}
