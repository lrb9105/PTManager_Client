package com.teamnova.ptmanager.repository.schedule.lecture;

import android.os.Handler;

import androidx.lifecycle.MutableLiveData;

import com.teamnova.ptmanager.model.lecture.pass.PassInfoDto;
import com.teamnova.ptmanager.model.userInfo.FriendInfoDto;
import com.teamnova.ptmanager.network.friend.FriendApiClient;
import com.teamnova.ptmanager.network.schedule.lecture.LectureApiClient;

public class LectureRepository {
    // 서버와 통신을 담당하는 객체
    private LectureApiClient lectureApiClient;

    // 초기화
    public LectureRepository() {
        lectureApiClient = new LectureApiClient();
    }

    // 강의목록 가져오기
    public void getLectureList(Handler handler, String ownerId){
        lectureApiClient.getLectureList(handler, ownerId);
    }

    // 수강 가능한 회원목록 가져오기
    public void getLectureRegisteredMemberList(Handler handler, String lectureId){
        lectureApiClient.getLectureRegisteredMemberList(handler, lectureId);
    }

    // 레슨 가능한 회원목록 가져오기
    public void getLessonRegisteredMemberList(Handler handler, String lessonId){
        lectureApiClient.getLessonRegisteredMemberList(handler, lessonId);
    }

    // 수강권 정보 등록하기
    public void registerPassInto(Handler handler, PassInfoDto passInfo){
        lectureApiClient.registerPassInto(handler, passInfo);
    }


    /*// 친구목록에 추가
    public void addToFriend(Handler handler, String ownerId, String ownerType, String friendId, String friendType){
        friendApiClient.addToFriend(handler, ownerId, ownerType, friendId, friendType);
    }*/

    /*// 친구정보 가져오기
    public void getFriendInfo(MutableLiveData<FriendInfoDto> friendInfo, String friendId){
        friendApiClient.getFriendInfo(friendInfo, friendId);
    }*/
}
