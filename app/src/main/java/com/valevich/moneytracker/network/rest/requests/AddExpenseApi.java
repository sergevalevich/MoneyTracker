package com.valevich.moneytracker.network.rest.requests;

import com.valevich.moneytracker.network.rest.model.AddedExpenseModel;

import retrofit.Callback;
import retrofit.http.POST;
import retrofit.http.Query;

public interface AddExpenseApi {

    @POST("/transactions/add")
    void addExpense(
            @Query("sum") double sum,
            @Query("comment") String comment,
            @Query("category_id") int categoryId,
            @Query("tr_date") String trDate,
            @Query("auth_token") String token,
            @Query("google_token") String googleToken,
            Callback<AddedExpenseModel> callback);
}
