package com.example.smarthealth.nutrient_intake;

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
        holder.totalNutrientText.setText(String.valueOf(currNutrientIntake.getTotalNutrient()) + currNutrientIntake.getIntakeUnit());
        holder.currentNutrientText.setText(String.valueOf(currNutrientIntake.getCurrentNutrient()) + currNutrientIntake.getIntakeUnit());
        holder.nutrientTitle.setText(currNutrientIntake.getNutrientName());
        holder.nutrientIcon.setImageDrawable(
                ContextCompat.getDrawable(holder.itemView.getContext(), currNutrientIntake.getNutrientIconId()));
    }

    @Override
    public int getItemCount() {
        return nutrientIntakeList.size();
    }
}
