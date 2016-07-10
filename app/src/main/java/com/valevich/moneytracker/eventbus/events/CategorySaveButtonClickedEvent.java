package com.valevich.moneytracker.eventbus.events;

/**
 * Created by User on 09.07.2016.
 */
public class CategorySaveButtonClickedEvent {
    private String mInputText;

    public CategorySaveButtonClickedEvent(String inputText) {
        mInputText = inputText;
    }

    public String getInputText() {
        return mInputText;
    }
}
