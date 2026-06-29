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
import com.webapp.talenthub.dto.ResetPasswordRequest;
import java.security.SecureRandom;

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

        if (request.getFullName() == null || request.getFullName().trim().length() < 2) {
            return "Full name must contain at least 2 characters.";
        }

        if (!request.getFullName().matches("^[A-Za-zÀ-ỹ\\s]+$")) {
            return "Full name contains invalid characters.";
        }


        if (request.getUsername().length() < 4
                || request.getUsername().length() > 50) {

            return "Username must be between 4 and 50 characters.";
        }


        if (!request.getUsername().matches("^[A-Za-z0-9_]+$")) {

            return "Username can only contain letters, numbers and underscore.";
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            return "Passwords do not match";
        }

        String error = validatePassword(request.getPassword());

        if (error != null) {
            return error;
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

        String error = validatePassword(request.getNewPassword());

        if (error != null) {
            return error;
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        userRepository.save(user);

        return "success";
    }

    public String forgotPassword(ForgotPasswordRequest request) {

        User user = userRepository.findByEmail(request.getEmail()).orElse(null);

        if (user == null) {
            return "success";
        }

        SecureRandom random = new SecureRandom();

        String code = String.format("%06d", random.nextInt(1000000));

        user.setResetCode(code);

        user.setResetCodeExpiry(System.currentTimeMillis() + 10 * 60 * 1000);

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

        if (System.currentTimeMillis() > user.getResetCodeExpiry()) {

            return "Reset code has expired";

        }

        if(!request.getNewPassword().equals(request.getConfirmPassword())){
            return "Passwords do not match";
        }

        String error = validatePassword(request.getNewPassword());

        if (error != null) {
            return error;
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        user.setResetCode(null);

        user.setResetCodeExpiry(null);

        userRepository.save(user);

        return "success";
    }

    private String validatePassword(String password) {

        if (password.length() < 8) {
            return "Password must be at least 8 characters.";
        }

        if (!password.matches(".*[A-Z].*")) {
            return "Password must contain at least one uppercase letter.";
        }

        if (!password.matches(".*\\d.*")) {
            return "Password must contain at least one number.";
        }

        return null;
    }

}