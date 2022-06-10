package com.example.petopia.Moments;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.petopia.Adapters.MomentAdapter;
import com.example.petopia.Constructors.Moments;
import com.example.petopia.HomeActivity;
import com.example.petopia.PetCare.ViewAllAppointmentActivity;
import com.example.petopia.PetCare.ViewAppointmentDetailsActivity;
import com.example.petopia.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewBlogPostActivity extends AppCompatActivity {

    private ListView viewAllMoments;
    private ArrayList<Moments> arrayList = new ArrayList<>();
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_blog_post);

//        Get ui components
        viewAllMoments = (ListView) findViewById(R.id.viewAllMoments);

//        Show moment details
        showMomentsOnList();

//        List view item click
        viewAllMoments.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                Intent detailsPage = new Intent(ViewBlogPostActivity.this, ViewBlogPostDetailsActivity.class);
                detailsPage.putExtra("MomentId", arrayList.get(i).getMomentId());
                startActivity(detailsPage);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
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
        Intent intent = new Intent(ViewBlogPostActivity.this, HomeActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    //    Method for show moment on list
    private void showMomentsOnList()
    {

//        Show appointment data on list view
        MomentAdapter momentAdapter = new MomentAdapter(this, R.layout.view_moment_row, arrayList);
        viewAllMoments.setAdapter(momentAdapter);

        reference = FirebaseDatabase.getInstance().getReference("moments/");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot != null) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {

                        arrayList.add(new Moments(snapshot1.child("momentId").getValue().toString(),
                                snapshot1.child("title").getValue().toString(),
                                snapshot1.child("imageUrl").getValue().toString()));
                        momentAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}