package com.valevich.moneytracker.network.rest.requests;

import com.valevich.moneytracker.model.Expense;
import com.valevich.moneytracker.network.rest.model.ExpenseData;
import com.valevich.moneytracker.network.rest.model.ExpensesSyncModel;
import com.valevich.moneytracker.network.rest.model.UserGoogleInfoModel;

import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.http.QueryMap;

/**
* Created by NotePad.by on 11.06.2016.*/
public interface SyncExpensesApi {

    @POST("/transactions/synch")
    ExpensesSyncModel syncExpenses(
            @Query(value = "data") String data,
            @Query("auth_token") String token,
            @Query("google_token") String googleToken);

}
