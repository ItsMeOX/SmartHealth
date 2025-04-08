package com.example.smarthealth.api_service;


import android.util.Log;

import com.google.gson.annotations.JsonAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class EventDto {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
    private Long id;
    private String eventTitle;
    private String eventDescription;
    @JsonAdapter(CalendarSerializer.class)  // for serialization
    private Calendar eventStartCalendar;

    @JsonAdapter(CalendarSerializer.class)  // for serialization
    private Calendar eventEndCalendar;
    private Long userId;

    public EventDto(String eventTitle, String eventDescription, Calendar eventStartCalendar, Calendar eventEndCalendar) {
        this.eventTitle = eventTitle;
        this.eventDescription = eventDescription;
        this.eventStartCalendar = (Calendar) eventStartCalendar.clone();
        this.eventEndCalendar = (Calendar) eventEndCalendar.clone();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public Calendar getEventStartCalendar() {
        return eventStartCalendar;
    }

    public void setEventStartCalendar(Calendar eventStartCalendar) {
        this.eventStartCalendar = eventStartCalendar;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Calendar getEventEndCalendar() {
        return eventEndCalendar;
    }

    public void setEventEndCalendar(Calendar eventEndCalendar) {
        this.eventEndCalendar = eventEndCalendar;
    }
}