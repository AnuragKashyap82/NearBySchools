package com.anurag.edusearch.Models;

public class ModelGalleryCategory {

    private String galleryId, galleryName, galleryImage, schoolId, uid;

    public ModelGalleryCategory() {
    }

    public ModelGalleryCategory(String galleryId, String galleryName, String galleryImage, String schoolId, String uid) {
        this.galleryId = galleryId;
        this.galleryName = galleryName;
        this.galleryImage = galleryImage;
        this.schoolId = schoolId;
        this.uid = uid;
    }

    public String getGalleryId() {
        return galleryId;
    }

    public void setGalleryId(String galleryId) {
        this.galleryId = galleryId;
    }

    public String getGalleryName() {
        return galleryName;
    }

    public void setGalleryName(String galleryName) {
        this.galleryName = galleryName;
    }

    public String getGalleryImage() {
        return galleryImage;
    }

    public void setGalleryImage(String galleryImage) {
        this.galleryImage = galleryImage;
    }

    public String getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}