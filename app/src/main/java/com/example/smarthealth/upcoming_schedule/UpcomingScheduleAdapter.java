package com.example.smarthealth.upcoming_schedule;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarthealth.R;

import java.util.List;

public class UpcomingScheduleAdapter extends RecyclerView.Adapter<UpcomingScheduleViewHolder> {
    private List<UpcomingSchedule> upcomingSchedules;

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
        if (schedule.getScheduleType() == UpcomingSchedule.ScheduleType.MEDICINE) {
            holder.scheduleTypeImage = R.drawable.up_schedule_medicine;
        } else if (schedule.getScheduleType() == UpcomingSchedule.ScheduleType.MEAL) {
            holder.scheduleTypeImage.setImageDrawable(R.drawable.up_schedule_meal);
        }
        holder.scheduleTitle.setText(schedule.getScheduleTitle());
        holder.scheduleTime = schedule.getScheduleCalender().getTime();
    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
