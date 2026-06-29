package com.webapp.talenthub.controller;

import com.webapp.talenthub.entity.Application;
import com.webapp.talenthub.entity.ApplicationStatus;
import com.webapp.talenthub.entity.User;
import com.webapp.talenthub.security.CustomUserDetails;
import com.webapp.talenthub.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.nio.file.Path;
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

    // Secure CV Download Endpoint
    @GetMapping("/{id}/cv")
    @ResponseBody
    public ResponseEntity<Resource> downloadCv(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        Application application = applicationService.getApplicationById(id);
        User user = userDetails.getUser();
        String role = user.getRole().name();

        boolean isOwner = application.getCandidate().getUser().getId().equals(user.getId());
        boolean isStaff = "ADMIN".equals(role) || "HR_MANAGER".equals(role) || "INTERVIEWER".equals(role);

        if (!isOwner && !isStaff) {
            return ResponseEntity.status(403).build();
        }

        if (application.getCvUrl() == null || application.getCvUrl().isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        try {
            Path filePath = applicationService.getCvPath(application.getCvUrl());
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() || resource.isReadable()) {
                String contentType = "application/pdf";
                if (application.getCvUrl().toLowerCase().endsWith(".docx")) {
                    contentType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
                }

                String candidateName = application.getCandidate().getUser().getFullName().replaceAll("\\s+", "_");
                String fileExtension = application.getCvUrl().toLowerCase().endsWith(".pdf") ? ".pdf" : ".docx";

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + candidateName + "_CV" + fileExtension + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
