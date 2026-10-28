package com.childzone.admin.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.ServerTimestamp;

public class Report {
    private String reportId;
    private String userId;
    private String userType;
    private String childId;
    private String userName;
    private String category;
    private String subject;
    private String description;
    private String status;
    private String adminReply;
    @ServerTimestamp
    private Timestamp createdAt;
    private Timestamp resolvedAt;

    public Report() {
        // Required for Firestore
    }

    public Report(String reportId, String userId, String userType, String childId, String userName, String category, String subject, String description) {
        this.reportId = reportId;
        this.userId = userId;
        this.userType = userType;
        this.childId = childId;
        this.userName = userName;
        this.category = category;
        this.subject = subject;
        this.description = description;
        this.status = "Pending";
    }

    // Getters and Setters
    public String getReportId() { return reportId; }
    public void setReportId(String reportId) { this.reportId = reportId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }

    public String getChildId() { return childId; }
    public void setChildId(String childId) { this.childId = childId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getAdminReply() { return adminReply; }
    public void setAdminReply(String adminReply) { this.adminReply = adminReply; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(Timestamp resolvedAt) { this.resolvedAt = resolvedAt; }
}
