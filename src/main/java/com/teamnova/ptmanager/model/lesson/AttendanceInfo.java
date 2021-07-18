package com.teamnova.ptmanager.model.lesson;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AttendanceInfo implements Serializable {
    @SerializedName("lessonSchId")
    private String lessonSchId;
    @SerializedName("lecturePassId")
    private String lecturePassId;
    @SerializedName("attendanceYn")
    private String attendanceYn;

    public AttendanceInfo(String lessonSchId, String lecturePassId, String attendanceYn) {
        this.lessonSchId = lessonSchId;
        this.lecturePassId = lecturePassId;
        this.attendanceYn = attendanceYn;
    }

    public String getLessonSchId() {
        return lessonSchId;
    }

    public void setLessonSchId(String lessonSchId) {
        this.lessonSchId = lessonSchId;
    }

    public String getLecturePassId() {
        return lecturePassId;
    }

    public void setLecturePassId(String lecturePassId) {
        this.lecturePassId = lecturePassId;
    }

    public String getAttendanceYn() {
        return attendanceYn;
    }

    public void setAttendanceYn(String attendanceYn) {
        this.attendanceYn = attendanceYn;
    }
}
