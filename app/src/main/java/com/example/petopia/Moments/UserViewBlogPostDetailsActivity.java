package com.example.petopia.Moments;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.petopia.PetCare.ViewAppointmentDetailsActivity;
import com.example.petopia.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class UserViewBlogPostDetailsActivity extends AppCompatActivity {

    private LinearLayout imagePicker;
    private ImageView momentImage;
    private TextInputLayout txtFieldTitle, txtFieldDesc;
    private static final int PICK_IMAGE = 100;
    private Uri imageUri;
    private Button btnUpdate, btnRemove, btnYes, btnNo;
    private Dialog deleteDialog;
    private String isUploaded = "no";

    private String momentId = "";
    private DatabaseReference ref;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_view_blog_post_details);

        Intent project = getIntent();
        momentId = project.getStringExtra("MomentId");

        deleteDialog = new Dialog(UserViewBlogPostDetailsActivity.this);
        deleteDialog.setContentView(R.layout.delete_dialog_box);

//        Get ui components
        imagePicker = (LinearLayout) findViewById(R.id.imagePicker);
        momentImage = (ImageView) findViewById(R.id.momentImage);

        txtFieldTitle = (TextInputLayout) this.findViewById(R.id.txtFieldTitle);
        txtFieldDesc = (TextInputLayout) this.findViewById(R.id.txtFieldDesc);

        btnUpdate = (Button) findViewById(R.id.btnUpdate);
        btnRemove = (Button) findViewById(R.id.btnRemove);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

//        Show moment details
        showMomentDetails(momentId);

//        For upload image
        imagePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

//        For update moment
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateMoment(momentId);
            }
        });

//        For delete moment
        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeMoment(momentId);
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
        Intent intent = new Intent(UserViewBlogPostDetailsActivity.this, UserAllBlogPostActivity.class);
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
                    txtFieldTitle.getEditText().setText(snapshot.child("title").getValue().toString());
                    txtFieldDesc.getEditText().setText(snapshot.child("description").getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

    }

//    Method for update moment
    private void updateMoment(String momentId)
    {
        String title = txtFieldTitle.getEditText().getText().toString();
        String description = txtFieldDesc.getEditText().getText().toString();

//        Check fields empty
        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(UserViewBlogPostDetailsActivity.this, "Text fields are empty!", Toast.LENGTH_SHORT).show();

        } else {

//            Upload image and details
            ref = FirebaseDatabase.getInstance().getReference("moments/"+momentId);
            if (isUploaded.equals("yes")) {

                final ProgressDialog progressDialog = new ProgressDialog(UserViewBlogPostDetailsActivity.this);
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

                                        ref.child("imageUrl").setValue(uri.toString());
                                        isUploaded = "no";
                                    }
                                });

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(UserViewBlogPostDetailsActivity.this, "Moment details update not successful!", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double progressPercentage = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                        progressDialog.setMessage("Uploading... " + (int) progressPercentage + "%");
                    }
                });

            }

//            Update moment details
            ref.child("title").setValue(title);
            ref.child("description").setValue(description);
            Toast.makeText(UserViewBlogPostDetailsActivity.this, "Moment details update successful!", Toast.LENGTH_SHORT).show();
        }

    }

//    Method for remove moment from firebase
    private void removeMoment(String momentId)
    {
        deleteDialog.show();
        TextView textTitle = (TextView) deleteDialog.findViewById(R.id.textTitle);
        btnYes = (Button) deleteDialog.findViewById(R.id.btnYes);
        btnNo = (Button) deleteDialog.findViewById(R.id.btnNo);

        textTitle.setText("Are You Sure Remove Moment Post?");
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ref = FirebaseDatabase.getInstance().getReference("moments/"+momentId);
                ref.removeValue();

                StorageReference desertRef = storageReference.child("Moment_Pictures/"+momentId);
                desertRef.delete();

                Toast.makeText(UserViewBlogPostDetailsActivity.this, "Blog post delete successful!", Toast.LENGTH_SHORT).show();
                navigateBack();
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