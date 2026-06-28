package com.webapp.talenthub.controller;

import com.webapp.talenthub.dto.ChangePasswordRequest;
import com.webapp.talenthub.dto.ForgotPasswordRequest;
import com.webapp.talenthub.dto.RegisterRequest;
import com.webapp.talenthub.dto.ResetPasswordRequest;
import com.webapp.talenthub.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    @Autowired
    private AuthService authService;

    // ===========================
    // Login
    // ===========================

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    // ===========================
    // Register
    // ===========================

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "register";
    }

    @PostMapping("/register")
    public String register(
            @ModelAttribute RegisterRequest registerRequest,
            Model model) {

        String result = authService.register(registerRequest);

        if ("success".equals(result)) {
            return "redirect:/login";
        }

        model.addAttribute("error", result);
        model.addAttribute("registerRequest", registerRequest);

        return "register";
    }

    // ===========================
    // Forgot Password
    // ===========================

    @GetMapping("/forgot-password")
    public String forgotPasswordPage(Model model) {

        model.addAttribute("forgotPasswordRequest",
                new ForgotPasswordRequest());

        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String forgotPassword(
            @ModelAttribute ForgotPasswordRequest request,
            Model model) {

        String result = authService.forgotPassword(request);

        if ("Email does not exist".equals(result)) {

            model.addAttribute("message", result);

            return "forgot-password";
        }

        model.addAttribute("message",
                "Reset Code: " + result);

        return "forgot-password";
    }

    // ===========================
    // Reset Password
    // ===========================

    @GetMapping("/reset-password")
    public String resetPasswordPage(Model model) {

        model.addAttribute("resetPasswordRequest",
                new ResetPasswordRequest());

        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String resetPassword(
            @ModelAttribute ResetPasswordRequest request,
            Model model) {

        String result = authService.resetPassword(request);

        if ("success".equals(result)) {

            model.addAttribute("success",
                    "Password reset successfully.");

            return "redirect:/login";

        } else {

            model.addAttribute("error", result);

            return "reset-password";
        }
    }

    // ===========================
    // Change Password
    // ===========================

    @GetMapping("/change-password")
    public String changePasswordPage(Model model) {

        model.addAttribute("changePasswordRequest",
                new ChangePasswordRequest());

        return "change-password";
    }

    @PostMapping("/change-password")
    public String changePassword(
            @ModelAttribute ChangePasswordRequest request,
            Model model) {

        String result = authService.changePassword(request);

        if ("success".equals(result)) {
            model.addAttribute("success", "Password changed successfully.");
        } else {
            model.addAttribute("error", result);
        }

        return "change-password";
    }

}