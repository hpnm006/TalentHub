package com.webapp.talenthub.service;

import com.webapp.talenthub.entity.Application;
import com.webapp.talenthub.entity.ApplicationStatus;
import com.webapp.talenthub.entity.Interview;
import com.webapp.talenthub.entity.InterviewStatus;
import com.webapp.talenthub.entity.User;
import com.webapp.talenthub.repository.InterviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class InterviewService {

    private final InterviewRepository interviewRepository;
    private final ApplicationService applicationService;

    @Autowired
    public InterviewService(InterviewRepository interviewRepository, ApplicationService applicationService) {
        this.interviewRepository = interviewRepository;
        this.applicationService = applicationService;
    }

    @Transactional
    public Interview scheduleInterview(Long applicationId, User interviewer, LocalDateTime scheduledAt, String linkOrLocation, String notes) {
        Application application = applicationService.getApplicationById(applicationId);
        
        Interview interview = new Interview();
        interview.setApplication(application);
        interview.setInterviewer(interviewer);
        interview.setScheduledAt(scheduledAt);
        interview.setLinkOrLocation(linkOrLocation);
        interview.setNotes(notes);
        interview.setStatus(InterviewStatus.SCHEDULED);

        applicationService.updateStatus(applicationId, ApplicationStatus.INTERVIEW);

        return interviewRepository.save(interview);
    }

    public List<Interview> getInterviewsByApplication(Long applicationId) {
        return interviewRepository.findByApplicationId(applicationId);
    }

    @Transactional
    public Interview updateStatus(Long interviewId, InterviewStatus newStatus) {
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new RuntimeException("Interview not found"));
        interview.setStatus(newStatus);
        return interviewRepository.save(interview);
    }
}
