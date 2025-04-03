//package com.example.smarthealth.calendar;
//
//import android.content.Context;
//import android.util.Pair;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Calendar;
//import java.util.List;
//
//public class DatabaseCalendarEventProvider implements CalendarEventProvider {
//    private final Context context;
//
//    public DatabaseCalendarEventProvider(Context context) {
//        this.context = context;
//    }
//
//    @Override
//    public boolean hasEvent(Calendar date) {
//        return !getEventsForDay(date).isEmpty();
//    }
//
//    @Override
//    public List<CalendarEvent> getEventsForDay(Calendar date) {
//        return queryDatabaseCalendar(date);
//    }
//
//    private List<CalendarEvent> queryDatabaseCalendar(Calendar date) {
//        // TODO: Link with database with Tristan
//        CalendarEvent calendarEvent = new CalendarEvent(
//                "Title",
//                "this is a long description.",
//                (Calendar) Calendar.getInstance().clone(),
//                (Calendar) Calendar.getInstance().clone()
//        );
//        return new ArrayList<>(Arrays.asList(calendarEvent, calendarEvent, calendarEvent, calendarEvent, calendarEvent));
//    }
//
//}


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
    private final EventService eventService;

    public DatabaseCalendarEventProvider(Context context) {
        this.context = context;
        this.eventService = RetrofitClient.getInstance().create(EventService.class);
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
                for(EventDto event : response.body()){
                    CalendarEvent calendarEvent = new CalendarEvent(event.getEventTitle(), event.getEventDescription(), (Calendar) event.getEventStartCalendar(), (Calendar) event.getEventEndCalendar());
                    calendarEvents.add(calendarEvent);
                }
            }

            @Override
            public void onFailure(Call<List<EventDto>> call, Throwable t) {
                CalendarEvent calendarEvent = new CalendarEvent("Title", "this is a long description.", (Calendar) Calendar.getInstance().clone(), (Calendar) Calendar.getInstance().clone());
                Log.d("EventCall", "Context: " + this);
            }
        });
        return calendarEvents;
    }

}

