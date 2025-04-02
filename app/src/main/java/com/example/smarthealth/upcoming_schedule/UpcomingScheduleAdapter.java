package com.example.smarthealth.upcoming_schedule;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarthealth.R;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class UpcomingScheduleAdapter extends RecyclerView.Adapter<UpcomingScheduleViewHolder> {
    private final List<UpcomingSchedule> upcomingSchedules;

    public UpcomingScheduleAdapter(List<UpcomingSchedule> upcomingSchedules) {
        this.upcomingSchedules = upcomingSchedules;
    }

    @NonNull
    @Override
    public UpcomingScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.upcoming_schedule_view, parent, false);

        return new UpcomingScheduleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UpcomingScheduleViewHolder holder, int position) {
        UpcomingSchedule schedule = upcomingSchedules.get(position);
        holder.scheduleTypeImage.setImageDrawable(
                ContextCompat.getDrawable(holder.itemView.getContext(), schedule.getScheduleType().getIconResId())
        );
        holder.scheduleTitle.setText(schedule.getScheduleTitle());
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        String formattedTime = sdf.format(schedule.getScheduleCalender().getTime());
        holder.scheduleTime.setText(formattedTime);
    }

    @Override
    public int getItemCount() {
        return upcomingSchedules.size();
    }
}
