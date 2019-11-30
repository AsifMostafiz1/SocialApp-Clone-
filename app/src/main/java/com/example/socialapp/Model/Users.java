package com.example.socialapp.Model;

public class Users {
    private String imageUrl,userFullName,status;

    public Users() {
    }

    public Users(String imageUrl, String userFullName, String status) {
        this.imageUrl = imageUrl;
        this.userFullName = userFullName;
        this.status = status;
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
