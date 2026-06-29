package com.webapp.talenthub.service;

import com.webapp.talenthub.entity.Role;
import com.webapp.talenthub.entity.User;
import com.webapp.talenthub.repository.UserRepository;
import jakarta.annotation.Nonnull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User getCurrentUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public void deactivateUser(Long id) {
        User user = getUserById(id);
        user.setEnabled(false);
        userRepository.save(user);
    }

    public void unlockUser(Long id) {
        User user = getUserById(id);
        user.setFailedAttempts(0);
        user.setLockTime(null);
        userRepository.save(user);
    }

    public List<User> filterByRole(Role role) {
        return userRepository.findByRole(role);
    }

    public List<User> searchUsers(String keyword) {
        return userRepository.search(keyword);
    }

    public long totalUsers() {
        return userRepository.count();
    }

    public long totalCandidates() {
        return userRepository.countByRole(Role.CANDIDATE);
    }

    public long totalHRManagers() {
        return userRepository.countByRole(Role.HR_MANAGER);
    }

    public long totalInterviewers() {
        return userRepository.countByRole(Role.INTERVIEWER);
    }

    public long lockedUsers() {
        return userRepository.countByEnabled(false);
    }

    public List<User> findAll() {

        return userRepository.findAllByOrderByIdDesc();

    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow();

    }

    public void save(User user) {
        userRepository.save(user);
    }

    public void disable(Long id) {
        User user = findById(id);
        user.setEnabled(false);
        userRepository.save(user);
    }

    public void enable(Long id) {
        User user = findById(id);
        user.setEnabled(true);
        userRepository.save(user);
    }

    public List<User> search(String keyword) {
        return userRepository.search(keyword);
    }

    public void unlock(Long id) {
        User user = findById(id);
        user.setFailedAttempts(0);
        user.setLockTime(null);
        user.setEnabled(true);
        userRepository.save(user);

    }

    public void updateUser(User updatedUser) {

        User user = getUserById(updatedUser.getId());

        user.setFullName(updatedUser.getFullName());
        user.setUsername(updatedUser.getUsername());
        user.setEmail(updatedUser.getEmail());
        user.setRole(updatedUser.getRole());
        user.setEnabled(updatedUser.getEnabled());

        if (updatedUser.getPassword() != null &&
                !updatedUser.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(updatedUser.getPassword())
            );
        }
        userRepository.save(user);
    }
}