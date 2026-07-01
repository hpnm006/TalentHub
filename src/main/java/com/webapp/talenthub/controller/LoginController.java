package com.webapp.talenthub.controller;

import com.webapp.talenthub.entity.Role;
import com.webapp.talenthub.entity.User;
import com.webapp.talenthub.repository.UserRepository;
import com.webapp.talenthub.util.SessionUtil;
import jakarta.servlet.http.HttpSession;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class LoginController {

    private final UserRepository repository;

    public LoginController(UserRepository repository) {
        this.repository = repository;
    }

    @PostMapping("/login")
    public String login(
            @RequestParam String username,
            @RequestParam String password,
            HttpSession session) {

        User user = repository.findByUsername(username)
                .or(() -> repository.findByEmail(username))
                .orElse(null);

        if (user == null) {
            return "redirect:/login?error";
        }

        if (!user.getEnabled()) {
            return "redirect:/login?disabled";
        }

        if (user.getLockTime() != null
                && user.getLockTime() > System.currentTimeMillis()) {

            return "redirect:/login?locked";
        }

        if (!BCrypt.checkpw(password, user.getPassword())) {

            user.setFailedAttempts(user.getFailedAttempts() + 1);

            int attempts = user.getFailedAttempts() == null ? 0 : user.getFailedAttempts();

            attempts++;

            user.setFailedAttempts(attempts);

            if (user.getFailedAttempts() >= 5) {

                user.setLockTime(System.currentTimeMillis() + 15 * 60 * 1000);

            }

            repository.save(user);

            return "redirect:/login?error";
        }

        user.setFailedAttempts(0);
        user.setLockTime(null);

        repository.save(user);

        SessionUtil.login(session, user);

        if (user.getRole() == Role.ADMIN) {
            return "redirect:/admin/dashboard";
        }

        if (user.getRole() == Role.HR_MANAGER) {
            return "redirect:/dashboard";
        }

        if (user.getRole() == Role.INTERVIEWER) {
            return "redirect:/applications";
        }

        return "redirect:/my-applications";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session){

        SessionUtil.logout(session);

        return "redirect:/login";
    }
}