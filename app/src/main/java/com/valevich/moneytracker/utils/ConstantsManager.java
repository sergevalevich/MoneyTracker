package com.valevich.moneytracker.utils;

/**
 * Created by NotePad.by on 26.05.2016.
 */
public final class ConstantsManager {
    public final static String G_PLUS_SCOPE =
            "oauth2:https://www.googleapis.com/auth/plus.me";
    public final static String USERINFO_SCOPE =
            "https://www.googleapis.com/auth/userinfo.profile";
    public final static String EMAIL_SCOPE =
            "https://www.googleapis.com/auth/userinfo.email";
    public final static String SCOPES = G_PLUS_SCOPE + " " + USERINFO_SCOPE + " " + EMAIL_SCOPE;

    //-----loftApiStatuses------//
    public static final String STATUS_SUCCESS = "success";
    public static final String STATUS_LOGIN_BUSY = "Login busy already";
    public static final String STATUS_WRONG_PASSWORD = "Wrong password";
    public static final String STATUS_WRONG_USERNAME = "Wrong login";
    public static final String STATUS_EMPTY = "";
}
