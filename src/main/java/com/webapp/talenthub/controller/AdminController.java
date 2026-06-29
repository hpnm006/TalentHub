package com.webapp.talenthub.controller;

import com.webapp.talenthub.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/admin/dashboard")
    public String dashboard(Model model) {

        model.addAttribute("totalUsers",
                userService.totalUsers());

        model.addAttribute("candidates",
                userService.totalCandidates());

        model.addAttribute("hrManagers",
                userService.totalHRManagers());

        model.addAttribute("interviewers",
                userService.totalInterviewers());

        model.addAttribute("lockedUsers",
                userService.lockedUsers());

        return "admin/dashboard";
    }

}