package com.teamnova.ptmanager.ui.changehistory.inbody;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.teamnova.ptmanager.R;
import com.teamnova.ptmanager.databinding.ActivityLectureRegisterBinding;
import com.teamnova.ptmanager.databinding.ActivityRegisterInBodyBinding;
import com.teamnova.ptmanager.model.changehistory.inbody.InBody;
import com.teamnova.ptmanager.model.userInfo.FriendInfoDto;
import com.teamnova.ptmanager.network.RetrofitInstance;
import com.teamnova.ptmanager.network.changehistory.inbody.InBodyService;
import com.teamnova.ptmanager.ui.home.member.MemberHomeActivity;
import com.teamnova.ptmanager.ui.schedule.lecture.LectureListActivity;
import com.teamnova.ptmanager.ui.schedule.lecture.LectureRegisterActivity;
import com.teamnova.ptmanager.util.GetDate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import retrofit2.Call;
import retrofit2.Retrofit;
/** 인바디 정보 저장 */
public class InBodyRegisterActivity extends AppCompatActivity implements View.OnClickListener{
    private ActivityRegisterInBodyBinding binding;
    /** 데이터 */
    private String dateToServer;
    private InBody inBody;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 해당 액티비티에서 사용할 xml파일에대한 클래스가 자동으로 생성되고 inflate함수를 이용해서 인스턴스를 생성한다.
        binding = ActivityRegisterInBodyBinding.inflate(getLayoutInflater());

        // 기존에는 layout이 들어갔는데 대신 getRoot를 넣어준다.
        setContentView(binding.getRoot());

        initialize();
    }

    // 초기화
    public void initialize(){
        /** 검사일시에 오늘날짜 넣어주기 */
        String todayYMD = GetDate.getTodayDate();
        String todayYMDWithKorean = GetDate.getDateWithYMD(todayYMD);
        String today = GetDate.getDayOfWeek(todayYMD);

        binding.inbodyCheckDateInput.setText(todayYMDWithKorean + "(" + today + ")");
        // 서버로 보낼 날짜 정보
        dateToServer = todayYMD;

        /** onClickListener 등록 */
        setOnclickListener();
    }
    // 온클릭리스너 등록
    public void setOnclickListener(){
        binding.btnInbodyRegister.setOnClickListener(this);
        binding.inbodyCheckDateInput.setOnClickListener(this);
        binding.btnInbodyRegister.setOnClickListener(this);
        binding.btnBack.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {

        Intent intent;

        switch(v.getId()){
            case  R.id.btn_inbody_register: // 인바디 등록
                inBody = new InBody();
                // 검사일시
                inBody.setCreDate(dateToServer);
                // 사용자 ID
                FriendInfoDto memberInfo = (FriendInfoDto)getIntent().getSerializableExtra("memberInfo");
                String userId = memberInfo.getUserId();
                inBody.setUserId(userId);

                // 몸무게
                inBody.setWeight(Float.parseFloat(binding.inbodyWeightInput.getText().toString()));
                // 키
                inBody.setHeight(Float.parseFloat(binding.inbodyHeightInput.getText().toString()));
                // 체지방량
                inBody.setBodyFat(Float.parseFloat(binding.inbodyBodyFatInput.getText().toString()));
                // 근육량
                inBody.setMuscleMass(Float.parseFloat(binding.inbodyMuscleMassInput.getText().toString()));
                // 내장지방레벨
                inBody.setFatLevel(Integer.parseInt(binding.inbbodyFatLevelInput.getText().toString()));

                //인바디정보 등록
                Retrofit retrofit= RetrofitInstance.getRetroClient();
                InBodyService service = retrofit.create(InBodyService.class);

                // http request 객체 생성
                Call<String> call = service.registerInBodyInfo(inBody);

                new RegisterInBodyCall().execute(call);

                break;
            case R.id.inbody_check_date_input: //검사일시
                DatePickerDialog dialog = new DatePickerDialog(this);

                // 검사일시 설정 완료 시
                dialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        dateToServer = year + "" + (month <10 ? "0"+(month+1) : (month+1)) + (dayOfMonth <10 ? "0" + dayOfMonth : dayOfMonth);

                        String todayYMDWithKorean = GetDate.getDateWithYMD(dateToServer);
                        String today = GetDate.getDayOfWeek(dateToServer);

                        // 화면에 출력
                        binding.inbodyCheckDateInput.setText(todayYMDWithKorean + "(" + today + ")");
                    }
                });

               dialog.show();
                break;
            case R.id.btn_back: // 뒤로가기
                onBackPressed();
                break;
            default:
                break;
        }
    }

    /** 인바디 정보등록록 */
    private class RegisterInBodyCall extends AsyncTask<Call, Void, String> {
        private retrofit2.Response<String> response;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("onPreExecute", "11");
        }

        @Override
        protected String  doInBackground(Call[] params) {
            try {
                Call<String> call = params[0];
                response = call.execute();

                return response.body().toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            Intent intent = new Intent(InBodyRegisterActivity.this, MemberHomeActivity.class);

            // 눈바디 정보리스트
            intent.putExtra("inBody", inBody);

            setResult(Activity.RESULT_OK, intent);
            finish();

        }
    }
}