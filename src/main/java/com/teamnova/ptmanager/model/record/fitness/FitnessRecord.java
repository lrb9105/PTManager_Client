package com.teamnova.ptmanager.model.record.fitness;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/** 운동기록 dto */
public class FitnessRecord implements Serializable {
    @SerializedName("fitnessRecordId")
    private String fitnessRecordId;
    @SerializedName("userId")
    private String userId;
    @SerializedName("fitnessKindsId")
    private String fitnessKindsId;
    @SerializedName("fitnessDate")
    private String fitnessDate;
    @SerializedName("fitnessRecordDetailList")
    private ArrayList<FitnessRecordDetail> fitnessRecordDetailList;

    public FitnessRecord(String fitnessRecordId, String userId, String fitnessKindsId, String fitnessDate, ArrayList<FitnessRecordDetail> fitnessRecordDetailList) {
        this.fitnessRecordId = fitnessRecordId;
        this.userId = userId;
        this.fitnessKindsId = fitnessKindsId;
        this.fitnessDate = fitnessDate;
        this.fitnessRecordDetailList = fitnessRecordDetailList;
    }

    public String getFitnessRecordId() {
        return fitnessRecordId;
    }

    public void setFitnessRecordId(String fitnessRecordId) {
        this.fitnessRecordId = fitnessRecordId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFitnessKindsId() {
        return fitnessKindsId;
    }

    public void setFitnessKindsId(String fitnessKindsId) {
        this.fitnessKindsId = fitnessKindsId;
    }

    public String getFitnessDate() {
        return fitnessDate;
    }

    public void setFitnessDate(String fitnessDate) {
        this.fitnessDate = fitnessDate;
    }

    public ArrayList<FitnessRecordDetail> getFitnessRecordDetailList() {
        return fitnessRecordDetailList;
    }

    public void setFitnessRecordDetailList(ArrayList<FitnessRecordDetail> fitnessRecordDetailList) {
        this.fitnessRecordDetailList = fitnessRecordDetailList;
    }
}
