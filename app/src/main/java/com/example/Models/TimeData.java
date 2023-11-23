package com.example.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TimeData {
    @SerializedName("year")
    @Expose
    public Integer year;
    @SerializedName("month")
    @Expose
    public Integer month;
    @SerializedName("day")
    @Expose
    public Integer day;
    @SerializedName("hour")
    @Expose
    public Integer hour;
    @SerializedName("minute")
    @Expose
    public Integer minute;
    @SerializedName("seconds")
    @Expose
    public Integer seconds;
    @SerializedName("milliSeconds")
    @Expose
    public Integer milliSeconds;
    @SerializedName("dateTime")
    @Expose
    public String dateTime;
    @SerializedName("date")
    @Expose
    public String date;
    @SerializedName("time")
    @Expose
    public String time;
    @SerializedName("timeZone")
    @Expose
    public String timeZone;
    @SerializedName("dayOfWeek")
    @Expose
    public String dayOfWeek;
    @SerializedName("dstActive")
    @Expose
    public Boolean dstActive;

    public Integer getYear() {
        return year;
    }

    public Integer getMonth() {
        return month;
    }

    public Integer getDay() {
        return day;
    }

    public Integer getHour() {
        return hour;
    }

    public Integer getMinute() {
        return minute;
    }

    public Integer getSeconds() {
        return seconds;
    }

    public Integer getMilliSeconds() {
        return milliSeconds;
    }

    public String getDateTime() {
        return dateTime;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public Boolean getDstActive() {
        return dstActive;
    }
}
