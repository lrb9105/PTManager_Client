package com.teamnova.ptmanager.model.record.meal;

import com.google.gson.annotations.SerializedName;
import com.teamnova.ptmanager.model.changehistory.eyebody.EyeBody;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 일자별 눈바디 정보를 담는 객체 일자, 일자별 눈바디 정보 리스트를 갖는다.
 * */
public class MealHistoryInfo implements Serializable {
    @SerializedName("mealDate")
    private String mealDate;
    @SerializedName("mealListByDay")
    private ArrayList<Meal> mealListByDay;

    public MealHistoryInfo(String mealDate, ArrayList<Meal> mealListByDay) {
        this.mealDate = mealDate;
        this.mealListByDay = mealListByDay;
    }

    public String getMealDate() {
        return mealDate;
    }

    public void setMealDate(String mealDate) {
        this.mealDate = mealDate;
    }

    public ArrayList<Meal> getMealListByDay() {
        return mealListByDay;
    }

    public void setMealListByDay(ArrayList<Meal> mealListByDay) {
        this.mealListByDay = mealListByDay;
    }
}
