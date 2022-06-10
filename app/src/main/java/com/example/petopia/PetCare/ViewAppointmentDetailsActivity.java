package com.example.petopia.PetCare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.petopia.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ViewAppointmentDetailsActivity extends AppCompatActivity {

    private TextInputLayout txtFieldTitle, txtFieldDetails, txtFiledDateSelector, txtFiledTimeSelector;
    private EditText txtFieldDate, txtFieldTime;
    private Button btnUpdate, btnRemove, btnYes, btnNo;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference ref;
    private String appointmentId = "";

    private Dialog deleteDialog;

    final Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_appointment_details);

        Intent project = getIntent();
        appointmentId = project.getStringExtra("AppointmentId");

        deleteDialog = new Dialog(ViewAppointmentDetailsActivity.this);
        deleteDialog.setContentView(R.layout.delete_dialog_box);

//        Get ui components
        txtFieldDate = (EditText) findViewById(R.id.txtFieldSelectDate);
        txtFieldTime = (EditText) findViewById(R.id.txtFieldTime);
        txtFieldDetails = (TextInputLayout) findViewById(R.id.txtFieldDetails);
        btnUpdate = (Button) findViewById(R.id.btnUpdate);
        btnRemove = (Button) findViewById(R.id.btnRemove);

        txtFieldTitle = (TextInputLayout) findViewById(R.id.txtFieldTitle);
        txtFieldDetails = (TextInputLayout) findViewById(R.id.txtFieldDetails);
        txtFiledDateSelector = (TextInputLayout) findViewById(R.id.txtFiledDateSelector);
        txtFiledTimeSelector = (TextInputLayout) findViewById(R.id.txtFiledTimeSelector);

        firebaseAuth = FirebaseAuth.getInstance();

//        Show appointment details
        showAppointmentDetails(appointmentId);

//        For update appointment
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateAppointmentDetails();
            }
        });

//        For delete appointment
        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAppointmentDetails();
            }
        });

//        Show calender
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {

                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDate();
            }

        };

//        Set some option to text layouts
        txtFieldDate.setEnabled(true);
        txtFieldDate.setTextIsSelectable(true);
        txtFieldDate.setFocusable(false);
        txtFieldDate.setFocusableInTouchMode(false);

        txtFieldTime.setEnabled(true);
        txtFieldTime.setTextIsSelectable(true);
        txtFieldTime.setFocusable(false);
        txtFieldTime.setFocusableInTouchMode(false);

//        Onclick for show date picker
        txtFieldDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(ViewAppointmentDetailsActivity.this, date, calendar
                        .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

//        Onclick for show time picker
        txtFieldTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar time = Calendar.getInstance();
                int hour = time.get(Calendar.HOUR_OF_DAY);
                int minute = time.get(Calendar.MINUTE);
                TimePickerDialog timePicker;
                timePicker = new TimePickerDialog(ViewAppointmentDetailsActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        txtFieldTime.setText( selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);
                timePicker.setTitle("Select Time");
                timePicker.show();
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
        Intent intent = new Intent(ViewAppointmentDetailsActivity.this, ViewAllAppointmentActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    private void updateDate() {
        String myFormat = "dd-MM-yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        txtFieldDate.setText(sdf.format(calendar.getTime()));
    }

//    Show appointment details
    private void  showAppointmentDetails(String appointmentId)
    {

        ref = FirebaseDatabase.getInstance().getReference("appointment/"+appointmentId);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot != null) {
//                   Set value to text boxes
                    txtFieldTitle.getEditText().setText(snapshot.child("title").getValue().toString());
                    txtFieldDetails.getEditText().setText(snapshot.child("details").getValue().toString());
                    txtFiledDateSelector.getEditText().setText(snapshot.child("date").getValue().toString());
                    txtFiledTimeSelector.getEditText().setText(snapshot.child("time").getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

    }

//    Update appointment details
    private void updateAppointmentDetails()
    {
        String title = txtFieldTitle.getEditText().getText().toString();
        String details = txtFieldDetails.getEditText().getText().toString();
        String date = txtFiledDateSelector.getEditText().getText().toString();
        String time = txtFiledTimeSelector.getEditText().getText().toString();

//        Check fields empty
        if (title.isEmpty() || details.isEmpty() || date.isEmpty() || time.isEmpty()) {
            Toast.makeText(ViewAppointmentDetailsActivity.this, "Text fields are empty!", Toast.LENGTH_SHORT).show();

        } else {

            ref = FirebaseDatabase.getInstance().getReference("appointment/"+appointmentId);
            ref.child("title").setValue(title);
            ref.child("details").setValue(details);
            ref.child("date").setValue(date);
            ref.child("time").setValue(time);

            Toast.makeText(ViewAppointmentDetailsActivity.this, "Appointment details update successful!", Toast.LENGTH_SHORT).show();
        }
    }

//    Method for remove appointment from firebase
    private void deleteAppointmentDetails()
    {
        deleteDialog.show();
        TextView textTitle = (TextView) deleteDialog.findViewById(R.id.textTitle);
        btnYes = (Button) deleteDialog.findViewById(R.id.btnYes);
        btnNo = (Button) deleteDialog.findViewById(R.id.btnNo);

        textTitle.setText("Are You Sure Remove Appointment?");
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ref = FirebaseDatabase.getInstance().getReference("appointment/"+appointmentId);
                ref.removeValue();
                Toast.makeText(ViewAppointmentDetailsActivity.this, "Appointment delete successful!", Toast.LENGTH_SHORT).show();

                goBack();
            }
        });

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDialog.dismiss();
            }
        });

    }

}