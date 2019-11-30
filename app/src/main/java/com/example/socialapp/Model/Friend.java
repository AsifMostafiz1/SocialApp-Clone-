package com.example.socialapp.Model;

public class Friend {

    private String date;
    private String imageUrl, userFullName, status;

    public Friend() {
    }

    public Friend(String date, String imageUrl, String userFullName, String status) {
        this.date = date;
        this.imageUrl = imageUrl;
        this.userFullName = userFullName;
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

