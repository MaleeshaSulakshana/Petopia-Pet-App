package com.example.petopia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.petopia.Constructors.Registration;
import com.example.petopia.Utiles.EmailValidator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrationActivity extends AppCompatActivity {

    private Button btnRegister;
    private TextView txtLogin;
    boolean doubleBackToExitPressedOnce = false;
    private Button btnExitYes, btnExitNo;
    private Dialog exitDialog;
    private TextInputLayout txtFieldPetName, txtFieldOwnerName, txtFieldEmail, txtFieldPsw, txtFieldCPsw;
    private ChipGroup chipGroup;
    private String selectedPetType = "";

    private ProgressDialog loadingBar;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

//        Get layouts
        exitDialog = new Dialog(RegistrationActivity.this);
        exitDialog.setContentView(R.layout.exit_dialog_box);

//        Get ui components
        btnRegister = (Button) findViewById(R.id.btnRegister);
        txtLogin = (TextView) findViewById(R.id.txtLogin);

        txtFieldPetName = (TextInputLayout) findViewById(R.id.txtFieldPetName);
        txtFieldOwnerName = (TextInputLayout) findViewById(R.id.txtFieldOwnerName);
        txtFieldEmail = (TextInputLayout) findViewById(R.id.txtFieldEmail);
        txtFieldPsw = (TextInputLayout) findViewById(R.id.txtFieldPsw);
        txtFieldCPsw = (TextInputLayout) findViewById(R.id.txtFieldCPsw);

        chipGroup = (ChipGroup) findViewById(R.id.petType);

        firebaseAuth = FirebaseAuth.getInstance();

        chipGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup chipGroup, int i) {

                Chip chip = chipGroup.findViewById(i);
                if (chip != null) {
                    selectedPetType = chip.getText().toString();
                }
            }
        });

//        For register button
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

//        For register text
        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToLogin();
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

//    Method for open login activity
    private void navigateToLogin()
    {
        Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

//    Method for open home activity
    private void navigateToHome()
    {
        Intent intent = new Intent(RegistrationActivity.this, HomeActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }


//    Method for registration
    private void register() {
        String petName = txtFieldPetName.getEditText().getText().toString();
        String ownerName = txtFieldOwnerName.getEditText().getText().toString();
        String email = txtFieldEmail.getEditText().getText().toString();
        String psw = txtFieldPsw.getEditText().getText().toString();
        String cpsw = txtFieldCPsw.getEditText().getText().toString();

//        Check fields empty
        if (petName.isEmpty() || email.isEmpty() || ownerName.isEmpty()) {
            Toast.makeText(RegistrationActivity.this, "Text fields are empty!", Toast.LENGTH_SHORT).show();

//        Validate email
        } else if (!EmailValidator.isValidEmail(email)) {
            Toast.makeText(RegistrationActivity.this, "Please check your email pattern!", Toast.LENGTH_SHORT).show();

//        Check length of mobile number
        } else if (!psw.equals(cpsw)) {
            Toast.makeText(RegistrationActivity.this, "Please check password and confirm password!", Toast.LENGTH_SHORT).show();

        } else if(selectedPetType.equals("")) {
            Toast.makeText(RegistrationActivity.this, "Please select pet type!", Toast.LENGTH_SHORT).show();

        }else {

//           Start progress bar
            loadingBar = new ProgressDialog(RegistrationActivity.this);
            loadingBar.setTitle("Creating new Account");
            loadingBar.setMessage("Please Wait");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

//            Create account with firebase authentication
            String finalPetType = selectedPetType;
            firebaseAuth.createUserWithEmailAndPassword(email, psw)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()){

//                               Get user id
                                String userId = firebaseAuth.getCurrentUser().getUid();
                                ref = FirebaseDatabase.getInstance().getReference("users/"+userId);

//                               Insert patient data to firebase real time database
                                Registration registration = new Registration(petName, ownerName, email, finalPetType);
                                ref.setValue(registration);
                                loadingBar.dismiss();

//                               Navigate to patient dashboard
                                navigateToHome();

                            } else {

                                String msg = task.getException().toString();
                                Toast.makeText(RegistrationActivity.this, "Error" + msg, Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                            }

                        }
                    });

        }

    }
}