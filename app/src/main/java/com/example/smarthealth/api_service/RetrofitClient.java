package com.example.smarthealth.api_service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Calendar;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit;
    private static final String BASE_URL = "https://smarthealth-backend.onrender.com/";

    public static Retrofit getInstance() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Calendar.class, new CalendarSerializer())
                .registerTypeAdapter(Calendar.class, new CalendarDeserializer())
                .create();

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }
}
