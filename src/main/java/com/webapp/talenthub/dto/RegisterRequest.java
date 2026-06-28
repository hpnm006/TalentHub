package com.webapp.talenthub.dto;

public class RegisterRequest {

    private String fullName;
    private String username;
    private String email;
    private String password;
    private String confirmPassword;

    public RegisterRequest() {
    }

    public RegisterRequest(String fullName, String username,
                           String email, String password,
                           String confirmPassword) {

        this.fullName = fullName;
        this.username = username;
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}