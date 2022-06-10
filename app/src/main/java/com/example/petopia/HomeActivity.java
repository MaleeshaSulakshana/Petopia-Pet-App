
package com.example.petopia;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.petopia.FoodProducts.FoodProductDashboardActivity;
import com.example.petopia.Moments.ViewBlogPostActivity;
import com.example.petopia.PetCare.PetCareDashboardActivity;
import com.example.petopia.Profile.ProfileActivity;
import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {

    private LinearLayout profile, moment, perCare, foodProduct, aboutUs, signOut;
    boolean doubleBackToExitPressedOnce = false;
    private Button btnExitYes, btnExitNo;
    private Dialog exitDialog;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

//        Get layouts
        exitDialog = new Dialog(HomeActivity.this);
        exitDialog.setContentView(R.layout.exit_dialog_box);

//        Get ui components
        profile = (LinearLayout) findViewById(R.id.profile);
        moment = (LinearLayout) findViewById(R.id.moment);
        perCare = (LinearLayout) findViewById(R.id.perCare);
        foodProduct = (LinearLayout) findViewById(R.id.foodProduct);
        aboutUs = (LinearLayout) findViewById(R.id.aboutUs);
        signOut = (LinearLayout) findViewById(R.id.signOut);

        firebaseAuth = FirebaseAuth.getInstance();

//        For profile button
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToProfile();
            }
        });

//        For moment text
        moment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToMoment();
            }
        });

//        For pet care text
        perCare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToPetCare();
            }
        });

//        For food product text
        foodProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToFoodProducts();
            }
        });

//        For about us text
        aboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToAboutUs();
            }
        });

//        For sign out text
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
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

//    Tap to close app
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        exitDialog.show();

        btnExitYes = (Button) exitDialog.findViewById(R.id.btnYes);
        btnExitYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAffinity();
                System.exit(0);
            }
        });

        btnExitNo = (Button) exitDialog.findViewById(R.id.btnNo);
        btnExitNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitDialog.dismiss();
            }
        });

    }

//    Method for open profile activity
    private void navigateToProfile()
    {
        Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

//    Method for open moment activity
    private void navigateToMoment()
    {
        Intent intent = new Intent(HomeActivity.this, ViewBlogPostActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

//    Method for open pet care activity
    private void navigateToPetCare()
    {
        Intent intent = new Intent(HomeActivity.this, PetCareDashboardActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

//    Method for open food and products activity
    private void navigateToFoodProducts()
    {
        Intent intent = new Intent(HomeActivity.this, FoodProductDashboardActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

//    Method for open about us activity
    private void navigateToAboutUs()
    {
        Intent intent = new Intent(HomeActivity.this, AboutUsActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

//    Method for open sign out activity
    private void signOut()
    {
        firebaseAuth.signOut();
        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }


}