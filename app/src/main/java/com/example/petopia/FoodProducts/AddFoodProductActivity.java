package com.example.petopia.FoodProducts;

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

import com.example.petopia.Constructors.Product;
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

public class AddFoodProductActivity extends AppCompatActivity {

    private LinearLayout imagePicker;
    private ImageView foodImage;
    private static final int PICK_IMAGE = 100;
    Uri imageUri;

    private TextInputLayout txtFieldFoodName, txtFieldDesc, txtFieldPrice, txtFieldMobileNumber;
    private Button btnAdd;
    private String isUploaded = "no";

    private FirebaseAuth firebaseAuth;
    private DatabaseReference ref;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    int randomNumber = 00000000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food_product);

//        Get ui components
        imagePicker = (LinearLayout) findViewById(R.id.imagePicker);
        foodImage = (ImageView) findViewById(R.id.foodImage);

        txtFieldFoodName = (TextInputLayout) this.findViewById(R.id.txtFieldFoodName);
        txtFieldDesc = (TextInputLayout) this.findViewById(R.id.txtFieldDesc);
        txtFieldPrice = (TextInputLayout) this.findViewById(R.id.txtFieldPrice);
        txtFieldMobileNumber = (TextInputLayout) this.findViewById(R.id.txtFieldMobileNumber);

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

//        For add item
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewItem();
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
        Intent intent = new Intent(AddFoodProductActivity.this, ViewUserFoodProductsActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

//    Method for open gallery
    private void openGallery()
    {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            imageUri = data.getData();
            isUploaded = "yes";
            foodImage.setImageURI(imageUri);
        }

    }

//    Method for add new item
    private void addNewItem()
    {
        String foodName = txtFieldFoodName.getEditText().getText().toString();
        String description = txtFieldDesc.getEditText().getText().toString();
        String price = txtFieldPrice.getEditText().getText().toString();
        String mobileNumber = txtFieldMobileNumber.getEditText().getText().toString();

//        Check fields empty
        if (foodName.isEmpty() || description.isEmpty() || price.isEmpty() || mobileNumber.isEmpty()) {
            Toast.makeText(AddFoodProductActivity.this, "Text fields are empty!", Toast.LENGTH_SHORT).show();

        } else if(isUploaded.equals("no")) {
            Toast.makeText(AddFoodProductActivity.this, "Please upload image for product!", Toast.LENGTH_SHORT).show();

        } else if(mobileNumber.length() != 10) {
            Toast.makeText(AddFoodProductActivity.this, "Please check your mobile number!", Toast.LENGTH_SHORT).show();

        } else {
//            Get user id
            String userId = firebaseAuth.getCurrentUser().getUid();

            randomNumber = RandomNumber.getRandomNumber();
            String dateTime = DateTimeGenerator.getDateTime();
            String productId = userId+dateTime+String.valueOf(randomNumber);

            ref = FirebaseDatabase.getInstance().getReference("products/"+productId);

//            Upload image and details
            final ProgressDialog progressDialog = new ProgressDialog(AddFoodProductActivity.this);
            progressDialog.setTitle("Uploading Image");
            progressDialog.show();

            StorageReference riversRef = storageReference.child("Products_Pictures/" + productId);
            riversRef.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

//                                    Insert product data to firebase real time database
                                    Product product = new Product(userId, productId, foodName, description, price, mobileNumber, uri.toString());
                                    ref.setValue(product)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()){
                                                        clear();
                                                        Toast.makeText(AddFoodProductActivity.this, "Product item add successful!", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(AddFoodProductActivity.this, "Product item add not successful", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(AddFoodProductActivity.this, "Product item add not successful!", Toast.LENGTH_SHORT).show();
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
        txtFieldFoodName.getEditText().setText("");
        txtFieldDesc.getEditText().setText("");
        txtFieldPrice.getEditText().setText("");
        txtFieldMobileNumber.getEditText().setText("");
        isUploaded = "no";
        foodImage.setImageDrawable(getResources().getDrawable(R.drawable.upload_image));
    }

}