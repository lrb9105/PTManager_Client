package com.teamnova.ptmanager.model.lecture;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * 강의정보를 담는 dto
 * */
public class LectureInfoDto implements Serializable {
    @SerializedName("lectureId")
    private String lectureId;
    @SerializedName("trainerId")
    private String trainerId;
    @SerializedName("lectureName")
    private String lectureName;
    @SerializedName("lectureDuration") //진행시간
    private int lectureDuration;
    @SerializedName("totalParticipationCnt") // 총원(명 or 팀)
    private int totalParticipationCnt;
    @SerializedName("participationCnt") //정원 - 한수업에 참여할 수 있는 인원
    private int participationCnt;
    @SerializedName("currentLecturedCnt") // 현재 수강중인 인원(명 or 팀)
    private int currentLecturedCnt;
    @SerializedName("memo") // 수업에 대한 메모
    private String memo;

    public LectureInfoDto(String lectureId, String trainerId, String lectureName, int lectureDuration, int totalParticipationCnt, int participationCnt, int currentLecturedCnt, String memo) {
        this.lectureId = lectureId;
        this.trainerId = trainerId;
        this.lectureName = lectureName;
        this.lectureDuration = lectureDuration;
        this.totalParticipationCnt = totalParticipationCnt;
        this.participationCnt = participationCnt;
        this.currentLecturedCnt = currentLecturedCnt;
        this.memo = memo;
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

    public String getLectureName() {
        return lectureName;
    }

    public void setLectureName(String lectureName) {
        this.lectureName = lectureName;
    }

    public int getLectureDuration() {
        return lectureDuration;
    }

    public void setLectureDuration(int lectureDuration) {
        this.lectureDuration = lectureDuration;
    }

    public int getTotalParticipationCnt() {
        return totalParticipationCnt;
    }

    public void setTotalParticipationCnt(int totalParticipationCnt) {
        this.totalParticipationCnt = totalParticipationCnt;
    }

    public int getParticipationCnt() {
        return participationCnt;
    }

    public void setParticipationCnt(int participationCnt) {
        this.participationCnt = participationCnt;
    }

    public int getCurrentLecturedCnt() {
        return currentLecturedCnt;
    }

    public void setCurrentLecturedCnt(int currentLecturedCnt) {
        this.currentLecturedCnt = currentLecturedCnt;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }
}
