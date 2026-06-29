package com.webapp.talenthub.controller;

import com.webapp.talenthub.entity.Application;
import com.webapp.talenthub.entity.ApplicationStatus;
import com.webapp.talenthub.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/applications")
public class ApplicationController {

    private final ApplicationService applicationService;

    @Autowired
    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @GetMapping("/job/{jobId}")
    public String getApplicationsByJob(@PathVariable Long jobId, Model model) {
        List<Application> applications = applicationService.getApplicationsByJob(jobId);
        model.addAttribute("applications", applications);
        model.addAttribute("jobId", jobId);
        return "applications/list";
    }

    @GetMapping("/{id}")
    public String getApplicationDetail(@PathVariable Long id, Model model) {
        Application application = applicationService.getApplicationById(id);
        model.addAttribute("application", application);
        model.addAttribute("statuses", ApplicationStatus.values());
        return "applications/detail";
    }

    @PostMapping("/{id}/status")
    public String updateApplicationStatus(@PathVariable Long id, @RequestParam("status") ApplicationStatus status) {
        Application updated = applicationService.updateStatus(id, status);
        return "redirect:/applications/" + id;
    }
}
