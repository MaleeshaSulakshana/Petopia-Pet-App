package com.example.petopia.Constructors;

public class Moment {

    String momentId, userId, title, description, imageUrl;

    public Moment(String momentId, String userId, String title, String description, String imageUrl) {
        this.momentId = momentId;
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    public String getMomentId() {
        return momentId;
    }

    public String getUserId() {
        return userId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
