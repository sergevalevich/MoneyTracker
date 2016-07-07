package com.valevich.moneytracker.utils;

import com.valevich.moneytracker.utils.formatters.PriceFormatter;

import org.androidannotations.annotations.EBean;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by NotePad.by on 27.05.2016.
 */
@EBean
public class InputFieldValidator {
    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    //SignUpScreen validation
    public boolean isUsernameValid(String username) {
        return username.length() > 4;
    }

    public boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    public boolean isEmailValid(String email) {
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);

        return matcher.matches();
    }

    //New ExpenseScreen validation
    public boolean isAmountValid(String amount) {
        amount = amount.trim();
        int length = amount.length();
        return length != 0
                && amount.charAt(0) != PriceFormatter.POINT
                && amount.charAt(length - 1) != PriceFormatter.POINT
                && Double.valueOf(amount) != 0
                && !(length > 1 && amount.charAt(0) == PriceFormatter.ZERO && amount.charAt(1) != PriceFormatter.POINT);
    }

    public boolean isDescriptionValid(String description) {
        return description.trim().length() != 0;
    }
}
