package com.valevich.moneytracker.network.rest;

import com.valevich.moneytracker.network.rest.requests.LoginUserApi;
import com.valevich.moneytracker.network.rest.requests.RegisterUserApi;
import com.valevich.moneytracker.network.rest.requests.SubmitGoogleTokenApi;

import org.androidannotations.annotations.EBean;

import retrofit.RestAdapter;

/**
 * Created by NotePad.by on 20.05.2016.
 */
@EBean
public class RestClient {
    private static final String BASE_URL = "http://lmt.loftblog.tmweb.ru";
    private RegisterUserApi registerUserApi;
    private LoginUserApi loginUserApi;
    private SubmitGoogleTokenApi submitGoogleTokenApi;
    public RestClient() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(BASE_URL)
                .setLogLevel(RestAdapter.LogLevel.FULL)//logging request details
                .build();
        registerUserApi = restAdapter.create(RegisterUserApi.class);
        loginUserApi = restAdapter.create(LoginUserApi.class);
        submitGoogleTokenApi = restAdapter.create(SubmitGoogleTokenApi.class);
    }
    public RegisterUserApi getRegisterUserApi() {
        return registerUserApi;
    }

    public LoginUserApi getLoginUserApi() {
        return loginUserApi;
    }

    public SubmitGoogleTokenApi getSubmitGoogleTokenApi() {
        return submitGoogleTokenApi;
    }
}
