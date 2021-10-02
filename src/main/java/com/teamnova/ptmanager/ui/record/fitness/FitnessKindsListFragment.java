package com.teamnova.ptmanager.ui.record.fitness;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.teamnova.ptmanager.R;
import com.teamnova.ptmanager.adapter.lesson.member.MemberLessonAdapter;
import com.teamnova.ptmanager.model.lesson.LessonSchInfo;
import com.teamnova.ptmanager.model.record.fitness.FitnessKinds;
import com.teamnova.ptmanager.model.record.fitness.FitnessRecord;
import com.teamnova.ptmanager.model.userInfo.FriendInfoDto;
import com.teamnova.ptmanager.test.TestActivity;
import com.teamnova.ptmanager.ui.record.fitness.adapter.FitnessKindsListAdapter;
import com.teamnova.ptmanager.viewmodel.record.fitness.FitnessViewModel;
import com.teamnova.ptmanager.viewmodel.schedule.lesson.LessonViewModel;

import java.util.ArrayList;

/**
 * 운동종류 리스트
 */
public class FitnessKindsListFragment extends Fragment {
    private FitnessViewModel fitnessViewModel;

    private FriendInfoDto memberInfo;

    // 리사이클러뷰
    private FitnessKindsListAdapter fitnessKindsListAdapter;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    // 레슨일정
    private ArrayList<LessonSchInfo> lessonSchList;

    // LessonDetail(레슨내역)에서 예약취소요청 완료 시 다시 넘어옴
    private ActivityResultLauncher<Intent> startActivityResult;

    // 해당 프래그가 뿌려야할 타입
    private int type;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FitnessKindsListFragment(int type, FriendInfoDto memberInfo) {
        this.type = type;
        this.memberInfo = memberInfo;
        // Required empty public constructor
    }


    public static FitnessKindsListFragment newInstance(String param1, String param2) {
        FitnessKindsListFragment fragment = new FitnessKindsListFragment(0, null);
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // 운동등록 완료 시 운동데이터 보냄
        startActivityResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // 운동입력 화면에서 받아온 운동기록
                            FitnessRecord fitnessInfo = (FitnessRecord)result.getData().getSerializableExtra("fitnessInfo");

                            Intent intent = new Intent(getActivity(), FitnessRegisterActivity.class);
                            intent.putExtra("fitnessInfo", fitnessInfo);

                            getActivity().setResult(Activity.RESULT_OK, intent);
                            getActivity().finish();

                        }
                    }
                });
        super.onCreate(savedInstanceState);
        fitnessViewModel = new ViewModelProvider(getActivity()).get(FitnessViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_kinds_list, container, false);

        /** 즐겨찾기*/
        if(type == 1) { //즐겨찾기 프래그먼트에만 적용되도록
            fitnessViewModel.getFavoriteList().observe(getActivity(), fitnessKindsList -> {
                // 리사이클러뷰 세팅
                recyclerView = view.findViewById(R.id.recyclerview_kinds_list);
                layoutManager = new LinearLayoutManager(getActivity());
                recyclerView.setLayoutManager(layoutManager);

                // 데이터 가져와서 adapter에 넘겨 줌
                fitnessKindsListAdapter = new FitnessKindsListAdapter(fitnessKindsList, getActivity(),startActivityResult, fitnessViewModel, memberInfo);
                recyclerView.setAdapter(fitnessKindsListAdapter);
            });
        }

        // getViewLifecycleOwner(): Fragment view의 라이프사이클이다. 따라서 view가 생성되는 onCreateView()에서 사용해야 한다!!
        // 부모 액트의 라이프사이클을 따라가게 하기위해 getActivity()를 사용
        fitnessViewModel.getFitnessKindsList().observe(getActivity(), fitnessKindsList -> {
            ArrayList<FitnessKinds> tempKindsList = new ArrayList<>();
            String typeStr = "";

            if(type != 1) { // 즐겨찾기 아닌 경우
                switch (type){
                    case 2: //전체
                        tempKindsList = fitnessKindsList;
                        break;
                    case 3: //가슴
                        typeStr = "가슴";
                        break;
                    case 4: //어깨
                        typeStr = "어깨";
                        break;
                    case 5: //등
                        typeStr = "등";
                        break;
                    case 6: //복근
                        typeStr = "복근";
                        break;
                    case 7: //삼두
                        typeStr = "삼두";
                        break;
                    case 8: //이두
                        typeStr = "이두";
                        break;
                    case 9: //엉덩이
                        typeStr = "엉덩이";
                        break;
                    case 10: //전신
                        typeStr = "전신";
                        break;
                    case 11: //코어
                        typeStr = "코어";
                        break;
                    case 12: //맨몸
                        typeStr = "맨몸";
                        break;
                    case 13: //유산소
                        typeStr = "유산소";
                        break;
                    case 14: //기타
                        typeStr = "기타";
                        break;
                }

                if(!typeStr.equals("")){
                    for(int i = 0; i < fitnessKindsList.size(); i++){
                        if(fitnessKindsList.get(i).getPart().equals(typeStr)){
                            tempKindsList.add(fitnessKindsList.get(i));
                        }
                    }
                }

                // 리사이클러뷰 세팅
                recyclerView = view.findViewById(R.id.recyclerview_kinds_list);
                layoutManager = new LinearLayoutManager(getActivity());
                recyclerView.setLayoutManager(layoutManager);

                // 데이터 가져와서 adapter에 넘겨 줌
                fitnessKindsListAdapter = new FitnessKindsListAdapter(tempKindsList, getActivity(),startActivityResult, fitnessViewModel, memberInfo);
                recyclerView.setAdapter(fitnessKindsListAdapter);
            } else{
                // DUMMY 아답터
                fitnessKindsListAdapter = new FitnessKindsListAdapter(fitnessKindsList, getActivity(),startActivityResult, fitnessViewModel, memberInfo);
                for(int i = 0; i < fitnessKindsList.size(); i++){
                    if(fitnessKindsList.get(i).getIsFavoriteYn().equals("1")){
                        // 즐겨찾기 프래그에 있는 애들은 처음부터 검은별
                        fitnessKindsList.get(i).setFavoriteChecked(true);
                        tempKindsList.add(fitnessKindsList.get(i));

                        fitnessViewModel.getFavoriteList().setValue(tempKindsList);
                    }
                }
            }
        });

        /** 운동종류 검색*/
        fitnessViewModel.getSearchText().observe(getActivity(), searchText ->{
            fitnessKindsListAdapter.getFilter().filter(searchText);
        });

        return view;
    }
}