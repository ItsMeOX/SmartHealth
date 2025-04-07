package com.example.smarthealth.api_service;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class CalendarSerializer implements JsonSerializer<Calendar> {
    @Override
    public JsonElement serialize(Calendar src, Type typeOfSrc, JsonSerializationContext context) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return new JsonPrimitive(sdf.format(src.getTime()));
    }
}
