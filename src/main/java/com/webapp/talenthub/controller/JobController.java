package com.webapp.talenthub.controller;

import com.webapp.talenthub.entity.Application;
import com.webapp.talenthub.entity.Job;
import com.webapp.talenthub.entity.JobStatus;
import com.webapp.talenthub.entity.User;
import com.webapp.talenthub.security.CustomUserDetails;
import com.webapp.talenthub.service.ApplicationService;
import com.webapp.talenthub.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/hr/jobs")
public class JobController {

    private final JobService jobService;
    private final ApplicationService applicationService;

    @Autowired
    public JobController(JobService jobService, ApplicationService applicationService) {
        this.jobService = jobService;
        this.applicationService = applicationService;
    }

    // SCR-10: Job List
    @GetMapping
    public String listJobs(
            @RequestParam(value = "status", defaultValue = "ALL") String statusStr,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "department", required = false) String department,
            Model model) {

        JobStatus status = null;
        if (!"ALL".equalsIgnoreCase(statusStr)) {
            try {
                status = JobStatus.valueOf(statusStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                statusStr = "ALL";
            }
        }

        List<Job> jobs = jobService.searchJobs(status, keyword, department, null);
        
        // Calculate counts for tabs
        long allCount = jobService.searchJobs(null, null, null, null).size();
        long draftCount = jobService.countByStatus(JobStatus.DRAFT);
        long activeCount = jobService.countByStatus(JobStatus.ACTIVE);
        long closedCount = jobService.countByStatus(JobStatus.CLOSED);

        List<String> departments = jobService.getDistinctDepartments();

        model.addAttribute("jobs", jobs);
        model.addAttribute("departments", departments);
        model.addAttribute("selectedStatus", statusStr.toUpperCase());
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedDepartment", department);

        model.addAttribute("allCount", allCount);
        model.addAttribute("draftCount", draftCount);
        model.addAttribute("activeCount", activeCount);
        model.addAttribute("closedCount", closedCount);

        return "jobs/list";
    }

    // SCR-11: Create Job Form
    @GetMapping("/new")
    public String createJobForm(Model model) {
        model.addAttribute("job", new Job());
        model.addAttribute("isClosed", false);
        return "jobs/form";
    }

    // SCR-11: Edit Job Form
    @GetMapping("/edit/{id}")
    public String editJobForm(@PathVariable("id") Long id, Model model) {
        Job job = jobService.getJobById(id);
        model.addAttribute("job", job);
        model.addAttribute("isClosed", job.getStatus() == JobStatus.CLOSED);
        return "jobs/form";
    }

    // SCR-11: Save Job (both Create and Edit)
    @PostMapping("/save")
    public String saveJob(
            @ModelAttribute("job") Job job,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            RedirectAttributes redirectAttributes) {

        if (userDetails == null) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng đăng nhập.");
            return "redirect:/login";
        }

        User user = userDetails.getUser();

        try {
            jobService.saveJob(job, user);
            redirectAttributes.addFlashAttribute("success", "Đã lưu thông tin bài đăng thành công!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            if (job.getId() != null) {
                return "redirect:/hr/jobs/edit/" + job.getId();
            } else {
                return "redirect:/hr/jobs/new";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lưu thất bại: " + e.getMessage());
            if (job.getId() != null) {
                return "redirect:/hr/jobs/edit/" + job.getId();
            } else {
                return "redirect:/hr/jobs/new";
            }
        }

        return "redirect:/hr/jobs";
    }

    // SCR-10: Publish Job
    @PostMapping("/{id}/publish")
    public String publishJob(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            RedirectAttributes redirectAttributes) {

        if (userDetails == null) return "redirect:/login";
        
        try {
            jobService.publishJob(id, userDetails.getUser());
            redirectAttributes.addFlashAttribute("success", "Đã phát hành bài đăng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Phát hành thất bại: " + e.getMessage());
        }
        return "redirect:/hr/jobs";
    }

    // SCR-10: Close Job
    @PostMapping("/{id}/close")
    public String closeJob(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            RedirectAttributes redirectAttributes) {

        if (userDetails == null) return "redirect:/login";
        
        try {
            jobService.closeJob(id, userDetails.getUser());
            redirectAttributes.addFlashAttribute("success", "Đã đóng tin tuyển dụng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Đóng tin thất bại: " + e.getMessage());
        }
        return "redirect:/hr/jobs";
    }

    // SCR-10: Delete Job
    @PostMapping("/{id}/delete")
    public String deleteJob(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            RedirectAttributes redirectAttributes) {

        if (userDetails == null) return "redirect:/login";
        
        try {
            jobService.deleteJob(id, userDetails.getUser());
            redirectAttributes.addFlashAttribute("success", "Đã xóa tin tuyển dụng nháp thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Xóa thất bại: " + e.getMessage());
        }
        return "redirect:/hr/jobs";
    }

    // SCR-12: Job Detail (Internal)
    @GetMapping("/detail/{id}")
    public String viewJobDetail(@PathVariable("id") Long id, Model model) {
        Job job = jobService.getJobById(id);
        List<Application> applications = applicationService.getApplicationsByJob(id);

        // Count applications per stage for Pipeline Summary
        long appliedCount = 0;
        long screeningCount = 0;
        long interviewCount = 0;
        long offerCount = 0;
        long hiredCount = 0;
        long rejectedCount = 0;
        long withdrawnCount = 0;

        for (Application app : applications) {
            switch (app.getStatus()) {
                case APPLIED:
                    appliedCount++;
                    break;
                case SCREENING:
                    screeningCount++;
                    break;
                case INTERVIEW:
                    interviewCount++;
                    break;
                case OFFER:
                    offerCount++;
                    break;
                case HIRED:
                    hiredCount++;
                    break;
                case REJECTED:
                    rejectedCount++;
                    break;
                case WITHDRAWN:
                    withdrawnCount++;
                    break;
            }
        }

        model.addAttribute("job", job);
        model.addAttribute("applicationsCount", applications.size());
        
        // Pipeline summary stats
        model.addAttribute("appliedCount", appliedCount);
        model.addAttribute("screeningCount", screeningCount);
        model.addAttribute("interviewCount", interviewCount);
        model.addAttribute("offerCount", offerCount);
        model.addAttribute("hiredCount", hiredCount);
        model.addAttribute("rejectedCount", rejectedCount);
        model.addAttribute("withdrawnCount", withdrawnCount);

        return "jobs/detail";
    }
}
