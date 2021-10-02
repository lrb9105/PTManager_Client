package com.teamnova.ptmanager.model.record.fitness;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/** 운동종류 dto */
public class FitnessKinds implements Serializable {
    @SerializedName("fitnessKindsId")
    private String fitnessKindId;
    @SerializedName("part")
    private String part;
    @SerializedName("fitnessKindName")
    private String fitnessKindName;
    @SerializedName("savePath")
    private String savePath;
    @SerializedName("fitnessType")
    private String fitnessType;
    @SerializedName("writerUserId")
    private String writerUserId;
    @SerializedName("fitnessKindsType")
    private String fitnessKindsType;
    @SerializedName("pubYn")
    private String pubYn;
    @SerializedName("fitnessKindsFavoriteId")
    private String fitnessKindsFavoriteId;
    @SerializedName("userId")
    private String userId;
    @SerializedName("isFavoriteYn") // 즐겨찾기 여부
    private String isFavoriteYn;
    // 추가 체크여부
    private boolean isChecked = false;
    // 즐겨찾기 체크여부
    private boolean isFavoriteChecked = false;

    public FitnessKinds(String fitnessKindId, String part, String fitnessKindName, String savePath, String fitnessType, String writerUserId, String fitnessKindsType, String pubYn, String fitnessKindsFavoriteId, String userId) {
        this.fitnessKindId = fitnessKindId;
        this.part = part;
        this.fitnessKindName = fitnessKindName;
        this.savePath = savePath;
        this.fitnessType = fitnessType;
        this.writerUserId = writerUserId;
        this.fitnessKindsType = fitnessKindsType;
        this.pubYn = pubYn;
        this.fitnessKindsFavoriteId = fitnessKindsFavoriteId;
        this.userId = userId;
    }

    public FitnessKinds(String fitnessKindId, String part, String fitnessKindName, String fitnessType, String userId) {
        this.fitnessKindId = fitnessKindId;
        this.part = part;
        this.fitnessKindName = fitnessKindName;
        this.fitnessType = fitnessType;
        this.userId = userId;
    }

    public FitnessKinds(String fitnessKindId, String part, String fitnessKindName, String savePath, String fitnessType, String writerUserId, String fitnessKindsType, String pubYn, String fitnessKindsFavoriteId, String userId, String isFavoriteYn) {
        this.fitnessKindId = fitnessKindId;
        this.part = part;
        this.fitnessKindName = fitnessKindName;
        this.savePath = savePath;
        this.fitnessType = fitnessType;
        this.writerUserId = writerUserId;
        this.fitnessKindsType = fitnessKindsType;
        this.pubYn = pubYn;
        this.fitnessKindsFavoriteId = fitnessKindsFavoriteId;
        this.userId = userId;
        this.isFavoriteYn = isFavoriteYn;
    }

    public FitnessKinds(){}

    public String getFitnessKindId() {
        return fitnessKindId;
    }

    public void setFitnessKindId(String fitnessKindId) {
        this.fitnessKindId = fitnessKindId;
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

    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    public String getFitnessType() {
        return fitnessType;
    }

    public void setFitnessType(String fitnessType) {
        this.fitnessType = fitnessType;
    }

    public String getWriterUserId() {
        return writerUserId;
    }

    public void setWriterUserId(String writerUserId) {
        this.writerUserId = writerUserId;
    }

    public String getFitnessKindsType() {
        return fitnessKindsType;
    }

    public void setFitnessKindsType(String fitnessKindsType) {
        this.fitnessKindsType = fitnessKindsType;
    }

    public String getPubYn() {
        return pubYn;
    }

    public void setPubYn(String pubYn) {
        this.pubYn = pubYn;
    }

    public String getFitnessKindsFavoriteId() {
        return fitnessKindsFavoriteId;
    }

    public void setFitnessKindsFavoriteId(String fitnessKindsFavoriteId) {
        this.fitnessKindsFavoriteId = fitnessKindsFavoriteId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public boolean isFavoriteChecked() {
        return isFavoriteChecked;
    }

    public void setFavoriteChecked(boolean favoriteChecked) {
        isFavoriteChecked = favoriteChecked;
    }

    public String getIsFavoriteYn() {
        return isFavoriteYn;
    }

    public void setIsFavoriteYn(String isFavoriteYn) {
        this.isFavoriteYn = isFavoriteYn;
    }
}
