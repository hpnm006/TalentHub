package com.webapp.talenthub.security;

import com.webapp.talenthub.entity.User;
import com.webapp.talenthub.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.springframework.security.authentication.LockedException;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        User user = userRepository.findByUsername(username)
                .or(() -> userRepository.findByEmail(username))
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found with username or email: " + username));


        if (user.getLockTime() != null) {

            long lockDuration = 10 * 60 * 1000;

            if (System.currentTimeMillis() - user.getLockTime() < lockDuration) {

                throw new LockedException(
                        "Your account has been temporarily locked after too many failed attempts. Please try again in 10 minutes.");

            }


            user.setFailedAttempts(0);
            user.setLockTime(null);
            userRepository.save(user);
        }

        return new CustomUserDetails(user);
    }
}