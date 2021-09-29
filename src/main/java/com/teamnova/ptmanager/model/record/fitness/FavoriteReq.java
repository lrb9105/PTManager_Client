package com.teamnova.ptmanager.model.record.fitness;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/** 즐겨찾기 dto */
public class FavoriteReq implements Serializable {
    @SerializedName("fitnessKindsId")
    private String fitnessKindId;
    @SerializedName("userId")
    private String userId;
    @SerializedName("isClicked")
    private String isClicked;

    public FavoriteReq(String fitnessKindId, String userId, String isClicked) {
        this.fitnessKindId = fitnessKindId;
        this.userId = userId;
        this.isClicked = isClicked;
    }

    public String getFitnessKindId() {
        return fitnessKindId;
    }

    public void setFitnessKindId(String fitnessKindId) {
        this.fitnessKindId = fitnessKindId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getIsClicked() {
        return isClicked;
    }

    public void setIsClicked(String isClicked) {
        this.isClicked = isClicked;
    }
}
