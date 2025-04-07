package com.example.smarthealth.api_service;

import com.example.smarthealth.upcoming_schedule.schedule_types.ScheduleType;

import java.util.Calendar;

public class UpcomingScheduleDto {
    private String scheduleTitle;
    private String scheduleDescription;
    private Calendar scheduleCalender;
    private ScheduleType scheduleType;
    private boolean isTaken;

    public UpcomingScheduleDto(String scheduleTitle, String scheduleDescription, boolean isTaken, Calendar scheduleCalender, ScheduleType scheduleType) {
        this.scheduleTitle = scheduleTitle;
        this.scheduleDescription = scheduleDescription;
        this.isTaken = isTaken;
        this.scheduleCalender = scheduleCalender;
        this.scheduleType = scheduleType;
    }

    public String getScheduleTitle() {
        return scheduleTitle;
    }

    public void setScheduleTitle(String scheduleTitle) {
        this.scheduleTitle = scheduleTitle;
    }

    public Calendar getScheduleCalender() {
        return scheduleCalender;
    }

    public void setScheduleCalender(Calendar scheduleCalender) {
        this.scheduleCalender = scheduleCalender;
    }

    public ScheduleType getScheduleType() {
        return scheduleType;
    }

    public void setScheduleType(ScheduleType scheduleType) {
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
