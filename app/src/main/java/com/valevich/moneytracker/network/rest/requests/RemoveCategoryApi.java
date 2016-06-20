package com.valevich.moneytracker.network.rest.requests;

import com.valevich.moneytracker.network.rest.model.AddedExpenseModel;
import com.valevich.moneytracker.network.rest.model.RemovedCategoryModel;

import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by User on 19.06.2016.
 */
public interface RemoveCategoryApi {
    @GET("/categories/del")
    RemovedCategoryModel removeCategory(
            @Query("id") int id,
            @Query("auth_token") String token,
            @Query("google_token") String googleToken);
}
