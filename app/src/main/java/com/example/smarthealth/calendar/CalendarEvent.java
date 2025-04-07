package com.example.smarthealth.calendar;

import android.util.Pair;

import java.util.Calendar;

public class CalendarEvent {
    private String eventTitle;
    private String eventDescription;
    private Calendar eventStartCalendar;
    private Calendar eventEndCalendar;
    private String type;
    private boolean isTaken;

    public CalendarEvent(String eventTitle, String eventDescription, Calendar eventStartCalendar, Calendar eventEndCalendar) {
        this.eventTitle = eventTitle;
        this.eventDescription = eventDescription;
        this.eventStartCalendar = (Calendar) eventStartCalendar.clone();
        this.eventEndCalendar = (Calendar) eventEndCalendar.clone();
    }

    public Calendar getEventEndCalendar() {
        return eventEndCalendar;
    }

    public Calendar getEventStartCalendar() {
        return eventStartCalendar;
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

    public Pair<Calendar, Calendar> getEventDateCalendar() {
        return new Pair<>((Calendar) eventStartCalendar.clone(), (Calendar) eventEndCalendar);
    }

    public void setEventStartCalendar(Calendar eventDateCalendar) {
        eventStartCalendar = eventDateCalendar;
    }

    public void setEventEndCalendar(Calendar eventDateCalendar) {
        eventEndCalendar = eventDateCalendar;
    }
}