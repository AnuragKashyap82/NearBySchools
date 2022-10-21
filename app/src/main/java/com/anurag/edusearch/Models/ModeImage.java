package com.anurag.edusearch.Models;

public class ModeImage {

    private String imageId, galleryId, image, schoolId;

    public ModeImage() {
    }

    public ModeImage(String imageId, String galleryId, String image, String schoolId) {
        this.imageId = imageId;
        this.galleryId = galleryId;
        this.image = image;
        this.schoolId = schoolId;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }
}
