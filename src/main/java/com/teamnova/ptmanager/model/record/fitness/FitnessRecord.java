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
    @SerializedName("part")
    private String part;
    @SerializedName("fitnessKindName")
    private String fitnessKindName;
    @SerializedName("fitnessType")
    private String fitnessType;
    @SerializedName("fitnessKindsType")
    private String fitnessKindsType;
    @SerializedName("fitnessRecordDetailList")
    private ArrayList<FitnessRecordDetail> fitnessRecordDetailList;

    public FitnessRecord(String fitnessRecordId, String userId, String fitnessKindsId, String fitnessDate, String part, String fitnessKindName, String fitnessType, String fitnessKindsType, ArrayList<FitnessRecordDetail> fitnessRecordDetailList) {
        this.fitnessRecordId = fitnessRecordId;
        this.userId = userId;
        this.fitnessKindsId = fitnessKindsId;
        this.fitnessDate = fitnessDate;
        this.part = part;
        this.fitnessKindName = fitnessKindName;
        this.fitnessType = fitnessType;
        this.fitnessKindsType = fitnessKindsType;
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

    public String getPart() {
        return part;
    }

    public void setPart(String part) {
        this.part = part;
    }

    public String getFitnessKindName() {
        return fitnessKindName;
    }

    public void setFitnessKindName(String fitnessKindName) {
        this.fitnessKindName = fitnessKindName;
    }

    public String getFitnessType() {
        return fitnessType;
    }

    public void setFitnessType(String fitnessType) {
        this.fitnessType = fitnessType;
    }

    public String getFitnessKindsType() {
        return fitnessKindsType;
    }

    public void setFitnessKindsType(String fitnessKindsType) {
        this.fitnessKindsType = fitnessKindsType;
    }

    public ArrayList<FitnessRecordDetail> getFitnessRecordDetailList() {
        return fitnessRecordDetailList;
    }

    public void setFitnessRecordDetailList(ArrayList<FitnessRecordDetail> fitnessRecordDetailList) {
        this.fitnessRecordDetailList = fitnessRecordDetailList;
    }
}
