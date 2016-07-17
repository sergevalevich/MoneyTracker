package com.valevich.moneytracker.network.rest.requests;

import com.valevich.moneytracker.network.rest.model.UserGoogleInfoModel;

import retrofit.Callback;
import retrofit.http.POST;
import retrofit.http.Query;

public interface SubmitGoogleTokenApi {
    @POST("/gjson")
    void submitGoogleToken(@Query("google_token") String token,
                           Callback<UserGoogleInfoModel> callback);
}
