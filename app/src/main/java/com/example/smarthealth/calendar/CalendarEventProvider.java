package com.example.smarthealth.calendar;

import java.util.Calendar;
import java.util.List;

public interface CalendarEventProvider {
    List<CalendarEvent> getEventsForDay(Calendar date, DatabaseCalendarEventProvider.OnDataLoadedCallback callback);
}
