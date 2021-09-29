package com.teamnova.ptmanager.ui.changehistory.inbody;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;

import com.google.android.material.snackbar.Snackbar;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.teamnova.ptmanager.R;
import com.teamnova.ptmanager.databinding.ActivityInBodyRegisterGpathPeriodBinding;
import com.teamnova.ptmanager.databinding.ActivityRegisterInBodyBinding;
import com.teamnova.ptmanager.model.userInfo.FriendInfoDto;
import com.teamnova.ptmanager.network.RetrofitInstance;
import com.teamnova.ptmanager.network.changehistory.inbody.InBodyService;
import com.teamnova.ptmanager.ui.home.member.MemberHomeActivity;
import com.teamnova.ptmanager.util.GetDate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import biz.kasual.materialnumberpicker.MaterialNumberPicker;
import retrofit2.Call;
import retrofit2.Retrofit;

public class InBodyRegisterGpathPeriodActivity extends AppCompatActivity implements View.OnClickListener{
    private ActivityInBodyRegisterGpathPeriodBinding binding;
    /** 데이터 */
    // 인바디 데이터가 있는 날짜 리스트
    private ArrayList<String> dateList;

    // 그래프 화면으로 보낼 데이터
    private String srtDate;
    private String endDate;

    // 종료일자 피커
    private NumberPicker srtNumberPicker;
    // 시작일자 피커
    private NumberPicker endNumberPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 해당 액티비티에서 사용할 xml파일에대한 클래스가 자동으로 생성되고 inflate함수를 이용해서 인스턴스를 생성한다.
        binding = ActivityInBodyRegisterGpathPeriodBinding.inflate(getLayoutInflater());

        // 기존에는 layout이 들어갔는데 대신 getRoot를 넣어준다.
        setContentView(binding.getRoot());

        initialize();
    }

    // 초기화
    public void initialize(){
        // 객체 초기화

        setOnclickListener();

        getInBodyDateList();
    }

    /** 인바디 데이터가 있는 날짜 가져오기*/
    public void getInBodyDateList(){
        //달력에 점찍기 위해 필요한 데이터 가져오기
        Retrofit retrofit= RetrofitInstance.getRetroClient();
        InBodyService service = retrofit.create(InBodyService.class);

        // 사용자 ID
        FriendInfoDto memberInfo = (FriendInfoDto)getIntent().getSerializableExtra("memberInfo");
        String userId = memberInfo.getUserId();

        // http request 객체 생성
        Call<ArrayList<String>> call = service.getInBodyDateList(memberInfo.getUserId());

        new GetHasInBodyDateListCall().execute(call);
    }

    // 온클릭리스너 등록
    public void setOnclickListener(){
        binding.inbodyGraphSrtDateInput.setOnClickListener(this);
        binding.inbodyGraphEndDateInput.setOnClickListener(this);
        binding.btnSelectInbodyPeriod.setOnClickListener(this);
        binding.btnBack.setOnClickListener(this);
    }

    // 시작, 종료 일자 세팅
    public void setSrtAndEndDate(){
        // 인바디 데이터가 있는 날짜 데이터 가져옴

        MaterialNumberPicker.Builder srtPicker = new MaterialNumberPicker.Builder(this);
        MaterialNumberPicker.Builder endPicker = new MaterialNumberPicker.Builder(this);

        srtNumberPicker = srtPicker
                .minValue(1)
                .maxValue(dateList.size())
                .defaultValue(dateList.size())
                .separatorColor(ContextCompat.getColor(this, R.color.colorAccent))
                .textColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .textSize(25)
                .formatter(new NumberPicker.Formatter() {
                    @Override
                    public String format(int value) {
                        String dateWithDot = "";
                        String date = dateList.get(value - 1);
                        String dateYMDWithKorean = GetDate.getDateWithYMDAndDot(date);
                        String dateDay = GetDate.getDayOfWeek(date);

                        dateWithDot = dateYMDWithKorean + "(" + dateDay +")";

                        return dateWithDot;
                    }
                }).build();

        endNumberPicker = endPicker
                .minValue(1)
                .maxValue(dateList.size())
                .defaultValue(dateList.size())
                .separatorColor(ContextCompat.getColor(this, R.color.colorAccent))
                .textColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .textSize(25)
                .formatter(new NumberPicker.Formatter() {
                    @Override
                    public String format(int value) {
                        String dateWithDot = "";
                        String date = dateList.get(value - 1);
                        String dateYMDWithKorean = GetDate.getDateWithYMDAndDot(date);
                        String dateDay = GetDate.getDayOfWeek(date);

                        dateWithDot = dateYMDWithKorean + "(" + dateDay + ")";

                        return dateWithDot;
                    }
                }).build();
    }

    @Override
    public void onClick(View v) {
        Intent intent;

        switch(v.getId()){
            case  R.id.btn_inbody_register: // 인바디 등록
                break;

            case  R.id.inbody_graph_srt_date_input: // 시작 일자
                if (srtNumberPicker.getParent() != null){
                    ((ViewGroup) srtNumberPicker.getParent()).removeView(srtNumberPicker);
                }

                new AlertDialog.Builder(this)
                        .setTitle("시작일자")
                        .setView(srtNumberPicker)
                        .setNegativeButton(getString(android.R.string.cancel), null)
                        .setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                srtDate = dateList.get(srtNumberPicker.getValue() - 1);

                                String dateWithDot = "";
                                String dateYMDWithKorean = GetDate.getDateWithYMDAndDot(srtDate);
                                String dateDay = GetDate.getDayOfWeek(srtDate);

                                dateWithDot = dateYMDWithKorean + "(" + dateDay +")";

                                binding.inbodyGraphSrtDateInput.setText(dateWithDot);
                                dialog.cancel();
                            }
                        })
                        .show();
                break;

            case  R.id.inbody_graph_end_date_input: // 종료 일자
                if (endNumberPicker.getParent() != null){
                    ((ViewGroup) endNumberPicker.getParent()).removeView(endNumberPicker);
                }

                new AlertDialog.Builder(this)
                        .setTitle("종료일자")
                        .setView(endNumberPicker)
                        .setNegativeButton(getString(android.R.string.cancel), null)
                        .setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                endDate = dateList.get(endNumberPicker.getValue() - 1);

                                String dateWithDot = "";
                                String dateYMDWithKorean = GetDate.getDateWithYMDAndDot(endDate);
                                String dateDay = GetDate.getDayOfWeek(endDate);

                                dateWithDot = dateYMDWithKorean + "(" + dateDay +")";

                                binding.inbodyGraphEndDateInput.setText(dateWithDot);
                                dialog.cancel();
                            }
                        })
                        .show();
                break;
            case R.id.btn_select_inbody_period: //기간설정 완료
                if(binding.inbodyGraphSrtDateInput.getText().toString() == null || binding.inbodyGraphSrtDateInput.getText().toString().equals("")){
                    new AlertDialog.Builder(this)
                            .setMessage("시작일자를 선택해주세요.")
                            .setNegativeButton(getString(android.R.string.cancel), null)
                            .setPositiveButton(getString(android.R.string.ok), null)
                            .show();
                    return;
                } else if(binding.inbodyGraphEndDateInput.getText().toString() == null || binding.inbodyGraphEndDateInput.getText().toString().equals("")){
                    new AlertDialog.Builder(this)
                            .setMessage("종료일자를 선택해주세요.")
                            .setNegativeButton(getString(android.R.string.cancel), null)
                            .setPositiveButton(getString(android.R.string.ok), null)
                            .show();
                    return;
                }

                intent = new Intent(this, InBodyViewGraphActivity.class);

                // 눈바디 정보리스트
                intent.putExtra("srtDate", srtDate);
                intent.putExtra("endDate", endDate);

                setResult(Activity.RESULT_OK, intent);
                finish();
                break;
            case R.id.btn_back: // 뒤로가기
                onBackPressed();
                break;
            default:
                break;
        }
    }

    /** 인바디 정보를 가지고 있는 날짜리스트 가져오기 */
    public class GetHasInBodyDateListCall extends AsyncTask<Call, Void, String> {
        private retrofit2.Response<ArrayList<String>> response;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("onPreExecute", "11");
        }

        @Override
        protected String  doInBackground(Call[] params) {
            try {
                Call<ArrayList<String>> call = params[0];
                response = call.execute();

                if(response.body() != null){
                    dateList = response.body();
                }

                return response.body().toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            // 결과값을 가져 왔다면 ->  db에서 아무런 데이터를 조회해오지 못하면 "[]"값을 가져온다.
            if(!result.equals("[]")){
                setSrtAndEndDate();
            }
        }
    }
}