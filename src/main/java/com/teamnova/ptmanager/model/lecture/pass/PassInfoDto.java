package com.teamnova.ptmanager.model.lecture.pass;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * 강의정보를 담는 dto
 * */
public class PassInfoDto implements Serializable {
    @SerializedName("lecturePassId") // 수강권 id
    private String lecturePassId;
    @SerializedName("lectureId") // 강의 ID
    private String lectureId;
    @SerializedName("trainerId") // 트레이너 ID(USER_ID임 서버에서 변경해줘야 함)
    private String trainerId;
    @SerializedName("memberId") // 회원 ID(USER_ID임 서버에서 변경해줘야 함)
    private String memberId;
    @SerializedName("srtDate") // 시작일
    private String srtDate;
    @SerializedName("totalCnt") // 총횟수
    private int totalCnt;
    @SerializedName("usedCnt") // 사용횟수
    private int usedCnt;
    @SerializedName("memo") // 수업에 대한 메모
    private String memo;

    public PassInfoDto(String lecturePassId, String lectureId, String trainerId, String memberId, String srtDate, int totalCnt, int usedCnt, String memo) {
        this.lecturePassId = lecturePassId;
        this.lectureId = lectureId;
        this.trainerId = trainerId;
        this.memberId = memberId;
        this.srtDate = srtDate;
        this.totalCnt = totalCnt;
        this.usedCnt = usedCnt;
        this.memo = memo;
    }

    public PassInfoDto() {}

    public String getLecturePassId() {
        return lecturePassId;
    }

    public void setLecturePassId(String lecturePassId) {
        this.lecturePassId = lecturePassId;
    }

    public String getLectureId() {
        return lectureId;
    }

    public void setLectureId(String lectureId) {
        this.lectureId = lectureId;
    }

    public String getTrainerId() {
        return trainerId;
    }

    public void setTrainerId(String trainerId) {
        this.trainerId = trainerId;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getSrtDate() {
        return srtDate;
    }

    public void setSrtDate(String srtDate) {
        this.srtDate = srtDate;
    }

    public int getTotalCnt() {
        return totalCnt;
    }

    public void setTotalCnt(int totalCnt) {
        this.totalCnt = totalCnt;
    }

    public int getUsedCnt() {
        return usedCnt;
    }

    public void setUsedCnt(int usedCnt) {
        this.usedCnt = usedCnt;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }
}
