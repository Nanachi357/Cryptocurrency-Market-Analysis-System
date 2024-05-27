package com.example.MarkPriceController.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

    private static final SimpleDateFormat DATE_FORMAT_WITH_MINUTES = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public static String convertMillisToDate(long millis) {
        Date date = new Date(millis);
        return DATE_FORMAT_WITH_MINUTES.format(date);
    }
}