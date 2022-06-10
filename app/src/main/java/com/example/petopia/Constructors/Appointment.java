package com.example.petopia.Constructors;

public class Appointment {

    String appointmentId, userId, title, date, time, details;

    public Appointment(String appointmentId, String userId, String title, String date, String time, String details) {
        this.appointmentId = appointmentId;
        this.userId = userId;
        this.title = title;
        this.date = date;
        this.time = time;
        this.details = details;
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public String getUserId() {
        return userId;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getDetails() {
        return details;
    }
}
