package com.teamnova.ptmanager.ui.schedule.lecture.pass;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import com.teamnova.ptmanager.R;
import com.teamnova.ptmanager.adapter.lecture.LectureListAdapter;
import com.teamnova.ptmanager.databinding.ActivityLessonRegisterBinding;
import com.teamnova.ptmanager.databinding.ActivityPassRegisterBinding;
import com.teamnova.ptmanager.model.lecture.LectureInfoDto;
import com.teamnova.ptmanager.model.lecture.pass.PassInfoDto;
import com.teamnova.ptmanager.model.userInfo.FriendInfoDto;
import com.teamnova.ptmanager.ui.home.trainer.TrainerHomeActivity;
import com.teamnova.ptmanager.ui.login.LoginActivity;
import com.teamnova.ptmanager.ui.login.findpw.FindPw3Activity;
import com.teamnova.ptmanager.ui.member.MemberAddActivity;
import com.teamnova.ptmanager.ui.schedule.lecture.LectureListActivity;
import com.teamnova.ptmanager.ui.schedule.lecture.LectureRegisterdMemberListActivity;
import com.teamnova.ptmanager.ui.schedule.lesson.LessonRegisterActivity;
import com.teamnova.ptmanager.ui.schedule.schedule.RepeatRegisterActivity;
import com.teamnova.ptmanager.util.GetDate;
import com.teamnova.ptmanager.viewmodel.schedule.lecture.LectureViewModel;

import java.util.ArrayList;

public class PassRegisterActivity extends AppCompatActivity implements View.OnClickListener{
    // binder
    private ActivityPassRegisterBinding binding;

    // 시작일자
    private String startDate;

    // 수강권 정보
    private PassInfoDto passInfoDto;

    // 회원 정보
    private FriendInfoDto memberInfo;

    // 다른 액티비티로 이동 후 다시 돌아와서 결과값을 받기 위한 객체
    private ActivityResultLauncher<Intent> startActivityResult;

    // LectureViewModel
    private LectureViewModel lectureViewModel;

    // Retrofit 통신의 결과를 전달하는 핸들러
    private Handler resultHandler;

    // 강의 정보
    private LectureInfoDto lectureInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 결과값을 받기 위해 사용하는 메소드
        // super.onCreate 이전에 배치해야 함
        startActivityResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {

                            lectureInfo = (LectureInfoDto)result.getData().getSerializableExtra("lectureInfo");

                            Log.d("강의목록 화면에서 강의정보 보내 옴", lectureInfo.toString());

                            // 1. 화면에 강의명 보여주기
                            binding.lectureType.setText(lectureInfo.getLectureName());

                            // 2. 수강권정보 dto에 강의정보, 트레이너 정보 넣기
                            passInfoDto.setLectureId(lectureInfo.getLectureId());
                            passInfoDto.setTrainerId(lectureInfo.getTrainerId());
                        }
                    }
                });

        super.onCreate(savedInstanceState);

        // 해당 액티비티에서 사용할 xml파일에대한 클래스가 자동으로 생성되고 inflate함수를 이용해서 인스턴스를 생성한다.
        binding = ActivityPassRegisterBinding.inflate(getLayoutInflater());

        // 기존에는 layout이 들어갔는데 대신 getRoot를 넣어준다.
        setContentView(binding.getRoot());

        initialize();
        setOnclickListener();
    }

    // 초기화
    public void initialize(){
        // viewModel 초기화
        lectureViewModel = new ViewModelProvider(this).get(LectureViewModel.class);

        Intent intent = getIntent();

        // 새로운 수강권 정보
        passInfoDto = new PassInfoDto();

        // 회원정보
        if(intent != null){
            memberInfo = (FriendInfoDto) getIntent().getSerializableExtra("memberInfo");

            // 회원정보 세팅
            binding.memberSelect.setText(memberInfo.getUserName());
            passInfoDto.setMemberId(memberInfo.getUserId());
        }

        // Retrofit 통신 핸들러
        resultHandler = new Handler(Looper.myLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                // 수강권 등록 성공
                if(msg.what == 3){
                    // 등록 성공여부
                    String result = (String)msg.obj;

                    if(result.contains("success")){ // 성공
                        // 다이얼로그빌더
                        AlertDialog.Builder builder = new AlertDialog.Builder(PassRegisterActivity.this);

                        builder.setMessage("수강권 등록이 완료되었습니다.");

                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int id)
                            {
                                // home 화면으로 이동
                                Intent intent = new Intent(PassRegisterActivity.this, TrainerHomeActivity.class);
                                // 이동할 액티비티가 이미 작업에서 실행중이라면 기존 인스턴스를 가져오고 위의 모든 액티비티를 삭제
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                                // memberInfo에 수강권 정보 추가
                                memberInfo.setTotalCnt(passInfoDto.getTotalCnt());
                                memberInfo.setUsedCnt(passInfoDto.getUsedCnt());
                                memberInfo.setLectureName(lectureInfo.getLectureName());

                                intent.putExtra("memberInfo",memberInfo);

                                startActivity(intent);
                            }
                        });

                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    } else { //실패
                        Log.d("수강권 등록 실패","fail");
                    }
                }
            }
        };
    }

    // 리스너 등록
    public void setOnclickListener(){
        binding.layoutLectureTypeSelect.setOnClickListener(this);
        binding.layoutStartDateSelect.setOnClickListener(this);
        binding.addLesson.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;

        switch(v.getId()){
            case  R.id.layout_lecture_type_select: // 강의종류
                // 강의목록 액티비티로 이동
                intent = new Intent(PassRegisterActivity.this, LectureListActivity.class);

                // 강의목록 액티비티에서 결과값을 받는 act가 두군데이므로 구분 위해 activityName을 보내줌
                intent.putExtra("activityName", "PassRegisterActivity");

                startActivityResult.launch(intent);

                break;
            case  R.id.layout_start_date_select: // 시작일자
                /**
                 * 1. datePicker
                 * */
                DatePickerDialog dialog = new DatePickerDialog(this);

                // 생년월일 설정 완료 시
                dialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String birthDayForDisplay = year + "년 " + (month <10 ? "0"+(month+1) : (month+1)) + "월 " + (dayOfMonth <10 ? "0" + dayOfMonth : dayOfMonth) + "일";
                        startDate = year + "" + (month <10 ? "0"+(month+1) : (month+1)) + (dayOfMonth <10 ? "0" + dayOfMonth : dayOfMonth);
                        String dayOfWeek = GetDate.getDayOfWeek(startDate);

                        // 화면에 출력
                        binding.startDate.setText(birthDayForDisplay + "(" + dayOfWeek + ")");

                        // 3. 시작읿자 등록
                        passInfoDto.setSrtDate(startDate);
                    }
                });

                dialog.show();

                break;

            case R.id.add_lesson: // 등록 버튼 클릭
                // 4. 횟수 등록
                passInfoDto.setTotalCnt(Integer.parseInt(binding.lessonCnt.getText().toString()));
                passInfoDto.setUsedCnt(0);

                // 5. 메모 등록
                passInfoDto.setMemo(binding.memo.getText().toString());

                // 수강권 정보 출력
                Log.d("수강권 정보 출력: ", passInfoDto.toString());

                // 유효성 체크
                if(passInfoDto.getLectureId().equals("")){
                    Toast.makeText(this, "강의를 선택해주세요", Toast.LENGTH_SHORT).show();
                    return;
                } else if(passInfoDto.getSrtDate().equals("")){
                    Toast.makeText(this, "시작일자를 선택해주세요", Toast.LENGTH_SHORT).show();
                    return;
                } else if(passInfoDto.getTotalCnt() == 0){

                    Log.d("총 횟수: ", "" + passInfoDto.getTotalCnt());

                    Toast.makeText(this, "횟수를 선택해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 수강권 정보 등록
                Log.d("registerPassInto: ", "registerPassInto가 안되나?");

                lectureViewModel.registerPassInto(resultHandler, passInfoDto);
                break;
            default:
                break;
        }
    }
}