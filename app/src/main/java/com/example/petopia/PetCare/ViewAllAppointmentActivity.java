package com.example.petopia.PetCare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.example.petopia.AboutUsActivity;
import com.example.petopia.Adapters.AppointmentAdapter;
import com.example.petopia.Constructors.Appointment;
import com.example.petopia.HomeActivity;
import com.example.petopia.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewAllAppointmentActivity extends AppCompatActivity {

    private Button btnAddAppointment;
    private ListView allAppointmentList;
    private ArrayList<Appointment> arrayList = new ArrayList<>();
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_appointment);

//        Get ui components
        btnAddAppointment = (Button) findViewById(R.id.btnAddAppointment);
        allAppointmentList = (ListView) findViewById(R.id.allAppointmentList);

//        Show appointment details
        showAppointmentOnList();

//        List view item click
        allAppointmentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                Intent detailsPage = new Intent(ViewAllAppointmentActivity.this, ViewAppointmentDetailsActivity.class);
                detailsPage.putExtra("AppointmentId", arrayList.get(i).getAppointmentId());
                startActivity(detailsPage);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        });

//        For open appointment activity
        btnAddAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToAppointment();
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
        Intent intent = new Intent(ViewAllAppointmentActivity.this, PetCareDashboardActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

//    Method for open appointment activity
    private void navigateToAppointment()
    {
        Intent intent = new Intent(ViewAllAppointmentActivity.this, AddAppointmentActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

//    Method for show appointment on list
    private void showAppointmentOnList()
    {
//        Show appointment data on list view
        AppointmentAdapter appointmentAdapter = new AppointmentAdapter(this, R.layout.view_appointment_row, arrayList);
        allAppointmentList.setAdapter(appointmentAdapter);

        reference = FirebaseDatabase.getInstance().getReference("appointment/");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot snapshot1 : snapshot.getChildren()) {

                    arrayList.add(new Appointment(snapshot1.child("appointmentId").getValue().toString(),
                            snapshot1.child("userId").getValue().toString(),
                            snapshot1.child("title").getValue().toString(),
                            snapshot1.child("date").getValue().toString(),
                            snapshot1.child("time").getValue().toString(),
                            snapshot1.child("details").getValue().toString()));
                    appointmentAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}