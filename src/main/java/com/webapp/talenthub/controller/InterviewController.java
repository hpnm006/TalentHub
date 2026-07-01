package com.webapp.talenthub.controller;

import com.webapp.talenthub.entity.Application;
import com.webapp.talenthub.entity.User;
import com.webapp.talenthub.service.ApplicationService;
import com.webapp.talenthub.service.InterviewService;
// Note: assuming a UserService exists to fetch users by id, or we mock it.
// We will mock fetching the interviewer for now.
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.webapp.talenthub.entity.Role;
import com.webapp.talenthub.util.SessionUtil;
import jakarta.servlet.http.HttpSession;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping("/interviews")
public class InterviewController {

    private final InterviewService interviewService;
    private final ApplicationService applicationService;

    @Autowired
    public InterviewController(InterviewService interviewService, ApplicationService applicationService) {
        this.interviewService = interviewService;
        this.applicationService = applicationService;
    }

    @GetMapping("/assign/{appId}")
    public String showAssignForm(@PathVariable Long appId,
                                 HttpSession session,
                                 Model model) {

        User loginUser = SessionUtil.getUser(session);

        if (loginUser == null) {
            return "redirect:/login";
        }

        if (loginUser.getRole() != Role.ADMIN
                && loginUser.getRole() != Role.HR_MANAGER) {

            return "redirect:/access-denied";
        }

        Application application = applicationService.getApplicationById(appId);
        model.addAttribute("application", application);
        return "interviews/assignment";
    }

    @PostMapping("/assign/{appId}")
    public String assignInterview(
            @PathVariable Long appId,
            @RequestParam("interviewerId") Long interviewerId,
            @RequestParam("scheduledAt") String scheduledAtStr,
            @RequestParam("linkOrLocation") String linkOrLocation,
            @RequestParam("notes") String notes, HttpSession session) {

        User loginUser = SessionUtil.getUser(session);

        if (loginUser == null) {
            return "redirect:/login";
        }

        if (loginUser.getRole() != Role.ADMIN
                && loginUser.getRole() != Role.HR_MANAGER) {

            return "redirect:/access-denied";
        }
        
        // In a real app, fetch User by interviewerId. Mocking a User for now.
        User interviewer = new User();
        interviewer.setId(interviewerId);
        interviewer.setFullName("Mock Interviewer"); // Mocked

        LocalDateTime scheduledAt = LocalDateTime.parse(scheduledAtStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        
        interviewService.scheduleInterview(appId, interviewer, scheduledAt, linkOrLocation, notes);
        
        return "redirect:/applications/" + appId;
    }
}
