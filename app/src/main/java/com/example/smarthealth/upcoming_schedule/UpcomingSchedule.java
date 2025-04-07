package com.example.smarthealth.upcoming_schedule;

import com.example.smarthealth.upcoming_schedule.schedule_types.ScheduleType;

import java.util.Calendar;

public class UpcomingSchedule {

    private final String scheduleTitle;
    private final String scheduleDescription;
    private final Calendar scheduleCalender;
    private final ScheduleType scheduleType;
    private final boolean isTaken;

    public UpcomingSchedule(String scheduleTitle, String scheduleDescription, Calendar scheduleCalendar, ScheduleType scheduleType, boolean isTaken) {
        this.scheduleTitle = scheduleTitle;
        this.scheduleDescription = scheduleDescription;
        this.scheduleCalender = (Calendar) scheduleCalendar.clone();
        this.scheduleType = scheduleType;
        this.isTaken = isTaken;
    }

    public boolean isTaken() {
        return isTaken;
    }

    public String getScheduleTitle() {
        return scheduleTitle;
    }

    public Calendar getScheduleCalender() {
        return (Calendar) scheduleCalender.clone();
    }

    public ScheduleType getScheduleType() {
        return scheduleType;
    }

    public String getScheduleDescription() {
        return scheduleDescription;
    }
}
