package com.teamnova.ptmanager.ui.moreinfo;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.teamnova.ptmanager.R;
import com.teamnova.ptmanager.databinding.FragmentChattingListBinding;
import com.teamnova.ptmanager.databinding.FragmentMoreinfoBinding;
import com.teamnova.ptmanager.manager.ChattingRoomManager;
import com.teamnova.ptmanager.model.chatting.ChatMsgInfo;
import com.teamnova.ptmanager.model.chatting.ChatRoomInfoDto;
import com.teamnova.ptmanager.model.chatting.ChatRoomInfoForListDto;
import com.teamnova.ptmanager.model.chatting.ChattingMemberDto;
import com.teamnova.ptmanager.model.lesson.LessonSchInfo;
import com.teamnova.ptmanager.model.userInfo.FriendInfoDto;
import com.teamnova.ptmanager.ui.chatting.ChattingActivity;
import com.teamnova.ptmanager.ui.chatting.ChattingPossibleMemberListActivity;
import com.teamnova.ptmanager.ui.chatting.adapter.ChattingListAdapter;
import com.teamnova.ptmanager.ui.home.member.MemberHomeActivity;
import com.teamnova.ptmanager.ui.home.trainer.TrainerHomeActivity;
import com.teamnova.ptmanager.ui.login.LoginActivity;
import com.teamnova.ptmanager.ui.login.findpw.FindPw3Activity;
import com.teamnova.ptmanager.util.GetDate;
import com.teamnova.ptmanager.viewmodel.chatting.ChattingViewModel;
import com.teamnova.ptmanager.viewmodel.friend.FriendViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.teamnova.ptmanager.ui.chatting.ChattingActivity.timeDifference;

/**
 * 채팅방 리스트 프래그먼트
 * */
public class ChatListFragment2 extends Fragment implements View.OnClickListener {
    FragmentChattingListBinding binding;

    // 데이터를 공유할 viewModel
    private FriendViewModel friendViewModel;
    private ChattingViewModel chattingViewModel;

    // 회원 정보
    private FriendInfoDto memberInfo;

    // 채팅방 나가기 시 채팅방 리스트에서 채팅방을 삭제하기 위한 정보를 가지고 있는 객체
    // 채팅방 만들기 시 채팅방 리스트에 채팅방을 추가하기 위한 정보를 가지고 있는 객체
    private ActivityResultLauncher<Intent> startActivityResult;

    // 회원선택 리스트에서 다시 이 화면으로 돌아와 다시 바로 채팅방화면으로 이동하기 위한 객체
    private ActivityResultLauncher<Intent> startActivityResult2;

    // 채팅방 매니져
    private ChattingRoomManager chattingRoomManager;

    // 리사이클러뷰
    private RecyclerView recyclerView;
    private ChattingListAdapter chattingListAdapter;
    private LinearLayoutManager linearLayoutManager;
    // 보정해야 하는지 여부 - 처음 조회시만 보정해야 한다!
    private boolean shouldCompensate = false;

    public ChatListFragment2() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TrainerHomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatListFragment2 newInstance(String param1, String param2) {
        ChatListFragment2 fragment = new ChatListFragment2();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // 채팅방리스트에서 채팅방 삭제
        startActivityResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            System.out.println("2. 뒤로가기 클릭 시 여기로 들어 옴");

                            // 방을 나간 채팅방 정보
                            ChatRoomInfoForListDto exitedChatRoomInfo = (ChatRoomInfoForListDto)result.getData().getSerializableExtra("exitedChatRoomInfo");
                            // 새로 생성 혹은 수정된 방 정보
                            ChatRoomInfoForListDto addedOrModifiedChatRoomInfo = (ChatRoomInfoForListDto)result.getData().getSerializableExtra("addedOrModifiedChatRoomInfo");

                            System.out.println("3. 수정 시 addedOrModifiedChatRoomInfo값 있어야 함:" + addedOrModifiedChatRoomInfo);

                            // 방 나가기
                            if(exitedChatRoomInfo != null){
                                // 채팅뷰모델의 채팅리스트 객체 업데이트
                                ArrayList<ChatRoomInfoForListDto> c = chattingViewModel.getChattingList().getValue();
                                // 나온 방 리스트에서 삭제
                                c.remove(exitedChatRoomInfo);

                                chattingViewModel.getChattingList().setValue(c);
                            } else if(addedOrModifiedChatRoomInfo != null) { // 방 생성 or 방 수정
                                // 채팅뷰모델의 채팅리스트 객체 업데이트
                                /*ArrayList<ChatRoomInfoForListDto> list = chattingViewModel.getChattingList().getValue();
                                // 리스트에서 넘어온 채팅방 정보 객체와 동일한 아이디를 갖는 객체가 있는지 확인
                                boolean isModified = false;
                                boolean isNotModifiedAndAdded = false;

                                for(int i = 0; i < list.size(); i++){
                                    System.out.println("4. 수정 시 여기 들어와야 함");
                                    // 이미 리스트에 존재하는 채팅방이고 내용이 수정되었다면 -> 포지션 0으로 옮기기
                                    Log.e("가져온 메시지 인덱스","" + addedOrModifiedChatRoomInfo.getMsgIdx());
                                    if(list.get(i).getChattingRoomId().equals(addedOrModifiedChatRoomInfo.getChattingRoomId())){
                                        // 메시지 idx가 동일하다면 변경안 된 것
                                        Log.e("기존 메시지 인덱스","" + list.get(i).getMsgIdx());

                                        // msgIdx가 다를때만 수정!
                                        if(list.get(i).getMsgIdx() == addedOrModifiedChatRoomInfo.getMsgIdx()) {
                                            // 같다면 읽지않은 메시지수만 변경해준다.
                                            break;
                                        }

                                        isModified = true;
                                        // 채팅방 수정시에는 시간보정 할 필요 없음
                                        shouldCompensate = false;
                                        // 수정된 값으로 변경하기 - 이전 리스트 삭제
                                        // 새로운 리스트 생성
                                        // 채팅방에서 가져온 메시지의 시간은 이미 보정이 완료된 상태이다. 따라서 역으로 한번더 보정을 해줘야 한다.
                                        //addedOrModifiedChatRoomInfo.setLatestMsgTime(GetDate.computeTimeDifferToServer(addedOrModifiedChatRoomInfo.getLatestMsgTime(), -timeDifference));

                                        list.remove(i);
                                        list.add(0, addedOrModifiedChatRoomInfo);

                                        chattingViewModel.getChattingList().setValue(list);

                                        System.out.println("5. 수정 시 여기 들어와야 함");

                                        break;
                                    } else{
                                        System.out.println("6. 수정 시 여기 들어오면 안 됨");
                                    }
                                }

                                System.out.println("isModified: " + isModified);*/

                                // 추가되었다면
                                /*if(!isModified) {
                                    System.out.println("여기 드러옴");
                                    // 채팅방 새로 생성시에는 시간보정 할 필요 없음
                                    shouldCompensate = false;

                                    list.add(0, addedOrModifiedChatRoomInfo);
                                    linearLayoutManager.scrollToPosition(0);
                                }*/
                            }
                        }
                    }
                });

        // 채팅방 만들기
        startActivityResult2 = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // 새로 초대한 인원 정보를 가져온다!
                            ArrayList<ChattingMemberDto> chatMemberList = (ArrayList<ChattingMemberDto>)result.getData().getSerializableExtra("chatMemberList");

                            Intent intent = new Intent(requireActivity(), ChattingActivity.class);
                            intent.putExtra("chatMemberList",chatMemberList);

                            Log.e("채팅방 만들기 ", "14. 채팅 참여자 정보와 함께 ChattingAct로 이동한다.");

                            startActivityResult.launch(intent);
                        }
                    }
                });

        super.onCreate(savedInstanceState);

        Log.d("홈 프래그먼트의 onCreate", "얍얍");

        // 채팅방 매니저
        chattingRoomManager = new ChattingRoomManager();

        // 서버시간 - 클라이언트 시간
        long serverTime = chattingRoomManager.getCurrentServerTime();
        long clientTime = System.currentTimeMillis();

        long timeDifference = clientTime - serverTime;

        System.out.println("serverTime: " + serverTime);
        System.out.println("clientTime: " + clientTime);
        System.out.println("timeDifference: " + timeDifference);


        // 부모 액티비티의 라이프사이클을 갖는 VIEWMODEL 생성
        friendViewModel = new ViewModelProvider(requireActivity()).get(FriendViewModel.class);
        chattingViewModel = new ViewModelProvider(requireActivity()).get(ChattingViewModel.class);

        // 가져온 채팅방리스트정보 observe
        chattingViewModel.getChattingList().observe(requireActivity(), chatRoomList -> {

            // 리사이클러뷰에 세팅
            chattingListAdapter = new ChattingListAdapter(chatRoomList, requireActivity(), startActivityResult, memberInfo, timeDifference,shouldCompensate);
            Log.e("채팅방 리사이클러뷰에 뿌려주기", "1. 아답터 생성");

            Log.e("채팅방 리사이클러뷰에 뿌려주기", " 2. 채팅방 리스트 정보 리사이클러뷰에 세팅 시작");
            recyclerView.setAdapter(chattingListAdapter);
            Log.e("채팅방 리사이클러뷰에 뿌려주기", " 3. 채팅방 리스트 정보 리사이클러뷰에 세팅 종료");

        });

        // 로그인한 회원 정보 observe
        friendViewModel.getFriendInfo().observe(requireActivity(), memberInfo -> {
            this.memberInfo = memberInfo;
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        Log.d("변화기록 프래그먼트의 onCreateView", "얍얍");

        // binder객체 생성 및 레이아웃 사용
        binding = FragmentChattingListBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        setOnclickListener();
        initialize();

        return view;
    }

    public void initialize(){
        // 리사이클러뷰 세팅
        recyclerView = binding.recyclerViewChattingList;
        linearLayoutManager = new LinearLayoutManager(requireActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        // 채팅방 매니져
        chattingRoomManager = new ChattingRoomManager();

        // 사용자 아이디
        String userId = null;

        if(memberInfo == null){
             userId = TrainerHomeActivity.staticLoginUserInfo.getUserId();
        } else{
            userId = memberInfo.getUserId();
        }

        // 서버에서 채팅방리스트 가져와서 뷰모델에 세팅
        Log.e("서버에서 채팅방리스트 가져와서 뷰모델에 세팅", " 1.시작");
        ArrayList<ChatRoomInfoForListDto> chatRoomList = this.getChatRoomList(userId);

        // 읽지않은 msg 세팅
        SharedPreferences sp = getContext().getSharedPreferences("chat", Activity.MODE_PRIVATE);
        Log.e("서버에서 채팅방리스트 가져와서 뷰모델에 세팅", " 9. 읽지 않은 메시지 수 세팅하기 위한 SharedPreference 생성 => " + sp);

        if(chatRoomList != null) {
            int chatRoomListSize = chatRoomList.size();
            Log.e("서버에서 채팅방리스트 가져와서 뷰모델에 세팅", " 10. 채팅방 리스트 존재 시 리스트 사이즈만큼 반복! 사이즈 => " + chatRoomListSize);

            for (int i = 0; i < chatRoomListSize; i++){
                // 각 채팅방 정보
                ChatRoomInfoForListDto chatRoom = chatRoomList.get(i);
                Log.e("서버에서 채팅방리스트 가져와서 뷰모델에 세팅", "" + i + "번째" + "11. 반복문 안에서 각 채팅방 객체 생성! => " + chatRoom);

                // 채팅방 아이디
                String chatRoomId = chatRoom.getChattingRoomId();
                Log.e("서버에서 채팅방리스트 가져와서 뷰모델에 세팅", "" + i + "번째" + " 12. 각 채팅방의 아이디! => " + chatRoomId);

                // 각 채팅방의 마지막 메시지 인덱스
                int lastMsgIdx = chatRoom.getMsgIdx();
                Log.e("서버에서 채팅방리스트 가져와서 뷰모델에 세팅", "" + i + "번째" + " 13. 각 채팅방 마지막 메시지의 인덱스! => " + lastMsgIdx);

                // 안읽은 메시지수를 계산하기 위한 각 채팅방의 마지막 메시지 객체 생성
                ChatMsgInfo lastMsgInChatRoom = new ChatMsgInfo(chatRoomId, lastMsgIdx);
                Log.e("서버에서 채팅방리스트 가져와서 뷰모델에 세팅", "" + i + "번째" + " 14. 각 채팅방 마지막 메시지 객체 생성! 채팅방아이디, 메시지인덱스 가지고 있음 => " + lastMsgInChatRoom);

                // 안읽은 메시지 값 세팅
                Log.e("서버에서 채팅방리스트 가져와서 뷰모델에 세팅", "" + i + "번째" + " 15. 안읽은 메시지 카운트 반환 시작");
                int notReadMsgCount = new ChattingRoomManager().getNotReadMsgCount(lastMsgInChatRoom, sp);
                Log.e("서버에서 채팅방리스트 가져와서 뷰모델에 세팅", "" + i + "번째" + " 16. 안읽은 메시지 카운트 반환 종료");

                // 안읽은 메시지가 0보다 크다면 채팅방정보에 넣어준다.
                if(notReadMsgCount > 0) {
                    chatRoom.setNotReadMsgCount(notReadMsgCount);
                    Log.e("서버에서 채팅방리스트 가져와서 뷰모델에 세팅", "" + i + "번째" + " 17. 안읽은 메시지 카운트가 0보다 크면 해당 채팅방정보에 저장해준다. => " + "notReadMsgCount: " + chatRoom.getNotReadMsgCount());
                }
            }

            Log.e("서버에서 채팅방리스트 가져와서 뷰모델에 세팅", " 18. 안읽은 메시지 갯수 세팅 완료 뷰모델에 채팅방리스트 넣어줌 ");
            chattingViewModel.getChattingList().setValue(chatRoomList);

            // 채팅방 만들기 버튼 트레이너인 경우에만 나오도록
            if(TrainerHomeActivity.staticLoginUserInfo != null) { //트레이너 라면
                binding.chatButtonInvite.setVisibility(View.VISIBLE);
            } else{
                binding.chatButtonInvite.setVisibility(View.GONE);
            }
        }
    }

    public void setOnclickListener(){
        binding.chatButtonInvite.setOnClickListener(this);
    }

    /** 사용자가 포함된 채팅방리스트를 가져와라*/
    private ArrayList<ChatRoomInfoForListDto> getChatRoomList(String userId){
        // 처음 조회시에는 보정을 해야한다!
        shouldCompensate = true;
        return chattingRoomManager.getChatRoomList(userId);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case  R.id.chat_button_invite: // 채팅방 만들기
                Intent intent = new Intent(requireActivity(), ChattingPossibleMemberListActivity.class);
                Log.e("채팅방 만들기 ", "1. 시작");

                intent.putExtra("chattingMemberDto",ChattingMemberDto.makeChatMemberInfo(TrainerHomeActivity.staticLoginUserInfo));

                startActivityResult2.launch(intent);
                break;
            default:
                break;
        }
    }
}