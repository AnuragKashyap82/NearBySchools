package com.anurag.edusearch.Models;

public class ModelDistrictUser {


    String id, category, uid, timestamp;


    public ModelDistrictUser() {
    }

    public ModelDistrictUser(String id, String category, String uid, String timestamp) {

        this.id = id;
        this.category = category;
        this.uid = uid;
        this.timestamp = timestamp;
    }

    public String getId() {

        return id;
    }

    public void setId(String id) {

        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}

