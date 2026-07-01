package com.webapp.talenthub.controller;

import com.webapp.talenthub.entity.Application;
import com.webapp.talenthub.entity.User;
import com.webapp.talenthub.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.webapp.talenthub.util.SessionUtil;
import jakarta.servlet.http.HttpSession;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/my-applications")
public class CandidateController {

    private final ApplicationService applicationService;

    @Autowired
    public CandidateController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    // SCR-15 Candidate Application List
    @GetMapping
    public String viewMyApplications(
            @RequestParam(value = "status", required = false) String statusStr,
            HttpSession session,
            Model model) {

        User user = SessionUtil.getUser(session);

        if (user == null) {
            return "redirect:/login";
        }
        List<Application> applications = applicationService.getApplicationsByCandidate(user.getId());

        // Optional status filtering
        if (statusStr != null && !statusStr.isEmpty() && !"ALL".equalsIgnoreCase(statusStr)) {
            applications = applications.stream()
                    .filter(app -> app.getStatus().name().equalsIgnoreCase(statusStr))
                    .collect(Collectors.toList());
        }

        model.addAttribute("applications", applications);
        model.addAttribute("selectedStatus", statusStr != null ? statusStr.toUpperCase() : "ALL");

        return "candidates/my-applications";
    }

    // SCR-15 Withdraw Application
    @PostMapping("/withdraw/{id}")
    public String withdrawApplication(
            @PathVariable("id") Long id,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        User user = SessionUtil.getUser(session);

        if (user == null) {
            return "redirect:/login";
        }
        try {
            applicationService.withdrawApplication(id, user);
            redirectAttributes.addFlashAttribute("success", "Application withdrawn successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to withdraw application: " + e.getMessage());
        }

        return "redirect:/my-applications";
    }
}
