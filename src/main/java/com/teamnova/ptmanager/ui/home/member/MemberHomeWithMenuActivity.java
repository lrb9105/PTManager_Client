package com.teamnova.ptmanager.ui.home.member;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Menu;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.teamnova.ptmanager.R;
import com.teamnova.ptmanager.model.userInfo.FriendInfoDto;
import com.teamnova.ptmanager.model.userInfo.UserInfoDto;
import com.teamnova.ptmanager.ui.home.trainer.fragment.TrainerHomeFragment;
import com.teamnova.ptmanager.ui.home.trainer.fragment.TrainerScheduleFragment;
import com.teamnova.ptmanager.viewmodel.friend.FriendViewModel;
import com.teamnova.ptmanager.viewmodel.login.LoginViewModel;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;

public class MemberHomeWithMenuActivity extends AppCompatActivity {
    // 회원 정보를 담을 dto
    public static UserInfoDto staticLoginUserInfo;

    // 트레이너 정보
    private UserInfoDto trainerInfo;

    // 뷰모델
    private LoginViewModel loginViewModel;

    // 로그인 시 로그인 act로부터 loginId 받아옴
    private String loginId;

    // Retrofit 통신의 결과를 전달하는 핸들러
    private Handler resultHandler;

    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_member_home_with_menu);

        initialize();
    }

    // 초기화
    public void initialize(){
        Intent intent = getIntent();

        // 로그인 액티비티에서 액티비티 전환이 됐다면
        if(intent != null){
            // 액티비티와 프래그먼트 사이에서 트랜잭션을 담당할 프래그먼트 매니저
            /*fragmentManager = getSupportFragmentManager();

            // 프래그먼트 추가, 교체, 삭제 등을 담당하는 트랜잭션
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            // 교체할 홈 프래그먼트
            trainerHomeFragment = new TrainerHomeFragment();

            // 교체할 일정 프래그먼트
            trainerScheduleFragment = new TrainerScheduleFragment();*/

            // viewModel 초기화
            loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

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

                    // 로그인 정보 가져 온 후 프래그먼트 생성
                    Toolbar toolbar = findViewById(R.id.toolbar);
                    setSupportActionBar(toolbar);

                    DrawerLayout drawer = findViewById(R.id.drawer_layout);
                    NavigationView navigationView = findViewById(R.id.nav_view);
                    // Passing each menu ID as a set of Ids because each
                    // menu should be considered as top level destinations.
                    mAppBarConfiguration = new AppBarConfiguration.Builder(
                            R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                            .setDrawerLayout(drawer)
                            .build();

                    NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
                    NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
                    BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
                    NavigationUI.setupWithNavController(navigationView, navController);
                    NavigationUI.setupWithNavController(bottomNav, navController);

                    loginViewModel.setLoginUserInfo2(loginUserInfo);
                }
            });

            // Retrofit 통신 핸들러
            resultHandler = new Handler(Looper.myLooper()){
                @Override
                public void handleMessage(@NonNull Message msg) {
                    // 트레이너 정보 가져오기 성공
                    if(msg.what == 0){
                        // 친구목록 가져오기
                        //friendViewModel.setFriendsList((ArrayList<FriendInfoDto>) msg.obj);

                        // 초기화 시 트레이너 정보 가져 옴
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
        /*binding.bottomNavigation.setOnNavigationItemSelectedListener(item ->  {
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
        });*/
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // 새로운 인텐트 발생 시
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.member_home_with_menu, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}