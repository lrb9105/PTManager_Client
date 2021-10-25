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
import com.teamnova.ptmanager.viewmodel.chatting.ChattingViewModel;
import com.teamnova.ptmanager.viewmodel.friend.FriendViewModel;

import java.util.ArrayList;

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

    // 채팅방 매니져
    private ChattingRoomManager chattingRoomManager;

    // 리사이클러뷰
    private RecyclerView recyclerView;
    private ChattingListAdapter chattingListAdapter;
    private LinearLayoutManager linearLayoutManager;

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
                            System.out.println("");
                            // 방을 나간 채팅방 정보
                            ChatRoomInfoForListDto exitedChatRoomInfo = (ChatRoomInfoForListDto)result.getData().getSerializableExtra("exitedChatRoomInfo");
                            // 새로 생성 혹은 수정된 방 정보
                            ChatRoomInfoForListDto addedOrModifiedChatRoomInfo = (ChatRoomInfoForListDto)result.getData().getSerializableExtra("addedOrModifiedChatRoomInfo");

                            // 방 나가기
                            if(exitedChatRoomInfo != null){
                                // 채팅뷰모델의 채팅리스트 객체 업데이트
                                ArrayList<ChatRoomInfoForListDto> c = chattingViewModel.getChattingList().getValue();
                                // 나온 방 리스트에서 삭제
                                c.remove(exitedChatRoomInfo);

                                chattingViewModel.getChattingList().setValue(c);
                            } else if(addedOrModifiedChatRoomInfo != null) { // 방 생성 or 방 수정
                                // 채팅뷰모델의 채팅리스트 객체 업데이트
                                ArrayList<ChatRoomInfoForListDto> list = chattingViewModel.getChattingList().getValue();
                                // 리스트에서 넘어온 채팅방 정보 객체와 동일한 아이디를 갖는 객체가 있는지 확인
                                boolean isModified = false;

                                for(int i = 0; i < list.size(); i++){
                                    // 이미 리스트에 존재하는 채팅방이라면 내용 수정 -> 포지션 0으로 옮기기
                                    if(list.get(i).getChattingRoomId().equals(addedOrModifiedChatRoomInfo.getChattingRoomId())){
                                        isModified = true;
                                        // 수정된 값으로 변경하기 - 이전 리스트 삭제
                                        // 새로운 리스트 생성
                                        list.remove(i);
                                        list.add(0, addedOrModifiedChatRoomInfo);
                                        break;
                                    }
                                }

                                // 추가라면
                                if(!isModified){
                                    list.add(0, addedOrModifiedChatRoomInfo);
                                }

                                chattingViewModel.getChattingList().setValue(list);
                            }
                        }
                    }
                });

        super.onCreate(savedInstanceState);

        Log.d("홈 프래그먼트의 onCreate", "얍얍");

        // 부모 액티비티의 라이프사이클을 갖는 VIEWMODEL 생성
        friendViewModel = new ViewModelProvider(requireActivity()).get(FriendViewModel.class);
        chattingViewModel = new ViewModelProvider(requireActivity()).get(ChattingViewModel.class);

        // 가져온 채팅방리스트정보 observe
        chattingViewModel.getChattingList().observe(requireActivity(), chatRoomList -> {
            // 리사이클러뷰에 세팅
            chattingListAdapter = new ChattingListAdapter(chatRoomList, requireActivity(), startActivityResult, memberInfo);
            recyclerView.setAdapter(chattingListAdapter);
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

        // 채팅방 리스트 가져와서 뷰모델에 세팅
        chattingViewModel.getChattingList().setValue(this.getChatRoomList(userId));

        // 채팅방 만들기 버튼 트레이너인 경우에만 나오도록
        if(TrainerHomeActivity.staticLoginUserInfo != null) { //트레이너 라면
            binding.chatButtonInvite.setVisibility(View.VISIBLE);
        } else{
            binding.chatButtonInvite.setVisibility(View.GONE);
        }

    }

    public void setOnclickListener(){
        binding.chatButtonInvite.setOnClickListener(this);
    }

    /** 사용자가 포함된 채팅방리스트를 가져와라*/
    private ArrayList<ChatRoomInfoForListDto> getChatRoomList(String userId){
        return chattingRoomManager.getChatRoomList(userId);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case  R.id.chat_button_invite: // 채팅방 만들기
                Intent intent = new Intent(requireActivity(), ChattingPossibleMemberListActivity.class);

                intent.putExtra("chattingMemberDto",ChattingMemberDto.makeChatMemberInfo(TrainerHomeActivity.staticLoginUserInfo));

                startActivityResult.launch(intent);
                break;
            default:
                break;
        }
    }
}