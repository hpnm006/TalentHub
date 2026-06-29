package com.webapp.talenthub.controller;

import com.webapp.talenthub.entity.Job;
import com.webapp.talenthub.entity.JobStatus;
import com.webapp.talenthub.entity.User;
import com.webapp.talenthub.repository.ApplicationRepository;
import com.webapp.talenthub.repository.InterviewRepository;
import com.webapp.talenthub.security.CustomUserDetails;
import com.webapp.talenthub.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class HRDashboardController {

    private final JobService jobService;
    private final ApplicationRepository applicationRepository;
    private final InterviewRepository interviewRepository;

    @Autowired
    public HRDashboardController(JobService jobService, 
                                 ApplicationRepository applicationRepository, 
                                 InterviewRepository interviewRepository) {
        this.jobService = jobService;
        this.applicationRepository = applicationRepository;
        this.interviewRepository = interviewRepository;
    }

    // SCR-06: HR Dashboard
    @GetMapping("/dashboard")
    public String viewDashboard(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model) {

        if (userDetails == null) {
            return "redirect:/login";
        }

        User user = userDetails.getUser();
        Long hrId = user.getId();

        // 1. ACTIVE jobs count for this HR
        long activeJobsCount = jobService.countByStatusAndCreator(JobStatus.ACTIVE, hrId);

        // 2. APPLIED applications count under jobs of this HR
        long newApplicationsCount = applicationRepository.countNewApplicationsForHr(hrId);

        // 3. Interviews scheduled in the next 7 days
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime sevenDaysLater = now.plusDays(7);
        long upcomingInterviewsCount = interviewRepository.countUpcomingInterviewsForHr(hrId, now, sevenDaysLater);

        // 4. List of ACTIVE jobs created by this HR
        List<Job> activeJobs = jobService.getJobsByStatusAndCreator(JobStatus.ACTIVE, hrId);

        model.addAttribute("activeJobsCount", activeJobsCount);
        model.addAttribute("newApplicationsCount", newApplicationsCount);
        model.addAttribute("upcomingInterviewsCount", upcomingInterviewsCount);
        model.addAttribute("activeJobs", activeJobs);

        return "dashboard";
    }
}
