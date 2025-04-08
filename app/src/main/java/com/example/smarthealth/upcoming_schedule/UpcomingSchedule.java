package com.example.smarthealth.upcoming_schedule;

import com.example.smarthealth.upcoming_schedule.schedule_types.ScheduleType;

import java.util.Calendar;

public class UpcomingSchedule {
    private final Long id;
    private final String scheduleTitle;
    private final String scheduleDescription;
    private final Calendar scheduleCalendar;
    private final ScheduleType scheduleType;
    private final boolean taken;

    public UpcomingSchedule(Long id, String scheduleTitle, String scheduleDescription, boolean taken, Calendar scheduleCalendar, ScheduleType scheduleType) {
        this.id = id;
        this.scheduleTitle = scheduleTitle;
        this.scheduleDescription = scheduleDescription;
        this.scheduleCalendar = (Calendar) scheduleCalendar.clone();
        this.scheduleType = scheduleType;
        this.taken = taken;
    }

    public Long getId() {
        return id;
    }

    public boolean getTaken() {
        return taken;
    }

    public String getScheduleTitle() {
        return scheduleTitle;
    }

    public Calendar getScheduleCalendar() {
        return (Calendar) scheduleCalendar.clone();
    }

    public ScheduleType getScheduleType() {
        return scheduleType;
    }

    public String getScheduleDescription() {
        return scheduleDescription;
    }
}
