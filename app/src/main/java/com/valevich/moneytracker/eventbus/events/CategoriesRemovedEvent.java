package com.valevich.moneytracker.eventbus.events;

import java.util.List;

/**
 * Created by User on 19.06.2016.
 */
public class CategoriesRemovedEvent {

    private List<Integer> mIds;

    public CategoriesRemovedEvent(List<Integer> ids) {
        mIds = ids;
    }

    public List<Integer> getIds() {
        return mIds;
    }
}
