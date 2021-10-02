package com.teamnova.ptmanager.ui.record.fitness;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.teamnova.ptmanager.R;
import com.teamnova.ptmanager.databinding.FragmentFitnessBinding;
import com.teamnova.ptmanager.databinding.FragmentInBodyBinding;
import com.teamnova.ptmanager.model.changehistory.inbody.InBody;
import com.teamnova.ptmanager.model.record.fitness.FitnessRecord;
import com.teamnova.ptmanager.model.userInfo.FriendInfoDto;
import com.teamnova.ptmanager.network.RetrofitInstance;
import com.teamnova.ptmanager.network.changehistory.inbody.InBodyService;
import com.teamnova.ptmanager.network.record.fitness.FitnessService;
import com.teamnova.ptmanager.ui.changehistory.inbody.InBodyRegisterActivity;
import com.teamnova.ptmanager.ui.changehistory.inbody.InBodyViewGraphActivity;
import com.teamnova.ptmanager.ui.changehistory.inbody.adapter.InBodyListAdapter;
import com.teamnova.ptmanager.ui.home.trainer.TrainerHomeActivity;
import com.teamnova.ptmanager.ui.record.fitness.adapter.FitnessListAdapter;
import com.teamnova.ptmanager.ui.schedule.schedule.decorator.DotDecorator;
import com.teamnova.ptmanager.ui.schedule.schedule.decorator.SaturdayDecorator;
import com.teamnova.ptmanager.ui.schedule.schedule.decorator.SundayDecorator;
import com.teamnova.ptmanager.ui.schedule.schedule.decorator.TodayDecorator;
import com.teamnova.ptmanager.viewmodel.changehistory.inbody.InBodyViewModel;
import com.teamnova.ptmanager.viewmodel.friend.FriendViewModel;
import com.teamnova.ptmanager.viewmodel.record.fitness.FitnessViewModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;

import retrofit2.Call;
import retrofit2.Retrofit;

/**
 * 운동 프래그먼트
 * */
public class FitnessFragment extends Fragment implements View.OnClickListener{
    private FragmentFitnessBinding binding;

    /** 데이터 */
    // 운동기록 데이터가 있는 날짜 리스트
    private ArrayList<String> dateList;

    // 리사이클러뷰에 널어주기 위한 데이터
    private ArrayList<FitnessRecord> clickedFitnessList;

    // 운동기록 데이터 등록 시 넘어오는 데이터 처리
    private ActivityResultLauncher<Intent> startActivityResult;

    // 운동기록 데이터 수정 시 넘어오는 데이터 처리
    private ActivityResultLauncher<Intent> startActivityResult2;

    // 회원 정보를 가져오기 위한 viewModel
    private FriendViewModel friendViewModel;

    // 운동기록 정보를 다루기 위한 viewModel
    private FitnessViewModel fitnessViewModel;

    // 회원 정보
    private FriendInfoDto memberInfo;

    // 달력객체
    private MaterialCalendarView calendar;

    // 운동기록 데이터가 있는 모든 날짜 가져오기
    private HashSet<CalendarDay> dateSet;

    // 리사이클러뷰
    private RecyclerView recyclerView;
    private FitnessListAdapter adapter;

    // 데코레이터
    private DotDecorator decorator;

    // 선택된 날짜
    private String selectedDateYMD;

    public FitnessFragment() {}

    // TODO: Rename and change types and number of parameters
    public static FitnessFragment newInstance(String param1, String param2) {
        FitnessFragment fragment = new FitnessFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // 운동기록 데이터 등록 시
        startActivityResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            System.out.println("들어옴3");
                            /** HashSet에 넣어준다.*/
                            //FitnessRecord addedFitnessRecord = (FitnessRecord)result.getData().getSerializableExtra("fitnessRecord");
                            //String date = addedFitnessRecord.getFitnessDate();

                            String selectedDateYMD = (String)result.getData().getSerializableExtra("selectedDateYMD");
                            ArrayList<FitnessRecord> fitnessRecordList = (ArrayList<FitnessRecord>)result.getData().getSerializableExtra("fitnessRecordList");

                            int year = Integer.parseInt(selectedDateYMD.substring(0,4));
                            // month는 0부터 시작!
                            int month = Integer.parseInt(selectedDateYMD.substring(4,6)) - 1;
                            int dayOfWeek = Integer.parseInt(selectedDateYMD.substring(6,8));

                            // HashSet에 추가
                            CalendarDay c = CalendarDay.from(year, month, dayOfWeek);
                            dateSet.add(c);

                            // 등록한 날짜가 선택되도록
                            System.out.println(111);
                            Calendar calendarToday = Calendar.getInstance();
                            calendarToday.set(year, Integer.parseInt(selectedDateYMD.substring(4,6)), dayOfWeek);

                            calendar.setDateSelected(calendarToday, true);

                            fitnessViewModel.getDateSet().setValue(dateSet);

                            // 텍스트 뷰 없애기
                            binding.btnAddFitness.setVisibility(View.GONE);

                            // 등록한 데이터 받아와서 뿌려주기
                            adapter = new FitnessListAdapter(fitnessRecordList, requireActivity(), startActivityResult2, memberInfo);
                            recyclerView.setAdapter(adapter);
                        }
                    }
                });

        // 운동기록 데이터 수정 혹은 삭제 시
        startActivityResult2 = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent intent = result.getData();
                            // 수정 시
                            String isModified = intent.getStringExtra("isModified");
                            // 삭제 시
                            String isDeleted = intent.getStringExtra("isDeleted");

                            // 수정 시
                            if(isModified != null && isModified.equals("true")){
                                ArrayList<FitnessRecord> modifiedFitnessRecordList = (ArrayList<FitnessRecord>)intent.getSerializableExtra("modifiedFitnessRecordList");

                                // 수정 시 리사이클러뷰 데이터 수정
                                adapter = new FitnessListAdapter(modifiedFitnessRecordList, requireActivity(), startActivityResult2, memberInfo);
                                recyclerView.setAdapter(adapter);
                            } else if(isDeleted != null && isDeleted.equals("true")){ // 삭제 시
                                String deletedDay = intent.getStringExtra("deletedDay");

                                System.out.println(deletedDay);

                                // 삭제한 날짜의 빨간점 지우기
                                dateSet = fitnessViewModel.getDateSet().getValue();

                                int year = Integer.parseInt(deletedDay.substring(0,4));
                                // month는 0부터 시작!
                                int month = Integer.parseInt(deletedDay.substring(4,6)) - 1;
                                int dayOfWeek = Integer.parseInt(deletedDay.substring(6,8));

                                CalendarDay c = CalendarDay.from(year, month, dayOfWeek);
                                System.out.println(dateSet.size());
                                dateSet.remove(c);
                                System.out.println(dateSet.size());

                                // viewModel에 저장
                                fitnessViewModel.getDateSet().setValue(dateSet);

                                // 운동기록(리사이클러뷰 삭제)
                                adapter.clearAllFitnessInfo();

                                // 텍스트 뷰 보여주기
                                binding.btnAddFitness.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });

        super.onCreate(savedInstanceState);

        // 뷰모델 초기화
        friendViewModel = new ViewModelProvider(requireActivity()).get(FriendViewModel.class);
        fitnessViewModel = new ViewModelProvider(requireActivity()).get(FitnessViewModel.class);

        // 로그인한 회원 정보 observe
        friendViewModel.getFriendInfo().observe(requireActivity(), memberInfo -> {
            this.memberInfo = memberInfo;
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

       // View view = inflater.inflate(R.layout.fragment_eye_body, container, false);

        binding = FragmentFitnessBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        initialize();

        return view;
    }

    /**
     * OnclickListener 등록
     * */
    public void setOnClickListener(){
        // 운동기록 등록
        binding.btnAddFitness.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_add_fitness: // 운동기록 등록
                Intent intent = new Intent(getActivity(), FitnessKindListActivity.class);

                intent.putExtra("memberInfo", memberInfo);
                intent.putExtra("selectedDateYMD", selectedDateYMD);

                startActivityResult.launch(intent);
                break;
        }
    }

    // 초기화
    public void initialize(){
        /** 오늘의 운동 버튼 없애기*/
        binding.btnAddFitnessToday.setVisibility(View.GONE);

        /** 달력 초기화*/
        // 달력
        calendar = binding.calendarFitness;
        setCalendar();

        //달력에 점찍기 위해 필요한 데이터 가져오기
        Retrofit retrofit= RetrofitInstance.getRetroClient();
        FitnessService service = retrofit.create(FitnessService.class);

        // http request 객체 생성
        Call<ArrayList<String>> call = service.getFitnessDateList(memberInfo.getUserId());

        new GetHasFitnessDateListCall().execute(call);

        /** 리사이클러뷰 초기화 */
        recyclerView = binding.recyclerViewFitness;
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));

        /** onClickListener 등록 */
        setOnClickListener();

        observe();
    }

    public void observe(){
        // 날짜리스트 데이터 변경된 경우 점찍기
        fitnessViewModel.getDateSet().observe(requireActivity(), dateSet -> {
            Log.d("dateSet사이즈3: ", "" + dateSet.size());
            calendar.removeDecorator(decorator);

            this.dateSet = dateSet;

            decorator = new DotDecorator(Color.RED,dateSet);

            calendar.addDecorator(decorator);
        });
    }

    /** 운동기록 데이터가 있는 날짜에 점찍기 */
    public void setCalendar(){
        calendar.addDecorator(new TodayDecorator(requireActivity()));
        calendar.addDecorator(new SundayDecorator());
        calendar.addDecorator(new SaturdayDecorator());

        // 날짜 변경(날짜 클릭 시) 실행되는 콜백 메소드
        calendar.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                //리사이클러뷰에 보여주기 위한 데이터 가져오기
                Retrofit retrofit= RetrofitInstance.getRetroClient();
                FitnessService service = retrofit.create(FitnessService.class);

                selectedDateYMD = ""+ date.getYear() + (date.getMonth() < 10 ? "0"+(date.getMonth()+1) : (date.getMonth()+1)) + (date.getDay() <10 ? "0" + date.getDay() : date.getDay());

                System.out.println("오늘 날짜: " + selectedDateYMD);

                // http request 객체 생성
                Call<ArrayList<FitnessRecord>> call = service.getFitnessInfoByDay(memberInfo.getUserId(),selectedDateYMD);

                new GetFitnessListCall().execute(call);

            }

        });

        // 오늘 날짜가 선택되도록
        CalendarDay today = CalendarDay.today();
        calendar.setDateSelected(today,false);
    }

    /**  운동 정보 가져오기 */
    public class GetFitnessListCall extends AsyncTask<Call, Void, String> {
        private retrofit2.Response<ArrayList<FitnessRecord>> response;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("onPreExecute", "11");
        }

        @Override
        protected String  doInBackground(Call[] params) {
            try {
                Call<ArrayList<FitnessRecord>> call = params[0];
                response = call.execute();

                if(response.body() != null){
                    clickedFitnessList = response.body();
                }

                return response.body().toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {

            //binding.btnAddFitnessToday.setVisibility(View.VISIBLE);

            // 데이터가 없다면
            if(result.equals("[]")){
                if(adapter != null){
                    adapter.clearAllFitnessInfo();
                    //binding.btnAddFitnessToday.setVisibility(View.GONE);
                }
                // 탭하여 운동기록 추가버튼 생성
                /** 트레이너가 아니라면*/
                if(TrainerHomeActivity.staticLoginUserInfo == null){
                    binding.btnAddFitness.setVisibility(View.VISIBLE);
                }
            } else {
                // 탭하여 운동기록 추가버튼 제거
                binding.btnAddFitness.setVisibility(View.GONE);
                // 결과값을 가져 왔다면 ->  db에서 아무런 데이터를 조회해오지 못하면 "[]"값을 가져온다.
                // 운동기록 정보 리사이클러뷰에 세팅
                adapter = new FitnessListAdapter(clickedFitnessList, requireActivity(), startActivityResult2, memberInfo);
                recyclerView.setAdapter(adapter);
            }
        }
    }

    /** 운동기록 정보를 가지고 있는 날짜리스트 가져오기 */
    public class GetHasFitnessDateListCall extends AsyncTask<Call, Void, String> {
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
                // 운동기록 데이터 있는 날짜 리스트 -> 해쉬맵으로 변환
                dateSet = new HashSet<>();

                for(String date : dateList){
                    // date: yyyyMMdd

                    int year = Integer.parseInt(date.substring(0,4));
                    // month는 0부터 시작!
                    int month = Integer.parseInt(date.substring(4,6)) - 1;
                    int dayOfWeek = Integer.parseInt(date.substring(6,8));

                    CalendarDay c = CalendarDay.from(year, month, dayOfWeek);
                    dateSet.add(c);
                }

                // viewModel에 저장
                fitnessViewModel.getDateSet().setValue(dateSet);
            }
        }
    }
}