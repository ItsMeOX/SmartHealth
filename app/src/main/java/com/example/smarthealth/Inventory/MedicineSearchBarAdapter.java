package com.example.smarthealth.Inventory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.smarthealth.R;

import java.util.ArrayList;
import java.util.List;

public class MedicineSearchBarAdapter extends ArrayAdapter<MedicineButton> {

    private Context context;
    private List<MedicineButton> medicineList;

    public MedicineSearchBarAdapter(Context context, List<MedicineButton> medicineList) {
        super(context, 0, medicineList);
        this.context = context;
        this.medicineList = medicineList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createView(position, convertView, parent);
    }

    private View createView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.history_item_dropdown, parent, false);
        }

        MedicineButton medicine = medicineList.get(position);

        // Set the image and text
        ImageView medicineImage = convertView.findViewById(R.id.imageHistory);
        TextView medicineName = convertView.findViewById(R.id.nameHistory);

        String imageUrl = medicine.getMedicineImage();

        Glide.with(convertView.getContext())
                .load(imageUrl)
                .into(medicineImage);
//        medicineImage.setImageDrawable(medicine.getMedicineImage());
        medicineName.setText(medicine.getMedicineName());

        return convertView;
    }
    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<MedicineButton> filteredList = new ArrayList<>();
                if (constraint == null || constraint.length() == 0) {
                    filteredList.addAll(medicineList); // full list
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (MedicineButton item : medicineList) {
                        if (item.getMedicineName().toLowerCase().contains(filterPattern)) {
                            filteredList.add(item);
                        }
                    }
                }

                FilterResults results = new FilterResults();
                results.values = filteredList;
                results.count = filteredList.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                medicineList.clear();
                medicineList.addAll((List) results.values);
                notifyDataSetChanged();
            }

            @Override
            public CharSequence convertResultToString(Object resultValue) {
                return ((MedicineButton) resultValue).getMedicineName();
            }
        };
    }
}