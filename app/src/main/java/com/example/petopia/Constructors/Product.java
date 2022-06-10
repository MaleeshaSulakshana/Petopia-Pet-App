package com.example.petopia.Constructors;

public class Product {

    String userId, productId, foodName, description, price, mobileNumber, imageUrl;

    public Product(String userId, String productId, String foodName, String description, String price, String mobileNumber, String imageUrl) {
        this.userId = userId;
        this.productId = productId;
        this.foodName = foodName;
        this.description = description;
        this.price = price;
        this.mobileNumber = mobileNumber;
        this.imageUrl = imageUrl;
    }

    public String getUserId() {
        return userId;
    }

    public String getProductId() {
        return productId;
    }

    public String getFoodName() {
        return foodName;
    }

    public String getDescription() {
        return description;
    }

    public String getPrice() {
        return price;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
