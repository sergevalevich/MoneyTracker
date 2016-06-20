package com.valevich.moneytracker.eventbus.buses;

/**
 * Created by User on 17.06.2016.
 */
public final class BusProvider {
    private static final OttoBus BUS = new OttoBus();

    public static OttoBus getInstance() {
        return BUS;
    }

    private BusProvider() {
    }
}
