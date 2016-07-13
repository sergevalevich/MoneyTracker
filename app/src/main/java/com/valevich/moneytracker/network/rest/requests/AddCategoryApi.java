package com.valevich.moneytracker.network.rest.requests;

import com.valevich.moneytracker.network.rest.model.AddedCategoryModel;

import retrofit.Callback;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by User on 20.06.2016.
 */
public interface AddCategoryApi {
    @POST("/categories/add")
    void addCategory(
            @Query("title") String title,
            @Query("auth_token") String token,
            @Query("google_token") String googleToken,
            Callback<AddedCategoryModel> callback);
}
