package com.anurag.edusearch.Models;

public class ModelSchoolUser {

    String schoolId, avgRating, schoolName, board, schoolImage, email, phoneNumber, districtId, district, address, uid;

    public ModelSchoolUser() {

    }

    public ModelSchoolUser(String schoolId, String avgRating, String schoolName, String board, String schoolImage, String email, String phoneNumber, String districtId, String district, String address, String uid) {
        this.schoolId = schoolId;
        this.avgRating = avgRating;
        this.schoolName = schoolName;
        this.board = board;
        this.schoolImage = schoolImage;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.districtId = districtId;
        this.district = district;
        this.address = address;
        this.uid = uid;
    }

    public String getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }

    public String getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(String avgRating) {
        this.avgRating = avgRating;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getBoard() {
        return board;
    }

    public void setBoard(String board) {
        this.board = board;
    }

    public String getSchoolImage() {
        return schoolImage;
    }

    public void setSchoolImage(String schoolImage) {
        this.schoolImage = schoolImage;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDistrictId() {
        return districtId;
    }

    public void setDistrictId(String districtId) {
        this.districtId = districtId;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}