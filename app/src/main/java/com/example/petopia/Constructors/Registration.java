package com.example.petopia.Constructors;

public class Registration {

    String PetName, OwnerName, OwnerEmail, PetType;

    public Registration(String petName, String ownerName, String ownerEmail, String petType) {
        PetName = petName;
        OwnerName = ownerName;
        OwnerEmail = ownerEmail;
        PetType = petType;
    }

    public String getPetName() {
        return PetName;
    }

    public String getOwnerName() {
        return OwnerName;
    }

    public String getOwnerEmail() {
        return OwnerEmail;
    }

    public String getPetType() {
        return PetType;
    }
}
