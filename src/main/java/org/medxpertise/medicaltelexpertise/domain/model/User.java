package org.medxpertise.medicaltelexpertise.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.medxpertise.medicaltelexpertise.domain.model.enums.Role;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "user_type", discriminatorType = DiscriminatorType.STRING, length = 20)
@DiscriminatorValue("USER")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @NotNull
    @Size(min = 60, max = 60)
    @Column(name = "password_hash", nullable = false, length = 60)
    private String passwordHash;

    @NotNull
    @Email
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    @Column(name = "created_at",nullable = false)
    private LocalDateTime createdAt;
    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @OneToMany(mappedBy = "recipient", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notification> notifications = new ArrayList<>();

    @OneToMany(mappedBy = "actor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AuditLog> auditLogs = new ArrayList<>();

    @OneToMany(mappedBy = "uploadedBy", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<Report> uploadedReports = new ArrayList<>();


    public User() {
        this.active = true;
        this.createdAt = LocalDateTime.now();
        this.role = Role.BASE;
    }

    protected User(String username, String firstName, String lastName, String passwordHash, String email, Role role) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.passwordHash = passwordHash;
        this.email = email;
        this.role = role;
        this.active = true;
        this.createdAt = LocalDateTime.now();
    }

    public void login() {
        this.lastLoginAt = LocalDateTime.now();
        addAuditLog(new AuditLog("USER_LOGIN", "User logged in", this));
    }

    public void logout() {
        addAuditLog(new AuditLog("USER_LOGOUT", "User logged out", this));
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(LocalDateTime lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }

    public List<AuditLog> getAuditLogs() {
        return auditLogs;
    }

    public void setAuditLogs(List<AuditLog> auditLogs) {
        this.auditLogs = auditLogs;
    }

    public void addNotification(Notification notification) {
        notification.setRecipient(this);
        this.notifications.add(notification);
    }

    public void addAuditLog(AuditLog auditLog) {
        auditLog.setActor(this);
        this.auditLogs.add(auditLog);
    }

    public List<Report> getUploadedReports() {
        return uploadedReports;
    }

    public void setUploadedReports(List<Report> uploadedReports) {
        this.uploadedReports = uploadedReports;
    }

    public void addUploadedReport(Report report) {
        report.setUploadedBy(this);
        this.uploadedReports.add(report);
    }

    public boolean isAdmin() {
        return  role == Role.ADMIN;
    }
}
