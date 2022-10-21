package com.anurag.edusearch.Models;

public class ModelAssignment {

    private String assignmentName, assignmentId, branch, semester, year, schoolId, dueDate, assignedBy, viewsCount, downloadsCount, timestamp, url, fullMarks;

    public ModelAssignment() {
    }

    public ModelAssignment(String assignmentName, String assignmentId, String branch, String semester, String year, String schoolId, String dueDate, String assignedBy, String viewsCount, String downloadsCount, String timestamp, String url, String fullMarks) {
        this.assignmentName = assignmentName;
        this.assignmentId = assignmentId;
        this.branch = branch;
        this.semester = semester;
        this.year = year;
        this.schoolId = schoolId;
        this.dueDate = dueDate;
        this.assignedBy = assignedBy;
        this.viewsCount = viewsCount;
        this.downloadsCount = downloadsCount;
        this.timestamp = timestamp;
        this.url = url;
        this.fullMarks = fullMarks;
    }

    public String getAssignmentName() {
        return assignmentName;
    }

    public void setAssignmentName(String assignmentName) {
        this.assignmentName = assignmentName;
    }

    public String getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(String assignmentId) {
        this.assignmentId = assignmentId;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getAssignedBy() {
        return assignedBy;
    }

    public void setAssignedBy(String assignedBy) {
        this.assignedBy = assignedBy;
    }

    public String getViewsCount() {
        return viewsCount;
    }

    public void setViewsCount(String viewsCount) {
        this.viewsCount = viewsCount;
    }

    public String getDownloadsCount() {
        return downloadsCount;
    }

    public void setDownloadsCount(String downloadsCount) {
        this.downloadsCount = downloadsCount;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFullMarks() {
        return fullMarks;
    }

    public void setFullMarks(String fullMarks) {
        this.fullMarks = fullMarks;
    }
}