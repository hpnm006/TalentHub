package com.webapp.talenthub.controller;

import com.webapp.talenthub.entity.User;
import com.webapp.talenthub.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.webapp.talenthub.util.SessionUtil;
import jakarta.servlet.http.HttpSession;

@Controller
public class ProfileController {

    private final UserRepository userRepository;

    public ProfileController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/profile")
    public String profile(HttpSession session,
                          Model model) {

        User user = SessionUtil.getUser(session);

        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);

        return "profile";
    }

}