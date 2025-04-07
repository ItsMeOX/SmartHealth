package com.example.smarthealth.api_service;

import com.example.smarthealth.upcoming_schedule.schedule_types.ScheduleType;
import com.google.gson.annotations.JsonAdapter;

import java.util.Calendar;

public class UpcomingScheduleDto {
    private Long id;
    private String scheduleTitle;
    private String scheduleDescription;
    @JsonAdapter(CalendarDeserializer.class)
    private Calendar scheduleCalender;
    private String scheduleType;
    private boolean isTaken;

    public UpcomingScheduleDto(String scheduleTitle, String scheduleDescription, boolean isTaken, Calendar scheduleCalendar, String scheduleType) {
        this.scheduleTitle = scheduleTitle;
        this.scheduleDescription = scheduleDescription;
        this.scheduleCalender = scheduleCalendar;
        this.scheduleType = scheduleType;
        this.isTaken = isTaken;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getScheduleTitle() {
        return scheduleTitle;
    }

    public void setScheduleTitle(String scheduleTitle) {
        this.scheduleTitle = scheduleTitle;
    }

    public Calendar getScheduleCalendar() {
        return scheduleCalender;
    }

    public void setScheduleCalender(Calendar scheduleCalendar) {
        this.scheduleCalender = scheduleCalendar;
    }

    public String getScheduleType() {
        return scheduleType;
    }

    public void setScheduleType(String scheduleType) {
        this.scheduleType = scheduleType;
    }

    public boolean isTaken() {
        return isTaken;
    }

    public void setTaken(boolean taken) {
        isTaken = taken;
    }

    public String getScheduleDescription() {
        return scheduleDescription;
    }

    public void setScheduleDescription(String scheduleDescription) {
        this.scheduleDescription = scheduleDescription;
    }
}
