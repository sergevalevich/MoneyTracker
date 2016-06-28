package com.valevich.moneytracker.network.rest;

import com.valevich.moneytracker.network.rest.model.AddedCategoryModel;
import com.valevich.moneytracker.network.rest.model.AddedExpenseModel;
import com.valevich.moneytracker.network.rest.model.CategoriesSyncModel;
import com.valevich.moneytracker.network.rest.model.ExpenseData;
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
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by NotePad.by on 20.05.2016.
 */
@EBean
public class RestService {
    private static final String REGISTER_FLAG = "1";

    @Bean
    RestClient restClient;

    public UserRegistrationModel register(String login, String password) {
        return restClient.getRegisterUserApi().registerUser(login, password, REGISTER_FLAG);
    }
    public UserLoginModel logIn (String login, String password) {
        return restClient.getLoginUserApi().logIn(login, password);
    }

    public UserGoogleInfoModel getGoogleInfo(String token) {
        return restClient.getSubmitGoogleTokenApi().submitGoogleToken(token);
    }

    public ExpensesSyncModel syncExpenses(String expenses, String token,String googleToken) {
        return restClient.getSyncExpensesApi().syncExpenses(expenses, token, googleToken);
    }

    public CategoriesSyncModel syncCategories(String categories, String token, String googleToken) {
        return restClient.getSyncCategoriesApi().syncCategories(categories, token, googleToken);
    }

    public UserLogoutModel logout() {
        return restClient.getLogoutUserApi().logOut();
    }

    public List<GlobalCategoriesDataModel> fetchGlobalCategoriesData(String authToken, String googleToken) {
        return restClient.getFetchGlobalCategoriesDataApi().fetchGlobalCategoriesData(authToken,googleToken);
    }

    public AddedExpenseModel addExpense(double sum, String comment, int categoryId,
                                        String trDate, String authToken, String googleToken) {
        return restClient.getAddExpenseApi().addExpense(sum,comment,categoryId,
                trDate,authToken,googleToken);
    }

    public RemovedCategoryModel removeCategory(int id, String authToken, String googleToken) {
        return restClient.getRemoveCategoryApi().removeCategory(id,authToken,googleToken);
    }

    public AddedCategoryModel addCategory(String title, String authToken, String googleToken) {
        return restClient.getAddCategoryApi().addCategory(title,authToken,googleToken);
    }

    public AddedCategoryModel updateCategory(String newTitle, int id, String authToken, String googleToken) {
        return restClient.getEditCategoryApi().updateCategory(newTitle,id,authToken,googleToken);
    }

    public void setRestClient(RestClient restClient) {
        this.restClient = restClient;
    }

}
