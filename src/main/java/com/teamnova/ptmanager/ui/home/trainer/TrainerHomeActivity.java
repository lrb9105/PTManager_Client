package com.teamnova.ptmanager.ui.home.trainer;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.teamnova.ptmanager.R;
import com.teamnova.ptmanager.databinding.ActivityHomeBinding;
import com.teamnova.ptmanager.model.lesson.LessonInfo;
import com.teamnova.ptmanager.model.userInfo.FriendInfoDto;
import com.teamnova.ptmanager.model.userInfo.UserInfoDto;
import com.teamnova.ptmanager.ui.home.trainer.fragment.TrainerHomeFragment;
import com.teamnova.ptmanager.ui.home.trainer.fragment.TrainerScheduleFragment;
import com.teamnova.ptmanager.ui.moreinfo.ChatListFragment2;
import com.teamnova.ptmanager.ui.moreinfo.MoreInfoFragment;
import com.teamnova.ptmanager.ui.schedule.schedule.fragment.WeekSchFragment;
import com.teamnova.ptmanager.viewmodel.friend.FriendViewModel;
import com.teamnova.ptmanager.viewmodel.login.LoginViewModel;

import java.util.ArrayList;

public class TrainerHomeActivity extends AppCompatActivity {
    // 사용자 정보를 담을 dto
    public static UserInfoDto staticLoginUserInfo;

    // 로그인 시 서버로부터 친구목록 가져와서 저장하기 위한 ArrayList
    public static ArrayList<FriendInfoDto> friendsList;

    // 뷰모델
    private LoginViewModel loginViewModel;
    private FriendViewModel friendViewModel;

    // binder
    private ActivityHomeBinding binding;

    // 프래그먼트 트랜잭션(프래그먼트 교체, 추가 , 삭제)을 처리할 프래그먼트 매니저
    private FragmentManager fragmentManager;

    // 교체할 프래그먼트 생성
    private TrainerHomeFragment trainerHomeFragment;
    private TrainerScheduleFragment trainerScheduleFragment;

    // 더보기 프래그먼트
    private MoreInfoFragment moreInfoFragment;

    // 채팅 프래그먼트
    private ChatListFragment2 chatListFragment;

    // 로그인 시 로그인 act로부터 loginId 받아옴
    private String loginId;

    // Retrofit 통신의 결과를 전달하는 핸들러
    private Handler resultHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // 해당 액티비티에서 사용할 xml파일에대한 클래스가 자동으로 생성되고 inflate함수를 이용해서 인스턴스를 생성한다.
        binding = ActivityHomeBinding.inflate(getLayoutInflater());

        // 기존에는 layout이 들어갔는데 대신 getRoot를 넣어준다.
        setContentView(binding.getRoot());

        initialize();

        Log.d("홈 액트의 onCreate", "얍얍");
    }

    // 초기화
    public void initialize(){
        Intent intent = getIntent();

        // 로그인 액티비티에서 액티비티 전환이 됐다면
        if(intent != null){
            // 액티비티와 프래그먼트 사이에서 트랜잭션을 담당할 프래그먼트 매니저
            fragmentManager = getSupportFragmentManager();

            // 프래그먼트 추가, 교체, 삭제 등을 담당하는 트랜잭션
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            // 교체할 홈 프래그먼트
            trainerHomeFragment = new TrainerHomeFragment();

            // 교체할 일정 프래그먼트
            trainerScheduleFragment = new TrainerScheduleFragment();

            // viewModel 초기화
            loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
            friendViewModel = new ViewModelProvider(this).get(FriendViewModel.class);

            /***
             * 1. 소유자(액티비티티)가 활성화(ifecycle.State.STARTED, Lifecycle.State.RESUMED)상태에서 관찰하고 있는 LiveData의 값이 변경 시 콜백함수가 호출된다.
             * 2. 로그인한 사용자 정보를 가져왔을 때 UI변경
             */
            loginViewModel.getUserInfo().observe(this, loginUserInfo ->{
                if(loginUserInfo.getLoginId() != null){
                    // 로그인 후 트레이너 정보 static 변수에 추가
                    staticLoginUserInfo = loginUserInfo;

                    // 사용자정보를 가지고 오면 fragment로 넘겨준다.
                    Log.d("로그인 사용자 정보 가져옴, id:", loginUserInfo.getLoginId());

                    /*// 프래그먼트로 보낼 bundle 객체.
                    Bundle bundle = new Bundle();
                    //번들객체 생성 - Activity의 데이터를 Fragment로 전달
                    bundle.putSerializable("loginUserInfo",loginUserInfo);

                    //trainerHomeFragment로 번들 전달
                    trainerHomeFragment.setArguments(bundle);*/

                    loginViewModel.setLoginUserInfo2(loginUserInfo);

                    // 친구목록 가져오기 - 로그인 정보 가져온 후 실행
                    friendViewModel.getFriendsList(resultHandler, loginId);
                }
            });

            // Retrofit 통신 핸들러
            resultHandler = new Handler(Looper.myLooper()){
                @Override
                public void handleMessage(@NonNull Message msg) {
                    // 친구목록 가져오기 성공
                    if(msg.what == 0){
                        // 친구목록 가져오기
                        //friendViewModel.setFriendsList((ArrayList<FriendInfoDto>) msg.obj);
                        friendsList = (ArrayList<FriendInfoDto>) msg.obj;

                        // 초기화 시 홈프래그먼트 추가 및 커밋 - 이 때 프래그먼트의 onCreateView()가 호출 됨
                        // 친구목록 다 가져온 후 실행
                        transaction.replace(binding.trainerFrame.getId(), trainerHomeFragment,"frag1").commit();

                        if(friendsList.size() > 0){

                            Log.d("친구목록 가져오기 USER_ID: ", friendsList.get(0).getUserId() );
                        }
                    }
                }
            };

            loginId = intent.getStringExtra("loginId");

            // 로그인한 사용자 정보 가져오기
            loginViewModel.getUserInfo(loginId,0);

            /*if(friendsList == null){
                friendViewModel.getFriendsList(resultHandler, loginId);
            }*/
        }

        // Bottom Navigation의 아이템 선택 리스너
        binding.bottomNavigation.setOnNavigationItemSelectedListener(item ->  {
            FragmentTransaction transaction2 = fragmentManager.beginTransaction();

            switch(item.getItemId()) {
                case R.id.item_home: // 홈
                    transaction2.replace(binding.trainerFrame.getId(), trainerHomeFragment,"frag1").commit();
                    break;
                case R.id.item_shcedule: // 스케줄
                    transaction2.replace(binding.trainerFrame.getId(), trainerScheduleFragment,"frag2").commit();
                    break;
                case R.id.item_more: // 더보기
                    if(moreInfoFragment == null) {
                        moreInfoFragment = new MoreInfoFragment();
                    }

                    transaction2.replace(binding.trainerFrame.getId(), moreInfoFragment,"frag3").commit();
                    break;
                case R.id.item_chatting: // 채팅
                    if(chatListFragment == null) {
                        chatListFragment = new ChatListFragment2();

                    }
                    transaction2.replace(binding.trainerFrame.getId(), chatListFragment,"frag4").commit();

                    /*Intent intent2 = new Intent(this, ChattingActivity.class);

                    // 회원 정보
                    UserInfoDto userInfo = staticLoginUserInfo;

                    intent2.putExtra("userInfo",ChattingMemberDto.makeChatMemberInfo(userInfo));
                    intent2.putExtra("chatRoomId","CHATTING_ROOM_16");

                    startActivity(intent2);*/
                    break;
            }
            return true;
        });
    }

    // 새로운 인텐트 발생 시
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // 추가한 회원정보
        FriendInfoDto friendInfo = (FriendInfoDto)intent.getSerializableExtra("memberInfo");
        // 추가한 레슨정보
        LessonInfo lessonInfo = (LessonInfo)intent.getSerializableExtra("lessonInfo");

        FragmentTransaction transaction3 = fragmentManager.beginTransaction();

        // 회원 추가 시
        if(friendInfo != null){
            // 홈프래그먼트 가져오기
            TrainerHomeFragment homeFragment = (TrainerHomeFragment) getSupportFragmentManager().findFragmentByTag("frag1");
            //fragment.getFriendAddAdapter().addFriend();

            homeFragment.getFriendAddAdapter().addFriend(friendInfo);
            homeFragment.scrollToTop();

            // transaction3.replace(binding.trainerFrame.getId(), trainerHomeFragment,"frag1").commit();
        } else if(// 레슨추가 시 || 예약 승인/거절완료 시 || 출석체크 완료 시 || 레슨예약 승인/거절 시
                lessonInfo != null
                || intent.getStringExtra("id").equals("ReservationApprovementActivity")
                || intent.getStringExtra("id").equals("LessonDetailActivity")
                || intent.getStringExtra("id").equals("ReservationAppropvementActivity")) {
            // 스케줄 프래그먼트 가져오기
            TrainerScheduleFragment schFragment = (TrainerScheduleFragment) getSupportFragmentManager().findFragmentByTag("frag2");
            WeekSchFragment weekSchFragment = WeekSchFragment.newInstance();
            schFragment.setChildFragment(weekSchFragment, "week_frag");

            Log.d("WeekSchFrag호출", "111");

            /*WeekDayView mWeekView = weekSchFragment.getmWeekView();

            Calendar startTime = Calendar.getInstance();

            startTime.set(Calendar.HOUR_OF_DAY, 3);
            startTime.set(Calendar.MINUTE, 0);
            startTime.set(Calendar.MONTH, 5);
            startTime.set(Calendar.YEAR, 2021);
            Calendar endTime = (Calendar) startTime.clone();
            endTime.add(Calendar.HOUR, 1);
            endTime.set(Calendar.MONTH, 5);
            WeekViewEvent event = new WeekViewEvent(1, "This is a Event!!", startTime, endTime);
            event.setColor(getResources().getColor(R.color.event_color_01));

            mWeekView.cacheEvent(event);*/

            // transaction3.replace(binding.trainerFrame.getId(), trainerScheduleFragment,"frag2").commit();

        }
    }
}