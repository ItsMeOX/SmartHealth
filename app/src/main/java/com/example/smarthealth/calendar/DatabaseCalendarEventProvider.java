package com.example.smarthealth.calendar;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.smarthealth.api_service.EventDto;
import com.example.smarthealth.api_service.EventService;
import com.example.smarthealth.api_service.RetrofitClient;
import com.example.smarthealth.calendar.QueryStrategy.CalendarQueryStrategy;
import com.example.smarthealth.calendar.QueryStrategy.DayQueryStrategy;
import com.example.smarthealth.calendar.QueryStrategy.MonthQueryStrategy;
import com.example.smarthealth.nutrient_intake.NutrientIntake;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DatabaseCalendarEventProvider implements CalendarEventProvider {
    private final EventService eventService;

    public DatabaseCalendarEventProvider() {
        eventService = RetrofitClient.getInstance().create(EventService.class);
    }

    @Override
    public List<CalendarEvent> getEventsForDay(long userId, Calendar date, OnDataLoadedCallback callback) {
        return queryDatabaseCalendar(userId, date, new DayQueryStrategy(), callback);
    }

    @Override
    public List<CalendarEvent> getEventsForMonth(long userId, Calendar date, OnDataLoadedCallback callback) {
        return queryDatabaseCalendar(userId, date, new MonthQueryStrategy(), callback);
    }

    private List<CalendarEvent> queryDatabaseCalendar(long userId, Calendar date, CalendarQueryStrategy strategy, OnDataLoadedCallback callback) {
        List<CalendarEvent> calendarEvents = new ArrayList<>();
        strategy.createCall(eventService, userId, date).enqueue(new Callback<List<EventDto>>() {
            @Override
            public void onResponse(@NonNull Call<List<EventDto>> call, @NonNull Response<List<EventDto>> response) {
                if(response.body() != null){
                    for(EventDto event : response.body()){
                        CalendarEvent calendarEvent = new CalendarEvent(
                                event.getEventTitle(),
                                event.getEventDescription(),
                                event.getEventStartCalendar(),
                                event.getEventEndCalendar()
                        );
                        calendarEvents.add(calendarEvent);
                    }
                }
                callback.onDataLoaded(calendarEvents);
            }
            @Override
            public void onFailure(@NonNull Call<List<EventDto>> call, @NonNull Throwable t) {
                Log.d("debug", "failed!" + " " + t.getMessage());
            }
        });

        return calendarEvents;
    }

    public interface OnDataLoadedCallback {
        void onDataLoaded(List<CalendarEvent> calendarEvents);
    }

}