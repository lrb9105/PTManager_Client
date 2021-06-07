package com.teamnova.ptmanager.repository.friend;

import android.os.Handler;

import androidx.lifecycle.MutableLiveData;

import com.teamnova.ptmanager.model.userInfo.FriendInfoDto;
import com.teamnova.ptmanager.model.userInfo.UserInfoDto;
import com.teamnova.ptmanager.network.friend.FriendApiClient;
import com.teamnova.ptmanager.network.login.LoginApiClient;

public class FriendRepository {
    // 서버와 통신을 담당하는 객체
    private FriendApiClient friendApiClient;

    // 초기화
    public FriendRepository() {
        friendApiClient = new FriendApiClient();
    }

    // 친구목록에 추가
    public void addToFriend(Handler handler, String ownerId, String ownerType, String friendId, String friendType){
        friendApiClient.addToFriend(handler, ownerId, ownerType, friendId, friendType);
    }

    // 친구목록에 가져오기
    public void getFriendsList(Handler handler, String ownerId){
        friendApiClient.getFriendsList(handler, ownerId);
    }

    // 친구정보 가져오기
    public void getFriendInfo(MutableLiveData<FriendInfoDto> friendInfo, String friendId){
        friendApiClient.getFriendInfo(friendInfo, friendId);
    }
}
