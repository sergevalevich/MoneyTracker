package com.valevich.moneytracker.utils.formatters;

import org.androidannotations.annotations.EBean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by User on 17.06.2016.
 */
@EBean
public class DateFormatter {
    private static final String PRESENTATION_FORMAT = "dd-MM-yyyy";
    private static final String DB_FORMAT = "yyyy-MM-dd";

    public String formatDateForDb(String dateToFormat) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(PRESENTATION_FORMAT, Locale.getDefault());
        Date date = new Date();
        try {
            date = simpleDateFormat.parse(dateToFormat);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        simpleDateFormat = new SimpleDateFormat(DB_FORMAT, Locale.getDefault());
        return simpleDateFormat.format(date);
    }

    public String formatDateFromDb(String dbDate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DB_FORMAT, Locale.getDefault());
        Date date = new Date();
        try {
            date = simpleDateFormat.parse(dbDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        simpleDateFormat = new SimpleDateFormat(PRESENTATION_FORMAT, Locale.getDefault());
        return simpleDateFormat.format(date);
    }

    public Date getDateFromString(String stringDate) {
        SimpleDateFormat sdf = new SimpleDateFormat(DB_FORMAT, Locale.getDefault());
        try {
            return sdf.parse(stringDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
