package com.teamnova.ptmanager.ui.home.member.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.teamnova.ptmanager.R;
import com.teamnova.ptmanager.adapter.friend.FriendAddAdapter;
import com.teamnova.ptmanager.databinding.FragmentMemberHomeBinding;
import com.teamnova.ptmanager.databinding.FragmentTrainerHomeBinding;
import com.teamnova.ptmanager.model.chatting.ChattingMemberDto;
import com.teamnova.ptmanager.model.lesson.LessonSchInfo;
import com.teamnova.ptmanager.model.userInfo.FriendInfoDto;
import com.teamnova.ptmanager.model.userInfo.UserInfoDto;
import com.teamnova.ptmanager.ui.chatting.ChattingActivity;
import com.teamnova.ptmanager.ui.home.member.DayViewActivity;
import com.teamnova.ptmanager.ui.home.member.Event;
import com.teamnova.ptmanager.ui.home.member.ViewPagerAdapter;
import com.teamnova.ptmanager.ui.home.trainer.TrainerHomeActivity;
import com.teamnova.ptmanager.ui.login.LoginActivity;
import com.teamnova.ptmanager.ui.login.findpw.FindPw3Activity;
import com.teamnova.ptmanager.ui.member.MemberAddActivity;
import com.teamnova.ptmanager.ui.member.memberinfo.MemberInfoActivity;
import com.teamnova.ptmanager.ui.schedule.schedule.DailyScheduleActivity;
import com.teamnova.ptmanager.ui.schedule.schedule.decorator.DotDecorator;
import com.teamnova.ptmanager.ui.schedule.schedule.decorator.SaturdayDecorator;
import com.teamnova.ptmanager.ui.schedule.schedule.decorator.SundayDecorator;
import com.teamnova.ptmanager.ui.schedule.schedule.decorator.TodayDecorator;
import com.teamnova.ptmanager.util.GetDate;
import com.teamnova.ptmanager.viewmodel.friend.FriendViewModel;
import com.teamnova.ptmanager.viewmodel.login.LoginViewModel;
import com.teamnova.ptmanager.viewmodel.schedule.lesson.LessonViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MemberHomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MemberHomeFragment extends Fragment implements View.OnClickListener {
    FragmentMemberHomeBinding binding;
    boolean isConnected = false;

    // 데이터를 공유할 viewModel
    private FriendViewModel friendViewModel;

    // 트레이너 정보를 가져오기 위한 viewModel
    private LoginViewModel loginViewModel;
    private LessonViewModel lessonViewModel;
    
    // 트레이너 정보
    private UserInfoDto trainerInfo;

    // 회원 정보
    private FriendInfoDto memberInfo;

    private MaterialCalendarView calendarView;

    // 점찍을 날짜를 저장한 hashSet
    public HashSet<CalendarDay> dateSet;

    // 예약 완료, 예약취소 시  시 데이터 동기화를 위한 객체
    private ActivityResultLauncher<Intent> startActivityResult;

    // 레슨리스트
    private ArrayList<LessonSchInfo> lessonSchList;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MemberHomeFragment() {
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
    public static MemberHomeFragment newInstance(String param1, String param2) {
        MemberHomeFragment fragment = new MemberHomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        startActivityResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // 레슨예약 완료 시 viewModel 데이터 업데이트
                            LessonSchInfo lessonSchInfo = (LessonSchInfo)result.getData().getSerializableExtra("lessonSchInfo");

                            // 예약완료 후 넘어온것인지 예약취소 후 넘어온 것인지 확인
                            String reserveOrCancel = result.getData().getStringExtra("reserveOrCancel");

                            if(reserveOrCancel.equals("reserve")){
                                // 예약완료: 레스트의 첫번째에 데이터를 추가해서 리사이클러뷰에서 추가된 데이터가 가장 위에 오도록 배치
                                lessonSchList.add(0,lessonSchInfo);
                            } else if(reserveOrCancel.equals("cancel")){
                                // 예약취소: 넘어온 레슨정보 기존 리스트에 있는 동일한 아이디를 가지는 정보와 교체
                                String lessonSchId = lessonSchInfo.getLessonSchId();

                                for(int i = 0; i < lessonSchList.size(); i++){
                                    LessonSchInfo tempInfo = lessonSchList.get(i);
                                    if(tempInfo.getLessonSchId().equals(lessonSchId)){
                                        lessonSchList.set(i, tempInfo);

                                        Log.d("예약취소 저장 잘 됐나?", lessonSchList.get(i).toString());

                                        break;
                                    }
                                }
                            }

                            lessonViewModel.getLessonSchList().setValue(lessonSchList);
                        }
                    }
                });
        super.onCreate(savedInstanceState);

        Log.d("홈 프래그먼트의 onCreate", "얍얍");

        // 부모 액티비티의 라이프사이클을 갖는 VIEWMODEL 생성
        friendViewModel = new ViewModelProvider(requireActivity()).get(FriendViewModel.class);
        loginViewModel = new ViewModelProvider(requireActivity()).get(LoginViewModel.class);
        lessonViewModel = new ViewModelProvider(requireActivity()).get(LessonViewModel.class);

        // 로그인한 회원 정보 observe
        friendViewModel.getFriendInfo().observe(requireActivity(), memberInfo -> {
            this.memberInfo = memberInfo;

            // 로그인한 회원 정보 변경 시 UI UPDATE
            updateMemberUi(memberInfo);

            // 나를 등록한 트레이너가 있고 연결이 완료되어있지 않다면 다이얼로그 출력
            if(memberInfo.getTrainerId() != null){
                if(memberInfo.getIsConnected().equals("N")){
                    Log.d("트레이너Id: ", memberInfo.getTrainerId());
                    Log.d("연결여부: ", memberInfo.getIsConnected());

                    // 다이얼로그빌더
                    AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

                    builder.setMessage("회원등록 요청이 있습니다. 수락하시겠습니까?");

                    builder.setPositiveButton("수락", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int id)
                        {
                            // 연결완료 - FRIEND테이블의 FRIEND_ID
                            friendViewModel.completeConnect(memberInfo.getUserId());
                            memberInfo.setIsConnected("Y");

                            // 회원등록 요청 수락 시 트레이너 정보 가져옴
                            loginViewModel.getUserInfo(memberInfo.getTrainerId(),0);
                        }
                    }).setNegativeButton("거부",new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int id)
                        { }
                    }
                    );



                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                } else if(memberInfo.getIsConnected().equals("Y")){ // 이미 연결되어 있다면
                    loginViewModel.getUserInfo(memberInfo.getTrainerId(),0);
                }
            }
        });

        // 트레이너 정보의 변경 확인
        loginViewModel.getUserInfo().observe(requireActivity(), loginUserInfo ->{

            System.out.println("=사용자아이디: " + loginUserInfo.getUserId());

            trainerInfo = loginUserInfo;
            updateTrainerUi(loginUserInfo);
        });

        // 회원의 레슨일정 변경
        lessonViewModel.getLessonSchList().observe(requireActivity(), lessonSchList ->{
            Log.d("레슨리스트 사이즈: ", "" + lessonSchList.size());
            this.lessonSchList = lessonSchList;
            makePointToDateOfLessonRegistered(lessonSchList);

        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        Log.d("홈 프래그먼트의 onCreateView", "얍얍");

        // binder객체 생성 및 레이아웃 사용
        binding = FragmentMemberHomeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // 일정 탭레이아웃 세팅
        String[] titleArr = new String[]{"전체일정","예약일정","지난일정"};

        TabLayout tabLayout = binding.tabLayout;
        ViewPager2 viewPager2 = binding.viewPager;

        ViewPagerAdapter adapter = new ViewPagerAdapter(this);

        viewPager2.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> tab.setText(titleArr[position])).attach();

        setOnclickListener();
        initialize();

        return view;
    }

    public void initialize(){
        // 달력
        calendarView = binding.monthSch;

        // 토, 일요일, 오늘 선택 되는 데코적용
        calendarView.addDecorator(new TodayDecorator(getContext()));
        calendarView.addDecorator(new SundayDecorator());
        calendarView.addDecorator(new SaturdayDecorator());
    }

    public void setOnclickListener(){
        binding.monthSch.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                // 연결되어있는 트레이너가 있다면
                if(memberInfo.getIsConnected() != null && memberInfo.getIsConnected().equals("Y")){
                    Intent intent = new Intent(getActivity(), DayViewActivity.class);

                    // trainerId
                    intent.putExtra("trainerId", memberInfo.getTrainerId());
                    // memberLoginId
                    intent.putExtra("memberLoginId", memberInfo.getLoginId());
                    // memberId
                    intent.putExtra("memberId", memberInfo.getUserId());
                    // memberInfo
                    intent.putExtra("memberInfo", memberInfo);

                    //date
                    String date2 = ""+ date.getYear() + (date.getMonth() < 9 ? "0"+(date.getMonth()+1) : (date.getMonth()+1)) + (date.getDay() <10 ? "0" + date.getDay() : date.getDay());

                    intent.putExtra("todayDate", date.getYear()+"년 " + (date.getMonth()+1) + "월 " + date.getDay() + "일");
                    intent.putExtra("todayDateForServer", date2);

                    startActivityResult.launch(intent);

                    //startActivity(intent);
                }
            }
        });

        binding.layoutTrainerInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 트레이너 정보 클릭 시 프로필 정보 or 채팅 가능한 다이얼로그 출력
                AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

                builder.setItems(new String[]{"트레이너정보 보기", "대화 하기"}, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int pos)
                    {
                        if(pos == 0){
                            /** 트레이너정보 보기 */

                        }else{
                            /** 채팅화면 생성*/
                            Intent intent = new Intent(requireActivity(), ChattingActivity.class);

                            /** 채팅참여자 정보 생성 */
                            ArrayList<ChattingMemberDto> chatMemberList = new ArrayList<>();

                            // 트레이너 정보
                            UserInfoDto trainerInfo = MemberHomeFragment.this.trainerInfo;

                            // 회원 정보
                            FriendInfoDto memberInfo = MemberHomeFragment.this.memberInfo;

                            chatMemberList.add(ChattingMemberDto.makeChatMemberInfo(memberInfo));
                            chatMemberList.add(ChattingMemberDto.makeChatMemberInfo(trainerInfo));

                            intent.putExtra("chatMemberList",chatMemberList);

                            requireActivity().startActivity(intent);
                        }
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }

    // 로그인한 회원의 ui 업데이트
    public void updateMemberUi(FriendInfoDto memberInfo){
        // 회원 프로필사진
        if(memberInfo.getProfileId() != null) {
            Glide.with(this).load("http://15.165.144.216" + memberInfo.getProfileId()).into(binding.userProfile);
        }
        // 회원명
        binding.userName.setText(memberInfo.getUserName());
    }

    // 수강권 정보 업데이트
    public void updateLecturePassInfo(){
        /** 수강권정보*/
        // 남은 횟수
        int remainCnt = memberInfo.getTotalCnt() - memberInfo.getUsedCnt();

        // 수강중인 수업이 있다면
        if(memberInfo.getLectureName() != null && !memberInfo.getLectureName().equals("") && remainCnt > 0){
            binding.remainCntExpDate.setText(memberInfo.getLectureName() + "(" + memberInfo.getUsedCnt() + "/" + memberInfo.getTotalCnt() + "회)");
            binding.remainCntExpDate.setVisibility(View.VISIBLE);
        } else{ //없다면
            binding.remainCntExpDate.setVisibility(View.GONE);
        }
    }

    // 로그인한 회원에게 연결된 트레이너가 있다면 트레이너의 ui 업데이트
    public void updateTrainerUi(UserInfoDto loginUserInfoDto){
        binding.layoutTrainerInfo.setVisibility(View.VISIBLE);

        if(loginUserInfoDto.getProfileId() != null){
            Glide.with(this).load("http://15.165.144.216" + loginUserInfoDto.getProfileId()).into(binding.trainerProfile);
        }

        binding.trainerName.setText(loginUserInfoDto.getUserName());
        binding.branchOffice.setText(loginUserInfoDto.getBranchOffice());

        // 연결된 트레이너가 있다면 수강권 정보 업데이트
        updateLecturePassInfo();

        // 트레이너 정보가 세팅되었다면 일정가져오기
        lessonViewModel.getLessonListByMember(this.memberInfo.getUserId());
    }

    // 회원의 일정이 등록된 날짜 밑에 점찍기
    public void makePointToDateOfLessonRegistered(ArrayList<LessonSchInfo> lessonSchList){
        dateSet = new HashSet<>();

        for(LessonSchInfo lesson : lessonSchList){
            String date = lesson.getLessonDate();
            int year = Integer.parseInt(date.substring(0,4));
            // month는 0부터 시작!
            int month = Integer.parseInt(date.substring(4,6)) - 1;
            int dayOfWeek = Integer.parseInt(date.substring(6,8));

            CalendarDay c = CalendarDay.from(year, month, dayOfWeek);
            dateSet.add(c);
        }

        calendarView.addDecorator(new DotDecorator(Color.RED,dateSet));
    }

    @Override
    public void onClick(View v) {
        Intent intent;

        /*switch(v.getId()){
            case  R.id.btn_add_member:

                break;
            default:
                break;
        }*/
    }
}