package com.valevich.moneytracker.network.rest.requests;

import com.valevich.moneytracker.network.rest.model.UserRegistrationModel;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

public interface RegisterUserApi {
    @GET("/auth")
    void registerUser(@Query("login") String login,
                      @Query("password") String password,
                      @Query("register") String registrationFlag,
                      Callback<UserRegistrationModel> callback);
}
