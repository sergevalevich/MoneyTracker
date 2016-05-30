package com.valevich.moneytracker.network.rest.requests;

import com.valevich.moneytracker.network.rest.model.UserGoogleInfoModel;
import com.valevich.moneytracker.network.rest.model.UserRegistrationModel;

import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by NotePad.by on 28.05.2016.
 */
public interface SubmitGoogleTokenApi {
    @POST("/gjson")
    UserGoogleInfoModel submitGoogleToken(@Query("google_token") String token);
}
