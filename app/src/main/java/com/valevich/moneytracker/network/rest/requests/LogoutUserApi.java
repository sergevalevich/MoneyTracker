package com.valevich.moneytracker.network.rest.requests;


import com.valevich.moneytracker.network.rest.model.UserLogoutModel;

import retrofit.http.GET;


/**
 * Created by User on 16.06.2016.
 */
public interface LogoutUserApi {
    @GET("/logout")
    UserLogoutModel logOut();
}
