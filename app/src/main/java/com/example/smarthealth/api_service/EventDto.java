package com.example.smarthealth.api_service;


import java.util.Calendar;

public class EventDto {
    private Long id;
    private String eventTitle;
    private String eventDescription;
    private Calendar eventStartCalendar;
    private Calendar eventEndCalendar;
    private Long userId;

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

