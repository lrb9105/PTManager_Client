package com.teamnova.ptmanager.viewmodel.chatting;

import android.os.Handler;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.teamnova.ptmanager.model.changehistory.eyebody.EyeBody;
import com.teamnova.ptmanager.model.changehistory.eyebody.EyeBodyCompare;
import com.teamnova.ptmanager.model.changehistory.eyebody.EyeBodyHistoryInfo;
import com.teamnova.ptmanager.model.chatting.ChatMsgInfo;
import com.teamnova.ptmanager.model.chatting.ChatRoomInfoForListDto;
import com.teamnova.ptmanager.repository.changehistory.eyebody.EyeBodyRepository;
import com.teamnova.ptmanager.repository.chatting.ChattingRepository;

import java.util.ArrayList;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/** 채팅정보를 저장할  viewModel
 * */
public class ChattingViewModel extends ViewModel {
    // 채팅관련데이터 처리를 담당할 저장소 객체
    private ChattingRepository chattingRepository;

    // 채팅방리스트에서 사용할 채팅방 정보
    private MutableLiveData<ArrayList<ChatRoomInfoForListDto>> chattingList;

    // 초기화
    public ChattingViewModel() {
        chattingRepository = new ChattingRepository();
        chattingList = new MutableLiveData<>();
    }

    // 채팅방 리스트 반환
    public MutableLiveData<ArrayList<ChatRoomInfoForListDto>> getChattingList() {
        return chattingList;
    }

    // 메시지 리스트 가져오기
    public ArrayList<ChatMsgInfo> getMsgListInfo(Handler handler, String roomId){
        return chattingRepository.getMsgListInfo(handler, roomId);
    }
}
