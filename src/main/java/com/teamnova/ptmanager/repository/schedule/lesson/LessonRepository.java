package com.teamnova.ptmanager.repository.schedule.lesson;

import android.os.Handler;

import com.teamnova.ptmanager.model.lecture.pass.PassInfoDto;
import com.teamnova.ptmanager.model.lesson.LessonInfo;
import com.teamnova.ptmanager.network.schedule.lecture.LectureApiClient;
import com.teamnova.ptmanager.network.schedule.lesson.LessonApiClient;

public class LessonRepository {
    // 서버와 통신을 담당하는 객체
    private LessonApiClient lessonApiClient;

    // 초기화
    public LessonRepository() {
        lessonApiClient = new LessonApiClient();
    }

    // 레슨 정보 등록하기
    public void registerLessonInfo(Handler handler, LessonInfo lessonInfo){
        lessonApiClient.registerLessonInfo(handler, lessonInfo);
    }

    /*// 강의목록 가져오기
    public void getLectureList(Handler handler, String ownerId){
        lessonApiClient.getLectureList(handler, ownerId);
    }

    // 수강 가능한 회원목록 가져오기
    public void getLectureRegisteredMemberList(Handler handler, String lectureId){
        lessonApiClient.getLectureRegisteredMemberList(handler, lectureId);
    }

    // 레슨 가능한 회원목록 가져오기
    public void getLessonRegisteredMemberList(Handler handler, String lessonId){
        lessonApiClient.getLessonRegisteredMemberList(handler, lessonId);
    }*/


    /*// 친구목록에 추가
    public void addToFriend(Handler handler, String ownerId, String ownerType, String friendId, String friendType){
        friendApiClient.addToFriend(handler, ownerId, ownerType, friendId, friendType);
    }*/

    /*// 친구정보 가져오기
    public void getFriendInfo(MutableLiveData<FriendInfoDto> friendInfo, String friendId){
        friendApiClient.getFriendInfo(friendInfo, friendId);
    }*/
}
