package com.anurag.edusearch.Models;

public class ModelComment {

    String commentId, materialId, timestamp, comment, uid;


    public ModelComment() {
    }

    public ModelComment(String commentId, String materialId, String timestamp, String comment, String uid) {
        this.commentId = commentId;
        this.materialId = materialId;
        this.timestamp = timestamp;
        this.comment = comment;
        this.uid = uid;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getMaterialId() {
        return materialId;
    }

    public void setMaterialId(String materialId) {
        this.materialId = materialId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
