package com.example.smarthealth.calendar;

import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalendarEventCache {
    private final Map<String, List<CalendarEvent>> eventsByDate = new HashMap<>();
    private boolean isLoaded = false;

    public interface OnMonthLoadedCallback {
        void onMonthlyEventsLoaded();
    }

    public void loadEventsForMonth(long userId, Calendar month, CalendarEventProvider provider, OnMonthLoadedCallback callback) {
        Log.d("debug", "loadEventFOrMonths" + isLoaded);
        if (isLoaded)
            return;

        provider.getEventsForMonth(userId, month, calendarEvents -> {
            for (CalendarEvent event : calendarEvents) {
                String key = CalendarUtil.calendarToDateString(event.getEventDateCalendar().first);
                eventsByDate.computeIfAbsent(key, k -> new ArrayList<>()).add(event);
            }
            isLoaded = true;
            callback.onMonthlyEventsLoaded();
        });

    }

    public void loadEventForDay(long userId, Calendar day, CalendarEventProvider provider) {
        String key = CalendarUtil.calendarToDateString(day);
        if (eventsByDate.containsKey(key)) {
            eventsByDate.get(key).clear();
        }
        provider.getEventsForDay(userId, day, calendarEvents -> {
            for (CalendarEvent event : calendarEvents) {
                eventsByDate.computeIfAbsent(key, k -> new ArrayList<>()).add(event);
            }
        });
    }

    public List<CalendarEvent> getEventsForDay(long userId, Calendar calendar) {
        String key = CalendarUtil.calendarToDateString(calendar);
        return eventsByDate.getOrDefault(key, new ArrayList<>());
    }

    public boolean hasEvent(Calendar date) {
        return eventsByDate.containsKey(CalendarUtil.calendarToDateString(date));
    }
}