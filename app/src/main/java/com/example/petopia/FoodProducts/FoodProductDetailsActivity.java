package com.example.petopia.FoodProducts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.petopia.PetCare.PetCareDashboardActivity;
import com.example.petopia.PetCare.TeachYourDogDashboardActivity;
import com.example.petopia.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class FoodProductDetailsActivity extends AppCompatActivity {

    private ImageView foodImage, increment, decrement;
    private TextView txtFieldFoodName, txtFieldDesc, txtFieldPrice, txtFieldMobileNumber, qty, total;
    private DatabaseReference ref;
    private String productId = "";
    private Button btnAddCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_product_details);

        Intent project = getIntent();
        productId = project.getStringExtra("ProductId");

//        Get ui components
        foodImage = (ImageView) findViewById(R.id.foodImage);

        txtFieldFoodName = (TextView) this.findViewById(R.id.txtFieldFoodName);
        txtFieldDesc = (TextView) this.findViewById(R.id.txtFieldDesc);
        txtFieldPrice = (TextView) this.findViewById(R.id.txtFieldPrice);
        txtFieldMobileNumber = (TextView) this.findViewById(R.id.txtFieldMobileNumber);

        qty = (TextView) this.findViewById(R.id.qty);
        total = (TextView) this.findViewById(R.id.total);
        increment = (ImageView) this.findViewById(R.id.increment);
        decrement = (ImageView) this.findViewById(R.id.decrement);

        btnAddCart = (Button) this.findViewById(R.id.btnAddCart);

//        Show product details
        showProductDetails(productId);

//        Make call
        txtFieldMobileNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeCall();
            }
        });

//        Increment qty
        increment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int q = Integer.parseInt(qty.getText().toString());
                double price = Double.parseDouble(txtFieldPrice.getText().toString());
                if (q >= 1){
                    int count = q +1;
                    double totalPrice = price * count;
                    qty.setText(String.valueOf(count));
                    total.setText("Total : Rs. "+String.valueOf(totalPrice)+"0");
                }
            }
        });

//        Decrement qty
        decrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int q = Integer.parseInt(qty.getText().toString());
                double price = Double.parseDouble(txtFieldPrice.getText().toString());
                if (q > 1){
                    int count = q - 1;
                    double totalPrice = price * count;
                    qty.setText(String.valueOf(count));
                    total.setText("Total : Rs. "+String.valueOf(totalPrice)+"0");
                }

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
        Intent intent = new Intent(FoodProductDetailsActivity.this, FoodProductDashboardActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
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
                    txtFieldFoodName.setText(snapshot.child("foodName").getValue().toString());
                    txtFieldDesc.setText(snapshot.child("description").getValue().toString());
                    txtFieldPrice.setText(snapshot.child("price").getValue().toString());

                    int q = Integer.parseInt(qty.getText().toString());
                    double price = Double.parseDouble(snapshot.child("price").getValue().toString());
                    double totalPrice = q * price;

                    total.setText("Total : Rs. "+String.valueOf(totalPrice)+"0");
                    txtFieldMobileNumber.setText(snapshot.child("mobileNumber").getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

    }

//    Method for make phone call
    private void makeCall()
    {
        String number = txtFieldMobileNumber.getText().toString();
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:"+number));
        startActivity(callIntent);
    }
}