package com.example.smarthealth.Inventory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarthealth.R;

import java.util.ArrayList;

public class MedicineAdapter extends RecyclerView.Adapter<MedicineAdapter.ViewHolder> {

    private final Context context;
    private ArrayList<MedicineButton> medicineButtonList;

    public MedicineAdapter(Context context, ArrayList<MedicineButton> medicineContainersList) {
        this.context = context;
        this.medicineButtonList = medicineContainersList;
    }

    @NonNull
    @Override
    public MedicineAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate medicine button
        View cardView = inflater.inflate(R.layout.medi_button, parent, false);
        return new ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicineAdapter.ViewHolder holder, int position) {
        // Get button from list
        MedicineButton model = medicineButtonList.get(position);
        // Set all information of the button
        holder.medicineName.setText(model.getMedicineName());
        holder.medicineDesc.setText(model.getMedicineDesc());
        holder.medicineAmount.setText(String.valueOf(model.getMedicineAmount()));

        // Handling image not found
        if (model.getMedicineImage() != 0) {
            // catch error
            holder.medicineImage.setImageResource(model.getMedicineImage());
        } else {
            // Default image if no image found
            holder.medicineImage.setImageResource(R.drawable.camera);
        }
    }

    @Override
    public int getItemCount() {
        // Return the number of items in the list
        return medicineButtonList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView medicineImage;
        private final TextView medicineName;
        private final TextView medicineDesc;
        private final TextView medicineAmount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            medicineImage = itemView.findViewById(R.id.mediImage);
            medicineName = itemView.findViewById(R.id.mediName);
            medicineDesc = itemView.findViewById(R.id.mediDesc);
            medicineAmount = itemView.findViewById(R.id.amountText);
        }
    }
}
