package com.teamnova.ptmanager.viewmodel.chatting;

import android.os.Handler;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.teamnova.ptmanager.model.chatting.ChatMsgInfo;
import com.teamnova.ptmanager.model.chatting.ChatRoomInfoForListDto;
import com.teamnova.ptmanager.repository.chatting.ChattingMsgRepository;
import com.teamnova.ptmanager.repository.chatting.ChattingRepository;

import java.util.ArrayList;

/** 채팅정보를 저장할  viewModel
 * */
public class ChattingMsgViewModel extends ViewModel {
    // 채팅관련데이터 처리를 담당할 저장소 객체
    private ChattingMsgRepository chattingMsgRepository;

    // 채팅방리스트에서 사용할 채팅방 정보
    private MutableLiveData<ArrayList<ChatRoomInfoForListDto>> chattingList;

    // 초기화
    public ChattingMsgViewModel() {
        chattingMsgRepository = new ChattingMsgRepository();
        chattingList = new MutableLiveData<>();
    }

    // 채팅방 리스트 반환
    public MutableLiveData<ArrayList<ChatRoomInfoForListDto>> getChattingList() {
        return chattingList;
    }

    // 메시지 리스트 가져오기
    public ArrayList<ChatMsgInfo> getMsgListInfo(String roomId, String userId, int limit, int pageNo){
        return chattingMsgRepository.getMsgListInfo(roomId, userId, limit, pageNo);
    }
}
