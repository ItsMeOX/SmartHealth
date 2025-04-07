package com.example.smarthealth.nutrient_intake;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarthealth.R;

import java.util.List;

public class NutrientIntakeAdapter extends RecyclerView.Adapter<NutrientIntakeViewHolder> {
    private final List<NutrientIntake> nutrientIntakeList;

    public NutrientIntakeAdapter(List<NutrientIntake> nutrientIntakeList) {
        this.nutrientIntakeList = nutrientIntakeList;
    }

    @NonNull
    @Override
    public NutrientIntakeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.nutrient_intake_view, parent, false);

        return new NutrientIntakeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NutrientIntakeViewHolder holder, int position) {
        NutrientIntake currNutrientIntake = nutrientIntakeList.get(position);
        double currentNutrient = currNutrientIntake.getCurrentNutrient();
        double totalNutrient = currNutrientIntake.getTotalNutrient();

        holder.currentNutrientText.setText(String.valueOf(currentNutrient) + currNutrientIntake.getIntakeUnit());
        holder.totalNutrientText.setText(String.valueOf(totalNutrient) + currNutrientIntake.getIntakeUnit());

        if (currentNutrient > totalNutrient) {
            holder.currentNutrientText.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.warning_red));
        }

        holder.nutrientTitle.setText(currNutrientIntake.getNutrientName());
        holder.nutrientIcon.setImageDrawable(
                ContextCompat.getDrawable(holder.itemView.getContext(), currNutrientIntake.getNutrientIconId()));
    }

    @Override
    public int getItemCount() {
        return nutrientIntakeList.size();
    }
}
