package com.valevich.moneytracker.network.rest.requests;

import com.valevich.moneytracker.network.rest.model.CategoriesSyncModel;
import com.valevich.moneytracker.network.rest.model.ExpensesSyncModel;

import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by NotePad.by on 14.06.2016.
 */
public interface SyncCategoriesApi {

    @POST("/categories/synch")
    CategoriesSyncModel syncCategories(
            @Query("data") String data,
            @Query("auth_token") String token,
            @Query("google_token") String googleToken);

}
