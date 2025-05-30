package com.example.smarthealth.calendar.QueryStrategy;

import com.example.smarthealth.api_service.EventDto;
import com.example.smarthealth.api_service.EventService;

import java.util.Calendar;
import java.util.List;

import retrofit2.Call;

public class DayQueryStrategy implements CalendarQueryStrategy {
    @Override
    public Call<List<EventDto>> createCall(EventService eventService, long userId, Calendar date) {
        return eventService.getUserEventsByDay (
                userId,
                date.get(Calendar.YEAR),
                date.get(Calendar.MONTH) + 1,
                date.get(Calendar.DAY_OF_MONTH)
        );
    }
}
