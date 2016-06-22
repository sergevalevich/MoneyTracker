package com.valevich.moneytracker.utils;

/**
 * Created by NotePad.by on 26.05.2016.
 */
public interface ConstantsManager {
    String G_PLUS_SCOPE =
            "oauth2:https://www.googleapis.com/auth/plus.me";
    String USERINFO_SCOPE =
            "https://www.googleapis.com/auth/userinfo.profile";
    String EMAIL_SCOPE =
            "https://www.googleapis.com/auth/userinfo.email";
    String SCOPES = G_PLUS_SCOPE + " " + USERINFO_SCOPE + " " + EMAIL_SCOPE;

    //-----loftApiStatuses------//
    String STATUS_SUCCESS = "success";
    String STATUS_LOGIN_BUSY = "Login busy already";
    String STATUS_WRONG_PASSWORD = "Wrong password";
    String STATUS_WRONG_USERNAME = "Wrong login";
    String STATUS_EMPTY = "";
    String STATUS_WRONG_ID = "Wrong id";

    //-----SYNC-----------------//
    String CONTENT_AUTHORITY = "com.loftschool.loftmoneytracker";
    String SYNC_ACCOUNT_TYPE = "loftmoneytracker.loftschool.com";

    //-----CHART---///
    float CHART_LABEL_SIZE = 14.0f;
    float CHART_LABEL_CENTER_SIZE = 16.0f;

}
