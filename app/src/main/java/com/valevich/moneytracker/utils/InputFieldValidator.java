package com.valevich.moneytracker.utils;

import com.valevich.moneytracker.database.data.CategoryEntry;
import com.valevich.moneytracker.utils.formatters.PriceFormatter;

import org.androidannotations.annotations.EBean;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        int length = amount.length();
        return length != 0
                && amount.charAt(0) != PriceFormatter.SEPARATOR
                && amount.charAt(length - 1) != PriceFormatter.SEPARATOR
                && Double.valueOf(amount) != 0
                && !(length > 1 && amount.charAt(0) == PriceFormatter.ZERO && amount.charAt(1) != PriceFormatter.SEPARATOR);
    }

    public boolean isDescriptionValid(String description) {
        return !description.trim().isEmpty();
    }

    public boolean isCategoryNameValid(String newName, String oldName) {
        return !newName.equals(oldName) && !newName.equals(CategoryEntry.DEFAULT_CATEGORY_NAME);
    }
}
