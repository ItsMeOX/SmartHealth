package com.example.smarthealth.upcoming_schedule;

import com.example.smarthealth.nutrient_intake.DatabaseNutrientIntakeProvider;

import java.util.List;

public interface UpcomingScheduleProvider {
    List<UpcomingSchedule> getTodaySchedules(long userId, DatabaseUpcomingScheduleProvider.OnDataLoadedCallback callback);
    void initializeUpcomingSchedule(long userId, DatabaseUpcomingScheduleProvider.OnUpcomingScheduleAddedCallback callback);
}
