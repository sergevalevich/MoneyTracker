package com.valevich.moneytracker.network.rest.requests;


import com.valevich.moneytracker.network.rest.model.UserLogoutModel;

import retrofit.Callback;
import retrofit.http.GET;


public interface LogoutUserApi {
    @GET("/logout")
    void logOut(Callback<UserLogoutModel> callback);
}
