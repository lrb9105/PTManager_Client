package com.teamnova.ptmanager.model.lesson;

import com.google.gson.annotations.SerializedName;
import com.teamnova.ptmanager.model.userInfo.FriendInfoDto;

import java.io.Serializable;
import java.util.ArrayList;

public class LessonInfo implements Serializable {
    @SerializedName("lessonId")
    private String lessonId;
    @SerializedName("trainerId")
    private String trainerId;
    @SerializedName("memberList")
    private ArrayList<FriendInfoDto> memberList;
    @SerializedName("repeatSchId")
    private String repeatSchId;
    @SerializedName("lectureId")
    private String lectureId;
    @SerializedName("lessonDate")
    private String lessonDate;
    @SerializedName("lessonSrtTime")
    private String lessonSrtTime;
    @SerializedName("lessonEndTime")
    private String lessonEndTime;
    @SerializedName("repeatYn")
    private String repeatYn;
    @SerializedName("confirmYn")
    private String confirmYn;
    @SerializedName("cancelYn")
    private String cancelYn;
    @SerializedName("cancelReason")
    private String cancelReason;
    @SerializedName("memo")
    private String memo;

    public LessonInfo(String lessonId, String trainerId, ArrayList<FriendInfoDto> memberList, String repeatSchId, String lectureId, String lessonDate, String lessonSrtTime, String lessonEndTime, String repeatYn, String confirmYn, String cancelYn, String cancelReason, String memo) {
        this.lessonId = lessonId;
        this.trainerId = trainerId;
        this.memberList = memberList;
        this.repeatSchId = repeatSchId;
        this.lectureId = lectureId;
        this.lessonDate = lessonDate;
        this.lessonSrtTime = lessonSrtTime;
        this.lessonEndTime = lessonEndTime;
        this.repeatYn = repeatYn;
        this.confirmYn = confirmYn;
        this.cancelYn = cancelYn;
        this.cancelReason = cancelReason;
        this.memo = memo;
    }

    public LessonInfo(){}

    public String getLessonId() {
        return lessonId;
    }

    public void setLessonId(String lessonId) {
        this.lessonId = lessonId;
    }

    public String getTrainerId() {
        return trainerId;
    }

    public void setTrainerId(String trainerId) {
        this.trainerId = trainerId;
    }

    public ArrayList<FriendInfoDto> getMemberList() {
        return memberList;
    }

    public void setMemberList(ArrayList<FriendInfoDto> memberList) {
        this.memberList = memberList;
    }

    public String getRepeatSchId() {
        return repeatSchId;
    }

    public void setRepeatSchId(String repeatSchId) {
        this.repeatSchId = repeatSchId;
    }

    public String getLectureId() {
        return lectureId;
    }

    public void setLectureId(String lectureId) {
        this.lectureId = lectureId;
    }

    public String getLessonDate() {
        return lessonDate;
    }

    public void setLessonDate(String lessonDate) {
        this.lessonDate = lessonDate;
    }

    public String getLessonSrtTime() {
        return lessonSrtTime;
    }

    public void setLessonSrtTime(String lessonSrtTime) {
        this.lessonSrtTime = lessonSrtTime;
    }

    public String getLessonEndTime() {
        return lessonEndTime;
    }

    public void setLessonEndTime(String lessonEndTime) {
        this.lessonEndTime = lessonEndTime;
    }

    public String getRepeatYn() {
        return repeatYn;
    }

    public void setRepeatYn(String repeatYn) {
        this.repeatYn = repeatYn;
    }

    public String getConfirmYn() {
        return confirmYn;
    }

    public void setConfirmYn(String confirmYn) {
        this.confirmYn = confirmYn;
    }

    public String getCancelYn() {
        return cancelYn;
    }

    public void setCancelYn(String cancelYn) {
        this.cancelYn = cancelYn;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    @Override
    public String toString() {
        return "LessonInfo{" +
                "trainerId='" + trainerId + '\'' +
                ", lectureId='" + lectureId + '\'' +
                ", lessonDate='" + lessonDate + '\'' +
                ", lessonSrtTime='" + lessonSrtTime + '\'' +
                ", lessonEndTime='" + lessonEndTime + '\'' +
                ", confirmYn='" + confirmYn + '\'' +
                ", cancelYn='" + cancelYn + '\'' +
                ", memo='" + memo + '\'' +
                ", getMemberList().size()'" + getMemberList().size() +
                '}';
    }
}
