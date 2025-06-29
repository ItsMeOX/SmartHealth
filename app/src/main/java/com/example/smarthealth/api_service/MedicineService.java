package com.example.smarthealth.api_service;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface MedicineService {
    @Multipart
    @POST("medicines/{userId}")
    public Call<MedicineDto> createMedicine(
            @Path("userId") Long userId,
            @Part("medicineDto") RequestBody medicineDto,
            @Part MultipartBody.Part imageFile);

    @GET("medicines/{userId}")
    public Call<List<MedicineDto>> getAllMedicinesByUser(
            @Path("userId") Long userId);

    @GET("medicines/medicine/{id}")
    public Call<MedicineDto> getMedicineById(@Path("id") Long id);

    @PUT("medicines/{id}")
    public Call<MedicineDto> updateMedicine(
            @Path("id") Long id,
            @Body MedicineDto medicineDto);

    // Build Delete Medicine REST API
    @DELETE("medicines/{id}")
    public Call<String> deleteMedicine(@Path("id") Long id);
}
