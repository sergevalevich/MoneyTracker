package com.valevich.moneytracker.network.rest;

import com.valevich.moneytracker.network.rest.model.AddedCategoryModel;
import com.valevich.moneytracker.network.rest.model.AddedExpenseModel;
import com.valevich.moneytracker.network.rest.model.CategoriesSyncModel;
import com.valevich.moneytracker.network.rest.model.ExpensesSyncModel;
import com.valevich.moneytracker.network.rest.model.GlobalCategoriesDataModel;
import com.valevich.moneytracker.network.rest.model.RemovedCategoryModel;
import com.valevich.moneytracker.network.rest.model.UserGoogleInfoModel;
import com.valevich.moneytracker.network.rest.model.UserLoginModel;
import com.valevich.moneytracker.network.rest.model.UserLogoutModel;
import com.valevich.moneytracker.network.rest.model.UserRegistrationModel;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import java.util.List;

import retrofit.Callback;

/**
 * Created by NotePad.by on 20.05.2016.
 */
@EBean
public class RestService {
    private static final String REGISTER_FLAG = "1";

    @Bean
    RestClient restClient;

    public void register(String login, String password, Callback<UserRegistrationModel> callback) {
        restClient.getRegisterUserApi().registerUser(login, password, REGISTER_FLAG, callback);
    }

    public void logIn(String login, String password, Callback<UserLoginModel> callback) {
        restClient.getLoginUserApi().logIn(login, password, callback);
    }

    public void getGoogleInfo(String token, Callback<UserGoogleInfoModel> callback) {
        restClient.getSubmitGoogleTokenApi().submitGoogleToken(token, callback);
    }

    public void syncExpenses(String expenses, String token, String googleToken, Callback<ExpensesSyncModel> callback) {
        restClient.getSyncExpensesApi().syncExpenses(expenses, token, googleToken, callback);
    }

    public void syncCategories(String categories, String token, String googleToken, Callback<CategoriesSyncModel> callback) {
        restClient.getSyncCategoriesApi().syncCategories(categories, token, googleToken, callback);
    }

    public void logout(Callback<UserLogoutModel> callback) {
        restClient.getLogoutUserApi().logOut(callback);
    }

    // TODO: 29.06.2016 JsonSyntaxException при входе с нового акка гугла
    public void fetchGlobalCategoriesData(String authToken, String googleToken, Callback<List<GlobalCategoriesDataModel>> callback) {
        restClient.getFetchGlobalCategoriesDataApi().fetchGlobalCategoriesData(authToken, googleToken, callback);
    }

    public void addExpense(double sum, String comment, int categoryId,
                           String trDate, String authToken, String googleToken, Callback<AddedExpenseModel> callback) {
        restClient.getAddExpenseApi().addExpense(sum, comment, categoryId,
                trDate, authToken, googleToken, callback);
    }

    public void removeCategory(int id, String authToken, String googleToken, Callback<RemovedCategoryModel> callback) {
        restClient.getRemoveCategoryApi().removeCategory(id, authToken, googleToken, callback);
    }

    public void addCategory(String title, String authToken, String googleToken, Callback<AddedCategoryModel> callback) {
        restClient.getAddCategoryApi().addCategory(title, authToken, googleToken, callback);
    }

    public void updateCategory(String newTitle, int id, String authToken, String googleToken, Callback<AddedCategoryModel> callback) {
        restClient.getEditCategoryApi().updateCategory(newTitle, id, authToken, googleToken, callback);
    }
}
