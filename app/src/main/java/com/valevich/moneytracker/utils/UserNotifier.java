package com.valevich.moneytracker.utils;

import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.ViewGroup;

import org.androidannotations.annotations.EBean;

/**
 * Created by NotePad.by on 28.05.2016.
 */
@EBean
public class UserNotifier {
    public void notifyUser(View layout, String message) {
        Snackbar.make(layout,message,Snackbar.LENGTH_LONG).show();
    }
}
