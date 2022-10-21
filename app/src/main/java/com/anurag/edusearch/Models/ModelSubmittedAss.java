package com.anurag.edusearch.Models;

public class ModelSubmittedAss {

    private String assignmentId, branch, semester, year, schoolId, dueDate, fullMarks, timestamp, uid, url, marksObtained;

    public ModelSubmittedAss() {
    }

    public ModelSubmittedAss(String assignmentId, String branch, String semester, String year, String schoolId, String dueDate, String fullMarks, String timestamp, String uid, String url, String marksObtained) {
        this.assignmentId = assignmentId;
        this.branch = branch;
        this.semester = semester;
        this.year = year;
        this.schoolId = schoolId;
        this.dueDate = dueDate;
        this.fullMarks = fullMarks;
        this.timestamp = timestamp;
        this.uid = uid;
        this.url = url;
        this.marksObtained = marksObtained;
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

    public String getFullMarks() {
        return fullMarks;
    }

    public void setFullMarks(String fullMarks) {
        this.fullMarks = fullMarks;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMarksObtained() {
        return marksObtained;
    }

    public void setMarksObtained(String marksObtained) {
        this.marksObtained = marksObtained;
    }
}