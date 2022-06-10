package com.example.petopia.FoodProducts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.petopia.Adapters.ProductAdapter;
import com.example.petopia.Constructors.Product;
import com.example.petopia.HomeActivity;
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

import java.util.ArrayList;

public class FoodProductDashboardActivity extends AppCompatActivity {

    private ListView viewAllFoodItems;
    private EditText search;
    private ArrayList<Product> arrayList = new ArrayList<>();
    private DatabaseReference reference;

    private ProductAdapter productAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_product_dashboard);

//        Get ui components
        viewAllFoodItems = (ListView) findViewById(R.id.viewAllFoodItems);
        search = (EditText) findViewById(R.id.search);

//        Show products details
        showProductsOnList();

//        List view item click
        viewAllFoodItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                Intent detailsPage = new Intent(FoodProductDashboardActivity.this, FoodProductDetailsActivity.class);
                detailsPage.putExtra("ProductId", arrayList.get(i).getProductId());
                startActivity(detailsPage);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        });

//        For search
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

//                FoodProductDashboardActivity.this.productAdapter.getFilter().filter(s);
//                productAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

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
        Intent intent = new Intent(FoodProductDashboardActivity.this, HomeActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

//    Method for show products on list
    private void showProductsOnList()
    {

//        Show products data on list view
        productAdapter = new ProductAdapter(this, R.layout.view_product_row, arrayList);
        viewAllFoodItems.setAdapter(productAdapter);
        viewAllFoodItems.setTextFilterEnabled(true);

        reference = FirebaseDatabase.getInstance().getReference("products/");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot != null) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {

                        arrayList.add(new Product(snapshot1.child("userId").getValue().toString(),
                                snapshot1.child("productId").getValue().toString(),
                                snapshot1.child("foodName").getValue().toString(),
                                snapshot1.child("description").getValue().toString(),
                                "Rs. "+snapshot1.child("price").getValue().toString(),
                                snapshot1.child("mobileNumber").getValue().toString(),
                                snapshot1.child("imageUrl").getValue().toString()));
                        productAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

    }

}