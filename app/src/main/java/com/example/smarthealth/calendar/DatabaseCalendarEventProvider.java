package com.example.smarthealth.calendar;

import android.content.Context;
import android.util.Log;

import com.example.smarthealth.api_service.EventDto;
import com.example.smarthealth.api_service.EventService;
import com.example.smarthealth.api_service.RetrofitClient;
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
        return queryDatabaseCalendar(userId, date, callback);
    }

    @Override
    public List<CalendarEvent> getEventsForMonth(long userId, Calendar date, OnDataLoadedCallback callback) {
        Log.d("debug", "geEventsForMonth");
        List<CalendarEvent> calendarEvents = new ArrayList<>();
        Call<List<EventDto>> call = eventService.getUsersEventsByMonth(userId, 2025, 4);
        call.enqueue(new Callback<List<EventDto>>() {
            @Override
            public void onResponse(Call<List<EventDto>> call, Response<List<EventDto>> response) {
                if(response.body() != null){
                    for(EventDto event : response.body()){
                        CalendarEvent calendarEvent = new CalendarEvent(
                                event.getEventTitle(),
                                event.getEventDescription(),
                                event.getEventStartCalendar(), (Calendar) event.getEventEndCalendar());
                        calendarEvents.add(calendarEvent);
                    }
                }
                Log.d("debug", calendarEvents+"");
                callback.onDataLoaded(calendarEvents);
            }
            @Override
            public void onFailure(Call<List<EventDto>> call, Throwable t) {
                Log.d("debug", t.getMessage());
            }
        });

        return calendarEvents;
    }

    private List<CalendarEvent> queryDatabaseCalendar(long userId, Calendar date, OnDataLoadedCallback callback) {
        List<CalendarEvent> calendarEvents = new ArrayList<>();
        Call<List<EventDto>> call = eventService.getUserEventsByDay(userId, 2025, 4, 3);
        call.enqueue(new Callback<List<EventDto>>() {
            @Override
            public void onResponse(Call<List<EventDto>> call, Response<List<EventDto>> response) {
                if(response.body() != null){
                    for(EventDto event : response.body()){
                        CalendarEvent calendarEvent = new CalendarEvent(
                                event.getEventTitle(),
                                event.getEventDescription(),
                                event.getEventStartCalendar(), (Calendar) event.getEventEndCalendar());
                        calendarEvents.add(calendarEvent);
                    }
                }
                callback.onDataLoaded(calendarEvents);
            }
            @Override
            public void onFailure(Call<List<EventDto>> call, Throwable t) {
                Log.d("debug", "failed!" + " " + t.getMessage());
            }
        });

        return calendarEvents;
    }

    public interface OnDataLoadedCallback {
        void onDataLoaded(List<CalendarEvent> calendarEvents);
    }

}