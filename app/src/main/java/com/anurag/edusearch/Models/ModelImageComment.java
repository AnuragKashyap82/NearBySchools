package com.anurag.edusearch.Models;

public class ModelImageComment {

    String comment, commentId, imageId, timestamp, galleryId, schoolId, uid;

    public ModelImageComment() {
    }

    public ModelImageComment(String comment, String commentId, String imageId, String timestamp, String galleryId, String schoolId, String uid) {
        this.comment = comment;
        this.commentId = commentId;
        this.imageId = imageId;
        this.timestamp = timestamp;
        this.galleryId = galleryId;
        this.schoolId = schoolId;
        this.uid = uid;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getGalleryId() {
        return galleryId;
    }

    public void setGalleryId(String galleryId) {
        this.galleryId = galleryId;
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