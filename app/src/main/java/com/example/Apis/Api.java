package com.example.Apis;

import com.example.Models.TimeData;

import retrofit2.Call;
import retrofit2.http.GET;

public interface Api {
    @GET("api/Time/current/zone?timeZone=Asia/Karachi")
    Call<TimeData> getNewTime();
}
