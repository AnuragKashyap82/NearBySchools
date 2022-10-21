package com.anurag.edusearch.Models;

public class ModelImageLikes {

    String imageId, galleryId, uid, schoolId, timestamp;

    public ModelImageLikes() {
    }

    public ModelImageLikes(String imageId, String galleryId, String uid, String schoolId, String timestamp) {
        this.imageId = imageId;
        this.galleryId = galleryId;
        this.uid = uid;
        this.schoolId = schoolId;
        this.timestamp = timestamp;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getGalleryId() {
        return galleryId;
    }

    public void setGalleryId(String galleryId) {
        this.galleryId = galleryId;
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

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
