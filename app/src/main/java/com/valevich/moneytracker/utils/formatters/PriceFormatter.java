package com.valevich.moneytracker.utils.formatters;

import com.valevich.moneytracker.R;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.res.StringRes;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

@EBean
public class PriceFormatter {

    public static final char SEPARATOR = '.';

    public static final char ZERO = '0';

    private static final String DEFAULT_CURRENCY_SYMBOL = " p.";

    @StringRes(R.string.max_value_msg)
    String mMaxValueMessage;

    private static final String UNNECESSARY_DECIMAL_REPLACEMENT_FORMAT = "#0";

    private static final String CATEGORY_TOTAL_FORMAT = "#0.00";

    private static final float CATEGORY_TOTAL_DISPLAY_MAX = 999999f;

    private static final float CATEGORY_TOTAL_DISPLAY_SUFFIX_MAX = 999999000000000000000000f;

    private static final NavigableMap<Float, String> SUFFIXES = new TreeMap<>();

    static {
        SUFFIXES.put(1000000.f, " Млн");
        SUFFIXES.put(1000000000.f, " Млр");
        SUFFIXES.put(1000000000000.f, " Трл");
        SUFFIXES.put(1000000000000000.f, " Квд");
        SUFFIXES.put(1000000000000000000.f, " Квт");
    }

    //don't show ".0000" if price is an Integer
    public String formatPriceForDb(String priceString) {
        float price = Float.valueOf(priceString);
        if (price % 1 == 0) {
            priceString = getDecimalFormatter(UNNECESSARY_DECIMAL_REPLACEMENT_FORMAT)
                    .format(price);
        }
        return priceString;
    }

    public String formatPriceFromDb(String priceString) {
        return priceString + DEFAULT_CURRENCY_SYMBOL;
    }

    //for CategoriesFragment list
    public String formatCategoryTotal(float total) {
        String totalString = "";
        if (total > 0) {
            if (total > CATEGORY_TOTAL_DISPLAY_SUFFIX_MAX) return mMaxValueMessage;
            String suffix = "";
            DecimalFormat decimalFormat;
            if (total > CATEGORY_TOTAL_DISPLAY_MAX) {
                Map.Entry<Float, String> e = SUFFIXES.floorEntry(total);
                Float divideBy = e.getKey();
                suffix = e.getValue();
                total = total / divideBy;
            }

            decimalFormat = total % 1 == 0 ?
                    getDecimalFormatter(UNNECESSARY_DECIMAL_REPLACEMENT_FORMAT) :
                    getDecimalFormatter(CATEGORY_TOTAL_FORMAT);

            totalString = decimalFormat.format(total) + suffix + DEFAULT_CURRENCY_SYMBOL;
        }
        return totalString;
    }

    private DecimalFormat getDecimalFormatter(String format) {
        return new DecimalFormat(format, getDecimalFormatSymbols());
    }

    private DecimalFormatSymbols getDecimalFormatSymbols() {
        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.getDefault());
        otherSymbols.setDecimalSeparator(SEPARATOR);
        otherSymbols.setGroupingSeparator(SEPARATOR);
        return otherSymbols;
    }
}
