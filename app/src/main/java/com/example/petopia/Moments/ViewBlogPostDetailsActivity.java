package com.example.petopia.Moments;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.petopia.HomeActivity;
import com.example.petopia.PetCare.PetCareDashboardActivity;
import com.example.petopia.PetCare.TeachYourDogDashboardActivity;
import com.example.petopia.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class ViewBlogPostDetailsActivity extends AppCompatActivity {

    private ImageView momentImage;
    private TextView title, description;
    private Button btnBack;

    private String momentId = "";
    private DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_blog_post_details);

        Intent project = getIntent();
        momentId = project.getStringExtra("MomentId");

//        Get ui components
        btnBack = (Button) findViewById(R.id.btnBack);
        momentImage = (ImageView) findViewById(R.id.momentImage);
        title = (TextView) this.findViewById(R.id.title);
        description = (TextView) this.findViewById(R.id.description);

//        Show moment details
        showMomentDetails(momentId);

//        Back button
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
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
        Intent intent = new Intent(ViewBlogPostDetailsActivity.this, ViewBlogPostActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

//    Get moment details
    private void  showMomentDetails(String momentId)
    {

//        Get moment data from firebase
        ref = FirebaseDatabase.getInstance().getReference("moments/"+momentId);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot != null) {
//                   Set value to text boxes
                    Picasso.get().load(snapshot.child("imageUrl").getValue().toString()).into(momentImage);
                    title.setText(snapshot.child("title").getValue().toString());
                    description.setText(snapshot.child("description").getValue().toString());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

    }
}