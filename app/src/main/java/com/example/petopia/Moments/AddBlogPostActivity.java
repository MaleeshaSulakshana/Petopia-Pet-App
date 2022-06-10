package com.example.petopia.Moments;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.petopia.Constructors.Moment;
import com.example.petopia.Profile.ProfileActivity;
import com.example.petopia.R;
import com.example.petopia.Utiles.DateTimeGenerator;
import com.example.petopia.Utiles.RandomNumber;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class AddBlogPostActivity extends AppCompatActivity {

    private LinearLayout imagePicker;
    private ImageView momentImage;
    private TextInputLayout txtFieldTitle, txtFieldDesc;
    private Button btnAdd;
    private String isUploaded = "no";

    private FirebaseAuth firebaseAuth;
    private DatabaseReference ref;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    int randomNumber = 00000000;
    private static final int PICK_IMAGE = 100;
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_blog_post);

//        Get ui components
        imagePicker = (LinearLayout) findViewById(R.id.imagePicker);
        momentImage = (ImageView) findViewById(R.id.momentImage);

        txtFieldTitle = (TextInputLayout) this.findViewById(R.id.txtFieldTitle);
        txtFieldDesc = (TextInputLayout) this.findViewById(R.id.txtFieldDesc);

        btnAdd = (Button) findViewById(R.id.btnAdd);

        firebaseAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

//        For upload image
        imagePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

//        For add moment
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewMoment();
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
        navigateBack();
    }

//    Method for go back activity
    private void navigateBack()
    {
        Intent intent = new Intent(AddBlogPostActivity.this, UserAllBlogPostActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

//    Method for open gallery
    private void openGallery()
    {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            imageUri = data.getData();
            isUploaded = "yes";
            momentImage.setImageURI(imageUri);
        }

    }

//    Method for add new moment
    private void addNewMoment()
    {
        String title = txtFieldTitle.getEditText().getText().toString();
        String description = txtFieldDesc.getEditText().getText().toString();

//        Check fields empty
        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(AddBlogPostActivity.this, "Text fields are empty!", Toast.LENGTH_SHORT).show();

        } else if(isUploaded.equals("no")) {
            Toast.makeText(AddBlogPostActivity.this, "Please upload image for post!", Toast.LENGTH_SHORT).show();

        } else {
//            Get user id
            String userId = firebaseAuth.getCurrentUser().getUid();

            randomNumber = RandomNumber.getRandomNumber();
            String dateTime = DateTimeGenerator.getDateTime();
            String momentId = userId+dateTime+String.valueOf(randomNumber);

            ref = FirebaseDatabase.getInstance().getReference("moments/"+momentId);

//            Upload image and details
            final ProgressDialog progressDialog = new ProgressDialog(AddBlogPostActivity.this);
            progressDialog.setTitle("Uploading Image");
            progressDialog.show();

            StorageReference riversRef = storageReference.child("Moment_Pictures/" + momentId);
            riversRef.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

//                                    Insert moment data to firebase real time database
                                    Moment moment = new Moment(momentId, userId, title, description, uri.toString());
                                    ref.setValue(moment)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull  Task<Void> task) {
                                                    if (task.isSuccessful()){
                                                        clear();
                                                        Toast.makeText(AddBlogPostActivity.this, "Moment shared successful!", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(AddBlogPostActivity.this, "Moment shared not successful!", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });

                                }
                            });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(AddBlogPostActivity.this, "Moment shared not successful!", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    double progressPercentage = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                    progressDialog.setMessage("Uploading... " + (int) progressPercentage + "%");
                }
            });

        }

    }

//    Method for clear text fields and image
    private void clear()
    {
        txtFieldTitle.getEditText().setText("");
        txtFieldDesc.getEditText().setText("");
        isUploaded = "no";
        momentImage.setImageDrawable(getResources().getDrawable(R.drawable.upload_image));
    }

}