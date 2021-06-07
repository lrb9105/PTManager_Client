package com.teamnova.ptmanager.ui.home.trainer;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.teamnova.ptmanager.R;
import com.teamnova.ptmanager.databinding.ActivityFindPw2Binding;
import com.teamnova.ptmanager.databinding.ActivityHomeBinding;
import com.teamnova.ptmanager.databinding.ActivityMainBinding;
import com.teamnova.ptmanager.model.userInfo.FriendInfoDto;
import com.teamnova.ptmanager.model.userInfo.UserInfoDto;
import com.teamnova.ptmanager.model.userInfo.UserInfoDtoWithUserId;
import com.teamnova.ptmanager.test.TestActivity;
import com.teamnova.ptmanager.ui.home.trainer.fragment.TrainerHomeFragment;
import com.teamnova.ptmanager.ui.home.trainer.fragment.TrainerScheduleFragment;
import com.teamnova.ptmanager.ui.register.RegisterActivity;
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

                        if(friendsList != null){

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
                case R.id.item_home:
                    transaction2.replace(binding.trainerFrame.getId(), trainerHomeFragment,"frag1").commit();
                    break;
                case R.id.item_shcedule:
                    transaction2.replace(binding.trainerFrame.getId(), trainerScheduleFragment,"frag1").commit();
                    break;
            }
            return false;
        });
    }

    // 새로운 인텐트 발생 시
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        //Log.d("새로운인텐트발생",intent.getStringExtra("id"));
        FriendInfoDto friendInfo = (FriendInfoDto)intent.getSerializableExtra("memberInfo");

        TrainerHomeFragment fragment = (TrainerHomeFragment) getSupportFragmentManager().findFragmentByTag("frag1");
        //fragment.getFriendAddAdapter().addFriend();

        fragment.getFriendAddAdapter().addFriend(friendInfo);
    }
}