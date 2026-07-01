package com.webapp.talenthub.controller;


import com.webapp.talenthub.entity.Role;
import com.webapp.talenthub.entity.User;
import com.webapp.talenthub.service.ActivityLogService;
import com.webapp.talenthub.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.webapp.talenthub.util.SessionUtil;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

    private final ActivityLogService activityLogService;
    private final UserService userService;

    public AdminUserController(ActivityLogService activityLogService, UserService userService) {
        this.activityLogService = activityLogService;
        this.userService = userService;

    }

    @GetMapping
    public String users(Model model) {
        model.addAttribute("users", userService.findAll());
        return "admin/users";

    }

    @GetMapping("/search")
    public String search(@RequestParam String keyword, Model model) {
        model.addAttribute("users", userService.search(keyword));
        model.addAttribute("keyword", keyword);
        return "admin/users";
    }

    @GetMapping("/{id}/disable")
    public String disable(@ModelAttribute User user,
                          HttpSession session) {

        User loginUser = SessionUtil.getUser(session);

        if (loginUser == null) {
            return "redirect:/login";
        }

        if (loginUser.getRole() != Role.ADMIN) {
            return "redirect:/access-denied";
        }

        userService.disable(user.getId());

        activityLogService.save(
                loginUser.getUsername(),
                "DISABLE USER",
                "Disabled " + user.getUsername()
        );

        return "redirect:/admin/users";
    }

    @GetMapping("/{id}/enable")
    public String enable(@ModelAttribute User user,
                         HttpSession session) {

        User loginUser = SessionUtil.getUser(session);

        if (loginUser == null) {
            return "redirect:/login";
        }

        if (loginUser.getRole() != Role.ADMIN) {
            return "redirect:/access-denied";
        }

        userService.enable(user.getId());

        activityLogService.save(
                loginUser.getUsername(),
                "ENABLE USER",
                "Enabled " + user.getUsername()
        );

        return "redirect:/admin/users";
    }

    @GetMapping("/{id}/unlock")
    public String unlock(@ModelAttribute User user,
                         HttpSession session) {

        User loginUser = SessionUtil.getUser(session);

        if (loginUser == null) {
            return "redirect:/login";
        }

        if (loginUser.getRole() != Role.ADMIN) {
            return "redirect:/access-denied";
        }

        userService.unlock(user.getId());

        activityLogService.save(
                loginUser.getUsername(),
                "UNLOCK USER",
                "Unlocked " + user.getUsername()
        );

        return "redirect:/admin/users";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", Role.values());
        return "admin/user-form";
    }

    @PostMapping("/create")
    public String createUser(@ModelAttribute User user,
                             HttpSession session) {

        User loginUser = SessionUtil.getUser(session);

        if (loginUser == null) {
            return "redirect:/login";
        }

        if (loginUser.getRole() != Role.ADMIN) {
            return "redirect:/access-denied";
        }

        userService.createUser(user);

        activityLogService.save(
                loginUser.getUsername(),
                "CREATE USER",
                "Created account: " + user.getUsername()
        );

        return "redirect:/admin/users";
    }

    @GetMapping("/edit/{id}")
    public String editUser(@PathVariable Long id,
                           Model model) {
        User user = userService.getUserById(id);
        user.setPassword("");
        model.addAttribute("user", user);
        model.addAttribute("roles", Role.values());
        model.addAttribute("isEdit", true);
        return "admin/user-form";
    }

    @PostMapping("/edit")
    public String updateUser(@ModelAttribute User user,
                             HttpSession session) {

        User loginUser = SessionUtil.getUser(session);

        if (loginUser == null) {
            return "redirect:/login";
        }

        if (loginUser.getRole() != Role.ADMIN) {
            return "redirect:/access-denied";
        }

        userService.updateUser(user);

        activityLogService.save(
                loginUser.getUsername(),
                "EDIT USER",
                "Updated account: " + user.getUsername()
        );

        return "redirect:/admin/users";
    }
}