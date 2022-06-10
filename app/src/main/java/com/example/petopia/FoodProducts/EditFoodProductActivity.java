package com.example.petopia.FoodProducts;

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

import com.example.petopia.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
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

public class EditFoodProductActivity extends AppCompatActivity {

    private LinearLayout imagePicker;
    private ImageView foodImage;
    private static final int PICK_IMAGE = 100;
    Uri imageUri;

    private TextInputLayout txtFieldFoodName, txtFieldDesc, txtFieldPrice, txtFieldMobileNumber;
    private Button btnUpdate, btnRemove, btnYes, btnNo;
    private Dialog deleteDialog;
    private String productId = "";
    private String isUploaded = "no";

    private FirebaseAuth firebaseAuth;
    private DatabaseReference ref;
    private FirebaseStorage storage;
    private StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_food_product);

        Intent project = getIntent();
        productId = project.getStringExtra("ProductId");

        deleteDialog = new Dialog(EditFoodProductActivity.this);
        deleteDialog.setContentView(R.layout.delete_dialog_box);

//        Get ui components
        imagePicker = (LinearLayout) findViewById(R.id.imagePicker);
        foodImage = (ImageView) findViewById(R.id.foodImage);

        txtFieldFoodName = (TextInputLayout) this.findViewById(R.id.txtFieldFoodName);
        txtFieldDesc = (TextInputLayout) this.findViewById(R.id.txtFieldDesc);
        txtFieldPrice = (TextInputLayout) this.findViewById(R.id.txtFieldPrice);
        txtFieldMobileNumber = (TextInputLayout) this.findViewById(R.id.txtFieldMobileNumber);

        btnUpdate = (Button) findViewById(R.id.btnUpdate);
        btnRemove = (Button) findViewById(R.id.btnRemove);

        firebaseAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

//        Show product details
        showProductDetails(productId);

//        For back button
        imagePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

//        For update product
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProduct(productId);
            }
        });

//        For delete product
        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeProduct(productId);
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
        Intent intent = new Intent(EditFoodProductActivity.this, ViewUserFoodProductsActivity.class);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data){

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            imageUri = data.getData();
            isUploaded = "yes";
            foodImage.setImageURI(imageUri);
        }

    }

//    Get product details
    private void  showProductDetails(String productId)
    {
//        Get product data from firebase
        ref = FirebaseDatabase.getInstance().getReference("products/"+productId);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot != null) {
//                   Set value to text boxes
                    Picasso.get().load(snapshot.child("imageUrl").getValue().toString()).into(foodImage);
                    txtFieldFoodName.getEditText().setText(snapshot.child("foodName").getValue().toString());
                    txtFieldDesc.getEditText().setText(snapshot.child("description").getValue().toString());
                    txtFieldPrice.getEditText().setText(snapshot.child("price").getValue().toString());
                    txtFieldMobileNumber.getEditText().setText(snapshot.child("mobileNumber").getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

    }

//    Method for update product
    private void updateProduct(String productId)
    {
        String foodName = txtFieldFoodName.getEditText().getText().toString();
        String description = txtFieldDesc.getEditText().getText().toString();
        String price = txtFieldPrice.getEditText().getText().toString();
        String mobileNumber = txtFieldMobileNumber.getEditText().getText().toString();

//        Check fields empty
        if (foodName.isEmpty() || description.isEmpty() || price.isEmpty() || mobileNumber.isEmpty()) {
            Toast.makeText(EditFoodProductActivity.this, "Text fields are empty!", Toast.LENGTH_SHORT).show();

        } else if(mobileNumber.length() != 10) {
            Toast.makeText(EditFoodProductActivity.this, "Please check your mobile number!", Toast.LENGTH_SHORT).show();

        } else {

//            Upload image and details
            ref = FirebaseDatabase.getInstance().getReference("products/"+productId);
            if (isUploaded.equals("yes")) {

                final ProgressDialog progressDialog = new ProgressDialog(EditFoodProductActivity.this);
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

                                        ref.child("imageUrl").setValue(uri.toString());
                                    }
                                });

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(EditFoodProductActivity.this, "Product details update not successful!", Toast.LENGTH_SHORT).show();
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
            ref.child("foodName").setValue(foodName);
            ref.child("description").setValue(description);
            ref.child("price").setValue(price);
            ref.child("mobileNumber").setValue(mobileNumber);
            Toast.makeText(EditFoodProductActivity.this, "Product details update successful!", Toast.LENGTH_SHORT).show();

        }

    }

//    Method for remove product from firebase
    private void removeProduct(String productId)
    {
        deleteDialog.show();
        TextView textTitle = (TextView) deleteDialog.findViewById(R.id.textTitle);
        btnYes = (Button) deleteDialog.findViewById(R.id.btnYes);
        btnNo = (Button) deleteDialog.findViewById(R.id.btnNo);

        textTitle.setText("Are You Sure Remove Product Item?");
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ref = FirebaseDatabase.getInstance().getReference("products/"+productId);
                ref.removeValue();

                StorageReference desertRef = storageReference.child("Products_Pictures/"+productId);
                desertRef.delete();

                Toast.makeText(EditFoodProductActivity.this, "Product delete successful!", Toast.LENGTH_SHORT).show();
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