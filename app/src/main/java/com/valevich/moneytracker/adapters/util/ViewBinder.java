package com.valevich.moneytracker.adapters.util;

/**
 * Created by User on 05.07.2016.
 */
public interface ViewBinder<T> {
    void bind(T item, boolean isSelected);
}
