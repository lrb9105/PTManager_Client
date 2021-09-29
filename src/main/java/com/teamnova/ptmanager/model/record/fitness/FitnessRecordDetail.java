package com.teamnova.ptmanager.model.record.fitness;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/** 운동기록상세 dto */
public class FitnessRecordDetail implements Serializable {
    @SerializedName("fitnessRecordDetailId")
    private String fitnessRecordDetailId;
    @SerializedName("setNum")
    private int setNum;
    @SerializedName("weight")
    private float weight;
    @SerializedName("num")
    private int num;
    @SerializedName("fitnessTimeMinute")
    private int fitnessTimeMinute;
    @SerializedName("km")
    private float km;

    public FitnessRecordDetail(String fitnessRecordDetailId, int setNum, float weight, int num, int fitnessTimeMinute, float km) {
        this.fitnessRecordDetailId = fitnessRecordDetailId;
        this.setNum = setNum;
        this.weight = weight;
        this.num = num;
        this.fitnessTimeMinute = fitnessTimeMinute;
        this.km = km;
    }

    public String getFitnessRecordDetailId() {
        return fitnessRecordDetailId;
    }

    public void setFitnessRecordDetailId(String fitnessRecordDetailId) {
        this.fitnessRecordDetailId = fitnessRecordDetailId;
    }

    public int getSetNum() {
        return setNum;
    }

    public void setSetNum(int setNum) {
        this.setNum = setNum;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getFitnessTimeMinute() {
        return fitnessTimeMinute;
    }

    public void setFitnessTimeMinute(int fitnessTimeMinute) {
        this.fitnessTimeMinute = fitnessTimeMinute;
    }

    public float getKm() {
        return km;
    }

    public void setKm(float km) {
        this.km = km;
    }
}
