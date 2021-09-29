package com.teamnova.ptmanager.ui.changehistory.eyebody;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.teamnova.ptmanager.R;
import com.teamnova.ptmanager.adapter.lesson.member.MemberLessonAdapter;
import com.teamnova.ptmanager.databinding.ActivityEyeBodyChangeHistoryBinding;
import com.teamnova.ptmanager.databinding.ActivityEyeBodyCompareHistoryBinding;
import com.teamnova.ptmanager.model.changehistory.eyebody.EyeBody;
import com.teamnova.ptmanager.model.changehistory.eyebody.EyeBodyCompare;
import com.teamnova.ptmanager.model.changehistory.eyebody.EyeBodyHistoryInfo;
import com.teamnova.ptmanager.model.lesson.LessonSchInfo;
import com.teamnova.ptmanager.model.userInfo.FriendInfoDto;
import com.teamnova.ptmanager.ui.changehistory.eyebody.adapter.EyeBodyCompareHistoryAdapter;
import com.teamnova.ptmanager.ui.home.member.MemberHomeActivity;
import com.teamnova.ptmanager.ui.home.trainer.TrainerHomeActivity;
import com.teamnova.ptmanager.ui.register.RegisterActivity;
import com.teamnova.ptmanager.viewmodel.friend.FriendViewModel;

import java.util.ArrayList;

/**
 * 눈바디 비교기록 더보기 시 출력되는 화면
 * */
public class EyeBodyCompareHistoryActivity extends AppCompatActivity implements View.OnClickListener{
    private ActivityEyeBodyCompareHistoryBinding binding;

    // 눈바디 비교사진 추가 후 돌아 옴
    private ActivityResultLauncher<Intent> startActivityResult;

    // 눈바디 비교사진 삭제 후 돌아 옴
    private ActivityResultLauncher<Intent> startActivityResult2;

    // 리사이클러뷰
    private RecyclerView recyclerView;
    private EyeBodyCompareHistoryAdapter eyeBodyCompareHistoryAdapter;
    private LinearLayoutManager linearLayoutManager;

    // 눈바디 비교 배열 리스트
    private ArrayList<EyeBodyCompare[]> eyeBodyCompareArrList;

    // 데이터 저장여부
    private boolean isAddedEyeBodyCompareInfo = false;

    // 데이터 삭제여부
    private boolean isDeletedEyeBodyCompareInfo = false;

    // 삭제한 리사이클러뷰 위치
    int parentPosition = 9999;

    // 추가한 눈바디 비교정보
    private EyeBodyCompare[] addedEyeBodyCompareArr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityEyeBodyCompareHistoryBinding.inflate(getLayoutInflater());

        // 기존에는 layout이 들어갔는데 대신 getRoot를 넣어준다.
        setContentView(binding.getRoot());

        // 눈바디 비교 저장 완료 시
        startActivityResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            addedEyeBodyCompareArr = (EyeBodyCompare[])result.getData().getSerializableExtra("addedEyeBodyCompareArr");
                            isAddedEyeBodyCompareInfo = true;

                            eyeBodyCompareHistoryAdapter.addEyeBodyCompareInfo(addedEyeBodyCompareArr);
                            recyclerView.scrollToPosition(0);
                        }
                    }
                });

        startActivityResult2 = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // isDeletedEyeBodyInfo = true
                            isDeletedEyeBodyCompareInfo = true;

                            Log.d("여기까지 오는가?11112321321", "11");

                            /** 부모, 자식 리사이클러뷰의 위치값 받아옴  */
                            parentPosition = result.getData().getIntExtra("parentPosition", 99999);

                            Log.d("부모리사위치22222: ", "" + parentPosition);

                            // 리사이클러뷰의 값을 삭제한다. -- 여기서 에러남!!
                            eyeBodyCompareHistoryAdapter.deleteEyeBodyCompare(parentPosition);
                        }
                    }
                });

        super.onCreate(savedInstanceState);

        setOnclickListener();

        initialize();
    }

    public void initialize(){
        // 트레이너라면 버튼 비활성화
        if(TrainerHomeActivity.staticLoginUserInfo != null){
            binding.btnAddEybodyCompare.setVisibility(View.GONE);
        }

        // 눈바디 비교배열 리스트
        eyeBodyCompareArrList = (ArrayList<EyeBodyCompare[]>)getIntent().getSerializableExtra("eyeBodyCompareArrList");

        Log.d("눈바디 비교데이터 잘들어오나?", "" + eyeBodyCompareArrList.size());
        Log.d("눈바디 비교데이터 잘들어오나222?", "" + eyeBodyCompareArrList.get(0)[1].getCreDate());

        // 리사이클러뷰 세팅
        recyclerView = binding.recyclerviewEyebodyCompareHistory;
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        // 데이터 가져와서 adapter에 넘겨 줌
        eyeBodyCompareHistoryAdapter = new EyeBodyCompareHistoryAdapter(eyeBodyCompareArrList, this,startActivityResult2);
        recyclerView.setAdapter(eyeBodyCompareHistoryAdapter);

        // 회원정보
        FriendInfoDto memberInfo = (FriendInfoDto) getIntent().getSerializableExtra("memberInfo");
    }

    private void setOnclickListener(){
        // 눈바디 비교 추가
        binding.btnAddEybodyCompare.setOnClickListener(this);
        binding.btnBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;

        switch(v.getId()) {
            // 눈바디 비교 추가
            case R.id.btn_add_eybody_compare:
                intent = new Intent(this, EyeBodySelectActivity.class);
                intent.putExtra("eyeBodyList", getIntent().getSerializableExtra("eyeBodyList"));

                startActivityResult.launch(intent);
                break;
            case R.id.btn_back: // 뒤로가기
                onBackPressed();
                break;
        }
    }

    // 뒤로가기 시 추가한 데이터가 있다면 보내주기

    @Override
    public void onBackPressed() {
        if(isAddedEyeBodyCompareInfo){ //눈바디 비교정보 추가 시
            Intent intent = null;

            intent = new Intent(this, MemberHomeActivity.class);

            // 눈바디 정보리스트
            intent.putExtra("addedEyeBodyCompareArr", addedEyeBodyCompareArr);

            setResult(Activity.RESULT_OK, intent);
            finish();
        } else if(isDeletedEyeBodyCompareInfo){ //눈바디 비교정보의 데이터 삭제 시
            Intent intent = null;

            intent = new Intent(this, MemberHomeActivity.class);

            intent.putExtra("deletedEyeBodyCompareInfoPosition",parentPosition);

            setResult(Activity.RESULT_OK, intent);
            finish();
        }

        super.onBackPressed();
    }
}