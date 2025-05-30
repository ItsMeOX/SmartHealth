package com.example.smarthealth.api_service;

import com.example.smarthealth.upcoming_schedule.schedule_types.ScheduleType;
import com.google.gson.annotations.JsonAdapter;

import java.util.Calendar;

public class UpcomingScheduleDto {
    private Long id;
    private String scheduleTitle;
    private String scheduleDescription;

    @JsonAdapter(CalendarSerializer.class)
    private Calendar scheduleCalendar;
    private String scheduleType;
    private boolean taken;
    private int intake;

    public UpcomingScheduleDto(String scheduleTitle, String scheduleDescription, Calendar scheduleCalendar, String scheduleType, boolean taken, Long medicineId, int intake) {
        this.scheduleTitle = scheduleTitle;
        this.scheduleDescription = scheduleDescription;
        this.scheduleCalendar = scheduleCalendar;
        this.scheduleType = scheduleType;
        this.taken = taken;
        this.medicineId = medicineId;
        this.intake = intake;
    }

    public Long getMedicineId() {
        return medicineId;
    }

    public void setMedicineId(Long medicineId) {
        this.medicineId = medicineId;
    }

    private Long medicineId;

    public void setScheduleCalendar(Calendar scheduleCalendar) {
        this.scheduleCalendar = scheduleCalendar;
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
        return scheduleCalendar;
    }

    public void setScheduleCalender(Calendar scheduleCalendar) {
        this.scheduleCalendar = scheduleCalendar;
    }

    public String getScheduleType() {
        return scheduleType;
    }

    public void setScheduleType(String scheduleType) {
        this.scheduleType = scheduleType;
    }

    public boolean getTaken() {
        return taken;
    }

    public void setTaken(boolean taken) {
        this.taken = taken;
    }

    public String getScheduleDescription() {
        return scheduleDescription;
    }

    public void setScheduleDescription(String scheduleDescription) {
        this.scheduleDescription = scheduleDescription;
    }
}
