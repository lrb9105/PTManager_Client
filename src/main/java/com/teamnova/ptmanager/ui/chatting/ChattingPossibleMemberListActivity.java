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

    // 기존에 생성된 채팅방에 참여중인 참여자 리스트
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
        Log.e("채팅방 만들기/사용자초대 ", "2. friendViewModel 객체 생성 => " +  friendViewModel);

        // 채팅참여자 정보
        Intent intent = getIntent();
        ChattingMemberDto chattingMemberDto = null;

        if(intent != null){
            // 채팅방 생성자 정보 - 채팅방리스트에서 새로운 대화방 생성 시
            chattingMemberDto = (ChattingMemberDto)intent.getSerializableExtra("chattingMemberDto");

            // 생성된 대화방에서 새로운 사용자 초대 시
            if(chattingMemberDto == null) {
                userId = intent.getStringExtra("userId");
                Log.e("사용자 초대 ", "3-1. 채팅방 생성자 userId => " +  userId);

                // 기존 채팅방에 참여중인 참가자 리스트
                // 이들은 체크박스가 없어야 하기 때문에 가져옴
                chatMemberList = (ArrayList<ChattingMemberDto>)intent.getSerializableExtra("chatMemberList");

                if(chatMemberList != null) {
                    Log.e("사용자 초대 ", "3-2. 기존 채팅방에 참여중인 참가자 리스트의 사이즈 => " +  chatMemberList.size());

                    // 로그인한 사용자(트레이너)의 정보를 chattingMemberDto에 넣는다.
                    // 리스트엔 트레이너의 정보도 포함되어 있음
                    for(ChattingMemberDto member : chatMemberList){
                        if(userId.equals(member.getUserId())){
                            chattingMemberDto = member;
                            break;
                        }
                    }
                } else {
                    Log.e("사용자 초대 ", "3-2. 채팅 참여자 목록이 null! 채팅방에서 참여자 리스트 못받아옴!!! => " + "null");
                }
            } else {
                Log.e("채팅방 만들기 ", "3. 채팅방 리스트에서 새로운 채팅방 생성 => 채팅방 생성자 userId => " +  chattingMemberDto.getUserId());
            }
        }

        // Retrofit 통신 핸들러
        resultHandler = new Handler(Looper.myLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                Log.e("채팅방 만들기/사용자 초대 ", "5. 해당 트레이너에게 수강중인 회원 리스트 가져오기 종료 ");

                // 회원 목록 가져오기 성공
                if(msg.what == 0){
                    // 회원 목록 가져오기
                    // 회원 목록 데이터 가져 옴
                    ArrayList<FriendInfoDto> registeredMemberList = (ArrayList<FriendInfoDto>)msg.obj;

                    Log.e("채팅방 만들기/사용자 초대 ", "6. 수강중인 회원목록 가져옴 => 사이즈: " + registeredMemberList.size());

                    // 새로운 채팅방 생성
                    // 수강중인 회원 목록(registeredMemberList)은 존재하고 현재 채팅방에 참여중인 회원(chatMemberList)은 없음!
                    if(registeredMemberList != null && chatMemberList == null){
                        // 리사이클러뷰 세팅
                        recyclerView_member_list = binding.recyclerviewMemberList;
                        Log.e("채팅방 만들기 ", "7-1. 수강중인 사용자 목록 리사이클러뷰에 저장 => 리사이클러뷰 생성 " + recyclerView_member_list);

                        layoutManager = new LinearLayoutManager(ChattingPossibleMemberListActivity.this);
                        Log.e("채팅방 만들기 ", "7-2. 수강중인 사용자 목록 리사이클러뷰에 저장 => 레이아웃 매니져 생성 " + layoutManager);

                        recyclerView_member_list.setLayoutManager(layoutManager);
                        Log.e("채팅방 만들기 ", "7-3. 수강중인 사용자 목록 리사이클러뷰에 저장 => 리사이클러뷰에 레이아웃 매니져 세팅 " + "true");

                        // 데이터 가져와서 adapter에 넘겨 줌
                        lectureRegisteredMemberListAdapter = new LectureRegisteredMemberListAdapter(registeredMemberList, ChattingPossibleMemberListActivity.this, activity);
                        Log.e("채팅방 만들기 ", "7-4. 수강중인 사용자 목록 리사이클러뷰에 저장 => adapter 생성" + lectureRegisteredMemberListAdapter);

                        recyclerView_member_list.setAdapter(lectureRegisteredMemberListAdapter);
                        Log.e("채팅방 만들기 ", "7-5. 수강중인 사용자 목록 리사이클러뷰에 저장 => adapter에 데이터 세팅 완료" + "true");

                    } else if(chatMemberList != null) {
                        // 기존에 생성된 채팅방에서 새로운 사용자 초대 시
                        // 현재 채팅방에 참여중인 회원목록(chatMemberList)이 존재

                        // 리사이클러뷰 세팅
                        recyclerView_member_list = binding.recyclerviewMemberList;
                        Log.e("사용자 초대 ", "7-1. 수강중인 사용자 목록 리사이클러뷰에 저장 => 리사이클러뷰 생성 " + recyclerView_member_list);

                        layoutManager = new LinearLayoutManager(ChattingPossibleMemberListActivity.this);
                        Log.e("사용자 초대 ", "7-2. 수강중인 사용자 목록 리사이클러뷰에 저장 => 레이아웃 매니져 생성 " + layoutManager);

                        recyclerView_member_list.setLayoutManager(layoutManager);
                        Log.e("사용자 초대 ", "7-3. 수강중인 사용자 목록 리사이클러뷰에 저장 => 리사이클러뷰에 레이아웃 매니져 세팅 " + "true");

                        // 데이터 가져와서 adapter에 넘겨 줌
                        // 채팅 참여자의 정보도 함께 넘겨주고 채팅 참여자의 경우 체크박스를 없앤다!!!
                        lectureRegisteredMemberListAdapter = new LectureRegisteredMemberListAdapter(registeredMemberList, chatMemberList, ChattingPossibleMemberListActivity.this, activity);
                        Log.e("사용자 초대  ", "7-4. 수강중인 사용자 목록 리사이클러뷰에 저장 => adapter 생성" + lectureRegisteredMemberListAdapter);

                        recyclerView_member_list.setAdapter(lectureRegisteredMemberListAdapter);
                        Log.e("사용자 초대 ", "7-5. 수강중인 사용자 목록 리사이클러뷰에 저장 => adapter에 데이터 세팅 완료" + "true");

                    } else{
                        Log.e("사용자 초대 ", "7. 수강중인 회원 없음");

                        Toast.makeText(ChattingPossibleMemberListActivity.this, "대화초대가 가능한 회원이 없습니다.", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        };

        // 해당 트레이너에게 수강중인 회원 리스트 가져오기
        Log.e("채팅방 만들기/사용자 초대 ", "4. 해당 트레이너에게 수강중인 회원 리스트 가져오기 시작 ");
        friendViewModel.getFriendsList(resultHandler, chattingMemberDto.getUserId());
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_select_member: // 회원 선택 완료
                // 회원 목록
                ArrayList<FriendInfoDto> allMemberList = lectureRegisteredMemberListAdapter.getMemberList();
                Log.e("채팅방 만들기/사용자 초대 ", "8. 모든 수강중인 회원목록 리스트 생성 => allMemberList: " + allMemberList);

                /** 채팅참여자 정보 생성 */
                ArrayList<ChattingMemberDto> chatMemberList = new ArrayList<>();
                Log.e("채팅방 만들기/사용자 초대 ", "9. 채팅 참여자 리스트 생성 => chatMemberList: " + chatMemberList);

                // 트레이너 정보 - 현재 채팅리스트에서 대화방 생성은 트레이너밖에 못하므로 트레이너 정보를 가져 옴
                UserInfoDto trainerInfo = TrainerHomeActivity.staticLoginUserInfo;

                chatMemberList.add(ChattingMemberDto.makeChatMemberInfo(trainerInfo));
                Log.e("채팅방 만들기/사용자 초대 ", "10. 채팅 참여자 리스트에 트레이너 정보 추가 => 채팅 참여자 리스트 사이즈: " + chatMemberList.size());

                // 모든 회원 수만큼 반복한다.
                for(FriendInfoDto member : allMemberList){
                    // 체크되어 있다면 리스트에 추가
                    if(member.getCheck() != null){
                        if(member.getCheck().equals("1")){
                            chatMemberList.add(ChattingMemberDto.makeChatMemberInfo(member));
                            Log.e("채팅방 만들기/사용자 초대 ", "11. 채팅 참여자 리스트에 체크된 사용자 정보 추가 => " + "추가된 사용자 아이디: " + member.getUserId() + " 채팅 참여자 리스트 사이즈: " + chatMemberList.size());
                        }
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
                    Log.e("사용자 초대 ", "12. 사용자 초대 시 채팅참여자 리스트에 저장된 트레이너 정보는 삭제 해준다 => 채팅 참여자 리스트 사이즈: " + chatMemberList.size());
                }

                intent.putExtra("chatMemberList",chatMemberList);
                setResult(Activity.RESULT_OK, intent);
                Log.e("채팅방 만들기/사용자 초대 ", "13. 원래 ACT로 다시 이동한다 => 채팅방 만들기: TrainerHomeAct, 사용자 초대: ChattingAct");

                finish();
                break;
            case R.id.btn_back: // 뒤로가기
                onBackPressed();
                break;
        }
    }
}