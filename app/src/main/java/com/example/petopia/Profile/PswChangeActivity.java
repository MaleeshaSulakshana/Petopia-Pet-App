package com.example.petopia.Profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.petopia.Moments.AddBlogPostActivity;
import com.example.petopia.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class PswChangeActivity extends AppCompatActivity {

    private Button btnUpdatePsw;
    private TextInputLayout txtFieldEmail, txtFieldPsw, txtFieldNewPsw, txtFieldConfirmNewPsw;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_psw_change);

//        Get ui components
        btnUpdatePsw = (Button) this.findViewById(R.id.btnUpdatePsw);
        txtFieldEmail = (TextInputLayout) this.findViewById(R.id.txtFieldEmail);
        txtFieldPsw = (TextInputLayout) this.findViewById(R.id.txtFieldPsw);
        txtFieldNewPsw = (TextInputLayout) this.findViewById(R.id.txtFieldNewPsw);
        txtFieldConfirmNewPsw = (TextInputLayout) this.findViewById(R.id.txtFieldConfirmNewPsw);

        firebaseAuth = FirebaseAuth.getInstance();

//        For update profile password button
        btnUpdatePsw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfilePsw();
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
        Intent intent = new Intent(PswChangeActivity.this, ProfileActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

//    Method for update psw
    private void updateProfilePsw()
    {
        String email = txtFieldEmail.getEditText().getText().toString();
        String psw = txtFieldPsw.getEditText().getText().toString();
        String newPsw = txtFieldNewPsw.getEditText().getText().toString();
        String newCPsw = txtFieldConfirmNewPsw.getEditText().getText().toString();

//       Check fields empty
        if (email.isEmpty() || psw.isEmpty() || newPsw.isEmpty() || newCPsw.isEmpty()) {
            Toast.makeText(PswChangeActivity.this, "Text fields are empty!", Toast.LENGTH_SHORT).show();

//       Check is match psw and confirm psw
        } else if (!newPsw.equals(newCPsw)) {
            Toast.makeText(PswChangeActivity.this, "Text fields are empty!", Toast.LENGTH_SHORT).show();

//       Change profile psw
        } else {

            AuthCredential authCredential = EmailAuthProvider.getCredential(email, psw);
            FirebaseUser user = firebaseAuth.getCurrentUser();

//            Check authentication
            user.reauthenticate(authCredential)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {

//                                Change psw
                                user.updatePassword(newPsw)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if (task.isSuccessful()){
                                                    Toast.makeText(PswChangeActivity.this, "Password Update Successful!", Toast.LENGTH_SHORT).show();
                                                    clear();
                                                } else {
                                                    String msg = task.getException().toString();
                                                    Toast.makeText(PswChangeActivity.this, "Error" + msg, Toast.LENGTH_SHORT).show();
                                                }

                                            }
                                        });

                            } else {
                                String msg = task.getException().toString();
                                Toast.makeText(PswChangeActivity.this, "Error" + msg, Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

        }

    }

//    Method for clear text fields
    private void clear()
    {
        txtFieldEmail.getEditText().setText("");
        txtFieldPsw.getEditText().setText("");
        txtFieldNewPsw.getEditText().setText("");
        txtFieldConfirmNewPsw.getEditText().setText("");
    }

}