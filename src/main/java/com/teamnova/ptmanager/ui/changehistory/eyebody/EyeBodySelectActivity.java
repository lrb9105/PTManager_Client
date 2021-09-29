package com.teamnova.ptmanager.ui.changehistory.eyebody;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.teamnova.ptmanager.R;
import com.teamnova.ptmanager.databinding.ActivityEyeBodyChangeHistoryBinding;
import com.teamnova.ptmanager.databinding.ActivityEyeBodySelectBinding;
import com.teamnova.ptmanager.model.changehistory.eyebody.EyeBody;
import com.teamnova.ptmanager.model.changehistory.eyebody.EyeBodyCompare;
import com.teamnova.ptmanager.model.changehistory.eyebody.EyeBodyHistoryInfo;
import com.teamnova.ptmanager.model.lesson.LessonSchInfo;
import com.teamnova.ptmanager.model.userInfo.FriendInfoDto;
import com.teamnova.ptmanager.network.RetrofitInstance;
import com.teamnova.ptmanager.network.changehistory.eyebody.EyeBodyService;
import com.teamnova.ptmanager.network.schedule.lesson.LessonService;
import com.teamnova.ptmanager.ui.changehistory.eyebody.adapter.EyeBodyChangeHistoryAdapter;
import com.teamnova.ptmanager.ui.changehistory.eyebody.adapter.EyeBodyChangeHistoryByDayAdapter;
import com.teamnova.ptmanager.ui.home.member.DayViewActivity;
import com.teamnova.ptmanager.ui.home.member.Event;
import com.teamnova.ptmanager.ui.home.member.MemberHomeActivity;
import com.teamnova.ptmanager.viewmodel.changehistory.eyebody.EyeBodyViewModel;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Retrofit;

/**
 * 눈바디 비교사진 선택하는 화면
 * */
public class EyeBodySelectActivity extends AppCompatActivity implements View.OnClickListener{
    private ActivityEyeBodySelectBinding binding;

    // 리사이클러뷰
    private EyeBodyChangeHistoryAdapter eyeBodyChangeHistoryAdapter;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    // 눈바디정보 리스트
    private ArrayList<EyeBody> eyeBodyList;

    // 눈바디 변화기록(날짜별)리스트
    private ArrayList<EyeBodyHistoryInfo> eyeBodyHistoryList;

    // 서버로 보낼 눈바디 비교 데이터
    private ArrayList<EyeBody> checkedEyeBodyInfoList;

    // 체크된 갯수를 관리
    public static int checkedEyeBodyInfoSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        binding = ActivityEyeBodySelectBinding.inflate(getLayoutInflater());

        // 기존에는 layout이 들어갔는데 대신 getRoot를 넣어준다.
        setContentView(binding.getRoot());

        super.onCreate(savedInstanceState);

        initialize();

        setOnClickListener();
    }

    public void setOnClickListener(){
        binding.selectEyebody.setOnClickListener(this);
        binding.btnBack.setOnClickListener(this);
    }

    // 초기화
    public void initialize(){
        // 사진 선택 갯수 초기화
        EyeBodySelectActivity.checkedEyeBodyInfoSize = 0;

        // 서버로 보낼 체크된 눈바디 정보
        checkedEyeBodyInfoList = new ArrayList<>();

        /** 날짜별 눈바디 변화 정보 가공*/
        // 눈바디 정보리스트
        eyeBodyList = (ArrayList<EyeBody>)getIntent().getSerializableExtra("eyeBodyList");

        int listIndex = 0;
        // 눈바디 변화기록리스트로 가공
        eyeBodyHistoryList = new ArrayList<>();

        if(eyeBodyList != null && eyeBodyList.size() > 0){
            for(int i = 0; i < eyeBodyList.size(); i++){
                EyeBody e = eyeBodyList.get(i);

                if(i > 0){ //i > 0이라면
                    String previousDate = eyeBodyList.get(i-1).getCreDate();

                    if(e.getCreDate().equals(previousDate)){ //이전 데이터와 날짜가 동일하다면
                        EyeBodyHistoryInfo tempHistoryInfo = eyeBodyHistoryList.get(listIndex);
                        ArrayList<EyeBody> tempList = tempHistoryInfo.getEyeBodyListByDay();
                        tempList.add(e);
                        tempHistoryInfo.setEyeBodyListByDay(tempList);
                        eyeBodyHistoryList.set(listIndex, tempHistoryInfo);
                    }else { // 이전데이터와 날짜가 동일하지 않다면
                        ArrayList<EyeBody> tempList = new ArrayList<>();
                        String date = e.getCreDate();
                        tempList.add(e);
                        EyeBodyHistoryInfo eyeBodyHistoryInfo = new EyeBodyHistoryInfo(date, tempList);
                        eyeBodyHistoryList.add(eyeBodyHistoryInfo);
                        listIndex++;
                    }
                } else { // i == 0이라면
                    ArrayList<EyeBody> tempList = new ArrayList<>();
                    String date = e.getCreDate();
                    tempList.add(e);
                    EyeBodyHistoryInfo eyeBodyHistoryInfo = new EyeBodyHistoryInfo(date, tempList);
                    eyeBodyHistoryList.add(eyeBodyHistoryInfo);
                }
            }

            // 리사이클러뷰 세팅
            recyclerView = binding.recyclerviewEyebodySelect;
            layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);

            EyeBodyChangeHistoryByDayAdapter.ItemClickListenerChild i = new EyeBodyChangeHistoryByDayAdapter.ItemClickListenerChild() {
                @Override
                public void onItemClicked(EyeBody e, boolean isClicked) {
                    // 채크되었다면 배열에 추가
                    if(isClicked && checkedEyeBodyInfoList.size() < 2){
                        checkedEyeBodyInfoList.add(e);
                    } else if(!isClicked && checkedEyeBodyInfoList.size() <= 2){
                        // 리스트에는 두개의 데이터만 들어올 수 있으므로 0아니면 1번째에 들어있음
                        if(checkedEyeBodyInfoList.get(0).getEyeBodyChangeId().equals(e.getEyeBodyChangeId())){
                            checkedEyeBodyInfoList.remove(0);
                        }else{
                            checkedEyeBodyInfoList.remove(1);
                        }
                    }

                }
            };

            // 데이터 가져와서 adapter에 넘겨 줌
            eyeBodyChangeHistoryAdapter = new EyeBodyChangeHistoryAdapter(eyeBodyHistoryList, this, 1,i,null);

            recyclerView.setAdapter(eyeBodyChangeHistoryAdapter);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.select_eyebody: // 눈바디 비교선택 완료
                /** 테스트 데이터 */
                /*int index = 0;

                Loop1:
                for(EyeBodyHistoryInfo e : eyeBodyHistoryList){
                    Loop2:
                    for(EyeBody e2 : e.getEyeBodyListByDay()){
                        checkedEyeBodyInfoList[index] = e2;
                        Log.d("1111223", checkedEyeBodyInfoList[index].getEyeBodyChangeId());

                        index++;
                        if(index == 2){
                            break Loop1;
                        }
                    }
                }*/


                if(checkedEyeBodyInfoList.size() < 2){
                    // 다이얼로그 출력
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);

                    builder.setMessage("2장의 사진을 선택해주세요");

                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int id)
                        {}
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                    return;
                }

                EyeBodySelectActivity.checkedEyeBodyInfoSize = 0;

                //서버로 전송
                Retrofit retrofit= RetrofitInstance.getRetroClient();
                EyeBodyService service = retrofit.create(EyeBodyService.class);

                // http request 객체 생성
                Call<String> call = service.registerEyeBodyCompareInfo(checkedEyeBodyInfoList);

                new RegisterEyeBodyCompareInfo().execute(call);
                break;
            case R.id.btn_back: // 뒤로가기
                onBackPressed();
                break;
        }
    }

    private class RegisterEyeBodyCompareInfo extends AsyncTask<Call, Void, String> {
        private retrofit2.Response<String> response;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("onPreExecute", "11");
        }

        @Override
        protected String  doInBackground(Call[] params) {
            try {
                retrofit2.Call<String> call = params[0];
                response = call.execute();


                return response.body();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            // 성공 시 commonId반환
            if(!result.equals("")){
                // 눈바디 비교 배열 생성
                EyeBodyCompare[] eyeBodyCompareArr = new EyeBodyCompare[2];

                for(int i = 0; i < eyeBodyCompareArr.length; i++){
                    eyeBodyCompareArr[i] = new EyeBodyCompare(
                              checkedEyeBodyInfoList.get(i).getEyeBodyChangeId()
                            , checkedEyeBodyInfoList.get(i).getUserId()
                            , checkedEyeBodyInfoList.get(i).getCreDate()
                            , checkedEyeBodyInfoList.get(i).getCreDateTime()
                            , checkedEyeBodyInfoList.get(i).getFileCreDateTime()
                            , checkedEyeBodyInfoList.get(i).getSavePath()
                            , checkedEyeBodyInfoList.get(i).getDelYn());

                    eyeBodyCompareArr[i].setCommonCompareId(result);
                }

                Intent intent = null;

                intent = new Intent(EyeBodySelectActivity.this, EyeBodyCompareHistoryActivity.class);

                // 눈바디 정보리스트
                intent.putExtra("addedEyeBodyCompareArr", eyeBodyCompareArr);

                setResult(Activity.RESULT_OK, intent);
                finish();
            }else{
                Log.d("눈바디 비교 저장 결과", result);
            }
        }
    }
}