package com.teamnova.ptmanager.viewmodel.schedule.lesson;

import android.os.Handler;

import androidx.lifecycle.ViewModel;

import com.teamnova.ptmanager.model.lecture.LectureInfoDto;
import com.teamnova.ptmanager.model.lecture.pass.PassInfoDto;
import com.teamnova.ptmanager.model.lesson.LessonInfo;
import com.teamnova.ptmanager.repository.schedule.lecture.LectureRepository;
import com.teamnova.ptmanager.repository.schedule.lesson.LessonRepository;

import java.util.ArrayList;

public class LessonViewModel extends ViewModel {
    // 강의관련 데이터처리를 담당할 저장소 객체
    private LessonRepository lessonRepository;

    // 초기화
    public LessonViewModel() {
        lessonRepository = new LessonRepository();
    }

    // 수강권 정보 등록하기
    public void registerLessonInfo(Handler handler, LessonInfo lessonInfo){
        lessonRepository.registerLessonInfo(handler, lessonInfo);
    }


    /*// 강의목록 가져오기
    public void getLectureList(Handler handler, String trainerId){
        lessonRepository.getLectureList(handler, trainerId);
    }

    // 수강 가능한 회원목록 가져오기
    public void getLectureRegisteredMemberList(Handler handler, String lectureId){
        lessonRepository.getLectureRegisteredMemberList(handler, lectureId);
    }

    // 레슨 가능한 회원목록 가져오기
    public void getLessonRegisteredMemberList(Handler handler, String lessonId){
        lessonRepository.getLessonRegisteredMemberList(handler, lessonId);
    }*/

    /*// 친구목록에 추가
    public void addToFriend(Handler handler, String ownerId, String ownerType, String friendId, String friendType){
        friendRepository.addToFriend(handler, ownerId, ownerType, friendId, friendType);
    }*/

    /*// 친구정보 가져오기
    public void getFriendInfo( String friendId){
        friendRepository.getFriendInfo(friendInfo, friendId);
    }

    public MutableLiveData<FriendInfoDto> getFriendInfo() {
        return friendInfo;
    }

    public ArrayList<FriendInfoDto> getFriendsList() {
        return friendsList;
    }

    public void setFriendsList(ArrayList<FriendInfoDto> friendsList) {
        this.friendsList = friendsList;
    }*/
}
