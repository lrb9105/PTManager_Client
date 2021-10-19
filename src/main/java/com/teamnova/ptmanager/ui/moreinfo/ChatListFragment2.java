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

    // 눈바디 비교 정보 가져올 객체
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
        startActivityResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // 방을 나간 채팅방 정보
                            ChatRoomInfoForListDto exitedChatRoomInfo = (ChatRoomInfoForListDto)result.getData().getSerializableExtra("exitedChatRoomInfo");

                            // 채팅뷰모델의 채팅리스트 객체 업데이트
                            ArrayList<ChatRoomInfoForListDto> c = chattingViewModel.getChattingList().getValue();
                            // 나온 방 리스트에서 삭제
                            c.remove(exitedChatRoomInfo);

                            chattingViewModel.getChattingList().setValue(c);
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
            case  R.id.chat_button_invite: // 로그아웃 버튼 클릭
                Intent intent = new Intent(requireActivity(), ChattingPossibleMemberListActivity.class);

                intent.putExtra("chattingMemberDto",ChattingMemberDto.makeChatMemberInfo(TrainerHomeActivity.staticLoginUserInfo));

                startActivity(intent);
                break;
            default:
                break;
        }
    }
}