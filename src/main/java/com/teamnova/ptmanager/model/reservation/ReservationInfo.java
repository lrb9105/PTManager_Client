package com.teamnova.ptmanager.model.reservation;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ReservationInfo implements Serializable {
    @SerializedName("lessonSchId")
    private String lessonSchId;
    @SerializedName("userName")
    private String userName;
    @SerializedName("confirmYn")
    private String confirmYn;
    @SerializedName("confirmYnName")
    private String confirmYnName;
    @SerializedName("lessonDate")
    private String lessonDate;
    @SerializedName("lessonSrtTime")
    private String lessonSrtTime;
    @SerializedName("lessonEndTime")
    private String lessonEndTime;
    @SerializedName("cancelYn")
    private String cancelYn;
    @SerializedName("cancelReqDatetime")
    private String cancelReqDatetime;
    @SerializedName("attendanceYn")
    private String attendanceYn;
    @SerializedName("attendanceYnName")
    private String attendanceYnName;
    @SerializedName("memo")
    private String memo;
    @SerializedName("lectureName")
    private String lectureName;
    @SerializedName("reservationConfirmYn")
    private String reservationConfirmYn;
    @SerializedName("profileId")
    private String profileId;
    @SerializedName("approvementYn")
    private String approvementYn;
    @SerializedName("rejectReason")
    private String rejectReason;
    @SerializedName("cancelReason")
    private String cancelReason;
    private String check;


    public ReservationInfo(String lessonSchId, String userName, String confirmYn, String confirmYnName, String lessonDate, String lessonSrtTime, String lessonEndTime, String cancelYn, String cancelReqDatetime, String attendanceYn, String attendanceYnName, String memo, String lectureName, String reservationConfirmYn, String profileId, String approvementYn, String rejectReason, String cancelReason) {
        this.lessonSchId = lessonSchId;
        this.userName = userName;
        this.confirmYn = confirmYn;
        this.confirmYnName = confirmYnName;
        this.lessonDate = lessonDate;
        this.lessonSrtTime = lessonSrtTime;
        this.lessonEndTime = lessonEndTime;
        this.cancelYn = cancelYn;
        this.cancelReqDatetime = cancelReqDatetime;
        this.attendanceYn = attendanceYn;
        this.attendanceYnName = attendanceYnName;
        this.memo = memo;
        this.lectureName = lectureName;
        this.reservationConfirmYn = reservationConfirmYn;
        this.profileId = profileId;
        this.approvementYn = approvementYn;
        this.rejectReason = rejectReason;
        this.cancelReason = cancelReason;
    }

    public String getLessonSchId() {
        return lessonSchId;
    }

    public void setLessonSchId(String lessonSchId) {
        this.lessonSchId = lessonSchId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getConfirmYn() {
        return confirmYn;
    }

    public void setConfirmYn(String confirmYn) {
        this.confirmYn = confirmYn;
    }

    public String getConfirmYnName() {
        return confirmYnName;
    }

    public void setConfirmYnName(String confirmYnName) {
        this.confirmYnName = confirmYnName;
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

    public String getCancelYn() {
        return cancelYn;
    }

    public void setCancelYn(String cancelYn) {
        this.cancelYn = cancelYn;
    }


    public String getAttendanceYn() {
        return attendanceYn;
    }

    public void setAttendanceYn(String attendanceYn) {
        this.attendanceYn = attendanceYn;
    }

    public String getAttendanceYnName() {
        return attendanceYnName;
    }

    public void setAttendanceYnName(String attendanceYnName) {
        this.attendanceYnName = attendanceYnName;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getLectureName() {
        return lectureName;
    }

    public void setLectureName(String lectureName) {
        this.lectureName = lectureName;
    }

    public String getReservationConfirmYn() {
        return reservationConfirmYn;
    }

    public void setReservationConfirmYn(String reservationConfirmYn) {
        this.reservationConfirmYn = reservationConfirmYn;
    }

    public String getCancelReqDatetime() {
        return cancelReqDatetime;
    }

    public void setCancelReqDatetime(String cancelReqDatetime) {
        this.cancelReqDatetime = cancelReqDatetime;
    }

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    public String getCheck() {
        return check;
    }

    public void setCheck(String check) {
        this.check = check;
    }

    public String getApprovementYn() {
        return approvementYn;
    }

    public void setApprovementYn(String approvementYn) {
        this.approvementYn = approvementYn;
    }

    public String getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    @Override
    public String toString() {
        return "ReservationInfo{" +
                "lessonSchId='" + lessonSchId + '\'' +
                ", userName='" + userName + '\'' +
                ", confirmYn='" + confirmYn + '\'' +
                ", confirmYnName='" + confirmYnName + '\'' +
                ", lessonDate='" + lessonDate + '\'' +
                ", lessonSrtTime='" + lessonSrtTime + '\'' +
                ", lessonEndTime='" + lessonEndTime + '\'' +
                ", cancelYn='" + cancelYn + '\'' +
                ", cancelReqDatetime='" + cancelReqDatetime + '\'' +
                ", attendanceYn='" + attendanceYn + '\'' +
                ", attendanceYnName='" + attendanceYnName + '\'' +
                ", memo='" + memo + '\'' +
                ", lectureName='" + lectureName + '\'' +
                ", reservationConfirmYn='" + reservationConfirmYn + '\'' +
                ", profileId='" + profileId + '\'' +
                ", check='" + check + '\'' +
                '}';
    }
}
