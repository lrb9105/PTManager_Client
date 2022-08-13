package com.teamnova.ptmanager.ui.schedule.lesson;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import com.teamnova.ptmanager.R;
import com.teamnova.ptmanager.databinding.ActivityLessonDetailBinding;
import com.teamnova.ptmanager.model.lecture.LectureInfoDto;
import com.teamnova.ptmanager.model.lecture.pass.PassInfoDto;
import com.teamnova.ptmanager.model.lesson.LessonSchInfo;
import com.teamnova.ptmanager.network.RetrofitInstance;
import com.teamnova.ptmanager.network.schedule.lesson.LessonService;
import com.teamnova.ptmanager.ui.home.member.DayViewActivity;
import com.teamnova.ptmanager.ui.home.member.MemberHomeActivity;
import com.teamnova.ptmanager.ui.home.trainer.TrainerHomeActivity;
import com.teamnova.ptmanager.ui.login.LoginActivity;
import com.teamnova.ptmanager.ui.register.SelectProfileActivity;
import com.teamnova.ptmanager.ui.schedule.lecture.LectureListActivity;
import com.teamnova.ptmanager.ui.schedule.lecture.LectureRegisterdMemberListActivity;
import com.teamnova.ptmanager.ui.schedule.schedule.RepeatRegisterActivity;
import com.teamnova.ptmanager.util.GetDate;
import com.teamnova.ptmanager.viewmodel.schedule.lesson.LessonViewModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.internal.Util;
import retrofit2.Call;
import retrofit2.Retrofit;

public class LessonDetailActivity extends AppCompatActivity implements View.OnClickListener{
    // binder
    private ActivityLessonDetailBinding binding;

    // viewModel
    private LessonViewModel lessonViewModel;

    // Retrofit 통신의 결과를 전달하는 핸들러
    private Handler resultHandler;

    // 레슨일정 id
    private String lessonSchId;

    // 레슨정보
    private LessonSchInfo lessonSchInfo;

    //
    private ActivityResultLauncher<Intent> startActivityResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        startActivityResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent intent = new Intent(LessonDetailActivity.this, TrainerHomeActivity.class);
                            intent.putExtra("id","LessonDetailActivity");

                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(intent);
                        }
                    }
                });

        super.onCreate(savedInstanceState);
        // 해당 액티비티에서 사용할 xml파일에대한 클래스가 자동으로 생성되고 inflate함수를 이용해서 인스턴스를 생성한다.
        binding = ActivityLessonDetailBinding.inflate(getLayoutInflater());

        // 기존에는 layout이 들어갔는데 대신 getRoot를 넣어준다.
        setContentView(binding.getRoot());

        initialize();

        setOnclickListener();
    }

    // 초기화
    public void initialize(){
        setOnclickListener();

        // viewModel
        lessonViewModel = new ViewModelProvider(this).get(LessonViewModel.class);

        // Retrofit 통신 핸들러
        resultHandler = new Handler(Looper.myLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                // 레슨정보 가져오기
                if(msg.what == 0){
                    lessonSchInfo = (LessonSchInfo)msg.obj;
                    Log.d("잘 가져오니?", lessonSchInfo.getLessonSchId());

                    updateUI(lessonSchInfo);

                    String userType = getIntent().getStringExtra("userType");

                    setVisibleBtn(userType);
                }
            }
        };

        // lessonSchId
        lessonSchId = getIntent().getStringExtra("lessonSchId");
        Log.d("레슨id 잘받아온는지 확인", lessonSchId);


        // 레슨정보 가져오기
        getLessonSchInfo(resultHandler, lessonSchId);
    }

    // 레슨정보 가져오기
    public void getLessonSchInfo(Handler resultHandler, String lessonSchId){
        lessonViewModel.getLessonSchInfo(resultHandler, lessonSchId);
    }

    // 사용자 타입에 따라 보여주는 버튼 다르게 하기
    public void setVisibleBtn(String userType){
        if("0".equals(userType) && lessonSchInfo.getAttendanceYn() == null
                && lessonSchInfo.getConfirmYn().equals("Y")
                && !lessonSchInfo.getCancelYn().equals("Y")
                && !lessonSchInfo.getCancelYn().equals("M")){ // 트레이너이고 출석/결석 하지 않았을 때
            binding.layoutLessonReserve.setVisibility(View.GONE);
            Log.d("트레이너","11");
        } else if("1".equals(userType) && lessonSchInfo.getConfirmYn().equals("Y") && lessonSchInfo.getAttendanceYn() == null && !lessonSchInfo.getCancelYn().equals("Y")){ //일반회원이고 출석,결석하지 않았고 예약 상태일 때
            binding.layoutAddLessonComplete.setVisibility(View.GONE);
            Log.d("일반회원","11");
        } else if("1".equals(userType)){ // 일반회원
            binding.layoutAddLessonComplete.setVisibility(View.GONE);
            binding.layoutLessonReserve.setVisibility(View.GONE);
            Log.d("일반회원","22");
        } else{
            binding.layoutAddLessonComplete.setVisibility(View.GONE);
            binding.layoutLessonReserve.setVisibility(View.GONE);
        }
        Log.d("예약취소 버튼 안나오게 하는 데이터 찾기: ", userType + " : " + lessonSchInfo.getConfirmYn() + " : " + lessonSchInfo.getAttendanceYn());

        Log.d("예약정보: ", userType + " : " + lessonSchInfo.getConfirmYn() + lessonSchInfo.getReservationConfirmYn());
    }

    // UI업데이트
    private void updateUI(LessonSchInfo lessonSchInfo){
        // 강의 종류
        binding.lectureType.setText(lessonSchInfo.getLectureName());
        // 회원
        binding.memberSelect.setText(lessonSchInfo.getUserName());
        // 시작일자
        String date = GetDate.getDateWithYMD(lessonSchInfo.getLessonDate());
        binding.startDate.setText(date);
        /**
         * 진행시간
         * */
        // 시작시간
        String[] srtTimeArr = lessonSchInfo.getLessonSrtTime().split(":");
        String srtTime = GetDate.getAmPmTime(Integer.parseInt(srtTimeArr[0]), Integer.parseInt(srtTimeArr[1]));
        binding.startTime.setText(srtTime + "부터");

        // 종료시간
        String[] endTimeArr = lessonSchInfo.getLessonEndTime().split(":");
        String endTime = GetDate.getAmPmTime(Integer.parseInt(endTimeArr[0]), Integer.parseInt(endTimeArr[1]));
        binding.endTime.setText(endTime + "까지");

        // 반복요일
        if(lessonSchInfo.getRepeatDay() == null){ // 반복이 없다면 ui숨기기
            binding.layoutRepeat.setVisibility(View.GONE);
        } else{
            binding.repeat.setText(lessonSchInfo.getRepeatDay());
        }

        // 메모
        if(lessonSchInfo.getMemo() == null || lessonSchInfo.getMemo().equals("")){ // 메모가 없다면 ui숨기기
            binding.layoutMemo.setVisibility(View.GONE);
        } else{
            binding.memo.setText(lessonSchInfo.getMemo());
        }

        // 취소요청 사유
        if(lessonSchInfo.getCancelReason() == null || lessonSchInfo.getCancelReason().equals("")){ // 취소요청 사유가 없다면 ui숨기기
            binding.layoutCancelReq.setVisibility(View.GONE);
        } else{
            binding.cancelReq.setText(lessonSchInfo.getCancelReason());
        }

        // 예약 거절 사유
        if(lessonSchInfo.getRejectReason() == null || lessonSchInfo.getRejectReason().equals("")){ // 거절 사유가 없다면 ui숨기기
            binding.layoutRejectReason.setVisibility(View.GONE);
        } else{
            binding.rejectReason.setText(lessonSchInfo.getRejectReason());
        }

        // 예약취소 거절 사유
        if(lessonSchInfo.getCancelDenyReason() == null || lessonSchInfo.getCancelDenyReason().equals("")){ // 거절 사유가 없다면 ui숨기기
            binding.layoutCancelDenyReason.setVisibility(View.GONE);
        } else{
            binding.cancelDenyReason.setText(lessonSchInfo.getCancelDenyReason());
        }

        // 레슨상태정보(예약대기, 예약, 출석, 결석)
        String attendanceYn = lessonSchInfo.getAttendanceYn();
        String attendanceYnName = "";
        String confirmYnName = "";
        String attendanceYnOrConfirmYn = "";


        if((attendanceYn == null || attendanceYn.isEmpty()) && lessonSchInfo.getCancelYn().equals("N")){
            Log.d("레슨상태 확인: ", lessonSchInfo.getCancelYn() + " : " + lessonSchInfo.getConfirmYn());

            if("Y".equals(lessonSchInfo.getConfirmYn())){
                confirmYnName = "(예약)";
            } else if(lessonSchInfo.getReservationConfirmYn().equals("M")){
                confirmYnName = "(예약대기)";
            }
            attendanceYnOrConfirmYn = confirmYnName;
        } else if(lessonSchInfo.getCancelYn() != null && !lessonSchInfo.getCancelYn().equals("N")){
            if(lessonSchInfo.getCancelYn().equals("M")){
                attendanceYnOrConfirmYn = "(취소대기)";
            } else if(lessonSchInfo.getCancelYn().equals("Y")){
                attendanceYnOrConfirmYn = "(예약취소)";
            }
        } else if(lessonSchInfo.getReservationConfirmYn() != null && lessonSchInfo.getReservationConfirmYn().equals("N")){
            attendanceYnOrConfirmYn = "(예약취소)";
        } else{
            attendanceYnName = lessonSchInfo.getAttendanceYnName();
            attendanceYnOrConfirmYn = "(" + attendanceYnName + ")";
        }

        binding.lessonDetail.setText("레슨 내역" + attendanceYnOrConfirmYn);
    }

    // 온클릭리스너 등록
    public void setOnclickListener(){
        binding.checkAttendance.setOnClickListener(this);
        binding.lessonReserveCancel.setOnClickListener(this);
        binding.btnBack.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        Intent intent;

        switch(v.getId()){
            case  R.id.check_attendance: // 출석체크
                // 출석체크 액티비티로 이동
                intent = new Intent(LessonDetailActivity.this, LessonAttendanceActivity.class);
                //String lessonId = ; - 시간별 일정에서 넘어올 때 레슨id를 가져옴!

                intent.putExtra("lessonInfo", lessonSchInfo);
                startActivityResult.launch(intent);

                break;
            case R.id.lesson_reserve_cancel: // 예약취소
                /**
                 * 1. 다이얼로그 출력
                 * 2. 확인 시 cancelLssson(레슨Id, 취소사유) 호출
                 * 3. 성공 시 actName에 따라 각 액트로 보내주기
                 * */
                View dialogView = ((LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.dialog_enter_reserve_cancel_reason, null);

                final EditText cancelReason = dialogView.findViewById(R.id.cancel_reason);

                cancelReason.requestFocus();

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setView(dialogView);

                builder.setTitle("예약 취소 요청");
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int pos)
                    {
                        // 레슨 예약 취소요청하기
                        Retrofit retrofit= RetrofitInstance.getRetroClient();
                        LessonService service = retrofit.create(LessonService.class);

                        // http request 객체 생성
                        Call<String> call = service.requestCancelLesson(lessonSchId,cancelReason.getText().toString());

                        new RequestLessonCancel().execute(call);

                        Log.d("취소 사유", cancelReason.getText().toString());
                    }
                }).setNegativeButton("취소", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int pos)
                    {}
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                break;
            case R.id.btn_back: // 뒤로가기
                onBackPressed();
                break;
            default:
                break;
        }
    }

    // 예약취소 요청
    private class RequestLessonCancel extends AsyncTask<Call, Void, String> {
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
            // 저장 성공했다면
            if(result.contains("success")){
                Intent intent = null;
                if(getIntent().getStringExtra("actName").equals("DayViewActivity")){ //DayViewAct
                    intent = new Intent(LessonDetailActivity.this, DayViewActivity.class);
                    intent.putExtra("lessonSchId", lessonSchId);
                } else if(getIntent().getStringExtra("actName").equals("MemberHomeActivity")){ //MemberHomeAct
                    intent = new Intent(LessonDetailActivity.this, MemberHomeActivity.class);
                    intent.putExtra("lessonSchId", lessonSchId);
                }

                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        }
    }
}