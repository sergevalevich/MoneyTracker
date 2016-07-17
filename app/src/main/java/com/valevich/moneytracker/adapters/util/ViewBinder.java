package com.valevich.moneytracker.adapters.util;

public interface ViewBinder<T> {
    void bind(T item, boolean isSelected);
}
