package com.teamnova.ptmanager.ui.chatting;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.teamnova.ptmanager.R;
import com.teamnova.ptmanager.databinding.FragmentChattingListBinding;
import com.teamnova.ptmanager.databinding.FragmentMoreinfoBinding;
import com.teamnova.ptmanager.model.userInfo.FriendInfoDto;
import com.teamnova.ptmanager.ui.login.LoginActivity;
import com.teamnova.ptmanager.viewmodel.friend.FriendViewModel;

/**
 * 더보기 화면 프래그먼트
 * */
public class MoreInfoFragment2 extends Fragment implements View.OnClickListener {
    FragmentChattingListBinding binding;

    // 데이터를 공유할 viewModel
    private FriendViewModel friendViewModel;

    // 회원 정보
    private FriendInfoDto memberInfo;

    // 눈바디 비교 정보 가져올 객체
    private ActivityResultLauncher<Intent> startActivityResult;


    public MoreInfoFragment2() {
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
    public static MoreInfoFragment2 newInstance(String param1, String param2) {
        MoreInfoFragment2 fragment = new MoreInfoFragment2();
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

        Log.d("홈 프래그먼트의 onCreate", "얍얍");

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
        // Inflate the layout for this fragment

        Log.d("변화기록 프래그먼트의 onCreateView", "얍얍");

        // binder객체 생성 및 레이아웃 사용
        binding = FragmentChattingListBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        setOnclickListener();
        initialize();

        return view;
    }

    public void initialize(){ }

    public void setOnclickListener(){
        //binding.logout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;

        switch(v.getId()){
            case  R.id.logout: // 로그아웃 버튼 클릭
                // shared 정보 삭제
                /** 로그아웃*/
                SharedPreferences auto = getActivity().getSharedPreferences("autoLogin", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = auto.edit();
                //editor.clear()는 auto에 들어있는 모든 정보를 기기에서 지웁니다.
                editor.clear();
                editor.commit();

                // 로그인 화면으로 이동
                intent = new Intent(requireActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();

                break;
            default:
                break;
        }
    }
}