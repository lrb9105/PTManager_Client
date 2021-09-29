package com.teamnova.ptmanager.model.record.meal;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class MealComment implements Serializable {
    @SerializedName("mealCommentId")
    private String mealCommentId;
    @SerializedName("mealRecordId")
    private String mealRecordId;
    @SerializedName("writerUserId")
    private String writerUserId;
    @SerializedName("contents")
    private String contents;
    @SerializedName("creDatetime")
    private String creDatetime;
    @SerializedName("modDatetime")
    private String modDatetime;
    @SerializedName("delYn")
    private String delYn;

    public MealComment(String mealCommentId, String mealRecordId, String writerUserId, String contents, String creDatetime, String modDatetime, String delYn) {
        this.mealCommentId = mealCommentId;
        this.mealRecordId = mealRecordId;
        this.writerUserId = writerUserId;
        this.contents = contents;
        this.creDatetime = creDatetime;
        this.modDatetime = modDatetime;
        this.delYn = delYn;
    }

    public String getMealCommentId() {
        return mealCommentId;
    }

    public void setMealCommentId(String mealCommentId) {
        this.mealCommentId = mealCommentId;
    }

    public String getMealRecordId() {
        return mealRecordId;
    }

    public void setMealRecordId(String mealRecordId) {
        this.mealRecordId = mealRecordId;
    }

    public String getWriterUserId() {
        return writerUserId;
    }

    public void setWriterUserId(String writerUserId) {
        this.writerUserId = writerUserId;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
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
}
