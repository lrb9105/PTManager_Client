package com.teamnova.ptmanager.viewmodel.schedule.lecture;

import android.os.Handler;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.teamnova.ptmanager.model.lecture.LectureInfoDto;
import com.teamnova.ptmanager.model.userInfo.FriendInfoDto;
import com.teamnova.ptmanager.repository.friend.FriendRepository;
import com.teamnova.ptmanager.repository.schedule.lecture.LectureRepository;

import java.util.ArrayList;

public class LectureViewModel extends ViewModel {
    // 강의관련 데이터처리를 담당할 저장소 객체
    private LectureRepository lectureRepository;

    // 친구 목록
    private ArrayList<LectureInfoDto> lectureList;

    // 초기화
    public LectureViewModel() {
        lectureRepository = new LectureRepository();

    }

    // 강의목록 가져오기
    public void getLectureList(Handler handler, String trainerId){
        lectureRepository.getLectureList(handler, trainerId);
    }

    // 수강 가능한 회원목록 가져오기
    public void getLectureRegisteredMemberList(Handler handler, String lectureId){
        lectureRepository.getLectureRegisteredMemberList(handler, lectureId);
    }

    // 레슨 가능한 회원목록 가져오기
    public void getLessonRegisteredMemberList(Handler handler, String lessonId){
        lectureRepository.getLessonRegisteredMemberList(handler, lessonId);
    }

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
