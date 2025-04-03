package com.example.smarthealth.calendar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CalendarUtil {
    public static Date stringToDate(String dateString, String dateFormat) throws ParseException {
        dateString = dateString.trim();
        SimpleDateFormat dateFormatter = new SimpleDateFormat(dateFormat, Locale.getDefault());
        return dateFormatter.parse(dateString);
    }

    public static Calendar stringToCalendar(String dateString, String dateFormat) throws ParseException {
        dateString = dateString.trim();
        Date date = stringToDate(dateString, dateFormat);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }
}
