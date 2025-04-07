package com.example.smarthealth.api_service;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

// Src: ChatGPT
public class CalendarDeserializer implements JsonDeserializer<Calendar> {
    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

    @Override
    public Calendar deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        try {
            String dateString = json.getAsString(); // Read as string
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
            Date date = sdf.parse(dateString);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            return calendar;
        } catch (Exception e) {
            throw new JsonParseException("Failed to parse date: " + json.getAsString(), e);
        }
    }
}