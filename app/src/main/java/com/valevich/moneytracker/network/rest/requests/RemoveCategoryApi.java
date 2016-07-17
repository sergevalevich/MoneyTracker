package com.valevich.moneytracker.network.rest.requests;

import com.valevich.moneytracker.network.rest.model.RemovedCategoryModel;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

public interface RemoveCategoryApi {
    @GET("/categories/del")
    void removeCategory(
            @Query("id") int id,
            @Query("auth_token") String token,
            @Query("google_token") String googleToken,
            Callback<RemovedCategoryModel> callback);
}
