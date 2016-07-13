package com.valevich.moneytracker.network.rest.requests;

import com.valevich.moneytracker.network.rest.model.AddedCategoryModel;

import retrofit.Callback;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by User on 21.06.2016.
 */
public interface EditCategoryApi {
    @POST("/categories/edit")
    void updateCategory(
            @Query("title") String title,
            @Query("id") int id,
            @Query("auth_token") String token,
            @Query("google_token") String googleToken,
            Callback<AddedCategoryModel> callback);
}
