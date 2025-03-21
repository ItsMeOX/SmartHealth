package com.example.smarthealth.upcoming_schedule;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.smarthealth.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class UpcomingScheduleViewHolder extends RecyclerView.ViewHolder {
    public final TextView scheduleTitle;
    public final ImageView scheduleTypeImage;
    public final TextView scheduleTime;

    public UpcomingScheduleViewHolder(@NonNull View itemView) {
        super(itemView);
        scheduleTitle = itemView.findViewById(R.id.upcomingScheduleName);
        scheduleTypeImage = itemView.findViewById(R.id.upcomingScheduleIcon);
        scheduleTime = itemView.findViewById(R.id.upcomingScheduleTime);
    }
}
