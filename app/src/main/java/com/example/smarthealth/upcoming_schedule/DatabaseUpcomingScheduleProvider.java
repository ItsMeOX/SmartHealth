package com.example.smarthealth.upcoming_schedule;

import android.util.Log;

import com.example.smarthealth.api_service.EventDto;
import com.example.smarthealth.api_service.EventService;
import com.example.smarthealth.api_service.NutrientIntakeDto;
import com.example.smarthealth.api_service.RetrofitClient;
import com.example.smarthealth.api_service.UpcomingScheduleDto;
import com.example.smarthealth.api_service.UpcomingScheduleService;
import com.example.smarthealth.calendar.CalendarEvent;
import com.example.smarthealth.calendar.DatabaseCalendarEventProvider;
import com.example.smarthealth.nutrient_intake.DatabaseNutrientIntakeProvider;
import com.example.smarthealth.upcoming_schedule.schedule_types.MealSchedule;
import com.example.smarthealth.upcoming_schedule.schedule_types.MedicineSchedule;
import com.example.smarthealth.upcoming_schedule.schedule_types.ScheduleType;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DatabaseUpcomingScheduleProvider implements UpcomingScheduleProvider {

    private final UpcomingScheduleService upcomingScheduleService;

    public DatabaseUpcomingScheduleProvider(){
        upcomingScheduleService = RetrofitClient.getInstance().create(UpcomingScheduleService.class);
    }

    @Override
    public List<UpcomingSchedule> getTodaySchedules(long userId, OnDataLoadedCallback callback) {
        List<UpcomingSchedule> schedules = new ArrayList<>();

        Calendar today = (Calendar) Calendar.getInstance().clone();
        int day = today.get(Calendar.DAY_OF_MONTH);     // Day of month (1-31)
        int month = today.get(Calendar.MONTH) + 1;       // Month (0-11), so add 1 for human-readable format
        int year = today.get(Calendar.YEAR);             // Full year (e.g., 2025)

        Call<List<UpcomingScheduleDto>> call = upcomingScheduleService.getSchedulesByDay(userId, year, month, day);

        call.enqueue(new Callback<List<UpcomingScheduleDto>>() {
            @Override
            public void onResponse(Call<List<UpcomingScheduleDto>> call, Response<List<UpcomingScheduleDto>> response) {
                if(response.isSuccessful() && response.body() != null){
                    for(UpcomingScheduleDto schedule : response.body()){
                        Log.d("debug", "schdule "+schedule.getTaken());
                        UpcomingSchedule upcomingSchedule = new UpcomingSchedule(
                                schedule.getId(),
                                schedule.getScheduleTitle(),
                                schedule.getScheduleDescription(),
                                schedule.getTaken(),
                                (Calendar) schedule.getScheduleCalendar(),
                                schedule.getScheduleType().equals("Meal") ? new MealSchedule() : new MedicineSchedule());
                        schedules.add(upcomingSchedule);
                    }
                }

                List<UpcomingSchedule> finalSchedules = new ArrayList<>();
                for(UpcomingSchedule schedule : schedules){
                    if(!schedule.getTaken()) {
                        finalSchedules.add(schedule);
                    }
                }
               finalSchedules.stream().forEach(
                        x -> Log.d("debug", String.valueOf(x.getTaken()))
                );
                callback.onDataLoaded(finalSchedules);
            }

            @Override
            public void onFailure(Call<List<UpcomingScheduleDto>> call, Throwable t) {
                Log.d("debug upcoming schedule", "failed!" + " " + t.getMessage());
            }
        });

        return schedules;
    }

    @Override
    public void initializeUpcomingSchedule(long userId, DatabaseUpcomingScheduleProvider.OnUpcomingScheduleAddedCallback callback) {  // Add callback parameter
        List<UpcomingScheduleDto> upcomingScheduleDtosToBeInserted = new ArrayList<>();

        Calendar today = (Calendar) Calendar.getInstance().clone();
        today.set(Calendar.HOUR_OF_DAY, 12);

        upcomingScheduleDtosToBeInserted.add(new UpcomingScheduleDto(
                "Lunch",
                "Please have lunch now!",
                today,
                "Meal",
                false,
                null,
                0
        ));

        Calendar secondToday = (Calendar) Calendar.getInstance().clone();
        secondToday.set(Calendar.HOUR_OF_DAY, 18);
        upcomingScheduleDtosToBeInserted.add(new UpcomingScheduleDto(
                "Dinner",
                "Please have dinner now!",
                secondToday,
                "Meal",
                false,
                null,
                0
        ));

        // Track successful inserts
        final int[] successfulInserts = {0};
        final int totalInserts = upcomingScheduleDtosToBeInserted.size();

        for(int i = 0; i < upcomingScheduleDtosToBeInserted.size(); i++){
            Call<UpcomingScheduleDto> call = upcomingScheduleService.createSchedule(userId, upcomingScheduleDtosToBeInserted.get(i));
            call.enqueue(new Callback<UpcomingScheduleDto>() {
                @Override
                public void onResponse(Call<UpcomingScheduleDto> call, Response<UpcomingScheduleDto> response) {
                    if (response.isSuccessful()) {
                        successfulInserts[0]++;
                        if(successfulInserts[0] == totalInserts) {
                            callback.onUpcomingScheduleAdded(true); // All inserts successful
                        }
                    } else {
                        callback.onUpcomingScheduleAdded(false);
                    }
                }

                @Override
                public void onFailure(Call<UpcomingScheduleDto> call, Throwable t) {
                    callback.onUpcomingScheduleAdded(false);
                }
            });
        }
    }

    public interface OnDataLoadedCallback {
        void onDataLoaded(List<UpcomingSchedule> upcomingScheduleList);
    }
    public interface OnUpcomingScheduleAddedCallback {
        void onUpcomingScheduleAdded(boolean success);
    }
}
