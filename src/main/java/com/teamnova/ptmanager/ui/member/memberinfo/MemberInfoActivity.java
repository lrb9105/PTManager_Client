package com.teamnova.ptmanager.ui.member.memberinfo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.teamnova.ptmanager.R;
import com.teamnova.ptmanager.databinding.ActivityLessonAttendanceBinding;
import com.teamnova.ptmanager.databinding.ActivityMemberHomeBinding;
import com.teamnova.ptmanager.databinding.ActivityMemberInfoBinding;
import com.teamnova.ptmanager.model.userInfo.FriendInfoDto;
import com.teamnova.ptmanager.test.TestActivity;
import com.teamnova.ptmanager.ui.changehistory.ChangeHistoryFragment;
import com.teamnova.ptmanager.ui.home.member.ViewPagerAdapter;
import com.teamnova.ptmanager.ui.home.member.fragment.MemberHomeFragment;
import com.teamnova.ptmanager.ui.member.memberinfo.adapter.MemberInfoVPAdapter;
import com.teamnova.ptmanager.viewmodel.friend.FriendViewModel;

/**
 * 트레이너가 회원을 클릭 시 출력되는 회원정보 화면
 * */
public class MemberInfoActivity extends AppCompatActivity {
    private ActivityMemberInfoBinding binding;

    // 회원정보
    private FriendInfoDto memberInfo;

    // 프래그먼트 트랜잭션(프래그먼트 교체, 추가 , 삭제)을 처리할 프래그먼트 매니저
    private FragmentManager fragmentManager;

    // 회원 기본정보 프래그먼트
    private MemberBasicInfoFragment memberBasicInfoFragment;

    // Friend
    private FriendViewModel friendViewModel;
    // 눈바디 프래그먼트


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityMemberInfoBinding.inflate(getLayoutInflater());

        // 기존에는 layout이 들어갔는데 대신 getRoot를 넣어준다.
        setContentView(binding.getRoot());

        super.onCreate(savedInstanceState);

        initialize();

        // 일정 탭레이아웃 세팅
        String[] titleArr = new String[]{"기본정보","눈바디","인바디","식사","운동"};

        TabLayout tabLayout = binding.tabLayout;
        ViewPager2 viewPager2 = binding.viewPager;

        MemberInfoVPAdapter adapter = new MemberInfoVPAdapter(this);

        viewPager2.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> tab.setText(titleArr[position])).attach();
    }

    /**
     * 초기화 메소드
     * */
    public void initialize(){
        // FriendViewModel 초기화
        friendViewModel = new ViewModelProvider(this).get(FriendViewModel.class);

        // 클릭된 회원정보
        memberInfo = (FriendInfoDto)getIntent().getSerializableExtra("memberInfo");

        // 회원정보 viewModel에 저장
        friendViewModel.getFriendInfo().setValue(memberInfo);
    }
}