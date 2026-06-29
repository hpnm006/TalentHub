package com.webapp.talenthub.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "jobs")
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String status = "DRAFT"; // DRAFT, ACTIVE, CLOSED

    private String department;
    private String location;

    @Column(columnDefinition = "TEXT")
    private String requirements;

    private String salaryRange;

    private java.time.LocalDateTime deadline;

    @Column(nullable = false, updatable = false)
    private java.time.LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = java.time.LocalDateTime.now();
    }

    public Job() {}

    public Job(Long id, String title, String description, String status, String department, String location, String requirements, String salaryRange, java.time.LocalDateTime deadline) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.department = department;
        this.location = location;
        this.requirements = requirements;
        this.salaryRange = salaryRange;
        this.deadline = deadline;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getRequirements() { return requirements; }
    public void setRequirements(String requirements) { this.requirements = requirements; }
    public String getSalaryRange() { return salaryRange; }
    public void setSalaryRange(String salaryRange) { this.salaryRange = salaryRange; }
    public java.time.LocalDateTime getDeadline() { return deadline; }
    public void setDeadline(java.time.LocalDateTime deadline) { this.deadline = deadline; }
    public java.time.LocalDateTime getCreatedAt() { return createdAt; }
}
