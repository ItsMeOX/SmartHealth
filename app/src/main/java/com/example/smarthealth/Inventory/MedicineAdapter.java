package com.example.smarthealth.Inventory;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarthealth.R;

import java.util.ArrayList;
import java.util.List;

public class MedicineAdapter extends RecyclerView.Adapter<MedicineAdapter.ViewHolder> {

    private final Context context;
    private ArrayList<MedicineButton> medicineButtonList;
    private List<MedicineButton> medicineSubList;
    private boolean isExpanded = true;
    private final int MAX_BUTTON_SHOWN = 4;

    public MedicineAdapter(Context context, ArrayList<MedicineButton> medicineContainersList) {
        this.context = context;
        this.medicineButtonList = medicineContainersList;
        this.medicineSubList = new ArrayList<>(medicineContainersList);medicineButtonList.subList(0, Math.min(MAX_BUTTON_SHOWN, medicineButtonList.size()));
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate medicine button
        View cardView = inflater.inflate(R.layout.medi_button, parent, false);
        return new ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get button from list
        MedicineButton model = medicineSubList.get(position);

        // Set the button's data (name, description, etc.)
        holder.medicineImage.setImageDrawable(model.getMedicineImage());
        holder.medicineName.setText(model.getMedicineName());
        holder.medicineTag.setText(model.getMedicineType().get(0));
        holder.medicineAmount.setText(String.valueOf(model.getMedicineAmount()));

//         Set visibility based on expanded or collapsed state
        if (isExpanded || position < 4) {
            holder.itemView.setVisibility(View.VISIBLE);
        } else {
            holder.itemView.setVisibility(View.GONE); // Hide items beyond the limit
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MedicineButton mediClicked = medicineButtonList.get(position);
                MedicineInfoPage.showInfo(context, mediClicked, v);
            }
        });

    }

    @Override
    public int getItemCount() {
        // Return the number of items in the list
        return medicineSubList != null ? medicineSubList.size() : 0;
    }

    public void toggleItemLimits(){
        isExpanded = !isExpanded;

        if (isExpanded) {
            // Show all items (reset to original list)
            medicineSubList = new ArrayList<>(medicineButtonList);
        } else {
//             Show only the first 4 items
            medicineSubList = medicineButtonList.subList(0, Math.min(MAX_BUTTON_SHOWN, medicineButtonList.size()));
        }

        // Notify the adapter to refresh the view
        notifyDataSetChanged();

    }

    public boolean getIsExpanded(){
        return isExpanded;
    }

    public void updateMedicineList(ArrayList<MedicineButton> newList){
        this.medicineButtonList = newList;
        if (!isExpanded) {
            // Show only the first 4 items
            this.medicineSubList = medicineButtonList.subList(0, Math.min(MAX_BUTTON_SHOWN, medicineButtonList.size()));
        } else {
            // Show all items
            this.medicineSubList = new ArrayList<>(medicineButtonList);
        }
        notifyDataSetChanged();

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView medicineImage;
        private final TextView medicineName;
        private final TextView medicineTag;
        private final TextView medicineAmount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            medicineImage = itemView.findViewById(R.id.mediImage);
            medicineName = itemView.findViewById(R.id.mediName);
            medicineTag = itemView.findViewById(R.id.mediDesc);
            medicineAmount = itemView.findViewById(R.id.amountText);
        }
    }
    public void showInfo(View anchorView) {
        View popupView = LayoutInflater.from(context).inflate(R.layout.medicine_fullview, null);

        PopupWindow popupWindow = new PopupWindow(popupView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                true);

        popupWindow.showAtLocation(anchorView.getRootView(), Gravity.CENTER, 0, 0);
    }

}
