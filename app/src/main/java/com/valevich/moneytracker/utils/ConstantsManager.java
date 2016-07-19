package com.valevich.moneytracker.utils;

public interface ConstantsManager {

    //-- G+ --//
    String G_PLUS_SCOPE =
            "oauth2:https://www.googleapis.com/auth/plus.me";
    String USERINFO_SCOPE =
            "https://www.googleapis.com/auth/userinfo.profile";
    String EMAIL_SCOPE =
            "https://www.googleapis.com/auth/userinfo.email";
    String SCOPES = G_PLUS_SCOPE + " " + USERINFO_SCOPE + " " + EMAIL_SCOPE;

    //--loftApiStatuses--//
    String STATUS_SUCCESS = "success";
    String STATUS_LOGIN_BUSY = "Login busy already";
    String STATUS_WRONG_PASSWORD = "Wrong password";
    String STATUS_WRONG_USERNAME = "Wrong login";
    String STATUS_EMPTY = "";
    String STATUS_WRONG_ID = "Wrong id";
    String STATUS_ERROR = "Error";
    String UNAUTHORIZED_ERROR_CODE = "401";

    //--SYNC--//
    String CONTENT_AUTHORITY = "com.loftschool.loftmoneytracker";
    String SYNC_ACCOUNT_TYPE = "loftmoneytracker.loftschool.com";
    int DEFAULT_SYNC_INTERVAL = 3600;//hour

    //--CHART---//
    float CHART_LABEL_SIZE = 14.0f;
    float CHART_LABEL_CENTER_SIZE = 16.0f;

    //---Dialog--//
    String CATEGORY_DIALOG_TAG = "CATEGORY_DIALOG";
    String PROGRESS_DIALOG_TAG = "PROGRESS_DIALOG";

    //--Intent--//
    int NOTIFICATION_INTENT_ID = 1;

    //--Activity result request code--//
    int PICK_ACCOUNT_REQUEST_CODE = 100;

    //--Loader ids--//
    int EXPENSES_LOADER_ID = 0;
    int CATEGORIES_LOADER_ID = 1;

    //--args keys--//
    String CATEGORY_NAME_KEY = "CATEGORY_NAME";
    String SELECTED_ITEMS_KEY = "SELECTED_ITEMS";

    //--Search--//
    String SEARCH_ID = "search_id";

    //--Notifications--//
    int NOTIFICATION_REQUEST_CODE = 0;
    int NOTIFICATION_ID = 0;

    //--Logs--//
    int MAX_LOG_LENGTH = 4000;

    String DATE_PICKER_TAG = "DATE_PICKER";


}
