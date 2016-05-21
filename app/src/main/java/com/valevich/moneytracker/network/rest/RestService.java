package com.valevich.moneytracker.network.rest;

import com.valevich.moneytracker.network.rest.model.UserLoginModel;
import com.valevich.moneytracker.network.rest.model.UserRegistrationModel;

/**
 * Created by NotePad.by on 20.05.2016.
 */
public class RestService {
    private static final String REGISTER_FLAG = "1";
    private RestClient restClient;
    public RestService() {
        restClient = new RestClient();
    }
    public UserRegistrationModel register(String login, String password) {
        return restClient.getRegisterUserApi().registerUser(login, password, REGISTER_FLAG);
    }
    public UserLoginModel logIn (String login, String password) {
        return restClient.getLoginUserApi().logIn(login, password);
    }
}
