package com.valevich.moneytracker.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by NotePad.by on 27.05.2016.
 */
public class InputFieldValidator {
    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    public static boolean isUsernameValid(String username) {
        return username.length() > 4;
    }
    public static boolean isPasswordValid(String password) {
        return password.length() > 4;
    }
    public static boolean isEmailValid(String email) {
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);

        return matcher.matches();
    }
}
