package com.example.smarthealth.upcoming_schedule;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.smarthealth.R;
import com.example.smarthealth.nutrient_intake.NutrientIntakeAdapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class UpcomingScheduleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public final TextView scheduleTitle;
    public final ImageView scheduleTypeImage;
    public final TextView scheduleTime;
    private final UpcomingScheduleAdapter.OnItemListener onItemListener;
    private final List<UpcomingSchedule> upcomingSchedules;

    public UpcomingScheduleViewHolder(@NonNull View itemView, UpcomingScheduleAdapter.OnItemListener onItemListener, List<UpcomingSchedule> upcomingSchedules) {
        super(itemView);
        scheduleTitle = itemView.findViewById(R.id.upcomingScheduleName);
        scheduleTypeImage = itemView.findViewById(R.id.upcomingScheduleIcon);
        scheduleTime = itemView.findViewById(R.id.upcomingScheduleTime);
        this.onItemListener = onItemListener;
        this.upcomingSchedules = upcomingSchedules;
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        onItemListener.onScheduleItemClick(getAdapterPosition(), upcomingSchedules);
    }
}