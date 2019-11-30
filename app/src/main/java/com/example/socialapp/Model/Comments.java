package com.example.socialapp.Model;

public class Comments {
    private String userProfileImage,userFullName,currentTime,currentDate,comment;

    public Comments() {
    }

    public Comments(String userProfileImage, String userFullName, String currentTime, String currentDate, String comment) {
        this.userProfileImage = userProfileImage;
        this.userFullName = userFullName;
        this.currentTime = currentTime;
        this.currentDate = currentDate;
        this.comment = comment;
    }

    public String getUserProfileImage() {
        return userProfileImage;
    }

    public void setUserProfileImage(String userProfileImage) {
        this.userProfileImage = userProfileImage;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
