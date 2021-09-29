package com.teamnova.ptmanager.ui.record;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.teamnova.ptmanager.R;
import com.teamnova.ptmanager.databinding.FragmentRecordBinding;
import com.teamnova.ptmanager.model.userInfo.FriendInfoDto;
import com.teamnova.ptmanager.viewmodel.friend.FriendViewModel;

/**
 * 기록(식사, 운동)프래그먼트를 담는 부모 프래그먼트
 * */
public class RecordFragment extends Fragment implements View.OnClickListener {
    FragmentRecordBinding binding;
    boolean isConnected = false;

    // 데이터를 공유할 viewModel
    private FriendViewModel friendViewModel;

    // 회원 정보
    private FriendInfoDto memberInfo;

    public RecordFragment() {
        // Required empty public constructor
    }

    public static RecordFragment newInstance(String param1, String param2) {
        RecordFragment fragment = new RecordFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        /*startActivityResult = registerForActivityResult(
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
                });*/
        super.onCreate(savedInstanceState);

        // 부모 액티비티의 라이프사이클을 갖는 VIEWMODEL 생성
        friendViewModel = new ViewModelProvider(requireActivity()).get(FriendViewModel.class);

        // 로그인한 회원 정보 observe
        friendViewModel.getFriendInfo().observe(requireActivity(), memberInfo -> {
            this.memberInfo = memberInfo;
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d("기록 프래그먼트의 onCreateView", "얍얍");

        // binder객체 생성 및 레이아웃 사용
        binding = FragmentRecordBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // 일정 탭레이아웃 세팅
        String[] titleArr = new String[]{"식사","운동"};

        TabLayout tabLayout = binding.tabLayout;
        ViewPager2 viewPager2 = binding.viewPager;

        MemberRecordVPAdapter adapter = new MemberRecordVPAdapter(this);

        viewPager2.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> tab.setText(titleArr[position])).attach();

        // 식사 아이콘 넣기
        tabLayout.getTabAt(0).setIcon(R.drawable.meal_icon);
        // 운동 아이콘 넣기
        tabLayout.getTabAt(1).setIcon(R.drawable.fitness_icon);

        setOnclickListener();
        initialize();

        return view;
    }

    public void initialize(){ }

    public void setOnclickListener(){}

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