package com.webapp.talenthub.security;

import com.webapp.talenthub.entity.User;
import com.webapp.talenthub.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.security.authentication.LockedException;

import java.io.IOException;

@Component
public class LoginFailureHandler implements AuthenticationFailureHandler {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception)
            throws IOException {

        String username = request.getParameter("username");

        User user = userRepository.findByUsername(username).orElse(null);

        if (user != null && user.getLockTime() == null) {

            int attempts = user.getFailedAttempts() + 1;

            user.setFailedAttempts(attempts);

            if (attempts == 5) {

                user.setLockTime(System.currentTimeMillis());

            }

            userRepository.save(user);
        }

        if (exception instanceof LockedException
                || (exception.getCause() instanceof LockedException)) {

            response.sendRedirect("/login?locked");

        } else {

            response.sendRedirect("/login?error");

        }
    }
}