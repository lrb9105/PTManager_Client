package com.teamnova.ptmanager.model.changehistory.eyebody;

/**
 *  눈바디 정보를 담는 객체 - 하나의 눈바디 정보만 갖는다.
 *
 * */
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class EyeBodyCompare implements Serializable {
    @SerializedName("eyeBodyChangeId")
    private String eyeBodyChangeId;
    @SerializedName("userId")
    private String userId;
    @SerializedName("creDate")
    private String creDate;
    @SerializedName("creDateTime")
    private String creDateTime;
    @SerializedName("fileCreDateTime")
    private String fileCreDateTime;
    @SerializedName("savePath")
    private String savePath;
    @SerializedName("delYn")
    private String delYn;
    @SerializedName("commonCompareId")
    private String commonCompareId;

    public EyeBodyCompare(String eyeBodyChangeId, String userId, String creDate, String creDateTime, String fileCreDateTime, String savePath, String delYn, String commonCompareId) {
        this.eyeBodyChangeId = eyeBodyChangeId;
        this.userId = userId;
        this.creDate = creDate;
        this.creDateTime = creDateTime;
        this.fileCreDateTime = fileCreDateTime;
        this.savePath = savePath;
        this.delYn = delYn;
        this.commonCompareId = commonCompareId;
    }

    public EyeBodyCompare(String eyeBodyChangeId, String userId, String creDate, String creDateTime, String fileCreDateTime, String savePath, String delYn) {
        this.eyeBodyChangeId = eyeBodyChangeId;
        this.userId = userId;
        this.creDate = creDate;
        this.creDateTime = creDateTime;
        this.fileCreDateTime = fileCreDateTime;
        this.savePath = savePath;
        this.delYn = delYn;
    }

    public EyeBodyCompare() {}

    public String getEyeBodyChangeId() {
        return eyeBodyChangeId;
    }

    public void setEyeBodyChangeId(String eyeBodyChangeId) {
        this.eyeBodyChangeId = eyeBodyChangeId;
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

    public String getFileCreDateTime() {
        return fileCreDateTime;
    }

    public void setFileCreDateTime(String fileCreDateTime) {
        this.fileCreDateTime = fileCreDateTime;
    }

    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    public String getDelYn() {
        return delYn;
    }

    public void setDelYn(String delYn) {
        this.delYn = delYn;
    }

    public String getCommonCompareId() {
        return commonCompareId;
    }

    public void setCommonCompareId(String commonCompareId) {
        this.commonCompareId = commonCompareId;
    }
}
