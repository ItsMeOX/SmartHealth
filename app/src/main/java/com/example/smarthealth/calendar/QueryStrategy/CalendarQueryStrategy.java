package com.example.smarthealth.calendar.QueryStrategy;


import com.example.smarthealth.api_service.EventDto;
import com.example.smarthealth.api_service.EventService;

import java.util.Calendar;
import java.util.List;

import retrofit2.Call;

public interface CalendarQueryStrategy {
    Call<List<EventDto>> createCall(EventService eventService, long userId, Calendar date);
}
