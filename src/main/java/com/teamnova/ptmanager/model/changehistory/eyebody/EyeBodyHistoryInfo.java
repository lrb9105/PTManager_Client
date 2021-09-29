package com.teamnova.ptmanager.model.changehistory.eyebody;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
/**
 * 일자별 눈바디 정보를 담는 객체 일자, 일자별 눈바디 정보 리스트를 갖는다.
 * */
public class EyeBodyHistoryInfo implements Serializable {
    @SerializedName("eyeBodyDate")
    private String eyeBodyDate;
    @SerializedName("eyeBodyListByDay")
    private ArrayList<EyeBody> eyeBodyListByDay;

    public EyeBodyHistoryInfo(String eyeBodyDate, ArrayList<EyeBody> eyeBodyListByDay) {
        this.eyeBodyDate = eyeBodyDate;
        this.eyeBodyListByDay = eyeBodyListByDay;
    }

    public String getEyeBodyDate() {
        return eyeBodyDate;
    }

    public void setEyeBodyDate(String eyeBodyDate) {
        this.eyeBodyDate = eyeBodyDate;
    }

    public ArrayList<EyeBody> getEyeBodyListByDay() {
        return eyeBodyListByDay;
    }

    public void setEyeBodyListByDay(ArrayList<EyeBody> eyeBodyListByDay) {
        this.eyeBodyListByDay = eyeBodyListByDay;
    }
}
