package com.example.smarthealth.nutrient_intake;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarthealth.R;

public class NutrientIntakeViewHolder extends RecyclerView.ViewHolder {
    public final TextView nutrientTitle;
    public final TextView currentNutrientText;
    public final TextView totalNutrientText;
    public final ImageView nutrientIcon;

    public NutrientIntakeViewHolder(@NonNull View itemView) {
        super(itemView);
        nutrientTitle = itemView.findViewById(R.id.nutrientTitle);
        currentNutrientText = itemView.findViewById(R.id.nutrientCurrentText);
        totalNutrientText = itemView.findViewById(R.id.nutrientTotalText);
        nutrientIcon = itemView.findViewById(R.id.nutrientIcon);
    }
}
