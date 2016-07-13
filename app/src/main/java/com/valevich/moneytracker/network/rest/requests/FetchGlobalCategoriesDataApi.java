package com.valevich.moneytracker.network.rest.requests;

import com.valevich.moneytracker.network.rest.model.GlobalCategoriesDataModel;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by User on 16.06.2016.
 */
public interface FetchGlobalCategoriesDataApi {
    @GET("/transcat")
    void fetchGlobalCategoriesData(
            @Query("auth_token") String token,
            @Query("google_token") String googleToken,
            Callback<List<GlobalCategoriesDataModel>> callback);
}
