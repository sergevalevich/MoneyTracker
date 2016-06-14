package com.valevich.moneytracker.network.rest;

import com.valevich.moneytracker.model.Expense;
import com.valevich.moneytracker.network.rest.model.ExpenseData;
import com.valevich.moneytracker.network.rest.model.ExpensesSyncModel;
import com.valevich.moneytracker.network.rest.model.UserGoogleInfoModel;
import com.valevich.moneytracker.network.rest.model.UserLoginModel;
import com.valevich.moneytracker.network.rest.model.UserRegistrationModel;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by NotePad.by on 20.05.2016.
 */
@EBean
public class RestService {
    private static final String REGISTER_FLAG = "1";

    @Bean
    RestClient restClient;

    public UserRegistrationModel register(String login, String password) {
        return restClient.getRegisterUserApi().registerUser(login, password, REGISTER_FLAG);
    }
    public UserLoginModel logIn (String login, String password) {
        return restClient.getLoginUserApi().logIn(login, password);
    }

    public UserGoogleInfoModel getGoogleInfo(String token) {
        return restClient.getSubmitGoogleTokenApi().submitGoogleToken(token);
    }

    public ExpensesSyncModel syncExpenses(String expenses, String token,String googleToken) {
        return restClient.getSyncExpensesApi().syncExpenses(expenses, token, googleToken);
    }

    public void setRestClient(RestClient restClient) {
        this.restClient = restClient;
    }

}
