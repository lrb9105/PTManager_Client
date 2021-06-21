package com.teamnova.ptmanager.ui.home.trainer.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.teamnova.ptmanager.R;
import com.teamnova.ptmanager.adapter.friend.FriendAddAdapter;
import com.teamnova.ptmanager.databinding.ActivityHomeBinding;
import com.teamnova.ptmanager.databinding.ActivityMainBinding;
import com.teamnova.ptmanager.databinding.FragmentTrainerHomeBinding;
import com.teamnova.ptmanager.model.userInfo.FriendInfoDto;
import com.teamnova.ptmanager.model.userInfo.UserInfoDto;
import com.teamnova.ptmanager.ui.home.trainer.TrainerHomeActivity;
import com.teamnova.ptmanager.ui.member.MemberAddActivity;
import com.teamnova.ptmanager.ui.schedule.schedule.DailyScheduleActivity;
import com.teamnova.ptmanager.util.GetDate;
import com.teamnova.ptmanager.viewmodel.friend.FriendViewModel;
import com.teamnova.ptmanager.viewmodel.login.LoginViewModel;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TrainerHomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TrainerHomeFragment extends Fragment implements View.OnClickListener {
    FragmentTrainerHomeBinding binding;

    // 데이터를 공유할 viewModel
    private LoginViewModel loginViewModel;
    private FriendViewModel friendViewModel;

    // 리사이클러뷰
    private FriendAddAdapter friendAddAdapter;
    private RecyclerView recyclerView_friends_list;
    private RecyclerView.LayoutManager layoutManager;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public TrainerHomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TrainerHomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TrainerHomeFragment newInstance(String param1, String param2) {
        TrainerHomeFragment fragment = new TrainerHomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("홈 프래그먼트의 onCreate", "얍얍");

        loginViewModel = new ViewModelProvider(requireActivity()).get(LoginViewModel.class);
        friendViewModel = new ViewModelProvider(requireActivity()).get(FriendViewModel.class);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        Log.d("홈 프래그먼트의 onCreateView", "얍얍");

        // binder객체 생성 및 레이아웃 사용
        binding = FragmentTrainerHomeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        //TrainerHomeAct-에서 전달한 번들 저장
        Bundle bundle = getArguments();

        /*//번들 안의 사용자 정보 불러오기
        if(bundle != null){
            UserInfoDto loginUserInfo = (UserInfoDto)bundle.get("loginUserInfo");

            if(bundle.get("id") != null){
                Log.d("id: ",(String)bundle.get("id"));
            }

            Log.d("프래그먼트에서 사용자정보 전달받음:", loginUserInfo.getLoginId());

            // 트레이너 정보가 입력되어있지 않다면 입력!
            if(binding.trainerName.getText().equals("") && binding.branchOffice.getText().equals("")){
                setTrainerInfo(loginUserInfo);
            }
        }*/

        if(binding.trainerName.getText().equals("") && binding.branchOffice.getText().equals("")){
            setTrainerInfo(loginViewModel.getLoginUserInfo2());
        }

        /**
         * 1. 서버에서 가져온 친구목록 리사이클러뷰로 보여주기
         * */

        if(TrainerHomeActivity.friendsList.size() == 0){
            //Log.d("프래그먼트에서 친구목록 전달받음:", TrainerHomeActivity.friendsList.get(0).getLoginId());
            TrainerHomeActivity.friendsList = new ArrayList<>();
        } else{
            Log.d("friendsList is null", "11");
        }

        // 리사이클러뷰 세팅
        recyclerView_friends_list = binding.recyclerviewFriendsList;
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView_friends_list.setLayoutManager(layoutManager);

        // 데이터 가져와서 adapter에 넘겨 줌
        friendAddAdapter = new FriendAddAdapter(TrainerHomeActivity.friendsList, getActivity());

        recyclerView_friends_list.setAdapter(friendAddAdapter);

        setOnclickListener();

        //return inflater.inflate(R.layout.fragment_trainer_home, container, false);
        return view;
    }

    // 로그인 시 트레이너 정보 화면에 연결
    public void setTrainerInfo(UserInfoDto loginUserInfoDto){
        if(loginUserInfoDto.getProfileId() != null){
            Glide.with(getActivity()).load("http://15.165.144.216" + loginUserInfoDto.getProfileId()).into(binding.userProfile);
        }

        binding.trainerName.setText(loginUserInfoDto.getUserName());
        binding.branchOffice.setText(loginUserInfoDto.getBranchOffice());
    }


    public void setOnclickListener(){
        binding.btnAddMember.setOnClickListener(this);
        binding.layoutScheduleOfToday.setOnClickListener(this);
        binding.btnScheduleOfToday.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;

        switch(v.getId()){
            case  R.id.btn_add_member: // 회원 추가 버튼 클릭
                // 회원추가 액티비티로 이동
                intent = new Intent(getActivity(), MemberAddActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_schedule_of_today:
            case R.id.layout_schedule_of_today: //오늘의 일정
                intent = new Intent(getActivity(), DailyScheduleActivity.class);
                String todayDate = GetDate.getTodayDate();
                intent.putExtra("date",todayDate);

                startActivity(intent);
                break;
            default:
                break;
        }
    }

    public FriendAddAdapter getFriendAddAdapter() {
        return friendAddAdapter;
    }

    public void aa(){
        Log.d("액티비티에서 프래그먼트 가져와서 사용", "11");
    }
}