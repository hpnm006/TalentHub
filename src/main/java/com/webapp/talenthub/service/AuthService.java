package com.webapp.talenthub.service;

import com.webapp.talenthub.dto.RegisterRequest;
import com.webapp.talenthub.entity.Role;
import com.webapp.talenthub.entity.User;
import com.webapp.talenthub.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.webapp.talenthub.dto.ChangePasswordRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.webapp.talenthub.dto.ForgotPasswordRequest;
import java.util.Random;
import com.webapp.talenthub.dto.ResetPasswordRequest;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public String register(RegisterRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            return "Username already exists";
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            return "Email already exists";
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            return "Passwords do not match";
        }

        User user = new User();

        user.setFullName(request.getFullName());

        user.setUsername(request.getUsername());

        user.setEmail(request.getEmail());

        user.setPassword(passwordEncoder.encode(request.getPassword()));

        user.setRole(Role.CANDIDATE);

        user.setEnabled(true);

        user.setFailedAttempts(0);

        user.setLockTime(null);

        userRepository.save(user);

        return "success";
    }

    public String changePassword(ChangePasswordRequest request) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String username = authentication.getName();

        User user = userRepository.findByUsername(username).orElse(null);

        if (user == null) {
            return "User not found";
        }

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            return "Current password is incorrect";
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            return "Passwords do not match";
        }

        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            return "New password must be different from current password";
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        userRepository.save(user);

        return "success";
    }

    public String forgotPassword(ForgotPasswordRequest request) {

        User user = userRepository.findByEmail(request.getEmail()).orElse(null);

        if (user == null) {
            return "Email does not exist";
        }

        Random random = new Random();

        String code = String.format("%06d", random.nextInt(1000000));

        user.setResetCode(code);

        userRepository.save(user);

        return code;
    }

    public String resetPassword(ResetPasswordRequest request){

        User user = userRepository.findByEmail(request.getEmail()).orElse(null);

        if(user == null){
            return "Email does not exist";
        }

        if(user.getResetCode() == null){
            return "No reset code";
        }

        if(!user.getResetCode().equals(request.getResetCode())){
            return "Invalid reset code";
        }

        if(!request.getNewPassword().equals(request.getConfirmPassword())){
            return "Passwords do not match";
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        user.setResetCode(null);

        userRepository.save(user);

        return "success";
    }

}