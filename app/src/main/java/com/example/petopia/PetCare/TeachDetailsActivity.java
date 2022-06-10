package com.example.petopia.PetCare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.petopia.AboutUsActivity;
import com.example.petopia.HomeActivity;
import com.example.petopia.R;

public class TeachDetailsActivity extends AppCompatActivity {

    private Button btnBack;
    private ImageView teachImage;
    private TextView teachTitle, teachDesc;
    private String status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teach_details);

//        Get put extra values
        Intent intent = getIntent();
        status = intent.getStringExtra("status");

//        Get ui components
        btnBack = (Button) findViewById(R.id.btnBack);
        teachImage = (ImageView) findViewById(R.id.teachImage);
        teachTitle = (TextView) findViewById(R.id.teachTitle);
        teachDesc = (TextView) findViewById(R.id.teachDesc);

//        Show details
        showDetails(status);

//        For back button
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToTeachDashboard();
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
        navigateToTeachDashboard();
    }

//    Method for open home activity
    private void navigateToTeachDashboard()
    {
        Intent intent = new Intent(TeachDetailsActivity.this, TeachYourDogDashboardActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

//    Method for show details
    private void showDetails(String status)
    {
        String value = "Lorem ipsum dolor sit amet, " +
                "consectetur adipiscing elit. Nunc vehicula vestibulum urna sed eleifend. " +
                "Etiam luctus nulla a arcu vehicula, a lobortis tellus vulputate. Cras auctor " +
                "mi a cursus ultricies. Cras tincidunt metus nec est ultrices, non imperdiet lacus aliquet. " +
                "Phasellus quis nisi pretium, suscipit urna non, varius libero. Donec tempor, sapien " +
                "ut malesuada bibendum, sapien nulla sodales nisl, a dapibus augue enim quis arcu. " +
                "Phasellus pharetra commodo odio id venenatis. Maecenas tincidunt sed lorem eget hendrerit. " +
                "Vivamus blandit libero vitae enim pretium finibus. Etiam pellentesque fermentum nisi, in porta" +
                " leo lacinia id. Vestibulum blandit pellentesque porttitor. " +
                "Proin at auctor augue. Vivamus at varius ante.";

        if (status.equals("sit")){
            teachImage.setImageDrawable(getResources().getDrawable(R.drawable.sit_teaching));
            teachTitle.setText("Teach your dog to sit");
            teachDesc.setText(value);

        } else if (status.equals("layDown")) {
            teachImage.setImageDrawable(getResources().getDrawable(R.drawable.lay_down));
            teachTitle.setText("Teach your dog to lay down");
            teachDesc.setText(value);

        } else if (status.equals("name")) {
            teachImage.setImageDrawable(getResources().getDrawable(R.drawable.dogs_name_training));
            teachTitle.setText("Teach your dog to its name");
            teachDesc.setText(value);

        } else if (status.equals("doctor")) {
            teachImage.setImageDrawable(getResources().getDrawable(R.drawable.dog_training_response));
            teachTitle.setText("Teach your dog to response to the clicker");
            teachDesc.setText(value);

        } else {
            navigateToTeachDashboard();
        }
    }
}