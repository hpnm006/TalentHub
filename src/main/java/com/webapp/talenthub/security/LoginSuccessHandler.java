package com.webapp.talenthub.security;

import com.webapp.talenthub.entity.User;
import com.webapp.talenthub.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        User user = userRepository
                .findByUsername(authentication.getName())
                .orElse(null);

        if (user != null) {

            user.setFailedAttempts(0);

            user.setLockTime(null);

            userRepository.save(user);
        }

        response.sendRedirect("/");
    }
}