package com.example.smarthealth.calendar;

import java.util.Calendar;
import java.util.List;

public interface CalendarEventProvider {
    List<CalendarEvent> getEventsForDay(long userId, Calendar date, DatabaseCalendarEventProvider.OnDataLoadedCallback callback);
    List<CalendarEvent> getEventsForMonth(long userId, Calendar date, DatabaseCalendarEventProvider.OnDataLoadedCallback callback);
}