package com.example.smarthealth.upcoming_schedule;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SampleUpcomingScheduleProvider implements UpcomingScheduleProvider {

    @Override
    public List<UpcomingSchedule> getTodaySchedules() {
        List<UpcomingSchedule> res = new ArrayList<>();
        res.add(new UpcomingSchedule("Aspirin", (Calendar) Calendar.getInstance().clone(), UpcomingSchedule.ScheduleType.MEDICINE));
        res.add(new UpcomingSchedule("Lunch", (Calendar) Calendar.getInstance().clone(), UpcomingSchedule.ScheduleType.MEAL));
        res.add(new UpcomingSchedule("No Spirit", (Calendar) Calendar.getInstance().clone(), UpcomingSchedule.ScheduleType.MEDICINE));

        return res;
    }
}
