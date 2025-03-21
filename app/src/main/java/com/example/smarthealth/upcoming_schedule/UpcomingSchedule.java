package com.example.smarthealth.upcoming_schedule;

import java.util.Calendar;

public class UpcomingSchedule {

    public enum ScheduleType {
        MEDICINE,
        MEAL
    }

    private String scheduleTitle;
    private Calendar scheduleCalender;
    private ScheduleType scheduleType;

    public UpcomingSchedule(String scheduleTitle, Calendar scheduleCalendar, ScheduleType scheduleType) {
        this.scheduleTitle = scheduleTitle;
        this.scheduleCalender = scheduleCalendar;
        this.scheduleType = scheduleType;
    }

    public String getScheduleTitle() {
        return scheduleTitle;
    }

    public Calendar getScheduleCalender() {
        return scheduleCalender;
    }

    public ScheduleType getScheduleType() {
        return scheduleType;
    }

}
