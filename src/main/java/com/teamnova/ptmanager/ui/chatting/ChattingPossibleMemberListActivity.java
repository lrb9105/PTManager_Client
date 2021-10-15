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
import com.teamnova.ptmanager.model.chatting.ChattingMemberDto;
import com.teamnova.ptmanager.model.userInfo.FriendInfoDto;
import com.teamnova.ptmanager.model.userInfo.UserInfoDto;
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


    // setResult를 사용하기 위해 해당 액티비티의 참조를 adapter로 보내 줌.
    private Activity activity;

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
        // lectureiewModel 초기화
        friendViewModel = new ViewModelProvider(this).get(FriendViewModel.class);

        // 채팅참여자 정보
        Intent intent = getIntent();
        ChattingMemberDto chattingMemberDto = null;

        if(intent != null){
            // 채팅방 생성자 정보
            chattingMemberDto = (ChattingMemberDto)intent.getSerializableExtra("chattingMemberDto");
        }

        // Retrofit 통신 핸들러
        resultHandler = new Handler(Looper.myLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                // 회원 목록 가져오기 성공
                if(msg.what == 1){
                    // 회원 목록 가져오기
                    // 회원 목록 데이터 가져 옴
                    ArrayList<FriendInfoDto> registeredMemberList = (ArrayList<FriendInfoDto>)msg.obj;

                    if(registeredMemberList != null){
                        // 리사이클러뷰 세팅
                        recyclerView_member_list = binding.recyclerviewMemberList;
                        layoutManager = new LinearLayoutManager(ChattingPossibleMemberListActivity.this);
                        recyclerView_member_list.setLayoutManager(layoutManager);

                        // 데이터 가져와서 adapter에 넘겨 줌
                        lectureRegisteredMemberListAdapter = new LectureRegisteredMemberListAdapter(registeredMemberList, ChattingPossibleMemberListActivity.this, activity);
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
                // Intent로 전송하기 위한 리스트
                ArrayList<FriendInfoDto> memberListForIntent = new ArrayList<>();

                Log.d("사이즈:", "" + memberList.size());

                /** 채팅참여자 정보 생성 */
                ArrayList<ChattingMemberDto> chatMemberList = new ArrayList<>();

                // 트레이너 정보
                UserInfoDto trainerInfo = TrainerHomeActivity.staticLoginUserInfo;

                chatMemberList.add(ChattingMemberDto.makeChatMemberInfo(trainerInfo));

                for(FriendInfoDto member : memberList){
                    Log.d("값:", "" + member.getUserName());
                    // 체크되었다면 리스트에 추가
                    if(member.getCheck() != null){
                        if(member.getCheck().equals("1")){
                            Log.d("체크된 멤버id: ", member.getUserId() + " - " + member.getCheck());
                            chatMemberList.add(ChattingMemberDto.makeChatMemberInfo(member));

                        } else{
                            Log.d("체크값이 0: ", member.getUserId() + " - " + member.getCheck());
                        }
                    } else{
                        Log.d("getCheck()가 null: ", member.getUserId());
                    }
                }

                Intent intent = null;

                intent = new Intent(this, ChattingActivity.class);

                intent.putExtra("chatMemberList",chatMemberList);

                startActivity(intent);

                finish();
                break;
            case R.id.btn_back: // 뒤로가기
                onBackPressed();
                break;
        }
    }
}