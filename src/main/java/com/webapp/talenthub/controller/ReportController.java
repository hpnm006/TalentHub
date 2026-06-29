package com.webapp.talenthub.controller;

import com.webapp.talenthub.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/pipeline")
    public String showPipelineReport(Model model) {
        Map<String, Object> report = reportService.getPipelineReport();
        model.addAttribute("report", report);
        return "reports/pipeline";
    }
}
