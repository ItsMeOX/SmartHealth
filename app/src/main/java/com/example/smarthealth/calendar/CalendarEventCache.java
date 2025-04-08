package com.example.smarthealth.calendar;

import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalendarEventCache {
    private final Map<String, List<CalendarEvent>> eventsByDate = new HashMap<>();

    public interface OnDataLoadedCallback {
        void onCalendarEventsLoaded();
    }
    public void loadEventsForMonth(long userId, Calendar month, CalendarEventProvider provider, OnDataLoadedCallback callback) {
        provider.getEventsForMonth(userId, month, calendarEvents -> {
            for (CalendarEvent event : calendarEvents) {
                String key = CalendarUtil.calendarToDateString(event.getEventStartCalendar());
                eventsByDate.computeIfAbsent(key, k -> new ArrayList<>()).add(event);
            }
            callback.onCalendarEventsLoaded();
        });
    }

    public void loadEventForDay(long userId, Calendar day, CalendarEventProvider provider, OnDataLoadedCallback callback) {
        String key = CalendarUtil.calendarToDateString(day);
        if (eventsByDate.containsKey(key)) {
            eventsByDate.get(key).clear();
        }
        Log.d("debug", "getEventsForDaya called with " + day);
        provider.getEventsForDay(userId, day, calendarEvents -> {
            for (CalendarEvent event : calendarEvents) {
                Log.d("debug", "event "+event);
                String currKey = CalendarUtil.calendarToDateString(event.getEventStartCalendar());
                eventsByDate.computeIfAbsent(currKey, k -> new ArrayList<>()).add(event);
                Log.d("debug", "currKey " + currKey);
                Log.d("debug", "dict " + eventsByDate.get(currKey));
            }
            callback.onCalendarEventsLoaded();
        });
    }

    public List<CalendarEvent> getEventsForDay(Calendar calendar) {
        String key = CalendarUtil.calendarToDateString(calendar);

        Log.d("debug", "currKey for day" + key);
        Log.d("debug", "dict for day" + eventsByDate.get(key));

        return eventsByDate.getOrDefault(key, new ArrayList<>());
    }

    public boolean hasEvent(Calendar date) {
        return eventsByDate.containsKey(CalendarUtil.calendarToDateString(date));
    }
}