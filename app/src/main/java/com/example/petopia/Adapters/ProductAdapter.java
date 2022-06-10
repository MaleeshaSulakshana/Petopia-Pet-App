package com.example.petopia.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.petopia.Constructors.Moments;
import com.example.petopia.Constructors.Product;
import com.example.petopia.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProductAdapter extends ArrayAdapter<Product> {

    private Context mContext;
    private int mResource;

    public ProductAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Product> objects) {
        super(context, resource, objects);

        this.mContext = context;
        this.mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        convertView = layoutInflater.inflate(mResource, parent, false);

        ImageView productImage = convertView.findViewById(R.id.productImage);
        TextView title = convertView.findViewById(R.id.title);
        TextView price = convertView.findViewById(R.id.price);

        title.setText(getItem(position).getFoodName());
        price.setText(getItem(position).getPrice());
        Picasso.get().load(getItem(position).getImageUrl()).into(productImage);

        return convertView;
    }

}
