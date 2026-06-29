package com.webapp.talenthub.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "candidate_profiles")
public class CandidateProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String phone;
    private String address;

    @Column(columnDefinition = "TEXT")
    private String skills;

    public CandidateProfile() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getSkills() { return skills; }
    public void setSkills(String skills) { this.skills = skills; }
}
