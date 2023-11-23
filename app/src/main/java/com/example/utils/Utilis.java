package com.example.utils;

import static com.example.utils.Constants.DATE_FORMAT;
import static com.example.utils.Constants.DATE_FORMAT;

import android.annotation.SuppressLint;
import android.text.format.DateFormat;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utilis {


    public static float smallValue(Double value, int range) {
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(range);
        df.setGroupingUsed(false);
        return Float.parseFloat(df.format(value));
    }

    public static float round2(Double value, int range) {
        BigDecimal bigDecimal = BigDecimal.valueOf(smallValue(value, range));
        String data = bigDecimal.stripTrailingZeros().toPlainString();
        return Float.parseFloat(data);
    }
    public static double getPercentage(double value, float percentage)
    {
        return round2((value / 100) * percentage,2);
    }
    public static String convertToDate(String value) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        try {
            Date date = dateFormat.parse(value);
            return (String) DateFormat.format("dd - MM - yyyy", date);
        } catch (ParseException e) {
            return "";
        }
    }

    public static String convertToTime(String value) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        try {
            Date date = dateFormat.parse(value);
            String day = (String) DateFormat.format("hh:mm", date); // 20
            return day;
        } catch (ParseException e) {
            return "";
        }
    }

    @SuppressLint("SimpleDateFormat")
    public static String getCurrentDateTime() {
        return new SimpleDateFormat(DATE_FORMAT).format(new Date());
    }
}
