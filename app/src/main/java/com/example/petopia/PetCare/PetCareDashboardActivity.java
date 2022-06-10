package com.example.petopia.PetCare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.example.petopia.FoodProducts.FoodProductDashboardActivity;
import com.example.petopia.HomeActivity;
import com.example.petopia.Moments.ViewBlogPostActivity;
import com.example.petopia.Profile.ProfileActivity;
import com.example.petopia.R;

public class PetCareDashboardActivity extends AppCompatActivity {

    private LinearLayout doctor, appointment, teachDog, pharmacy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_care_dashboard);

//        Get ui components
        doctor = (LinearLayout) findViewById(R.id.doctor);
        appointment = (LinearLayout) findViewById(R.id.appointment);
        teachDog = (LinearLayout) findViewById(R.id.teachDog);
        pharmacy = (LinearLayout) findViewById(R.id.pharmacy);

//        For doctor button
        doctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToDoctor();
            }
        });

//        For appointment text
        appointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToAppointment();
            }
        });

//        For teach dog text
        teachDog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToTeach();
            }
        });

//        For pharmacy text
        pharmacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToPharmacy();
            }
        });

    }

//    Hide status bar and navigation bar
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        goBack();
    }

//    Method for navigate previous activity
    private void goBack()
    {
        Intent intent = new Intent(PetCareDashboardActivity.this, HomeActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

//    Method for open doctor activity
    private void navigateToDoctor()
    {
        Intent intent = new Intent(PetCareDashboardActivity.this, DoctorsActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

//    Method for open appointment activity
    private void navigateToAppointment()
    {
        Intent intent = new Intent(PetCareDashboardActivity.this, ViewAllAppointmentActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

//    Method for open teach activity
    private void navigateToTeach()
    {
        Intent intent = new Intent(PetCareDashboardActivity.this, TeachYourDogDashboardActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

//    Method for open pharmacy activity
    private void navigateToPharmacy()
    {
        Intent intent = new Intent(PetCareDashboardActivity.this, PharmacyActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

}