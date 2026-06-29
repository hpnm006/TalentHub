package com.webapp.talenthub.controller;

import com.webapp.talenthub.service.ActivityLogService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/activity-log")
public class ActivityLogController {

    private final ActivityLogService activityLogService;

    public ActivityLogController(ActivityLogService activityLogService) {

        this.activityLogService = activityLogService;

    }

    @GetMapping
    public String logs(Model model){

        model.addAttribute(
                "logs",
                activityLogService.findAll());

        return "admin/activity-log";

    }

}
