package com.example.smarthealth.upcoming_schedule;

import java.util.List;

public interface UpcomingScheduleProvider {
    List<UpcomingSchedule> getTodaySchedules();
}
