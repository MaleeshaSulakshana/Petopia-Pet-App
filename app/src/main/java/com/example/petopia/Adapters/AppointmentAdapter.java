package com.example.petopia.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.petopia.Constructors.Appointment;
import com.example.petopia.R;

import java.util.ArrayList;

public class AppointmentAdapter extends ArrayAdapter<Appointment> {

    private Context mContext;
    private int mResource;

    public AppointmentAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Appointment> objects) {
        super(context, resource, objects);

        this.mContext = context;
        this.mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        convertView = layoutInflater.inflate(mResource, parent, false);

        TextView txtTitle = convertView.findViewById(R.id.txtTitle);
        TextView txtDate = convertView.findViewById(R.id.txtDate);
        TextView txtTime = convertView.findViewById(R.id.txtTime);

        txtTitle.setText(getItem(position).getTitle());
        txtDate.setText(getItem(position).getDate());
        txtTime.setText(getItem(position).getTime());

        return convertView;
    }

}
