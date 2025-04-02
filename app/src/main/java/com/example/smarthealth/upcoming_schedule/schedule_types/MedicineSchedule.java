package com.example.smarthealth.upcoming_schedule.schedule_types;

import com.example.smarthealth.R;

public class MedicineSchedule implements ScheduleType {
    @Override
    public String getType() {
        return "Medicine";
    }

    @Override
    public int getIconResId() {
        return R.drawable.up_schedule_medicine;
    }
}