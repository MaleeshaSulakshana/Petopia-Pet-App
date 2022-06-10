package com.example.petopia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.petopia.Utiles.EmailValidator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private Button btnLogin;
    private TextView txtRegister;
    boolean doubleBackToExitPressedOnce = false;
    private Button btnExitYes, btnExitNo;
    private Dialog exitDialog;
    private TextInputLayout txtFieldEmail, txtFieldPsw;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

//        Get layouts
        exitDialog = new Dialog(LoginActivity.this);
        exitDialog.setContentView(R.layout.exit_dialog_box);

//        Get ui components
        btnLogin = (Button) findViewById(R.id.btnLogin);
        txtRegister = (TextView) findViewById(R.id.txtRegister);
        txtFieldEmail = (TextInputLayout) findViewById(R.id.txtFieldEmail);
        txtFieldPsw = (TextInputLayout) findViewById(R.id.txtFieldPsw);

        firebaseAuth = FirebaseAuth.getInstance();

//        For login button
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

//        For register text
        txtRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToRegister();
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

//    Method for open register activity
    private void navigateToRegister()
    {
        Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

//    Method for open home activity
    private void navigateToHome()
    {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

//    Method for login
    private void login()
    {
        String semail = txtFieldEmail.getEditText().getText().toString();
        String spsw = txtFieldPsw.getEditText().getText().toString();

        if (semail.isEmpty() || spsw.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Text fields are empty!", Toast.LENGTH_SHORT).show();
        }
        else if (!EmailValidator.isValidEmail(semail)) {
            Toast.makeText(LoginActivity.this, "Your email not validate", Toast.LENGTH_SHORT).show();
        }
        else {

//            Start Progress bar
            loadingBar = new ProgressDialog(LoginActivity.this);
            loadingBar.setTitle("Waiting for login");
            loadingBar.setMessage("Please Wait");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

//            For login
            firebaseAuth.signInWithEmailAndPassword(semail, spsw)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                loadingBar.dismiss();
                                navigateToHome();
                            }
                            else {
                                loadingBar.dismiss();
                                Toast.makeText(LoginActivity.this, "Login Failed!", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

        }
    }

}