package com.webapp.talenthub.controller;

import com.webapp.talenthub.entity.Job;
import com.webapp.talenthub.entity.JobStatus;
import com.webapp.talenthub.entity.User;
import com.webapp.talenthub.repository.ApplicationRepository;
import com.webapp.talenthub.repository.InterviewRepository;
import com.webapp.talenthub.service.JobService;
import com.webapp.talenthub.util.SessionUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
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
    public String viewDashboard(HttpSession session,
                                Model model) {

        User user = SessionUtil.getUser(session);

        if (user == null) {
            return "redirect:/login";
        }

        Long hrId = user.getId();

        // ACTIVE jobs count
        long activeJobsCount =
                jobService.countByStatusAndCreator(JobStatus.ACTIVE, hrId);

        // New applications
        long newApplicationsCount =
                applicationRepository.countNewApplicationsForHr(hrId);

        // Upcoming interviews
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime sevenDaysLater = now.plusDays(7);

        long upcomingInterviewsCount =
                interviewRepository.countUpcomingInterviewsForHr(
                        hrId,
                        now,
                        sevenDaysLater
                );

        // Active jobs
        List<Job> activeJobs =
                jobService.getJobsByStatusAndCreator(
                        JobStatus.ACTIVE,
                        hrId
                );

        model.addAttribute("activeJobsCount", activeJobsCount);
        model.addAttribute("newApplicationsCount", newApplicationsCount);
        model.addAttribute("upcomingInterviewsCount", upcomingInterviewsCount);
        model.addAttribute("activeJobs", activeJobs);

        return "dashboard";
    }
}