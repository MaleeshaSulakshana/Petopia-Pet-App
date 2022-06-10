package com.example.petopia.Profile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.petopia.FoodProducts.AddFoodProductActivity;
import com.example.petopia.FoodProducts.EditFoodProductActivity;
import com.example.petopia.FoodProducts.ViewUserFoodProductsActivity;
import com.example.petopia.HomeActivity;
import com.example.petopia.LoginActivity;
import com.example.petopia.Moments.AddBlogPostActivity;
import com.example.petopia.Moments.UserAllBlogPostActivity;
import com.example.petopia.R;
import com.example.petopia.RegistrationActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

public class ProfileActivity extends AppCompatActivity {

    private TextView txtPswChange;
    private Button btnUpdateProfile, btnRemoveProfile, btnAllMoments, btnAllFoodItems, btnYes, btnNo, btnCancel, btnSubmit;
    private Dialog deleteDialog, authenticateDialog;
    private TextInputLayout txtFieldPetName, txtFieldOwnerName, txtFieldEmail;
    private ChipGroup chipGroup;
    private String selectedPetType = "";

    private FirebaseAuth firebaseAuth;
    private DatabaseReference ref;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    private ImageView profileImage;
    private static final int PICK_IMAGE = 100;
    public Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        deleteDialog = new Dialog(ProfileActivity.this);
        deleteDialog.setContentView(R.layout.delete_dialog_box);

        authenticateDialog = new Dialog(ProfileActivity.this);
        authenticateDialog.setContentView(R.layout.authenticate);

//        Get ui components
        btnUpdateProfile = (Button) findViewById(R.id.btnUpdateProfile);
        btnRemoveProfile = (Button) findViewById(R.id.btnRemoveProfile);
        btnAllMoments = (Button) findViewById(R.id.btnAllMoments);
        btnAllFoodItems = (Button) findViewById(R.id.btnAllFoodItems);
        txtPswChange = (TextView) findViewById(R.id.txtPswChange);

        txtFieldPetName = (TextInputLayout) this.findViewById(R.id.txtFieldPetName);
        txtFieldOwnerName = (TextInputLayout) this.findViewById(R.id.txtFieldOwnerName);
        txtFieldEmail = (TextInputLayout) this.findViewById(R.id.txtFieldEmail);

        chipGroup = (ChipGroup) findViewById(R.id.petType);

        profileImage = (ImageView) findViewById(R.id.profileImage);

        firebaseAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        chipGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup chipGroup, int i) {

                Chip chip = chipGroup.findViewById(i);
                if (chip != null) {
                    selectedPetType = chip.getText().toString();
                }
            }
        });

//        Show profile details
        showProfileDetails();

//        For back button
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

//        For update profile button
        btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfileDetails();
            }
        });

//        For remove profile button
        btnRemoveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeAccount();
            }
        });

//        For view moments button
        btnAllMoments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewUserMoments();
            }
        });

//        For view food products button
        btnAllFoodItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewUserFoodItems();
            }
        });

//        For psw change text
        txtPswChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToChangePsw();
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
        Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

//    Method for open profile psw change activity
    private void navigateToChangePsw()
    {
        Intent intent = new Intent(ProfileActivity.this, PswChangeActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

//    Method for open view user moments activity
    private void viewUserMoments()
    {
        Intent intent = new Intent(ProfileActivity.this, UserAllBlogPostActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

//    Method for view food items activity
    private void viewUserFoodItems()
    {
        Intent intent = new Intent(ProfileActivity.this, ViewUserFoodProductsActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

//    Get profile details
    private void  showProfileDetails()
    {

//       Get user id
        String userId = firebaseAuth.getCurrentUser().getUid();
        ref = FirebaseDatabase.getInstance().getReference("users/"+userId);

//        Get profile image from firebase storage
        storageReference.child("Profile_Pictures/" + userId).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profileImage);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

//        Get profile data from firebase
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot != null) {
//                   Set value to text boxes
                    txtFieldPetName.getEditText().setText(snapshot.child("petName").getValue().toString());
                    txtFieldOwnerName.getEditText().setText(snapshot.child("ownerName").getValue().toString());
                    txtFieldEmail.getEditText().setText(snapshot.child("ownerEmail").getValue().toString());

                    String petType = snapshot.child("petType").getValue().toString();

                    int petTypeNumber = 4;
                    if (petType.equals("dog")){ petTypeNumber = 0; }
                    else if (petType.equals("cat")){ petTypeNumber = 1; }
                    else if (petType.equals("bird")){ petTypeNumber = 2; }

                    int finalPetTypeNumber = petTypeNumber;

                    for (int i=0; i<chipGroup.getChildCount();i++){
                        Chip chip = (Chip)chipGroup.getChildAt(i);

                        if (petType.equals(chip.getText().toString())){
                            chip.setChecked(true);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

    }

//    Update profile details
    private void updateProfileDetails()
    {

        String petName = txtFieldPetName.getEditText().getText().toString();
        String ownerName = txtFieldOwnerName.getEditText().getText().toString();

//        Check fields empty
        if (petName.isEmpty() || ownerName.isEmpty()) {
            Toast.makeText(ProfileActivity.this, "Text fields are empty!", Toast.LENGTH_SHORT).show();

        } else if(selectedPetType.equals("")) {
            Toast.makeText(ProfileActivity.this, "Please select pet type!", Toast.LENGTH_SHORT).show();

        } else {

//           Get user id
            String userId = firebaseAuth.getCurrentUser().getUid();
            ref = FirebaseDatabase.getInstance().getReference("users/"+userId);
            ref.child("petName").setValue(petName);
            ref.child("PetType").setValue(selectedPetType);
            ref.child("ownerName").setValue(ownerName);

            Toast.makeText(ProfileActivity.this, "Profile details updated successful!", Toast.LENGTH_SHORT).show();
        }
    }

//    Method for open gallery
    private void openGallery()
    {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

//    Method for upload profile picture
    private void uploadProfilePicture() {

        final ProgressDialog progressDialog = new ProgressDialog(ProfileActivity.this);
        progressDialog.setTitle("Uploading Image");
        progressDialog.show();

        final String userId = firebaseAuth.getCurrentUser().getUid();
        StorageReference riversRef = storageReference.child("Profile_Pictures/" + userId);
        riversRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();
                        Toast.makeText(ProfileActivity.this, "Image Uploaded!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(ProfileActivity.this, "Upload Failed!", Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progressPercentage = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                progressDialog.setMessage("Uploading... " + (int) progressPercentage + "%");
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            imageUri = data.getData();
            profileImage.setImageURI(imageUri);
//            Upload to firebase storage
            uploadProfilePicture();
        }

    }

//    Method for remove account
    private void removeAccount()
    {

        deleteDialog.show();
        TextView textTitle = (TextView) deleteDialog.findViewById(R.id.textTitle);
        btnYes = (Button) deleteDialog.findViewById(R.id.btnYes);
        btnNo = (Button) deleteDialog.findViewById(R.id.btnNo);

        textTitle.setText("Are You Sure Remove Your Account?");
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                authenticateDialog.show();
                TextInputLayout txtFieldReEmail = (TextInputLayout) authenticateDialog.findViewById(R.id.txtFieldReEmail);
                TextInputLayout txtFieldRePsw = (TextInputLayout) authenticateDialog.findViewById(R.id.txtFieldRePsw);
                btnCancel = (Button) authenticateDialog.findViewById(R.id.btnCancel);
                btnSubmit = (Button) authenticateDialog.findViewById(R.id.btnSubmit);

//                For submit button
                btnSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                   Set value to text boxes
                        String reEmail = txtFieldReEmail.getEditText().getText().toString();
                        String rePsw = txtFieldRePsw.getEditText().getText().toString();

                        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        String userId = user.getUid();
                        AuthCredential credential = EmailAuthProvider
                                .getCredential(reEmail, rePsw);

                        firebaseAuth.signOut();
                        user.reauthenticate(credential)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        user.delete()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {

//                                                            Remove account details from database
                                                            ref = FirebaseDatabase.getInstance().getReference("users/"+userId);
                                                            ref.removeValue();

//                                                            Remove profile picture
                                                            StorageReference desertRef = storageReference.child("Profile_Pictures/" + userId);
                                                            desertRef.delete();

                                                            Toast.makeText(ProfileActivity.this, "Your account removed successful!", Toast.LENGTH_SHORT).show();
                                                            login();

                                                        }
                                                    }
                                                });

                                    }
                                });

                    }
                });

//                For cancel button
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        authenticateDialog.dismiss();
                    }
                });

            }
        });

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDialog.dismiss();
            }
        });

    }

//    Method for navigate login activity
    private void login()
    {
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

}