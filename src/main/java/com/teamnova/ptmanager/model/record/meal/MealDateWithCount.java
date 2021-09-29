package com.teamnova.ptmanager.model.record.meal;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class MealDateWithCount implements Serializable {
    @SerializedName("mealDate")
    private String date;
    @SerializedName("count")
    private int count;

    public MealDateWithCount(String date, int count) {
        this.date = date;
        this.count = count;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
