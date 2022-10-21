package com.anurag.edusearch.Models;

public class ModelCategory {

    private String facultyId, facultyName, facultyDescription, facultyImage, uid, schoolId;

    public ModelCategory() {
    }

    public ModelCategory(String facultyId, String facultyName, String facultyDescription, String facultyImage, String uid, String schoolId) {
        this.facultyId = facultyId;
        this.facultyName = facultyName;
        this.facultyDescription = facultyDescription;
        this.facultyImage = facultyImage;
        this.uid = uid;
        this.schoolId = schoolId;
    }

    public String getFacultyId() {
        return facultyId;
    }

    public void setFacultyId(String facultyId) {
        this.facultyId = facultyId;
    }

    public String getFacultyName() {
        return facultyName;
    }

    public void setFacultyName(String facultyName) {
        this.facultyName = facultyName;
    }

    public String getFacultyDescription() {
        return facultyDescription;
    }

    public void setFacultyDescription(String facultyDescription) {
        this.facultyDescription = facultyDescription;
    }

    public String getFacultyImage() {
        return facultyImage;
    }

    public void setFacultyImage(String facultyImage) {
        this.facultyImage = facultyImage;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }
}