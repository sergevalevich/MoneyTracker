package com.valevich.moneytracker.utils;

import org.androidannotations.annotations.EBean;

/**
 * Created by User on 13.07.2016.
 */
@EBean
public class TriesCounter {

    private static final int DEFAULT_TRIES_COUNT = 3;

    private int mTriesCount = DEFAULT_TRIES_COUNT;

    private int mTriesLeft = DEFAULT_TRIES_COUNT;

    public boolean areTriesLeft() {
        return mTriesLeft > 0;
    }

    public void reduceTry() {
        mTriesLeft--;
    }

    public void resetTries() {
        mTriesLeft = mTriesCount;
    }

    public void setTriesCount(int triesCount) {
        mTriesCount = triesCount;
        mTriesLeft = triesCount;
    }

}
