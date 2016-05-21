package com.valevich.moneytracker.network.rest;

import com.valevich.moneytracker.network.rest.requests.LoginUserApi;
import com.valevich.moneytracker.network.rest.requests.RegisterUserApi;

import retrofit.RestAdapter;

/**
 * Created by NotePad.by on 20.05.2016.
 */
public class RestClient {
    private static final String BASE_URL = "http://lmt.loftblog.tmweb.ru";
    private RegisterUserApi registerUserApi;
    private LoginUserApi loginUserApi;
    public RestClient() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(BASE_URL)
                .setLogLevel(RestAdapter.LogLevel.FULL)//logging request details
                .build();
        registerUserApi = restAdapter.create(RegisterUserApi.class);
        loginUserApi = restAdapter.create(LoginUserApi.class);
    }
    public RegisterUserApi getRegisterUserApi() {
        return registerUserApi;
    }

    public LoginUserApi getLoginUserApi() {
        return loginUserApi;
    }
}
