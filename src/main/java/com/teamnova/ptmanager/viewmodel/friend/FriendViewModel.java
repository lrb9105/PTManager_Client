package com.teamnova.ptmanager.viewmodel.friend;

import android.os.Handler;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.teamnova.ptmanager.model.userInfo.FriendInfoDto;
import com.teamnova.ptmanager.model.userInfo.UserInfoDto;
import com.teamnova.ptmanager.repository.friend.FriendRepository;
import com.teamnova.ptmanager.repository.login.LoginRepository;

import java.util.ArrayList;

public class FriendViewModel extends ViewModel {
    // 친구관련 데이터처리를 담당할 저장소 객체
    private FriendRepository friendRepository;

    // 친구 정보
    private MutableLiveData<FriendInfoDto> friendInfo;

    // 친구 목록
    private ArrayList<FriendInfoDto> friendsList;

    // 초기화
    public FriendViewModel() {
        friendRepository = new FriendRepository();
        friendInfo = new MutableLiveData<>();

    }

    // 친구목록에 추가
    public void addToFriend(Handler handler, String ownerId, String ownerType, String friendId, String friendType){
        friendRepository.addToFriend(handler, ownerId, ownerType, friendId, friendType);
    }

    // 친구목록에 가져오기
    public void getFriendsList(Handler handler, String ownerId){
        friendRepository.getFriendsList(handler, ownerId);
    }

    // 친구정보 가져오기
    public void getFriendInfo( String friendId){
        friendRepository.getFriendInfo(friendInfo, friendId);
    }

    // 트레이너-회원 연결 완료
    public void completeConnect(String memberId){
        friendRepository.completeConnect(memberId);
    }

    public MutableLiveData<FriendInfoDto> getFriendInfo() {
        return friendInfo;
    }

    public ArrayList<FriendInfoDto> getFriendsList() {
        return friendsList;
    }

    public void setFriendsList(ArrayList<FriendInfoDto> friendsList) {
        this.friendsList = friendsList;
    }
}
