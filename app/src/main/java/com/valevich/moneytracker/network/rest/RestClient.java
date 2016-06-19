package com.valevich.moneytracker.network.rest;

import com.valevich.moneytracker.network.rest.requests.AddCategoryApi;
import com.valevich.moneytracker.network.rest.requests.AddExpenseApi;
import com.valevich.moneytracker.network.rest.requests.FetchGlobalCategoriesDataApi;
import com.valevich.moneytracker.network.rest.requests.LoginUserApi;
import com.valevich.moneytracker.network.rest.requests.LogoutUserApi;
import com.valevich.moneytracker.network.rest.requests.RegisterUserApi;
import com.valevich.moneytracker.network.rest.requests.RemoveCategoryApi;
import com.valevich.moneytracker.network.rest.requests.SubmitGoogleTokenApi;
import com.valevich.moneytracker.network.rest.requests.SyncCategoriesApi;
import com.valevich.moneytracker.network.rest.requests.SyncExpensesApi;

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
    private SyncExpensesApi syncExpensesApi;
    private SyncCategoriesApi syncCategoriesApi;
    private LogoutUserApi logoutUserApi;
    private FetchGlobalCategoriesDataApi fetchGlobalCategoriesDataApi;
    private AddExpenseApi addExpenseApi;
    private RemoveCategoryApi removeCategoryApi;
    private AddCategoryApi addCategoryApi;

    public RestClient() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(BASE_URL)
                .setLogLevel(RestAdapter.LogLevel.FULL)//logging request details
                .build();
        registerUserApi = restAdapter.create(RegisterUserApi.class);
        loginUserApi = restAdapter.create(LoginUserApi.class);
        submitGoogleTokenApi = restAdapter.create(SubmitGoogleTokenApi.class);
        syncExpensesApi = restAdapter.create(SyncExpensesApi.class);
        syncCategoriesApi = restAdapter.create(SyncCategoriesApi.class);
        logoutUserApi = restAdapter.create(LogoutUserApi.class);
        fetchGlobalCategoriesDataApi = restAdapter.create(FetchGlobalCategoriesDataApi.class);
        addExpenseApi = restAdapter.create(AddExpenseApi.class);
        removeCategoryApi = restAdapter.create(RemoveCategoryApi.class);
        addCategoryApi = restAdapter.create(AddCategoryApi.class);
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

    public SyncExpensesApi getSyncExpensesApi() {
        return syncExpensesApi;
    }

    public SyncCategoriesApi getSyncCategoriesApi() {
        return syncCategoriesApi;
    }

    public LogoutUserApi getLogoutUserApi() {return logoutUserApi;}

    public FetchGlobalCategoriesDataApi getFetchGlobalCategoriesDataApi() {return fetchGlobalCategoriesDataApi;}

    public AddExpenseApi getAddExpenseApi() {
        return addExpenseApi;
    }

    public RemoveCategoryApi getRemoveCategoryApi() {
        return removeCategoryApi;
    }

    public AddCategoryApi getAddCategoryApi() {
        return addCategoryApi;
    }
}
