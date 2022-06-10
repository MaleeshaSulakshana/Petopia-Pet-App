package com.example.petopia.PetCare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.example.petopia.R;

public class TeachYourDogDashboardActivity extends AppCompatActivity {

    private LinearLayout sit, layDown, name, doctor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teach_your_dog_dashboard);

//        Get ui components
        sit = (LinearLayout) findViewById(R.id.sit);
        layDown = (LinearLayout) findViewById(R.id.layDown);
        name = (LinearLayout) findViewById(R.id.name);
        doctor = (LinearLayout) findViewById(R.id.doctor);

//        For sit button
        sit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToTeachDetails("sit");
            }
        });

//        For lay down button
        layDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToTeachDetails("layDown");
            }
        });

//        For name button
        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToTeachDetails("name");
            }
        });

//        For doctor button
        doctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToTeachDetails("doctor");
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
        Intent intent = new Intent(TeachYourDogDashboardActivity.this, PetCareDashboardActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

//    Method for teach details activity
    private void navigateToTeachDetails(String status)
    {
        Intent intent = new Intent(TeachYourDogDashboardActivity.this, TeachDetailsActivity.class);
        intent.putExtra("status", status);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
}