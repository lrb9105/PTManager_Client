package com.teamnova.ptmanager.ui.home.member;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.teamnova.ptmanager.R;
import com.teamnova.ptmanager.databinding.ActivityMemberHomeBinding;
import com.teamnova.ptmanager.ui.changehistory.ChangeHistoryFragment;
import com.teamnova.ptmanager.ui.home.member.fragment.MemberHomeFragment;
import com.teamnova.ptmanager.ui.moreinfo.MoreInfoFragment;
import com.teamnova.ptmanager.ui.record.RecordFragment;
import com.teamnova.ptmanager.viewmodel.friend.FriendViewModel;

public class MemberHomeActivity extends AppCompatActivity {
    ActivityMemberHomeBinding binding;

    // 프래그먼트 트랜잭션(프래그먼트 교체, 추가 , 삭제)을 처리할 프래그먼트 매니저
    private FragmentManager fragmentManager;

    // 홈프래그먼트
    private MemberHomeFragment memberHomeFragment;

    // 변화기록 프래그먼트
    private ChangeHistoryFragment changeHistoryFragment;

    // 기록 프래그먼트
    private RecordFragment recordFragment;

    // 더보기 프래그먼트
    private MoreInfoFragment moreInfoFragment;

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
                    Log.d("여기서 문제?", "111");

                    if(memberHomeFragment == null) {
                        memberHomeFragment = new MemberHomeFragment();
                    }

                    if(recordFragment != null) fragmentManager.beginTransaction().hide(recordFragment).commit();
                    if(changeHistoryFragment != null) fragmentManager.beginTransaction().hide(changeHistoryFragment).commit();
                    if(memberHomeFragment != null) transaction2.show(memberHomeFragment).commit();
                    if(moreInfoFragment != null) fragmentManager.beginTransaction().hide(moreInfoFragment).commit();

                    break;
                case R.id.item_history: // 변화관리
                    if(changeHistoryFragment == null) {
                        changeHistoryFragment = new ChangeHistoryFragment();
                        transaction2.add(binding.trainerFrame.getId(), changeHistoryFragment,"frag2");
                    }

                    if(memberHomeFragment != null) transaction2.hide(memberHomeFragment).commit();
                    if(recordFragment != null) fragmentManager.beginTransaction().hide(recordFragment).commit();
                    if(changeHistoryFragment != null) fragmentManager.beginTransaction().show(changeHistoryFragment).commit();
                    if(moreInfoFragment != null) fragmentManager.beginTransaction().hide(moreInfoFragment).commit();

                    //transaction2.replace(binding.trainerFrame.getId(), changeHistoryFragment,"frag2").commit();
                    break;
                case R.id.item_cahnge: // 기록
                    if(recordFragment == null) {
                        recordFragment = new RecordFragment();
                        transaction2.add(binding.trainerFrame.getId(), recordFragment,"frag3");
                    }

                    if(memberHomeFragment != null) transaction2.hide(memberHomeFragment).commit();
                    if(changeHistoryFragment != null) fragmentManager.beginTransaction().hide(changeHistoryFragment).commit();
                    if(recordFragment != null) fragmentManager.beginTransaction().show(recordFragment).commit();
                    if(moreInfoFragment != null) fragmentManager.beginTransaction().hide(moreInfoFragment).commit();

                    //transaction2.replace(binding.trainerFrame.getId(), changeHistoryFragment,"frag2").commit();
                    break;
                case R.id.item_more: // 더보기
                    if(moreInfoFragment == null) {
                        moreInfoFragment = new MoreInfoFragment();
                        transaction2.add(binding.trainerFrame.getId(), moreInfoFragment,"frag4");
                    }

                    if(memberHomeFragment != null) transaction2.hide(memberHomeFragment).commit();
                    if(changeHistoryFragment != null) fragmentManager.beginTransaction().hide(changeHistoryFragment).commit();
                    if(recordFragment != null) fragmentManager.beginTransaction().hide(recordFragment).commit();
                    if(moreInfoFragment != null) fragmentManager.beginTransaction().show(moreInfoFragment).commit();

                    //transaction2.replace(binding.trainerFrame.getId(), changeHistoryFragment,"frag2").commit();
                    break;
            }
            return true;
        });
    }
}