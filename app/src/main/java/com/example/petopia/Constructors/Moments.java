package com.example.petopia.Constructors;

public class Moments {

    String momentId, title, momentImageUrl;

    public Moments(String momentId, String title, String momentImageUrl) {
        this.momentId = momentId;
        this.title = title;
        this.momentImageUrl = momentImageUrl;
    }

    public String getMomentId() {
        return momentId;
    }

    public String getTitle() {
        return title;
    }

    public String getMomentImageUrl() {
        return momentImageUrl;
    }
}
