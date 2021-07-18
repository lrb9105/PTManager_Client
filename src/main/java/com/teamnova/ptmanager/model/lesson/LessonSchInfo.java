package com.teamnova.ptmanager.model.lesson;

import com.google.gson.annotations.SerializedName;
import com.teamnova.ptmanager.model.userInfo.FriendInfoDto;

import java.io.Serializable;
import java.util.ArrayList;

public class LessonSchInfo implements Serializable {
    @SerializedName("lessonSchId")
    private String lessonSchId;
    @SerializedName("userName")
    private String userName;
    @SerializedName("confirmYn")
    private String confirmYn;
    @SerializedName("confirmYnName")
    private String confirmYnName;
    @SerializedName("repeatSchId")
    private String repeatSchId;
    @SerializedName("lessonDate")
    private String lessonDate;
    @SerializedName("lessonSrtTime")
    private String lessonSrtTime;
    @SerializedName("lessonEndTime")
    private String lessonEndTime;
    @SerializedName("repeatYn")
    private String repeatYn;
    @SerializedName("cancelYn")
    private String cancelYn;
    @SerializedName("cancelReason")
    private String cancelReason;
    @SerializedName("attendanceYn")
    private String attendanceYn;
    @SerializedName("attendanceYnName")
    private String attendanceYnName;
    @SerializedName("memo")
    private String memo;
    @SerializedName("lectureName")
    private String lectureName;
    @SerializedName("repeatDay")
    private String repeatDay;
    @SerializedName("loginId")
    private String loginId;
    @SerializedName("reservationConfirmYn")
    private String reservationConfirmYn;


    public LessonSchInfo(String lessonSchId, String userName, String confirmYn, String confirmYnName, String repeatSchId, String lessonDate, String lessonSrtTime, String lessonEndTime, String repeatYn, String cancelYn, String cancelReason, String attendanceYn, String attendanceYnName, String memo, String lectureName, String repeatDay, String loginId, String reservationConfirmYn) {
        this.lessonSchId = lessonSchId;
        this.userName = userName;
        this.confirmYn = confirmYn;
        this.confirmYnName = confirmYnName;
        this.repeatSchId = repeatSchId;
        this.lessonDate = lessonDate;
        this.lessonSrtTime = lessonSrtTime;
        this.lessonEndTime = lessonEndTime;
        this.repeatYn = repeatYn;
        this.cancelYn = cancelYn;
        this.cancelReason = cancelReason;
        this.attendanceYn = attendanceYn;
        this.attendanceYnName = attendanceYnName;
        this.memo = memo;
        this.lectureName = lectureName;
        this.repeatDay = repeatDay;
        this.loginId = loginId;
        this.reservationConfirmYn = reservationConfirmYn;
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

    public String getRepeatSchId() {
        return repeatSchId;
    }

    public void setRepeatSchId(String repeatSchId) {
        this.repeatSchId = repeatSchId;
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

    public String getRepeatDay() {
        return repeatDay;
    }

    public void setRepeatDay(String repeatDay) {
        this.repeatDay = repeatDay;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getReservationConfirmYn() {
        return reservationConfirmYn;
    }

    public void setReservationConfirmYn(String reservationConfirmYn) {
        this.reservationConfirmYn = reservationConfirmYn;
    }
}
