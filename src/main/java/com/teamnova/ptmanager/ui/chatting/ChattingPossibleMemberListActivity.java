package com.teamnova.ptmanager.ui.chatting;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.teamnova.ptmanager.R;
import com.teamnova.ptmanager.adapter.lecture.LectureRegisteredMemberListAdapter;
import com.teamnova.ptmanager.databinding.ActivityChattingPossibleMemberListBinding;
import com.teamnova.ptmanager.databinding.ActivityLectureRegisterdMemberListBinding;
import com.teamnova.ptmanager.manager.ChattingRoomManager;
import com.teamnova.ptmanager.model.chatting.ChattingMemberDto;
import com.teamnova.ptmanager.model.userInfo.FriendInfoDto;
import com.teamnova.ptmanager.model.userInfo.UserInfoDto;
import com.teamnova.ptmanager.ui.home.member.MemberHomeActivity;
import com.teamnova.ptmanager.ui.home.trainer.TrainerHomeActivity;
import com.teamnova.ptmanager.ui.schedule.lesson.LessonRegisterActivity;
import com.teamnova.ptmanager.viewmodel.friend.FriendViewModel;
import com.teamnova.ptmanager.viewmodel.schedule.lecture.LectureViewModel;

import java.util.ArrayList;

/** 채팅가능한 회원 리스트*/
public class ChattingPossibleMemberListActivity extends AppCompatActivity implements View.OnClickListener{
    // binder
    private ActivityChattingPossibleMemberListBinding binding;

    // FriendViewModel
    private FriendViewModel friendViewModel;

    // Retrofit 통신의 결과를 전달하는 핸들러
    private Handler resultHandler;

    // 리사이클러뷰
    private LectureRegisteredMemberListAdapter lectureRegisteredMemberListAdapter;
    private RecyclerView recyclerView_member_list;
    private RecyclerView.LayoutManager layoutManager;

    // 기존에 생성된 대화방의 채팅 참여자 리스트
    private ArrayList<ChattingMemberDto> chatMemberList;

    // setResult를 사용하기 위해 해당 액티비티의 참조를 adapter로 보내 줌.
    private Activity activity;

    // 사용자 id
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 해당 액티비티에서 사용할 xml파일에대한 클래스가 자동으로 생성되고 inflate함수를 이용해서 인스턴스를 생성한다.
        binding = ActivityChattingPossibleMemberListBinding.inflate(getLayoutInflater());

        // 기존에는 layout이 들어갔는데 대신 getRoot를 넣어준다.
        setContentView(binding.getRoot());

        initialize();

        setOnClickListener();
    }

    public void setOnClickListener(){
        binding.btnSelectMember.setOnClickListener(this);
        binding.btnBack.setOnClickListener(this);
    }

    // 초기화
    public void initialize(){
        // viewModel 초기화
        friendViewModel = new ViewModelProvider(this).get(FriendViewModel.class);

        // 채팅참여자 정보
        Intent intent = getIntent();
        ChattingMemberDto chattingMemberDto = null;

        if(intent != null){
            // 채팅방 생성자 정보 - 채팅방리스트에서 새로운 대화방 생성 시
            chattingMemberDto = (ChattingMemberDto)intent.getSerializableExtra("chattingMemberDto");

            // 생성된 대화방에서 새로운 사용자 초대 시
            if(chattingMemberDto == null) {
                userId = intent.getStringExtra("userId");

                chatMemberList = (ArrayList<ChattingMemberDto>)intent.getSerializableExtra("chatMemberList");

                // 로그인한 사용자(트레이너)의 정보를 chattingMemberDto에 넣는다.
                for(ChattingMemberDto member : chatMemberList){
                    if(userId.equals(member.getUserId())){
                        chattingMemberDto = member;
                        break;
                    }
                }
            }
        }

        // Retrofit 통신 핸들러
        resultHandler = new Handler(Looper.myLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                // 회원 목록 가져오기 성공
                if(msg.what == 0){
                    // 회원 목록 가져오기
                    // 회원 목록 데이터 가져 옴
                    ArrayList<FriendInfoDto> registeredMemberList = (ArrayList<FriendInfoDto>)msg.obj;

                    System.out.println("회원리스트 사이즈: " + registeredMemberList.size());

                    // 새로운 대화방 생성 시, 채팅에 참여중인 회원리스트가 없음!
                    if(registeredMemberList != null && chatMemberList == null){
                        // 리사이클러뷰 세팅
                        recyclerView_member_list = binding.recyclerviewMemberList;
                        layoutManager = new LinearLayoutManager(ChattingPossibleMemberListActivity.this);
                        recyclerView_member_list.setLayoutManager(layoutManager);

                        // 데이터 가져와서 adapter에 넘겨 줌
                        lectureRegisteredMemberListAdapter = new LectureRegisteredMemberListAdapter(registeredMemberList, ChattingPossibleMemberListActivity.this, activity);
                        recyclerView_member_list.setAdapter(lectureRegisteredMemberListAdapter);
                    } else if(chatMemberList != null) { // 생성된 대화방에서 새로운 사용자 초대 시, 채팅에 참여중인 회원리스트가 있음!
                        // 리사이클러뷰 세팅
                        recyclerView_member_list = binding.recyclerviewMemberList;
                        layoutManager = new LinearLayoutManager(ChattingPossibleMemberListActivity.this);
                        recyclerView_member_list.setLayoutManager(layoutManager);

                        // 데이터 가져와서 adapter에 넘겨 줌
                        // 채팅 참여자의 정보도 함께 넘겨주고 채팅 참여자의 경우 체크박스를 없앤다!!!
                        lectureRegisteredMemberListAdapter = new LectureRegisteredMemberListAdapter(registeredMemberList, chatMemberList, ChattingPossibleMemberListActivity.this, activity);
                        recyclerView_member_list.setAdapter(lectureRegisteredMemberListAdapter);
                    } else{
                        Toast.makeText(ChattingPossibleMemberListActivity.this, "대화초대가 가능한 회원이 없습니다.", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        };

        // 대화가능한 상대정보 가져오기
        friendViewModel.getFriendsList(resultHandler, chattingMemberDto.getUserId());
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_select_member: // 회원 선택 완료
                // 회원 목록
                ArrayList<FriendInfoDto> memberList = lectureRegisteredMemberListAdapter.getMemberList();
                Log.e("채팅방에서 사용자 초대 3.모든 회원리스트 memberList.size()", "" + memberList.size());

                /** 채팅참여자 정보 생성 */
                ArrayList<ChattingMemberDto> chatMemberList = new ArrayList<>();

                // 트레이너 정보 - 현재 채팅리스트에서 대화방 생성은 트레이너밖에 못하므로 트레이너 정보를 가져 옴
                UserInfoDto trainerInfo = TrainerHomeActivity.staticLoginUserInfo;

                chatMemberList.add(ChattingMemberDto.makeChatMemberInfo(trainerInfo));
                Log.e("채팅방에서 사용자 초대 4.채팅에 참여할 멤버리스트 chatMemberList.add", "" + chatMemberList.size());

                // 모든 회원 수만큼 반복한다.
                for(FriendInfoDto member : memberList){
                    // 체크되어 있다면 리스트에 추가
                    if(member.getCheck() != null){
                        if(member.getCheck().equals("1")){
                            chatMemberList.add(ChattingMemberDto.makeChatMemberInfo(member));
                            Log.e("채팅방에서 사용자 초대 5.채팅에 참여자 추가", "" + chatMemberList.size());

                        }/* else{
                            Log.d("체크값이 0: ", member.getUserId() + " - " + member.getCheck());
                        }*/
                    } else{
                        //Log.d("getCheck()가 null: ", member.getUserId());
                    }
                }


                Intent intent = null;

                if(TrainerHomeActivity.staticLoginUserInfo != null) { //트레이너 라면
                    intent = new Intent(this, TrainerHomeActivity.class);
                } else{
                    intent = new Intent(this, MemberHomeActivity.class);
                }

                // 새로운 방 생성 시
                // 해당 화면은 초대 및 방생성에서 같이 사용됨 이때 userId null이 아니라면 새로운 회원을 초대한 것이다.
                // 리스트의 첫번째는 내 아이디(트레이너 아이디)가 들어있으므로 이를 지워준다.
                if (userId != null) { // 기존에 생성된 대화방에서 새로운 사용자 초대 시
                    // 새로 생성된 초대자 리스트에서 첫번쨰는 트레이너 아이디 이므로 지워준다!
                    chatMemberList.remove(0);
                    Log.e("채팅방에서 사용자 초대 6.chatMemberList.remove(0)", "" + chatMemberList.size());

                }

                intent.putExtra("chatMemberList",chatMemberList);
                setResult(Activity.RESULT_OK, intent);
                Log.e("채팅방에서 사용자 초대 7.다시 ChattingActivity로 이동", "true");

                finish();
                break;
            case R.id.btn_back: // 뒤로가기
                onBackPressed();
                break;
        }
    }
}