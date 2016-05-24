package com.valevich.moneytracker.utils;

import org.androidannotations.annotations.sharedpreferences.DefaultString;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

/**
 * Created by NotePad.by on 22.05.2016.
 */
@SharedPref(value=SharedPref.Scope.UNIQUE)
public interface Preferences {
    @DefaultString("")
    String token();
}
