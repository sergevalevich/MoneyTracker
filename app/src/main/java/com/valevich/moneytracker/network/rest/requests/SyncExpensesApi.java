package com.valevich.moneytracker.network.rest.requests;

import com.valevich.moneytracker.network.rest.model.ExpensesSyncModel;

import retrofit.Callback;
import retrofit.http.POST;
import retrofit.http.Query;

public interface SyncExpensesApi {

    @POST("/transactions/synch")
    void syncExpenses(
            @Query("data") String data,
            @Query("auth_token") String token,
            @Query("google_token") String googleToken,
            Callback<ExpensesSyncModel> callback);

}
