package com.teamnova.ptmanager.ui.home.member;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.teamnova.ptmanager.R;
import com.teamnova.ptmanager.databinding.ActivityHomeBinding;
import com.teamnova.ptmanager.databinding.ActivityMemberHomeBinding;
import com.teamnova.ptmanager.model.userInfo.FriendInfoDto;
import com.teamnova.ptmanager.ui.home.member.fragment.MemberHomeFragment;
import com.teamnova.ptmanager.ui.home.trainer.fragment.TrainerHomeFragment;
import com.teamnova.ptmanager.ui.home.trainer.fragment.TrainerScheduleFragment;
import com.teamnova.ptmanager.viewmodel.friend.FriendViewModel;
import com.teamnova.ptmanager.viewmodel.login.LoginViewModel;
import com.teamnova.ptmanager.viewmodel.schedule.lesson.LessonViewModel;

import java.util.ArrayList;

public class MemberHomeActivity extends AppCompatActivity {
    ActivityMemberHomeBinding binding;

    // 프래그먼트 트랜잭션(프래그먼트 교체, 추가 , 삭제)을 처리할 프래그먼트 매니저
    private FragmentManager fragmentManager;

    // 홈프래그먼트
    private MemberHomeFragment memberHomeFragment;

    // 뷰모델
    private FriendViewModel friendViewModel;

    // 로그인 시 로그인 act로부터 loginId 받아옴
    private String loginId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 해당 액티비티에서 사용할 xml파일에대한 클래스가 자동으로 생성되고 inflate함수를 이용해서 인스턴스를 생성한다.
        binding = ActivityMemberHomeBinding.inflate(getLayoutInflater());

        // 기존에는 layout이 들어갔는데 대신 getRoot를 넣어준다.
        setContentView(binding.getRoot());
        
        super.onCreate(savedInstanceState);

        initialize();
    }

    // 초기화
    public void initialize(){
        Intent intent = getIntent();

        // 로그인 액티비티에서 액티비티 전환이 됐다면 -> 최초 로그인 시에만 Intent가 있음
        if(intent != null){
            // 액티비티와 프래그먼트 사이에서 트랜잭션을 담당할 프래그먼트 매니저
            fragmentManager = getSupportFragmentManager();

            // 프래그먼트 추가, 교체, 삭제 등을 담당하는 트랜잭션
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            // 교체할 홈 프래그먼트
            memberHomeFragment = new MemberHomeFragment();

            transaction.add(binding.trainerFrame.getId(), memberHomeFragment,"frag1");

            transaction.commit();

            // viewModel 초기화
            friendViewModel = new ViewModelProvider(this).get(FriendViewModel.class);

            loginId = intent.getStringExtra("loginId");

            // 로그인한 회원 정보 가져오기
            friendViewModel.getFriendInfo(loginId);

        }

        // Bottom Navigation의 아이템 선택 리스너
        binding.bottomNavigation.setOnNavigationItemSelectedListener(item ->  {
            FragmentTransaction transaction2 = fragmentManager.beginTransaction();

            switch(item.getItemId()) {
                case R.id.item_home:
                    transaction2.replace(binding.trainerFrame.getId(), memberHomeFragment,"frag1").commit();
                    break;
            }
            return true;
        });
    }
}