package com.anurag.edusearch.Models;

public class ModelMaterial {

    private String materialId, subject, topic, branch, semester, schoolId, timestamp, url;

    public ModelMaterial() {
    }

    public ModelMaterial(String materialId, String subject, String topic, String branch, String semester, String schoolId, String timestamp, String url) {
        this.materialId = materialId;
        this.subject = subject;
        this.topic = topic;
        this.branch = branch;
        this.semester = semester;
        this.schoolId = schoolId;
        this.timestamp = timestamp;
        this.url = url;

    }

    public String getMaterialId() {
        return materialId;
    }

    public void setMaterialId(String materialId) {
        this.materialId = materialId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
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

    public String getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
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
}