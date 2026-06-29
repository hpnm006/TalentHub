package com.webapp.talenthub.controller;

import com.webapp.talenthub.entity.Job;
import com.webapp.talenthub.entity.User;
import com.webapp.talenthub.security.CustomUserDetails;
import com.webapp.talenthub.service.ApplicationService;
import com.webapp.talenthub.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/jobs")
public class PublicJobController {

    private final JobService jobService;
    private final ApplicationService applicationService;

    @Autowired
    public PublicJobController(JobService jobService, ApplicationService applicationService) {
        this.jobService = jobService;
        this.applicationService = applicationService;
    }

    // SCR-13: Public Job List
    @GetMapping
    public String listPublicJobs(
            @RequestParam(value = "department", required = false) String department,
            @RequestParam(value = "location", required = false) String location,
            Model model) {

        List<Job> jobs = jobService.getActiveJobsFiltered(department, location);
        List<String> departments = jobService.getDistinctDepartments();
        List<String> locations = jobService.getDistinctLocations();

        model.addAttribute("jobs", jobs);
        model.addAttribute("departments", departments);
        model.addAttribute("locations", locations);
        model.addAttribute("selectedDepartment", department);
        model.addAttribute("selectedLocation", location);

        return "jobs/public-list";
    }

    // SCR-14: Public Job Detail
    @GetMapping("/{id}")
    public String viewPublicJobDetail(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model) {

        Job job = jobService.getJobById(id);
        boolean hasApplied = false;
        boolean isCandidate = false;

        if (userDetails != null) {
            User user = userDetails.getUser();
            isCandidate = "CANDIDATE".equals(user.getRole().name());
            hasApplied = applicationService.hasApplied(id, user.getId());
        }

        model.addAttribute("job", job);
        model.addAttribute("hasApplied", hasApplied);
        model.addAttribute("isCandidate", isCandidate);
        model.addAttribute("isClosed", !"ACTIVE".equals(job.getStatus()));

        return "jobs/public-detail";
    }

    // SCR-14 Submit Application
    @PostMapping("/{id}/apply")
    public String applyForJob(
            @PathVariable("id") Long id,
            @RequestParam("coverLetter") String coverLetter,
            @RequestParam("cvFile") MultipartFile cvFile,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            RedirectAttributes redirectAttributes) {

        if (userDetails == null) {
            redirectAttributes.addFlashAttribute("error", "You must login to apply for positions.");
            return "redirect:/login";
        }

        User user = userDetails.getUser();
        if (!"CANDIDATE".equals(user.getRole().name())) {
            redirectAttributes.addFlashAttribute("error", "Only Candidate role can apply for jobs.");
            return "redirect:/jobs/" + id;
        }

        if (cvFile.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Please upload your CV file.");
            return "redirect:/jobs/" + id;
        }

        // Validate file type (PDF, DOCX) and size (max 5MB)
        String originalFilename = cvFile.getOriginalFilename();
        if (originalFilename != null) {
            String lower = originalFilename.toLowerCase();
            if (!lower.endsWith(".pdf") && !lower.endsWith(".docx")) {
                redirectAttributes.addFlashAttribute("error", "Only PDF and DOCX files are allowed.");
                return "redirect:/jobs/" + id;
            }
        }

        if (cvFile.getSize() > 5 * 1024 * 1024) { // 5 MB
            redirectAttributes.addFlashAttribute("error", "File size exceeds limit of 5MB.");
            return "redirect:/jobs/" + id;
        }

        try {
            applicationService.applyJob(id, user, coverLetter, cvFile);
            redirectAttributes.addFlashAttribute("success", "Your application has been submitted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to submit application: " + e.getMessage());
        }

        return "redirect:/jobs/" + id;
    }
}
