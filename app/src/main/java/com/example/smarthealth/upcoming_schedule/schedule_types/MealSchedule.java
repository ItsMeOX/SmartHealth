package com.example.smarthealth.upcoming_schedule.schedule_types;

import com.example.smarthealth.R;

public class MealSchedule implements ScheduleType {
    @Override
    public String getType() {
        return "Meal";
    }

    @Override
    public int getIconResId() {
        return R.drawable.up_schedule_meal;
    }
}