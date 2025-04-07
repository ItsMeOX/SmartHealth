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
    private boolean isTaken;

    public MedicineDto getMedicineDto() {
        return medicineDto;
    }

    public void setMedicineDto(MedicineDto medicineDto) {
        this.medicineDto = medicineDto;
    }

    public void setScheduleCalendar(Calendar scheduleCalendar) {
        this.scheduleCalendar = scheduleCalendar;
    }

    private MedicineDto medicineDto;

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
