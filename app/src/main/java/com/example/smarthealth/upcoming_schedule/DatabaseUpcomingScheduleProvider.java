package com.example.smarthealth.upcoming_schedule;

import com.example.smarthealth.upcoming_schedule.schedule_types.MealSchedule;
import com.example.smarthealth.upcoming_schedule.schedule_types.MedicineSchedule;
import com.example.smarthealth.upcoming_schedule.schedule_types.ScheduleType;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DatabaseUpcomingScheduleProvider implements UpcomingScheduleProvider {

    @Override
    public List<UpcomingSchedule> getTodaySchedules() {
        List<UpcomingSchedule> res = new ArrayList<>();
        // TODO: to be connected to db by Tristan
        UpcomingSchedule medicineSchedule1 = new UpcomingSchedule("Aspirin", (Calendar) Calendar.getInstance().clone(), new MedicineSchedule());
        UpcomingSchedule mealSchedule1 = new UpcomingSchedule("Lunch", (Calendar) Calendar.getInstance().clone(), new MealSchedule());
        UpcomingSchedule medicineSchedule2 = new UpcomingSchedule("No Spirit", (Calendar) Calendar.getInstance().clone(), new MedicineSchedule());

        res.add(medicineSchedule1);
        res.add(mealSchedule1);
        res.add(medicineSchedule2);

        return res;
    }
}
