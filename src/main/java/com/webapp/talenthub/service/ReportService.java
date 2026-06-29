package com.webapp.talenthub.service;

import com.webapp.talenthub.entity.Application;
import com.webapp.talenthub.entity.ApplicationStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private final ApplicationService applicationService;

    @Autowired
    public ReportService(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    public Map<String, Object> getPipelineReport() {
        List<Application> allApplications = applicationService.getAllApplications();
        int totalApplied = allApplications.size();

        Map<ApplicationStatus, Long> countsByStatus = allApplications.stream()
                .collect(Collectors.groupingBy(Application::getStatus, Collectors.counting()));

        long appliedCount = countsByStatus.getOrDefault(ApplicationStatus.APPLIED, 0L);
        long screeningCount = countsByStatus.getOrDefault(ApplicationStatus.SCREENING, 0L);
        long interviewCount = countsByStatus.getOrDefault(ApplicationStatus.INTERVIEW, 0L);
        long offerCount = countsByStatus.getOrDefault(ApplicationStatus.OFFER, 0L);
        long hiredCount = countsByStatus.getOrDefault(ApplicationStatus.HIRED, 0L);
        long rejectedCount = countsByStatus.getOrDefault(ApplicationStatus.REJECTED, 0L);
        long withdrawnCount = countsByStatus.getOrDefault(ApplicationStatus.WITHDRAWN, 0L);

        Map<String, Object> report = new HashMap<>();
        report.put("totalApplied", totalApplied);
        report.put("appliedCount", appliedCount);
        report.put("screeningCount", screeningCount);
        report.put("interviewCount", interviewCount);
        report.put("offerCount", offerCount);
        report.put("hiredCount", hiredCount);
        report.put("rejectedCount", rejectedCount);
        report.put("withdrawnCount", withdrawnCount);

        if (totalApplied > 0) {
            report.put("interviewRate", (double) interviewCount / totalApplied * 100);
            report.put("hireRate", (double) hiredCount / totalApplied * 100);
        } else {
            report.put("interviewRate", 0.0);
            report.put("hireRate", 0.0);
        }

        return report;
    }
}
