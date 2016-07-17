package com.valevich.moneytracker.network.rest;

import com.valevich.moneytracker.network.rest.requests.AddCategoryApi;
import com.valevich.moneytracker.network.rest.requests.AddExpenseApi;
import com.valevich.moneytracker.network.rest.requests.EditCategoryApi;
import com.valevich.moneytracker.network.rest.requests.FetchGlobalCategoriesDataApi;
import com.valevich.moneytracker.network.rest.requests.LoginUserApi;
import com.valevich.moneytracker.network.rest.requests.LogoutUserApi;
import com.valevich.moneytracker.network.rest.requests.RegisterUserApi;
import com.valevich.moneytracker.network.rest.requests.RemoveCategoryApi;
import com.valevich.moneytracker.network.rest.requests.SubmitGoogleTokenApi;
import com.valevich.moneytracker.network.rest.requests.SyncCategoriesApi;
import com.valevich.moneytracker.network.rest.requests.SyncExpensesApi;
import com.valevich.moneytracker.utils.errorHandlers.CustomRestErrorHandler;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import retrofit.RestAdapter;

/**
 * Created by NotePad.by on 20.05.2016.
 */
@EBean
public class RestClient {
    private static final String BASE_URL = "http://lmt.loftblog.tmweb.ru";
    private RegisterUserApi mRegisterUserApi;
    private LoginUserApi mLoginUserApi;
    private SubmitGoogleTokenApi mSubmitGoogleTokenApi;
    private SyncExpensesApi mSyncExpensesApi;
    private SyncCategoriesApi mSyncCategoriesApi;
    private LogoutUserApi mLogoutUserApi;
    private FetchGlobalCategoriesDataApi mFetchGlobalCategoriesDataApi;
    private AddExpenseApi mAddExpenseApi;
    private RemoveCategoryApi mRemoveCategoryApi;
    private AddCategoryApi mAddCategoryApi;
    private EditCategoryApi mEditCategoryApi;

    @Bean
    CustomRestErrorHandler mCustomRestErrorHandler;

    @AfterInject
    void setUpRestClient() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(BASE_URL)
                .setErrorHandler(mCustomRestErrorHandler)
                .setLogLevel(RestAdapter.LogLevel.FULL)//logging request details
                .build();
        mRegisterUserApi = restAdapter.create(RegisterUserApi.class);
        mLoginUserApi = restAdapter.create(LoginUserApi.class);
        mSubmitGoogleTokenApi = restAdapter.create(SubmitGoogleTokenApi.class);
        mSyncExpensesApi = restAdapter.create(SyncExpensesApi.class);
        mSyncCategoriesApi = restAdapter.create(SyncCategoriesApi.class);
        mLogoutUserApi = restAdapter.create(LogoutUserApi.class);
        mFetchGlobalCategoriesDataApi = restAdapter.create(FetchGlobalCategoriesDataApi.class);
        mAddExpenseApi = restAdapter.create(AddExpenseApi.class);
        mRemoveCategoryApi = restAdapter.create(RemoveCategoryApi.class);
        mAddCategoryApi = restAdapter.create(AddCategoryApi.class);
        mEditCategoryApi = restAdapter.create(EditCategoryApi.class);
    }

    public RegisterUserApi getRegisterUserApi() {
        return mRegisterUserApi;
    }

    public LoginUserApi getLoginUserApi() {
        return mLoginUserApi;
    }

    public SubmitGoogleTokenApi getSubmitGoogleTokenApi() {
        return mSubmitGoogleTokenApi;
    }

    public SyncExpensesApi getSyncExpensesApi() {
        return mSyncExpensesApi;
    }

    public SyncCategoriesApi getSyncCategoriesApi() {
        return mSyncCategoriesApi;
    }

    public LogoutUserApi getLogoutUserApi() {
        return mLogoutUserApi;
    }

    public FetchGlobalCategoriesDataApi getFetchGlobalCategoriesDataApi() {
        return mFetchGlobalCategoriesDataApi;
    }

    public AddExpenseApi getAddExpenseApi() {
        return mAddExpenseApi;
    }

    public RemoveCategoryApi getRemoveCategoryApi() {
        return mRemoveCategoryApi;
    }

    public AddCategoryApi getAddCategoryApi() {
        return mAddCategoryApi;
    }

    public EditCategoryApi getEditCategoryApi() {
        return mEditCategoryApi;
    }
}
