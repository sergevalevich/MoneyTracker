package com.valevich.moneytracker.utils;

import android.util.Log;

import com.crashlytics.android.Crashlytics;

import org.androidannotations.annotations.EBean;

import timber.log.Timber;

/**
 * Created by User on 04.07.2016.
 */
@EBean
public class ReleaseTree extends Timber.Tree {

    private static int MAX_LOG_LENGTH = 4000;

    @Override
    protected boolean isLoggable(int priority) {
        if(priority == Log.DEBUG || priority == Log.VERBOSE || priority == Log.INFO) {
            return false;
        }
        //only log Warn, Error, Wtf
        return true;
    }

    @Override
    protected void log(int priority, String tag, String message, Throwable t) {
        if(isLoggable(priority)) {
//            Message is short enough. Doesn't need to be broken into chunks

            if(priority == Log.ERROR && t != null) {
                Crashlytics.log(message);
            }


            if(message.length() < MAX_LOG_LENGTH) {
                if(priority == Log.ASSERT) {
                    Log.wtf(tag,message);
                } else {
                    Log.println(priority,tag,message);
                }
                return;
            }

            for (int i = 0, length = message.length(); i<length; i++) {
                int newLine = message.indexOf('\n',i);
                newLine = newLine!=-1 ? newLine:length;
                do {
                    int end = Math.min(newLine, i+MAX_LOG_LENGTH);
                    String part = message.substring(i,end);
                    if(priority == Log.ASSERT) {
                        Log.wtf(tag,part);
                    } else {
                        Log.println(priority,tag,part);
                    }
                    i = end;
                } while (i<newLine);
            }

        }
    }
}
