package com.example.smarthealth.calendar;

import android.content.Context;
import android.util.Log;

import com.example.smarthealth.api_service.EventDto;
import com.example.smarthealth.api_service.EventService;
import com.example.smarthealth.api_service.RetrofitClient;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DatabaseCalendarEventProvider implements CalendarEventProvider {
    private final Context context;
    private EventService eventService;

    public DatabaseCalendarEventProvider(Context context) {
        this.context = context;
        eventService = RetrofitClient.getInstance().create(EventService.class);
    }

    @Override
    public boolean hasEvent(Calendar date) {
        return !getEventsForDay(date).isEmpty();
    }

    @Override
    public List<CalendarEvent> getEventsForDay(Calendar date) {
        return queryDatabaseCalendar(date);
    }

    private List<CalendarEvent> queryDatabaseCalendar(Calendar date) {
        List<CalendarEvent> calendarEvents = new ArrayList<>();
        Call<List<EventDto>> call = eventService.getUserEvents(16);
        call.enqueue(new Callback<List<EventDto>>() {
            @Override
            public void onResponse(Call<List<EventDto>> call, Response<List<EventDto>> response) {
                if(response.body() != null){
                    Log.d("EventCall", "Successful");
                    for(EventDto event : response.body()){
                        Log.d("EventCall", "UserID: " + event.getUserId());
                        CalendarEvent calendarEvent = new CalendarEvent(
                                event.getEventTitle(),
                                event.getEventDescription(),
                                event.getEventStartCalendar(), (Calendar) event.getEventEndCalendar());
                        calendarEvents.add(calendarEvent);
                    }
                }
            }
            @Override
            public void onFailure(Call<List<EventDto>> call, Throwable t) {

            }
        });
        Log.d("EventCall", "Context: " + calendarEvents.size());
        return calendarEvents;
    }

}
