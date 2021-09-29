package com.teamnova.ptmanager.ui.schedule.reservation;

import android.app.Activity;
import android.app.AlertDialog;
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
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.teamnova.mylibrary.WeekViewEvent;
import com.teamnova.ptmanager.R;
import com.teamnova.ptmanager.adapter.friend.FriendAddAdapter;
import com.teamnova.ptmanager.adapter.lecture.LectureRegisteredMemberListAdapter;
import com.teamnova.ptmanager.adapter.lesson.reservation.ReservationListAdapter;
import com.teamnova.ptmanager.databinding.ActivityLectureRegisterdMemberListBinding;
import com.teamnova.ptmanager.databinding.ActivityReservationApprovementBinding;
import com.teamnova.ptmanager.model.lesson.LessonSchInfo;
import com.teamnova.ptmanager.model.reservation.ReservationInfo;
import com.teamnova.ptmanager.model.userInfo.FriendInfoDto;
import com.teamnova.ptmanager.network.RetrofitInstance;
import com.teamnova.ptmanager.network.schedule.lesson.LessonService;
import com.teamnova.ptmanager.ui.home.member.DayViewActivity;
import com.teamnova.ptmanager.ui.home.member.Event;
import com.teamnova.ptmanager.ui.home.member.MemberHomeActivity;
import com.teamnova.ptmanager.ui.home.trainer.TrainerHomeActivity;
import com.teamnova.ptmanager.ui.member.MemberAddActivity;
import com.teamnova.ptmanager.ui.schedule.lecture.LectureRegisterdMemberListActivity;
import com.teamnova.ptmanager.ui.schedule.lesson.LessonRegisterActivity;
import com.teamnova.ptmanager.ui.schedule.schedule.fragment.WeekSchFragment;
import com.teamnova.ptmanager.viewmodel.schedule.lecture.LectureViewModel;
import com.teamnova.ptmanager.viewmodel.schedule.lesson.LessonViewModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * 예약 승인/거절 화면
 * */
public class ReservationApprovementActivity extends AppCompatActivity implements View.OnClickListener{
    // binder
    private ActivityReservationApprovementBinding binding;

    // 리사이클러뷰
    private RecyclerView recyclerviewReservationList;
    private ReservationListAdapter reservationListAdapter;
    private RecyclerView.LayoutManager layoutManager;

    // 리사이클러뷰 테스트
    /*private LectureRegisteredMemberListAdapter lectureRegisteredMemberListAdapter;
    private RecyclerView recyclerView_member_list;
    private RecyclerView.LayoutManager layoutManager;
    private LectureViewModel lectureViewModel;
    private Activity activity;*/

    // 예약목록
    private ArrayList<ReservationInfo> reservationList;

    // Retrofit 통신의 결과를 전달하는 핸들러
    private Handler resultHandler;

    // 레슨뷰모델
    private LessonViewModel lessonViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 해당 액티비티에서 사용할 xml파일에대한 클래스가 자동으로 생성되고 inflate함수를 이용해서 인스턴스를 생성한다.
        binding = ActivityReservationApprovementBinding.inflate(getLayoutInflater());

        // 기존에는 layout이 들어갔는데 대신 getRoot를 넣어준다.
        setContentView(binding.getRoot());

        initialize();

        setOnClickListener();


        // 테스트
        // Retrofit 통신 핸들러
        /*lectureViewModel = new ViewModelProvider(this).get(LectureViewModel.class);

        resultHandler = new Handler(Looper.myLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                // 회원 목록 가져오기 성공
                if(msg.what == 1){
                    // 회원 목록 가져오기
                    // 회원 목록 데이터 가져 옴
                    ArrayList<FriendInfoDto> registeredMemberList = (ArrayList<FriendInfoDto>)msg.obj;

                    Log.d("회원 목록 가져오기", "" + registeredMemberList.size());

                    if(registeredMemberList != null){
                        // 리사이클러뷰 세팅
                        recyclerView_member_list = binding.recyclerviewReservedList22;
                        layoutManager = new LinearLayoutManager(ReservationApprovementActivity.this);
                        recyclerView_member_list.setLayoutManager(layoutManager);

                        // 데이터 가져와서 adapter에 넘겨 줌
                        lectureRegisteredMemberListAdapter = new LectureRegisteredMemberListAdapter(registeredMemberList, ReservationApprovementActivity.this, activity);
                        recyclerView_member_list.setAdapter(lectureRegisteredMemberListAdapter);
                    }
                }
            }
        };

        lectureViewModel.getLectureRegisteredMemberList(resultHandler, "1");*/

    }

    public void setOnClickListener(){
        binding.rejectSelected.setOnClickListener(this);
        binding.approveSelected.setOnClickListener(this);
    }

    // 초기화
    public void initialize(){
        reservationList = new ArrayList<>();
        lessonViewModel = new ViewModelProvider(this).get(LessonViewModel.class);

        // Retrofit 통신 핸들러
        resultHandler = new Handler(Looper.myLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                // 예약정보 리스트 가져오기 성공
                if(msg.what == 10){
                    // 예약정보 리스트 가져오기 성공
                    reservationList = (ArrayList<ReservationInfo>) msg.obj;

                    Log.d("예약정보 사이즈: ", "" + reservationList.size());

                    recyclerviewReservationList = binding.recyclerviewReservedList;
                    layoutManager = new LinearLayoutManager(ReservationApprovementActivity.this);
                    recyclerviewReservationList.setLayoutManager(layoutManager);

                    // 예약정보 가져오면 리사이클러뷰 세팅
                    reservationListAdapter = new ReservationListAdapter(reservationList, ReservationApprovementActivity.this);
                    recyclerviewReservationList.setAdapter(reservationListAdapter);
                }
            }
        };

        // 예약목록 가져오기
        lessonViewModel.getReservedMemberList(resultHandler, TrainerHomeActivity.staticLoginUserInfo.getLoginId());




        /*
        // 예약목록 데이터 가져오기
        Retrofit retrofit= RetrofitInstance.getRetroClient();
        LessonService service = retrofit.create(LessonService.class);

        // http request 객체 생성
        Call<ArrayList<ReservationInfo>> call = service.getReservedMemberList(TrainerHomeActivity.staticLoginUserInfo.getLoginId());

        // 서버에서 데이터를 가져오는 동기 함수의 쓰레드
        *//*SyncRetrofitThread t = new SyncRetrofitThread(call);
        t.start();*//*

        new GetReservation().execute(call);*/
    }

    @Override
    public void onClick(View v) {
        /**
         * 예약정보 리스트 중 체크된 것만 서버로 전송
         * */
        //ArrayList<ReservationInfo> reservationList =  reservationListAdapter.getReservationList();

        ArrayList<ReservationInfo> reservationListForServer =  new ArrayList<>();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View dialogView = ((LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.dialog_enter_reserve_cancel_reason, null);

        final EditText rejectReason = dialogView.findViewById(R.id.cancel_reason);
        String approvementYn = null;

        switch(v.getId()){
            case R.id.approve_selected: // 선택 승인
                // http request 객체 생성
                approvementYn = "Y";
                break;
            case R.id.reject_selected: // 선택 거부
                // http request 객체 생성
                approvementYn = "N";
                break;
        }

        final String approvementYnFinal = approvementYn;

        Log.d("approvementYn", approvementYn);

        if(approvementYn.equals("N")){ /// 거절
            Log.d("선택 거절 왜 안들어오지?", "11");
            rejectReason.requestFocus();

            builder.setView(dialogView);

            builder.setTitle("거절");
            builder.setMessage("선택하신 요청을 거절하시겠습니까?");
        } else { // 승인
            builder.setTitle("승인");
            builder.setMessage("선택하신 요청을 승인하시겠습니까?");
        }

        builder.setPositiveButton("확인", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int pos)
            {
                for(ReservationInfo r : reservationList){
                    // 테스트 데이터 만들기
                    /*if(r.getLessonSchId().equals("LESSON_SCH_77")){ // 예약대기
                        r.setCheck("1");
                    } else if(r.getLessonSchId().equals("LESSON_SCH_85")){ // 취소
                        r.setCheck("1");
                    }*/

                    if(r.getCheck() != null && r.getCheck().equals("1")){
                        //Log.d("테스트데이터 잘 들어옴?", r.toString());

                        r.setApprovementYn(approvementYnFinal);

                        // 거절사유가 있다면 저장
                        if(rejectReason.getText() != null && !rejectReason.getText().toString().isEmpty()){
                            r.setRejectReason(rejectReason.getText().toString());
                        }
                        reservationListForServer.add(r);
                    }
                }

                // 예약 승인/거절 데이터 저장
                Retrofit retrofit= RetrofitInstance.getRetroClient();
                LessonService service = retrofit.create(LessonService.class);

                Call<String> call = service.approveReservation(reservationListForServer);

                new ReservationApprovement().execute(call);
            }
        }).setNegativeButton("취소", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int pos)
            {}
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    // 예약목록 가져오기
    /*private class GetReservation extends AsyncTask<Call, Void, String> {
        private retrofit2.Response<ArrayList<ReservationInfo>> response;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("onPreExecute", "11");
        }

        @Override
        protected String  doInBackground(Call[] params) {
            try {
                retrofit2.Call<ArrayList<ReservationInfo>> call = params[0];
                response = call.execute();

                if(response.body() != null){
                    reservationList = response.body();
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
            if(!result.equals("[]")) {
                Log.d("리사이클러뷰 사이즈: ", "" + reservationList.size());
                Log.d("리사이클러뷰 데이터: ", "" + reservationList.get(0).toString());

                // 데이터 가져와서 adapter에 넘겨 줌
                //Log.d("adapter:", reservationListAdapter.toString());

                reservationListAdapter = new ReservationListAdapter(ReservationApprovementActivity.this);
                reservationListAdapter.setReservationList(reservationList);
                recyclerviewReservationList.setLayoutManager(layoutManager);
                recyclerviewReservationList.setAdapter(reservationListAdapter);

                Log.d("리사이클러뷰 사이즈22: ", "" + reservationListAdapter.getItemCount());

            }
        }
    }*/

    // 예약 승인/거절
    private class ReservationApprovement extends AsyncTask<Call, Void, String> {
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
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response.body();
        }

        @Override
        protected void onPostExecute(String result) {
            // 저장을 완료했다면
            if(result.contains("success")){
                Intent intent = new Intent(ReservationApprovementActivity.this, TrainerHomeActivity.class);
                // 이동할 액티비티가 이미 작업에서 실행중이라면 기존 인스턴스를 가져오고 위의 모든 액티비티를 삭제
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("id","ReservationAppropvementActivity");

                startActivity(intent);
            }
        }
    }
}