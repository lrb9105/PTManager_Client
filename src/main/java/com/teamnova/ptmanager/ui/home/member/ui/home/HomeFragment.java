package com.teamnova.ptmanager.ui.home.member.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.teamnova.ptmanager.R;
import com.teamnova.ptmanager.databinding.FragmentHomeBinding;
import com.teamnova.ptmanager.databinding.FragmentTrainerHomeBinding;
import com.teamnova.ptmanager.model.userInfo.UserInfoDto;
import com.teamnova.ptmanager.viewmodel.login.LoginViewModel;


public class HomeFragment extends Fragment {
    // 데이터를 공유할 viewModel
    private LoginViewModel loginViewModel;

    private FragmentHomeBinding binding;

    //private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // binder객체 생성 및 레이아웃 사용
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        loginViewModel = new ViewModelProvider(requireActivity()).get(LoginViewModel.class);

        // 회원정보 화면에 출력
        //setMemberInfo(loginViewModel.getLoginUserInfo2());
        return view;
    }

    // 로그인 시 트레이너 정보 화면에 연결
    public void setMemberInfo(UserInfoDto loginUserInfoDto){
        Glide.with(getActivity()).load("http://15.165.144.216" + loginUserInfoDto.getProfileId()).into(binding.userProfile);
        binding.memberName.setText(loginUserInfoDto.getUserName());
    }
}