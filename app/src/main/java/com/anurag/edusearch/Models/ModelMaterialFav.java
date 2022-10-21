package com.anurag.edusearch.Models;

public class ModelMaterialFav {

    String materialId, subject, topic, branch, semester, schoolId, url;
    long viewsCount, downloadsCount, timestamp;
    boolean favorite;

    public ModelMaterialFav() {
    }

    public ModelMaterialFav(String materialId, String subject, String topic, String branch, String semester, String schoolId, String url, long viewsCount, long downloadsCount, long timestamp, boolean favorite) {
        this.materialId = materialId;
        this.subject = subject;
        this.topic = topic;
        this.branch = branch;
        this.semester = semester;
        this.schoolId = schoolId;
        this.url = url;
        this.viewsCount = viewsCount;
        this.downloadsCount = downloadsCount;
        this.timestamp = timestamp;
        this.favorite = favorite;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getViewsCount() {
        return viewsCount;
    }

    public void setViewsCount(long viewsCount) {
        this.viewsCount = viewsCount;
    }

    public long getDownloadsCount() {
        return downloadsCount;
    }

    public void setDownloadsCount(long downloadsCount) {
        this.downloadsCount = downloadsCount;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }
}