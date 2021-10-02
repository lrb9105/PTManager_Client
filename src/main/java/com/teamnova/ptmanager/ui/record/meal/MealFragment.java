package com.teamnova.ptmanager.ui.record.meal;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
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

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.teamnova.ptmanager.R;
import com.teamnova.ptmanager.databinding.FragmentMealBinding;
import com.teamnova.ptmanager.model.record.meal.Meal;
import com.teamnova.ptmanager.model.record.meal.MealDateWithCount;
import com.teamnova.ptmanager.model.record.meal.MealHistoryInfo;
import com.teamnova.ptmanager.model.userInfo.FriendInfoDto;
import com.teamnova.ptmanager.network.RetrofitInstance;
import com.teamnova.ptmanager.network.record.meal.MealService;
import com.teamnova.ptmanager.ui.changehistory.inbody.InBodyFragment;
import com.teamnova.ptmanager.ui.home.trainer.TrainerHomeActivity;
import com.teamnova.ptmanager.ui.record.meal.adapter.MealSeeAllAdapter;
import com.teamnova.ptmanager.ui.schedule.schedule.decorator.DotDecorator;
import com.teamnova.ptmanager.ui.schedule.schedule.decorator.SaturdayDecorator;
import com.teamnova.ptmanager.ui.schedule.schedule.decorator.SundayDecorator;
import com.teamnova.ptmanager.ui.schedule.schedule.decorator.TodayDecorator;
import com.teamnova.ptmanager.viewmodel.friend.FriendViewModel;
import com.teamnova.ptmanager.viewmodel.record.MealViewModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import retrofit2.Call;
import retrofit2.Retrofit;

/**
 * 식사 프래그먼트
 * */
public class MealFragment extends Fragment implements View.OnClickListener{
    private FragmentMealBinding binding;

    /** 데이터 */
    // 식사 데이터가 있는 날짜 리스트
    private ArrayList<MealDateWithCount> dateList;

    // 리사이클러뷰에 널어주기 위한 데이터(아침, 점심, 저녁, 간식2, 야식 최대 6개의 데이터 가능)
    private MealHistoryInfo MealHistoryInfo;

    // 식사 데이터 등록 시 넘어오는 데이터 처리
    private ActivityResultLauncher<Intent> startActivityResult;

    // 식사 데이터 삭제 시 넘어오는 데이터 처리
    private ActivityResultLauncher<Intent> startActivityResult2;

    // 회원 정보를 가져오기 위한 viewModel
    private FriendViewModel friendViewModel;

    // 식사 정보를 다루기 위한 viewModel
    private MealViewModel mealViewModel;

    // 회원 정보
    private FriendInfoDto memberInfo;

    // 달력객체
    private MaterialCalendarView calendar;

    // 식사 데이터가 있는 모든 날짜 가져오기
    private HashSet<CalendarDay> dateSet;

    // 날짜별 식사기록 데이터의 갯수를 가지고 있는 HashMap
    private HashMap<String, Integer> countByDateMap;

    // 데코레이터
    private DotDecorator decorator;

    public MealFragment() {}

    // TODO: Rename and change types and number of parameters
    public static InBodyFragment newInstance(String param1, String param2) {
        InBodyFragment fragment = new InBodyFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // 식사 데이터 등록 시
        startActivityResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Log.d("여기까지 오는가?", "123123");

                            /** HashSet에 넣어준다.*/
                            Meal addedMeal = (Meal)result.getData().getSerializableExtra("meal");
                            String date = addedMeal.getMealDate();

                            int year = Integer.parseInt(date.substring(0,4));
                            // month는 0부터 시작!
                            int month = Integer.parseInt(date.substring(4,6)) - 1;
                            int dayOfWeek = Integer.parseInt(date.substring(6,8));

                            CalendarDay c = CalendarDay.from(year, month, dayOfWeek);
                            dateSet.add(c);

                            // map에 데이터 추가
                            if(countByDateMap.get(date) == null){
                                countByDateMap.put(date,1);
                            } else{
                                countByDateMap.put(date, countByDateMap.get(date)+1);
                            }

                            Log.d("식사등록 시 mapCount: ", "" + countByDateMap.get(date));

                            mealViewModel.getDateSet().setValue(dateSet);
                        }
                    }
                });

        // 식사 데이터 삭제 시
        startActivityResult2 = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Log.d("여기까지 오는가?", "123123");

                            /** 삭제한 날의 카운트가 1이라면 HashSet에서 삭제한다.*/
                            Meal deletedMeal = (Meal)result.getData().getSerializableExtra("deletedMealInfo");
                            Meal meal = (Meal)result.getData().getSerializableExtra("meal");

                            if(deletedMeal != null){
                                String date = deletedMeal.getMealDate();

                                int count  = countByDateMap.get(date);

                                Log.d("카운트: ", "" + count);

                                // 하나밖에 없었다면 HashSet삭제
                                if(count == 1){
                                    calendar.removeDecorator(decorator);

                                    int year = Integer.parseInt(date.substring(0,4));
                                    // month는 0부터 시작!
                                    int month = Integer.parseInt(date.substring(4,6)) - 1;
                                    int dayOfWeek = Integer.parseInt(date.substring(6,8));

                                    CalendarDay c = CalendarDay.from(year, month, dayOfWeek);
                                    dateSet.remove(c);

                                    mealViewModel.getDateSet().setValue(dateSet);
                                }

                                Log.d("식사타입","" + deletedMeal.getMealType());


                                // 달력아래에 출력되는 데이터 삭제
                                switch (deletedMeal.getMealType()){
                                    case 0: //아침
                                        binding.layoutMorning.expandableLayout.setVisibility(View.GONE);
                                        binding.layoutMorning.contents.setVisibility(View.GONE);
                                        binding.layoutMorning.hidden.setVisibility(View.GONE);
                                        break;
                                    case 1: //간식1
                                        binding.layoutSnack1.expandableLayout.setVisibility(View.GONE);
                                        binding.layoutSnack1.contents.setVisibility(View.GONE);
                                        binding.layoutSnack1.hidden.setVisibility(View.GONE);
                                        break;
                                    case 2: //점심
                                        binding.layoutLunch.expandableLayout.setVisibility(View.GONE);
                                        binding.layoutLunch.contents.setVisibility(View.GONE);
                                        binding.layoutLunch.hidden.setVisibility(View.GONE);

                                        break;
                                    case 3: //간식2
                                        binding.layoutSnack2.expandableLayout.setVisibility(View.GONE);
                                        binding.layoutSnack2.contents.setVisibility(View.GONE);
                                        binding.layoutSnack2.hidden.setVisibility(View.GONE);

                                        break;
                                    case 4: //저녁
                                        binding.layoutEvening.expandableLayout.setVisibility(View.GONE);
                                        binding.layoutEvening.contents.setVisibility(View.GONE);
                                        binding.layoutEvening.hidden.setVisibility(View.GONE);

                                        break;
                                    case 5: //야식
                                        binding.layoutNight.expandableLayout.setVisibility(View.GONE);
                                        binding.layoutNight.contents.setVisibility(View.GONE);
                                        binding.layoutNight.hidden.setVisibility(View.GONE);

                                        break;
                                }
                            } else if(meal != null){ //식사수정
                                Log.d("식사수정", meal.getMealContents());

                                // 수정
                                switch (meal.getMealType()){
                                    case 0: //아침
                                        binding.layoutMorning.contents.setText(meal.getMealContents());
                                        break;
                                    case 1: //간식1
                                        binding.layoutSnack1.contents.setText(meal.getMealContents());
                                        break;
                                    case 2: //점심
                                        binding.layoutLunch.contents.setText(meal.getMealContents());

                                        break;
                                    case 3: //간식2
                                        binding.layoutSnack2.contents.setText(meal.getMealContents());

                                        break;
                                    case 4: //저녁
                                        binding.layoutEvening.contents.setText(meal.getMealContents());

                                        break;
                                    case 5: //야식
                                        binding.layoutNight.contents.setText(meal.getMealContents());
                                        break;
                                }
                            }
                        }
                    }
                });

        super.onCreate(savedInstanceState);

        // 뷰모델 초기화
        friendViewModel = new ViewModelProvider(requireActivity()).get(FriendViewModel.class);
        mealViewModel = new ViewModelProvider(requireActivity()).get(MealViewModel.class);


        // 로그인한 회원 정보 observe
        friendViewModel.getFriendInfo().observe(requireActivity(), memberInfo -> {
            this.memberInfo = memberInfo;
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // View view = inflater.inflate(R.layout.fragment_eye_body, container, false);

        binding = FragmentMealBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        initialize();

        return view;
    }

    // 초기화
    public void initialize(){
        /** countByDateMap 초기화*/
        countByDateMap = new HashMap<>();

        /** 달력 초기화*/
        // 달력
        calendar = binding.calendarInBody;
        setCalendar();

        //달력에 점찍기 위해 필요한 데이터 가져오기
        Retrofit retrofit= RetrofitInstance.getRetroClient();
        MealService service = retrofit.create(MealService.class);

        // http request 객체 생성
        Call<ArrayList<MealDateWithCount>> call = service.getMealDateList(memberInfo.getUserId());
        //Call<String> call = service.getMealDateList(memberInfo.getUserId());

        // 식사데이터가 있는 날짜리스트 가져오기
        new GetHasMealDateListCall().execute(call);

        /** onClickListener 등록 */
        setOnClickListener();

        /** 트레이너라면 비활성화*/
        if(TrainerHomeActivity.staticLoginUserInfo != null){
            binding.addMeal.setVisibility(View.GONE);
        }

        /** 식사 정보 observe
         * 1. 식사 정보를 가져오거나 추가했을 때 관리하고 있는 map에 넣어준다
         * */
        /*mealViewModel.getDateSet().observe(requireActivity(), inBodyMap -> {
            this.inBodyMap = inBodyMap;
        });*/

        observe();

        /** 식사 정보 */
        // 아침
        binding.layoutMorning.title.setText("아침");

        // 간식
        binding.layoutSnack1.title.setText("간식(아침-점심)");

        // 점심
        binding.layoutLunch.title.setText("점심");

        // 간식2
        binding.layoutSnack2.title.setText("간식(점심-저녁)");

        // 저녁
        binding.layoutEvening.title.setText("저녁");

        // 야식
        binding.layoutNight.title.setText("야식");


    }

    /**
     * OnclickListener 등록
     * */
    public void setOnClickListener(){
        binding.addMeal.setOnClickListener(this);
        binding.seeAllMeal.setOnClickListener(this);
        binding.layoutMorning.expandableLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireActivity(), MealDetailActivity.class);
                // 식사기록 정보
                intent.putExtra("mealId", binding.layoutMorning.hidden.getText().toString());
                intent.putExtra("memberInfo", memberInfo);
                startActivityResult2.launch(intent);
            }
        });
        binding.layoutSnack1.expandableLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireActivity(), MealDetailActivity.class);
                // 식사기록 정보
                intent.putExtra("mealId", binding.layoutSnack1.hidden.getText().toString());
                intent.putExtra("memberInfo", memberInfo);
                startActivity(intent);
            }
        });
        binding.layoutLunch.expandableLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireActivity(), MealDetailActivity.class);
                // 식사기록 정보
                intent.putExtra("mealId", binding.layoutLunch.hidden.getText().toString());
                intent.putExtra("memberInfo", memberInfo);
                startActivity(intent);
            }
        });;
        binding.layoutSnack2.expandableLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireActivity(), MealDetailActivity.class);
                // 식사기록 정보
                intent.putExtra("mealId", binding.layoutSnack2.hidden.getText().toString());
                intent.putExtra("memberInfo", memberInfo);
                startActivity(intent);
            }
        });;
        binding.layoutEvening.expandableLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireActivity(), MealDetailActivity.class);
                // 식사기록 정보
                intent.putExtra("mealId", binding.layoutEvening.hidden.getText().toString());
                intent.putExtra("memberInfo", memberInfo);
                startActivity(intent);
            }
        });;
        binding.layoutNight.expandableLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireActivity(), MealDetailActivity.class);
                // 식사기록 정보
                intent.putExtra("mealId", binding.layoutNight.hidden.getText().toString());
                intent.putExtra("memberInfo", memberInfo);
                startActivity(intent);
            }
        });;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.add_meal: // 식사 등록
                Intent intent = new Intent(getActivity(), MealRegisterActivity.class);

                intent.putExtra("memberInfo", memberInfo);

                startActivityResult.launch(intent);
                break;
            case R.id.see_all_meal: // 모아보기
                intent = new Intent(getActivity(), MealSeeAllActivity.class);

                intent.putExtra("memberInfo", memberInfo);

                startActivityResult2.launch(intent);
                break;
        }
    }

    public void observe(){
        // 날짜리스트 데이터 변경된 경우 점찍기
        mealViewModel.getDateSet().observe(requireActivity(), dateSet -> {
            this.dateSet = dateSet;

            decorator = new DotDecorator(Color.RED,dateSet);

            calendar.addDecorator(decorator);
        });
    }


    /** 식사 데이터가 있는 날짜에 점찍기 */
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
                MealService service = retrofit.create(MealService.class);

                String dateYMD = ""+ date.getYear() + (date.getMonth() < 9 ? "0"+(date.getMonth()+1) : (date.getMonth()+1)) + (date.getDay() <10 ? "0" + date.getDay() : date.getDay());

                // http request 객체 생성
                Call<MealHistoryInfo> call = service.getMealList(memberInfo.getUserId(),dateYMD);

                new GetMealHistoryCall().execute(call);

            }
        });

        // 오늘 날짜가 선택되도록
        CalendarDay today = CalendarDay.today();
        calendar.setDateSelected(today,false);
    }

    /** 식사 정보 기록 가져오기 */
    private class GetMealHistoryCall extends AsyncTask<Call, Void, String> {
        private retrofit2.Response<MealHistoryInfo> response;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            binding.layoutMorning.expandableLayout.setVisibility(View.GONE);
            binding.layoutSnack1.expandableLayout.setVisibility(View.GONE);
            binding.layoutLunch.expandableLayout.setVisibility(View.GONE);
            binding.layoutSnack2.expandableLayout.setVisibility(View.GONE);
            binding.layoutEvening.expandableLayout.setVisibility(View.GONE);
            binding.layoutNight.expandableLayout.setVisibility(View.GONE);

            Log.d("onPreExecute", "11");
        }

        @Override
        protected String  doInBackground(Call[] params) {
            try {
                Call<MealHistoryInfo> call = params[0];
                response = call.execute();

                if(response.body() != null){
                    MealHistoryInfo = response.body();
                }

                return response.body().toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            // 날짜에 해당하는 식사기록 데이터가 있을 경우
            if(MealHistoryInfo.getMealListByDay().size() > 0){

                ArrayList<Meal> mealList = MealHistoryInfo.getMealListByDay();

                for(Meal m : mealList){
                    switch (m.getMealType()){
                        case 0: //아침
                            binding.layoutMorning.expandableLayout.setVisibility(View.VISIBLE);
                            binding.layoutMorning.contents.setVisibility(View.VISIBLE);
                            binding.layoutMorning.contents.setText(m.getMealContents());
                            binding.layoutMorning.hidden.setText(m.getMealRecordId());
                            break;
                        case 1: //간식1
                            binding.layoutSnack1.expandableLayout.setVisibility(View.VISIBLE);
                            binding.layoutSnack1.contents.setVisibility(View.VISIBLE);
                            binding.layoutSnack1.contents.setText(m.getMealContents());
                            binding.layoutSnack1.hidden.setText(m.getMealRecordId());
                            break;
                        case 2: //점심
                            binding.layoutLunch.expandableLayout.setVisibility(View.VISIBLE);
                            binding.layoutLunch.contents.setVisibility(View.VISIBLE);
                            binding.layoutLunch.contents.setText(m.getMealContents());
                            binding.layoutLunch.hidden.setText(m.getMealRecordId());

                            break;
                        case 3: //간식2
                            binding.layoutSnack2.expandableLayout.setVisibility(View.VISIBLE);
                            binding.layoutSnack2.contents.setVisibility(View.VISIBLE);
                            binding.layoutSnack2.contents.setText(m.getMealContents());
                            binding.layoutSnack2.hidden.setText(m.getMealRecordId());

                            break;
                        case 4: //저녁
                            binding.layoutEvening.expandableLayout.setVisibility(View.VISIBLE);
                            binding.layoutEvening.contents.setVisibility(View.VISIBLE);
                            binding.layoutEvening.contents.setText(m.getMealContents());
                            binding.layoutEvening.hidden.setText(m.getMealRecordId());

                            break;
                        case 5: //야식
                            binding.layoutNight.expandableLayout.setVisibility(View.VISIBLE);
                            binding.layoutNight.contents.setVisibility(View.VISIBLE);
                            binding.layoutNight.contents.setText(m.getMealContents());
                            binding.layoutNight.hidden.setText(m.getMealRecordId());

                            break;
                    }
                }
            }
        }
    }

    /** 식사 정보를 가지고 있는 날짜리스트 가져오기 */
    public class GetHasMealDateListCall extends AsyncTask<Call, Void, String> {
        private retrofit2.Response<ArrayList<MealDateWithCount>> response;
        //private retrofit2.Response<String> response;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("onPreExecute", "11");
        }

        @Override
        protected String  doInBackground(Call[] params) {
            try {
                Call<ArrayList<MealDateWithCount>> call = params[0];
                //Call<String> call = params[0];
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
                // 식사 데이터 있는 날짜 리스트 -> 해쉬맵으로 변환
                dateSet = new HashSet<>();

                for(MealDateWithCount m : dateList){
                    // date: yyyyMMdd

                    int year = Integer.parseInt(m.getDate().substring(0,4));
                    // month는 0부터 시작!
                    int month = Integer.parseInt(m.getDate().substring(4,6)) - 1;
                    int dayOfWeek = Integer.parseInt(m.getDate().substring(6,8));

                    CalendarDay c = CalendarDay.from(year, month, dayOfWeek);

                    // 날짜별 식사갯수 저장
                    countByDateMap.put(m.getDate(),m.getCount());

                    Log.d("맵에 잘들어가나?", m.getDate() + " - " + countByDateMap.get(m.getDate()));

                    dateSet.add(c);
                }

                // viewModel에 저장
                mealViewModel.getDateSet().setValue(dateSet);
            }
        }
    }
}