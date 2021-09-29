package com.teamnova.ptmanager.model.record.meal;

/**
 *  식사 정보를 담는 객체
 *
 * */
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class Meal implements Serializable {
    @SerializedName("mealRecordId")
    private String mealRecordId;
    @SerializedName("userId")
    private String userId;
    @SerializedName("mealDate")
    private String mealDate;
    @SerializedName("mealType")
    private int mealType;
    @SerializedName("mealContents")
    private String mealContents;
    @SerializedName("creDatetime")
    private String creDatetime;
    @SerializedName("modDatetime")
    private String modDatetime;
    @SerializedName("delYn")
    private String delYn;
    @SerializedName("imgList")
    private ArrayList<String> imgList;
    @SerializedName("commentList")
    private ArrayList<MealComment> commentList;
    @SerializedName("savePath")
    private String savePath;

    public String getMealRecordId() {
        return mealRecordId;
    }

    public void setMealRecordId(String mealRecordId) {
        this.mealRecordId = mealRecordId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMealDate() {
        return mealDate;
    }

    public void setMealDate(String mealDate) {
        this.mealDate = mealDate;
    }

    public int getMealType() {
        return mealType;
    }

    public void setMealType(int mealType) {
        this.mealType = mealType;
    }

    public String getMealContents() {
        return mealContents;
    }

    public void setMealContents(String mealContents) {
        this.mealContents = mealContents;
    }

    public String getCreDatetime() {
        return creDatetime;
    }

    public void setCreDatetime(String creDatetime) {
        this.creDatetime = creDatetime;
    }

    public String getModDatetime() {
        return modDatetime;
    }

    public void setModDatetime(String modDatetime) {
        this.modDatetime = modDatetime;
    }

    public String getDelYn() {
        return delYn;
    }

    public void setDelYn(String delYn) {
        this.delYn = delYn;
    }

    public ArrayList<String> getImgList() {
        return imgList;
    }

    public void setImgList(ArrayList<String> imgList) {
        this.imgList = imgList;
    }

    public ArrayList<MealComment> getCommentList() {
        return commentList;
    }

    public void setCommentList(ArrayList<MealComment> commentList) {
        this.commentList = commentList;
    }

    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }
}
