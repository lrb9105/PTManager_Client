package com.teamnova.ptmanager.model.schedule;

import com.google.gson.annotations.SerializedName;
import com.teamnova.ptmanager.model.userInfo.FriendInfoDto;

import java.io.Serializable;
import java.util.ArrayList;

public class RepeatSchInfo implements Serializable {
    @SerializedName("repeatSchId")
    private String repeatSchId;
    @SerializedName("repeatDay")
    private String repeatDay;
    @SerializedName("repeatSrtDate")
    private String repeatSrtDate;
    @SerializedName("repeatEndDate")
    private String repeatEndDate;

    public RepeatSchInfo(String repeatSchId, String repeatDay, String repeatSrtDate, String repeatEndDate) {
        this.repeatSchId = repeatSchId;
        this.repeatDay = repeatDay;
        this.repeatSrtDate = repeatSrtDate;
        this.repeatEndDate = repeatEndDate;
    }

    public RepeatSchInfo(String repeatDay, String repeatEndDate) {
        this.repeatDay = repeatDay;
        this.repeatEndDate = repeatEndDate;
    }

    public String getRepeatSchId() {
        return repeatSchId;
    }

    public void setRepeatSchId(String repeatSchId) {
        this.repeatSchId = repeatSchId;
    }

    public String getRepeatDay() {
        return repeatDay;
    }

    public void setRepeatDay(String repeatDay) {
        this.repeatDay = repeatDay;
    }

    public String getRepeatSrtDate() {
        return repeatSrtDate;
    }

    public void setRepeatSrtDate(String repeatSrtDate) {
        this.repeatSrtDate = repeatSrtDate;
    }

    public String getRepeatEndDate() {
        return repeatEndDate;
    }

    public void setRepeatEndDate(String repeatEndDate) {
        this.repeatEndDate = repeatEndDate;
    }
}
