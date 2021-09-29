package com.teamnova.ptmanager.ui.changehistory.inbody;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.teamnova.ptmanager.R;
import com.teamnova.ptmanager.databinding.FragmentEyeBodyBinding;
import com.teamnova.ptmanager.databinding.FragmentInBodyBinding;
import com.teamnova.ptmanager.databinding.ItemEyebodyChangeBinding;
import com.teamnova.ptmanager.databinding.ItemEyebodyCompareBinding;
import com.teamnova.ptmanager.model.changehistory.eyebody.EyeBody;
import com.teamnova.ptmanager.model.changehistory.eyebody.EyeBodyCompare;
import com.teamnova.ptmanager.model.changehistory.eyebody.EyeBodyHistoryInfo;
import com.teamnova.ptmanager.model.changehistory.inbody.InBody;
import com.teamnova.ptmanager.model.lesson.LessonSchInfo;
import com.teamnova.ptmanager.model.userInfo.FriendInfoDto;
import com.teamnova.ptmanager.network.RetrofitInstance;
import com.teamnova.ptmanager.network.changehistory.eyebody.EyeBodyService;
import com.teamnova.ptmanager.network.changehistory.inbody.InBodyService;
import com.teamnova.ptmanager.network.schedule.lesson.LessonService;
import com.teamnova.ptmanager.test.WeekViewActivity7;
import com.teamnova.ptmanager.ui.changehistory.eyebody.EyeBodyChangeHistoryActivity;
import com.teamnova.ptmanager.ui.changehistory.eyebody.EyeBodyCompareHistoryActivity;
import com.teamnova.ptmanager.ui.changehistory.eyebody.EyeBodySaveActivity;
import com.teamnova.ptmanager.ui.changehistory.eyebody.adapter.EyeBodyChangeHistoryAdapter;
import com.teamnova.ptmanager.ui.changehistory.inbody.adapter.InBodyListAdapter;
import com.teamnova.ptmanager.ui.home.trainer.TrainerHomeActivity;
import com.teamnova.ptmanager.ui.schedule.schedule.decorator.DotDecorator;
import com.teamnova.ptmanager.ui.schedule.schedule.decorator.SaturdayDecorator;
import com.teamnova.ptmanager.ui.schedule.schedule.decorator.SundayDecorator;
import com.teamnova.ptmanager.ui.schedule.schedule.decorator.TodayDecorator;
import com.teamnova.ptmanager.util.GetDate;
import com.teamnova.ptmanager.viewmodel.changehistory.eyebody.EyeBodyViewModel;
import com.teamnova.ptmanager.viewmodel.changehistory.inbody.InBodyViewModel;
import com.teamnova.ptmanager.viewmodel.friend.FriendViewModel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;

/**
 * 인바디 프래그먼트
 * */
public class InBodyFragment extends Fragment implements View.OnClickListener{
    private FragmentInBodyBinding binding;

    /** 데이터 */
    // 인바디 데이터가 있는 날짜 리스트
    private ArrayList<String> dateList;

    // 리사이클러뷰에 널어주기 위한 데이터
    private ArrayList<InBody> clickedInBodyList;

    // 인바디 데이터 등록 시 넘어오는 데이터 처리
    private ActivityResultLauncher<Intent> startActivityResult;

    // 인바디 데이터 수정 시 넘어오는 데이터 처리
    private ActivityResultLauncher<Intent> startActivityResult2;

    // 회원 정보를 가져오기 위한 viewModel
    private FriendViewModel friendViewModel;

    // 인인바디 정보를 다루기 위한 viewModel
    private InBodyViewModel inBodyViewModel;

    // 회원 정보
    private FriendInfoDto memberInfo;

    // 달력객체
    private MaterialCalendarView calendar;

    // 인바디 데이터가 있는 모든 날짜 가져오기
    private HashSet<CalendarDay> dateSet;

    // 플로팅버튼
    private boolean fabMain_status = false;
    private FloatingActionButton fabMain;
    private FloatingActionButton fabInBodyRegister;
    private FloatingActionButton fabInBodyGraph;

    // 리사이클러뷰
    private RecyclerView recyclerView;
    private InBodyListAdapter adapter;

    // 리스너
    private InBodyListAdapter.ItemDeleteListenerChild listener;

    // 데코레이터
    private DotDecorator decorator;

    /** 날짜별 인바디기록을 저장할 리스트
     * 1. 첫번째: 년월
     * 2. 두번째: 일
     * */
    //private HashMap<String,HashMap<String,ArrayList<InBody>>> inBodyMap;

    // Retrofit 통신의 결과를 전달하는 핸들러
    private Handler resultHandler;

    public InBodyFragment() {}

    // TODO: Rename and change types and number of parameters
    public static InBodyFragment newInstance(String param1, String param2) {
        InBodyFragment fragment = new InBodyFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // 인바디 데이터 등록 시
        startActivityResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            /** HashSet에 넣어준다.*/
                            InBody addedInBody = (InBody)result.getData().getSerializableExtra("inBody");
                            String date = addedInBody.getCreDate();

                            int year = Integer.parseInt(date.substring(0,4));
                            // month는 0부터 시작!
                            int month = Integer.parseInt(date.substring(4,6)) - 1;
                            int dayOfWeek = Integer.parseInt(date.substring(6,8));

                            CalendarDay c = CalendarDay.from(year, month, dayOfWeek);
                            dateSet.add(c);

                            inBodyViewModel.getDateSet().setValue(dateSet);
                        }
                    }
                });

        // 인바디 데이터 수정 시
        startActivityResult2 = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            InBody modifiedInBody = (InBody)result.getData().getSerializableExtra("inBody");
                            adapter.modifyInBodyInfo(0,modifiedInBody);
                        }
                    }
                });

        super.onCreate(savedInstanceState);

        // 뷰모델 초기화
        friendViewModel = new ViewModelProvider(requireActivity()).get(FriendViewModel.class);
        inBodyViewModel = new ViewModelProvider(requireActivity()).get(InBodyViewModel.class);

        // 인바디 데이터를 저장할 hashMap
        //inBodyMap = new HashMap<>();

        // 로그인한 회원 정보 observe
        friendViewModel.getFriendInfo().observe(requireActivity(), memberInfo -> {
            this.memberInfo = memberInfo;
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

       // View view = inflater.inflate(R.layout.fragment_eye_body, container, false);

        binding = FragmentInBodyBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        initialize();

        return view;
    }

    /**
     * OnclickListener 등록
     * */
    public void setOnClickListener(){
        // 메인플로팅 버튼 클릭
        /*fabMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFab();
            }
        });*/

        binding.addInbody.setOnClickListener(this);
        binding.graphInbody.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.add_inbody: // 인바디 등록
                Intent intent = new Intent(getActivity(), InBodyRegisterActivity.class);

                intent.putExtra("memberInfo", memberInfo);

                startActivityResult.launch(intent);
                break;
            case R.id.graph_inbody: // 인바디 변화 그래프
                intent = new Intent(getActivity(), InBodyViewGraphActivity.class);

                intent.putExtra("memberInfo", memberInfo);
                intent.putExtra("dateList", dateList);

                startActivity(intent);
                break;
        }
    }

    // 초기화
    public void initialize(){
        /** 달력 초기화*/
        // 달력
        calendar = binding.calendarInBody;
        setCalendar();

        //달력에 점찍기 위해 필요한 데이터 가져오기
        Retrofit retrofit= RetrofitInstance.getRetroClient();
        InBodyService service = retrofit.create(InBodyService.class);

        // http request 객체 생성
        Call<ArrayList<String>> call = service.getInBodyDateList(memberInfo.getUserId());

        new GetHasInBodyDateListCall().execute(call);

        /** 플로팅 버튼 초기화*/
        /*fabMain = binding.mainFab;
        fabInBodyRegister = binding.inBodyRegister;
        fabInBodyGraph = binding.inBodyGraph;
*/
        /** 리사이클러뷰 초기화 */
        recyclerView = binding.recyclerViewInBody;
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));

        /** onClickListener 등록 */
        setOnClickListener();

        /** 트레이너인 경우 비활성화*/
        if(TrainerHomeActivity.staticLoginUserInfo != null){
            binding.addInbody.setVisibility(View.GONE);
        }

        /** 인바디 정보 observe
         * 1. 인바디 정보를 가져오거나 추가했을 때 관리하고 있는 map에 넣어준다
         * */
        /*inBodyViewModel.getInBodyMap().observe(requireActivity(), inBodyMap -> {
            this.inBodyMap = inBodyMap;
        });*/

        /** 삭제 시 작동할 리스터*/
        listener = new InBodyListAdapter.ItemDeleteListenerChild() {
            @Override
            public void onItemDeleted(InBody i) {
                String date = i.getCreDate();

                Log.d("삭제날짜:" , date);

                int year = Integer.parseInt(date.substring(0,4));
                // month는 0부터 시작!
                int month = Integer.parseInt(date.substring(4,6)) - 1;
                int dayOfWeek = Integer.parseInt(date.substring(6,8));

                CalendarDay c = CalendarDay.from(year, month, dayOfWeek);
                Log.d("dateSet사이즈1: ", "" + dateSet.size());

                adapter.deleteInBodyInfo(0);

                calendar.removeDecorator(decorator);

                dateSet.remove(c);

                Log.d("dateSet사이즈2: ", "" + dateSet.size());


                inBodyViewModel.getDateSet().setValue(dateSet);
            }
        };

        observe();
    }

    public void observe(){
        // 날짜리스트 데이터 변경된 경우 점찍기
        inBodyViewModel.getDateSet().observe(requireActivity(), dateSet -> {
            Log.d("dateSet사이즈3: ", "" + dateSet.size());

            this.dateSet = dateSet;

            decorator = new DotDecorator(Color.RED,dateSet);

            calendar.addDecorator(decorator);
        });
    }

    // 플로팅 액션 버튼 클릭시 애니메이션 효과
    public void toggleFab() {
        if(fabMain_status) {
            // 플로팅 액션 버튼 닫기
            // 애니메이션 추가
            ObjectAnimator fc_animation = ObjectAnimator.ofFloat(fabInBodyRegister, "translationY", 0f);
            fc_animation.start();
            ObjectAnimator fe_animation = ObjectAnimator.ofFloat(fabInBodyGraph, "translationY", 0f);
            fe_animation.start();
            // 메인 플로팅 이미지 변경
            fabMain.setImageResource(R.drawable.ic_baseline_add_24);

        }else {
            // 플로팅 액션 버튼 열기
            ObjectAnimator fc_animation = ObjectAnimator.ofFloat(fabInBodyRegister, "translationY", -200f);
            fc_animation.start();
            ObjectAnimator fe_animation = ObjectAnimator.ofFloat(fabInBodyGraph, "translationY", -400f);
            fe_animation.start();
            // 메인 플로팅 이미지 변경
            fabMain.setImageResource(R.drawable.all_cell);
        }
        // 플로팅 버튼 상태 변경
        fabMain_status = !fabMain_status;
    }

    /** 인바디 데이터가 있는 날짜에 점찍기 */
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
                InBodyService service = retrofit.create(InBodyService.class);

                String dateYMD = ""+ date.getYear() + (date.getMonth() <10 ? "0"+(date.getMonth()+1) : (date.getMonth()+1)) + (date.getDay() <10 ? "0" + date.getDay() : date.getDay());

                // http request 객체 생성
                Call<ArrayList<InBody>> call = service.getInBodyInfo(memberInfo.getUserId(),dateYMD);

                new GetInBodyListCall().execute(call);

            }
        });

        // 오늘 날짜가 선택되도록
        CalendarDay today = CalendarDay.today();
        calendar.setDateSelected(today,false);
    }

    /** 인바디 정보 가져오기 */
    private class GetInBodyListCall extends AsyncTask<Call, Void, String> {
        private retrofit2.Response<ArrayList<InBody>> response;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("onPreExecute", "11");
        }

        @Override
        protected String  doInBackground(Call[] params) {
            try {
                Call<ArrayList<InBody>> call = params[0];
                response = call.execute();

                if(response.body() != null){
                    clickedInBodyList = response.body();
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
            // 인바디 정보 리사이클러뷰에 세팅
            adapter = new InBodyListAdapter(clickedInBodyList, requireActivity(), startActivityResult2,memberInfo, listener);
            recyclerView.setAdapter(adapter);

            // 데이터가 없다면
            if(result.equals("[]")){
                adapter.clearAllInBodyInfo();
            }
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
                // 인바디 데이터 있는 날짜 리스트 -> 해쉬맵으로 변환
                dateSet = new HashSet<>();

                for(String date : dateList){
                    // date: yyyyMMdd

                    Log.d("인바디 날짜:", date);

                    int year = Integer.parseInt(date.substring(0,4));
                    // month는 0부터 시작!
                    int month = Integer.parseInt(date.substring(4,6)) - 1;
                    int dayOfWeek = Integer.parseInt(date.substring(6,8));

                    CalendarDay c = CalendarDay.from(year, month, dayOfWeek);
                    dateSet.add(c);
                }

                // viewModel에 저장
                inBodyViewModel.getDateSet().setValue(dateSet);
            }
        }
    }
}