package com.teamnova.ptmanager.ui.schedule.reservation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.teamnova.ptmanager.ui.schedule.lesson.LessonRegisterActivity;
import com.teamnova.ptmanager.viewmodel.schedule.lecture.LectureViewModel;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Retrofit;

/**
 * 예약 승인/거절 화면
 * */
public class ReservationApprovementActivity extends AppCompatActivity implements View.OnClickListener{
    // binder
    private ActivityReservationApprovementBinding binding;

    // 리사이클러뷰
    private ReservationListAdapter reservationListAdapter;
    private RecyclerView recyclerviewReservationList;
    private RecyclerView.LayoutManager layoutManager;

    // 예약목록
    private ArrayList<ReservationInfo> reservationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 해당 액티비티에서 사용할 xml파일에대한 클래스가 자동으로 생성되고 inflate함수를 이용해서 인스턴스를 생성한다.
        binding = ActivityReservationApprovementBinding.inflate(getLayoutInflater());

        // 기존에는 layout이 들어갔는데 대신 getRoot를 넣어준다.
        setContentView(binding.getRoot());

        initialize();

        setOnClickListener();
    }

    public void setOnClickListener(){
        binding.approveSelected.setOnClickListener(this);
    }

    // 초기화
    public void initialize(){
        recyclerviewReservationList = findViewById(R.id.recyclerview_reserved_list);
        // Adapter를 생성할 때 받아오는 데이터와 컨텍스트
        reservationListAdapter = new ReservationListAdapter(this);
        layoutManager = new LinearLayoutManager(this);

        Log.d("리사이클러뷰 세팅 완료:", "111");

        // 예약목록 데이터 가져오기
        Retrofit retrofit= RetrofitInstance.getRetroClient();
        LessonService service = retrofit.create(LessonService.class);

        // http request 객체 생성
        Call<ArrayList<ReservationInfo>> call = service.getReservedMemberList(TrainerHomeActivity.staticLoginUserInfo.getLoginId());

        new GetReservation().execute(call);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.approve_selected: // 선택 승인
                /*Intent intent = new Intent(ReservationApprovementActivity.this, TrainerHomeActivity.class);
                // 이동할 액티비티가 이미 작업에서 실행중이라면 기존 인스턴스를 가져오고 위의 모든 액티비티를 삭제
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("id","ReservationApprovementActivity");

                startActivity(intent);*/
                break;
        }
    }

    // 예약목록 가져오기
    private class GetReservation extends AsyncTask<Call, Void, String> {
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
                Log.d("adapter:", reservationListAdapter.toString());
                reservationListAdapter.setReservationList(reservationList);
                recyclerviewReservationList.setLayoutManager(layoutManager);
                recyclerviewReservationList.setAdapter(reservationListAdapter);

                Log.d("리사이클러뷰 사이즈22: ", "" + reservationListAdapter.getItemCount());

            }
        }
    }
}