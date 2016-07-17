package com.valevich.moneytracker.eventbus.events;


public class CategorySaveButtonClickedEvent {
    private String mInputText;

    public CategorySaveButtonClickedEvent(String inputText) {
        mInputText = inputText;
    }

    public String getInputText() {
        return mInputText;
    }
}
