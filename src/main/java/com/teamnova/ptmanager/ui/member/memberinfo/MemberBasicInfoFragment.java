package com.teamnova.ptmanager.ui.member.memberinfo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.teamnova.ptmanager.R;
import com.teamnova.ptmanager.adapter.lesson.member.MemberLessonAdapter;
import com.teamnova.ptmanager.databinding.FragmentHomeBinding;
import com.teamnova.ptmanager.databinding.FragmentMemberBasicInfoBinding;
import com.teamnova.ptmanager.model.lesson.LessonSchInfo;
import com.teamnova.ptmanager.model.userInfo.FriendInfoDto;
import com.teamnova.ptmanager.util.GetDate;
import com.teamnova.ptmanager.viewmodel.friend.FriendViewModel;
import com.teamnova.ptmanager.viewmodel.schedule.lesson.LessonViewModel;

import java.util.ArrayList;

/**
 * 일반회원 기본정보
 */
public class MemberBasicInfoFragment extends Fragment {
    private FragmentMemberBasicInfoBinding binding;

    // 회원 기본정보를 가져올 viewmodel
    private FriendViewModel friendViewModel;

    // 회원정보
    private FriendInfoDto memberInfo;

    public MemberBasicInfoFragment() {
        // Required empty public constructor
    }

    public static MemberBasicInfoFragment newInstance(String param1, String param2) {
        MemberBasicInfoFragment fragment = new MemberBasicInfoFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("회원 기본정보 onCreate", "11");
        friendViewModel = new ViewModelProvider(requireActivity()).get(FriendViewModel.class);

        // 결과객체가 FriendDto
        // 멤버정보 변경 시 저장
        friendViewModel.getFriendInfo().observe(requireActivity(), memberInfo ->{
            this.memberInfo = memberInfo;
            Log.d("회원 기본정보 출력","11");
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentMemberBasicInfoBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        if(memberInfo != null){
            Log.d("회원 기본정보 출력","22");

            /** 회원정보 연결*/
            // 프로필사진
            if(memberInfo.getProfileId() != null){
                Glide.with(this).load("http://15.165.144.216" + memberInfo.getProfileId()).into(binding.memberProfile);
            }

            //이름
            binding.nameInput.setText(memberInfo.getUserName());

            // 생년월일
            binding.birthdayInput.setText(GetDate.getDateWithYMD(memberInfo.getBirth()));
            // 성별
            // 성별
            String gender;
            if(memberInfo.getGender() == 0){
                gender = "남";
            } else{
                gender = "여";
            }
            binding.genderInput.setText(gender);
            // 전화번호
            binding.phoneNumInput.setText(memberInfo.getPhoneNum());

        } else{
            Log.d("회원 기본정보 출력","33");
        }

        Log.d("회원 기본정보 onCreateView", "22");
        return view;
    }
}