package com.valevich.moneytracker.network.rest.requests;

import com.valevich.moneytracker.network.rest.model.AddedExpenseModel;
import com.valevich.moneytracker.network.rest.model.ExpensesSyncModel;

import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by User on 18.06.2016.
 */
public interface AddExpenseApi {

    @POST("/transactions/add")
    AddedExpenseModel addExpense(
            @Query("sum") double sum,
            @Query("comment") String comment,
            @Query("category_id") int categoryId,
            @Query("tr_date") String trDate,
            @Query("auth_token") String token,
            @Query("google_token") String googleToken);
}
