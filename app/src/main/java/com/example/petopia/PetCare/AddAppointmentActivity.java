package com.example.petopia.PetCare;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.petopia.Constructors.Appointment;
import com.example.petopia.R;
import com.example.petopia.Utiles.DateTimeGenerator;
import com.example.petopia.Utiles.RandomNumber;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddAppointmentActivity extends AppCompatActivity {

    private TextInputLayout txtFieldTitle, txtFieldDetails, txtFiledDateSelector, txtFiledTimeSelector;
    private EditText txtFieldDate, txtFieldTime;
    private Button btnAdd;
    final Calendar calendar = Calendar.getInstance();

    private FirebaseAuth firebaseAuth;
    private DatabaseReference ref;

    int randomNumber = 00000000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_appointment);

//        Get ui components
        txtFieldDate = (EditText) findViewById(R.id.txtFieldSelectDate);
        txtFieldTime = (EditText) findViewById(R.id.txtFieldTime);

        txtFieldTitle = (TextInputLayout) findViewById(R.id.txtFieldTitle);
        txtFieldDetails = (TextInputLayout) findViewById(R.id.txtFieldDetails);
        txtFiledDateSelector = (TextInputLayout) findViewById(R.id.txtFiledDateSelector);
        txtFiledTimeSelector = (TextInputLayout) findViewById(R.id.txtFiledTimeSelector);

        btnAdd = (Button) findViewById(R.id.btnAdd);

        firebaseAuth = FirebaseAuth.getInstance();


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
                new DatePickerDialog(AddAppointmentActivity.this, date, calendar
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
                timePicker = new TimePickerDialog(AddAppointmentActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        txtFieldTime.setText( selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);
                timePicker.setTitle("Select Time");
                timePicker.show();
            }
        });

//        Onclick for add appointment
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAppointment();
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
        Intent intent = new Intent(AddAppointmentActivity.this, ViewAllAppointmentActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

//    Method for show date on text box
    private void updateDate() {
        String myFormat = "dd-MM-yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        txtFieldDate.setText(sdf.format(calendar.getTime()));
    }

//    Method for add appointment
    private void addAppointment()
    {
        String title = txtFieldTitle.getEditText().getText().toString();
        String details = txtFieldDetails.getEditText().getText().toString();
        String date = txtFiledDateSelector.getEditText().getText().toString();
        String time = txtFiledTimeSelector.getEditText().getText().toString();

//        Check fields empty
        if (title.isEmpty() || date.isEmpty() || time.isEmpty() || details.isEmpty()) {
            Toast.makeText(AddAppointmentActivity.this, "Text fields are empty!", Toast.LENGTH_SHORT).show();

        } else {
//            Get user id
            String userId = firebaseAuth.getCurrentUser().getUid();

            randomNumber = RandomNumber.getRandomNumber();
            String dateTime = DateTimeGenerator.getDateTime();
            String random = userId+dateTime+String.valueOf(randomNumber);

            ref = FirebaseDatabase.getInstance().getReference("appointment/"+random);

//            Insert patient data to firebase real time database
            Appointment appointment = new Appointment(random, userId, title, date, time, details);
            ref.setValue(appointment);
            Toast.makeText(AddAppointmentActivity.this, "Appointment added successfully!", Toast.LENGTH_SHORT).show();
            clear();
            goBack();
        }

    }

//    Method for clear text fields
    private void clear()
    {
        txtFiledDateSelector.getEditText().setText("");
        txtFiledTimeSelector.getEditText().setText("");
        txtFieldDetails.getEditText().setText("");
    }

}