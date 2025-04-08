package com.example.smarthealth.api_service;

import com.example.smarthealth.upcoming_schedule.UpcomingSchedule;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UpcomingScheduleService {
    @POST("schedules/{userId}")
    Call<UpcomingScheduleDto> createSchedule(@Path("userId") long userId, @Body UpcomingScheduleDto upcomingScheduleDto);

    @GET("schedules/{userId}/day/{year}/{month}/{day}")
    Call<List<UpcomingScheduleDto>> getSchedulesByDay(
            @Path("userId") long userId,
            @Path("year") int year,
            @Path("month") int month,
            @Path("day") int day
    );

    @GET("schedules/{scheduleId}")
    Call<UpcomingScheduleDto> getScheduleById(
            @Path("scheduleId") long scheduleId
    );

    @DELETE("schedules/{scheduleId}")
    Call<String> deleteSchedule(
            @Path("scheduleId") long scheduleId
    );
}
