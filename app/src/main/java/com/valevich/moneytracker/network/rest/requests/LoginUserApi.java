package com.valevich.moneytracker.network.rest.requests;

import com.valevich.moneytracker.network.rest.model.UserLoginModel;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

public interface LoginUserApi {
    @GET("/auth")
    void logIn(@Query("login") String login,
               @Query("password") String password,
               Callback<UserLoginModel> callback);
}
