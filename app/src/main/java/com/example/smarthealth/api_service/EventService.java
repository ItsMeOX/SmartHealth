package com.example.smarthealth.api_service;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface EventService {
    @POST("events/{userId}")
    Call<EventDto> createEvent(@Path("userId") long userId, EventDto eventDto);

    @GET("events/{userId}/day/{year}/{month}/{day}")
    Call<List<EventDto>> getUserEventsByDay(
            @Path("userId") long userId,
            @Path("year") int year,
            @Path("month") int month,
            @Path("day") int day
    );

    @GET("events/{userId}/month/{year}/{month}")
    Call<List<EventDto>> getUsersEventsByMonth(
            @Path("userId") long userId,
            @Path("year") int year,
            @Path("month") int month
    );

    @GET("events/{eventId}")
    Call<EventDto> getEventById(
            @Path("eventId") long eventId
    );


    @PUT("events/{eventId}")
    Call<EventDto> updateEvent(
            @Path("eventId") long eventId,
            EventDto eventDto
    );

    @DELETE("events/{eventId}")
    Call<String> deleteEvent(
            @Path("eventId") long eventId
    );
}