package com.teamnova.ptmanager.ui.home.member.fragment;

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

import com.teamnova.ptmanager.R;
import com.teamnova.ptmanager.adapter.lecture.LectureListAdapter;
import com.teamnova.ptmanager.adapter.lecture.LectureRegisteredMemberListAdapter;
import com.teamnova.ptmanager.adapter.lesson.member.MemberLessonAdapter;
import com.teamnova.ptmanager.model.lesson.LessonSchInfo;
import com.teamnova.ptmanager.ui.home.member.Event;
import com.teamnova.ptmanager.ui.schedule.lecture.LectureListActivity;
import com.teamnova.ptmanager.viewmodel.schedule.lesson.LessonViewModel;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * 전체일정 리스트
 */
public class ChildFragment1 extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private LessonViewModel lessonViewModel;

    // 리사이클러뷰
    private MemberLessonAdapter memberLessonAdapter;
    private RecyclerView recyclerviewMemberLessonList1;
    private RecyclerView.LayoutManager layoutManager;

    // 레슨일정
    private ArrayList<LessonSchInfo> lessonSchList;

    // LessonDetail(레슨내역)에서 예약취소요청 완료 시 다시 넘어옴
    private ActivityResultLauncher<Intent> startActivityResult;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ChildFragment1() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChildFragment1.
     */
    // TODO: Rename and change types and number of parameters
    public static ChildFragment1 newInstance(String param1, String param2) {
        ChildFragment1 fragment = new ChildFragment1();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // 예약취소 완료 시
        startActivityResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // 예약취소 완료 시 여기로 넘어오는지 확인
                            String beModifiedLessonSchId = result.getData().getStringExtra("lessonSchId");
                            for(int i = 0; i < lessonSchList.size(); i++){
                                LessonSchInfo tempInfo = lessonSchList.get(i);
                                if(tempInfo.getLessonSchId().equals(beModifiedLessonSchId)){
                                    tempInfo.setCancelYn("M");
                                    lessonSchList.set(i, tempInfo);
                                    Log.d("예약취소 저장 잘 됐나?", lessonSchList.get(i).getCancelYn());

                                    break;
                                }
                            }
                            ArrayList<LessonSchInfo> newList = new ArrayList<>();
                            newList.addAll(lessonSchList);

                            Log.d("새로생성한 것과 기존것이 같나?", "" + (newList == lessonSchList));

                            lessonViewModel.getLessonSchList().setValue(newList);
                        }
                    }
                });
        super.onCreate(savedInstanceState);
        lessonViewModel = new ViewModelProvider(requireActivity()).get(LessonViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_child1, container, false);

        // getViewLifecycleOwner(): Fragment view의 라이프사이클이다. 따라서 view가 생성되는 onCreateView()에서 사용해야 한다!!
        // 부모 액트의 라이프사이클을 따라가게 하기위해 getActivity()를 사용
        lessonViewModel.getLessonSchList().observe(getActivity(), lessonSchList -> {
            // 테스트: cancelYn을 보기위한!
            if(this.lessonSchList != null){
                for(int i = 0; i < this.lessonSchList.size(); i++){
                    if(lessonSchList.get(i).getCancelYn() != null){
                        Log.d(i + " - beforecanCelYn: ",lessonSchList.get(i).getCancelYn());
                    }
                }
            }

            this.lessonSchList = lessonSchList;

            // 테스트: cancelYn을 보기위한!
            for(int i = 0; i < lessonSchList.size(); i++){
                if(lessonSchList.get(i).getCancelYn() != null){
                    Log.d(i + " - canCelYn: ",lessonSchList.get(i).getCancelYn());
                }
            }
            Log.d("child1 호출되나?","11");

            // 리사이클러뷰 세팅
            recyclerviewMemberLessonList1 = view.findViewById(R.id.recyclerview_member_lesson_list1);
            layoutManager = new LinearLayoutManager(requireActivity());
            recyclerviewMemberLessonList1.setLayoutManager(layoutManager);
            // 데이터 가져와서 adapter에 넘겨 줌
            memberLessonAdapter = new MemberLessonAdapter(this.lessonSchList, requireActivity(),startActivityResult);
            recyclerviewMemberLessonList1.setAdapter(memberLessonAdapter);

            if(this.lessonSchList != null && this.lessonSchList.size() > 0){
                ((TextView)view.findViewById(R.id.text1)).setText(this.lessonSchList.get(0).getLessonSchId() + "1111");
            }
        });

        return view;
    }
}