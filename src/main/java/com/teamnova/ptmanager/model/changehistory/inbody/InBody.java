package com.teamnova.ptmanager.model.changehistory.inbody;

/**
 *  눈바디 정보를 담는 객체 - 하나의 눈바디 정보만 갖는다.
 *
 * */
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class InBody implements Serializable {
    @SerializedName("inBodyId")
    private String inBodyId;
    @SerializedName("userId")
    private String userId;
    @SerializedName("creDate")
    private String creDate;
    @SerializedName("creDateTime")
    private String creDateTime;
    @SerializedName("weight")
    private float weight;
    @SerializedName("height")
    private float height;
    @SerializedName("bodyFat")
    private float bodyFat;
    @SerializedName("muscleMass")
    private float muscleMass;
    @SerializedName("bmi")
    private float bmi;
    @SerializedName("bodyFatPercent")
    private float bodyFatPercent;
    @SerializedName("basalMetabolicRate")
    private float basalMetabolicRate;
    @SerializedName("fatLevel")
    private int fatLevel;

    public InBody(String inBodyId, String userId, String creDate, String creDateTime, float weight, float height, float bodyFat, float muscleMass, float bmi, float bodyFatPercent, float basalMetabolicRate, int fatLevel) {
        this.inBodyId = inBodyId;
        this.userId = userId;
        this.creDate = creDate;
        this.creDateTime = creDateTime;
        this.weight = weight;
        this.height = height;
        this.bodyFat = bodyFat;
        this.muscleMass = muscleMass;
        this.bmi = bmi;
        this.bodyFatPercent = bodyFatPercent;
        this.basalMetabolicRate = basalMetabolicRate;
        this.fatLevel = fatLevel;
    }

    public InBody(){}

    public String getInBodyId() {
        return inBodyId;
    }

    public void setInBodyId(String inBodyId) {
        this.inBodyId = inBodyId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCreDate() {
        return creDate;
    }

    public void setCreDate(String creDate) {
        this.creDate = creDate;
    }

    public String getCreDateTime() {
        return creDateTime;
    }

    public void setCreDateTime(String creDateTime) {
        this.creDateTime = creDateTime;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getBodyFat() {
        return bodyFat;
    }

    public void setBodyFat(float bodyFat) {
        this.bodyFat = bodyFat;
    }

    public float getMuscleMass() {
        return muscleMass;
    }

    public void setMuscleMass(float muscleMass) {
        this.muscleMass = muscleMass;
    }

    public float getBmi() {
        return bmi;
    }

    public void setBmi(float bmi) {
        this.bmi = bmi;
    }

    public float getBodyFatPercent() {
        return bodyFatPercent;
    }

    public void setBodyFatPercent(float bodyFatPercent) {
        this.bodyFatPercent = bodyFatPercent;
    }

    public float getBasalMetabolicRate() {
        return basalMetabolicRate;
    }

    public void setBasalMetabolicRate(float basalMetabolicRate) {
        this.basalMetabolicRate = basalMetabolicRate;
    }

    public int getFatLevel() {
        return fatLevel;
    }

    public void setFatLevel(int fatLevel) {
        this.fatLevel = fatLevel;
    }
}
