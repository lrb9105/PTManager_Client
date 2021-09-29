package com.teamnova.ptmanager.ui.changehistory.inbody;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.teamnova.ptmanager.R;
import com.teamnova.ptmanager.databinding.ActivityInBodyViewGraphBinding;
import com.teamnova.ptmanager.databinding.ActivityRegisterInBodyBinding;
import com.teamnova.ptmanager.model.changehistory.inbody.InBodyForGraph;
import com.teamnova.ptmanager.model.userInfo.FriendInfoDto;
import com.teamnova.ptmanager.network.RetrofitInstance;
import com.teamnova.ptmanager.network.changehistory.inbody.InBodyService;
import com.teamnova.ptmanager.util.GetDate;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;

import retrofit2.Call;
import retrofit2.Retrofit;

public class InBodyViewGraphActivity extends AppCompatActivity implements View.OnClickListener{
    private ActivityInBodyViewGraphBinding binding;
    private SimpleDateFormat dateFormat = new SimpleDateFormat ("yyyyMMdd");

    /** 시작일, 종료일*/
    private String srtDate;
    private String endDate;

    /** 그래프 그릴 기간 가지고 오는 객체 */
    private ActivityResultLauncher<Intent> startActivityResult;
    
    /** 그래프를 그리기 위한 데이터*/
    private InBodyForGraph inBodyForGraph;

    private int index = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /** 기간설정 시 */
        startActivityResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            /** 그래프 그길 기간 변경하기 */
                            srtDate = result.getData().getStringExtra("srtDate");
                            endDate = result.getData().getStringExtra("endDate");

                            setInBodyPeriod(srtDate, endDate);

                            /** 서버에서 데이터 가져와서 그래프 그려주기 */
                            getDataForGraph();
                        }
                    }
                });

        super.onCreate(savedInstanceState);

        // 해당 액티비티에서 사용할 xml파일에대한 클래스가 자동으로 생성되고 inflate함수를 이용해서 인스턴스를 생성한다.
        binding = ActivityInBodyViewGraphBinding.inflate(getLayoutInflater());

        // 기존에는 layout이 들어갔는데 대신 getRoot를 넣어준다.
        setContentView(binding.getRoot());

        initialize();
    }

    // 초기화
    public void initialize(){
        /** 시작일, 종료일 세팅*/
        ArrayList<String> dateList = getIntent().getStringArrayListExtra("dateList");

        if(dateList != null && dateList.size() > 0){
            srtDate = dateList.get(0);
            endDate = dateList.get(dateList.size() - 1);

            setInBodyPeriod(srtDate, endDate);

            /** 그래프에 그리기 위한 데이터 가져오기 */
            getDataForGraph();
        }


        /** 온클릭리스너 등록 */
        setOnclickListener();
    }
    /** 온클릭리스너 등록 */
    public void setOnclickListener(){
        binding.btnInbodyPeriod.setOnClickListener(this);
        binding.btnBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;

        switch(v.getId()){
            case  R.id.btn_inbody_period: // 기간설정
                intent = new Intent(this, InBodyRegisterGpathPeriodActivity.class);
                intent.putExtra("memberInfo", (FriendInfoDto)getIntent().getSerializableExtra("memberInfo"));

                startActivityResult.launch(intent);
                break;
            case R.id.btn_back: // 뒤로가기
                onBackPressed();
                break;

            default:
                break;
        }
    }

    /** 인바디 보여주는 기간 세팅 */
    public void setInBodyPeriod(String srtDate, String endDate){
        String period = "";
        String srtDateYMDWithKorean = GetDate.getDateWithYMDAndDot(srtDate);
        String srtDateDay = GetDate.getDayOfWeek(srtDate);

        String endDateYMDWithKorean = GetDate.getDateWithYMDAndDot(endDate);
        String endDateDay = GetDate.getDayOfWeek(endDate);

        period = srtDateYMDWithKorean + "(" + srtDateDay +")" + " - " + endDateYMDWithKorean + "(" + endDateDay +")";

        binding.btnInbodyPeriod.setText(period);
    }

    /** 그래프에 그리기 위한 데이터 가져오기 */
    public void getDataForGraph(){
        Retrofit retrofit= RetrofitInstance.getRetroClient();
        InBodyService service = retrofit.create(InBodyService.class);

        // 사용자 ID
        FriendInfoDto memberInfo = (FriendInfoDto)getIntent().getSerializableExtra("memberInfo");
        String userId = memberInfo.getUserId();

        // http request 객체 생성
        Call<InBodyForGraph> call = service.getInBodyList(userId, srtDate, endDate);

        Log.d("그래프 데이터 가져오기위한 값: ", userId + " : " + srtDate + " : " + endDate);

        new GetInBodyForGraphCall().execute(call);
    }


    /** 그래프 그리기 */
    public void drawGraph(GraphView graph, ArrayList<String> dateList, ArrayList<Integer> dataList, ArrayList<Float> dataList2, int type){
        SimpleDateFormat df = new java.text.SimpleDateFormat("yyyyMMdd");
        int max = 0;

        /*// generate Dates
        Calendar calendar = Calendar.getInstance();
        Date d1 = calendar.getTime();
        calendar.add(Calendar.DATE, 1);
        Date d2 = calendar.getTime();
        calendar.add(Calendar.DATE, 1);
        Date d3 = calendar.getTime();


        // you can directly pass Date objects to DataPoint-Constructor
        // this will convert the Date to double via Date#getTime()
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(d1, 1),
                new DataPoint(d2, 5),
                new DataPoint(d3, 3)
        });

        graph.addSeries(series);

        // set date label formatter
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(this));
        graph.getGridLabelRenderer().setNumHorizontalLabels(3); // only 4 because of the space

        // set manual x bounds to have nice steps
        graph.getViewport().setMinX(d1.getTime());
        graph.getViewport().setMaxX(d3.getTime());
        graph.getViewport().setXAxisBoundsManual(true);*/



        // 그릴 데이터
        DataPoint[] points = new DataPoint[dateList.size()];

        for (int i = 0; i < points.length; i++) {
            Date date = null;
            try {
                date = df.parse(dateList.get(i));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if(dataList != null){
                points[i] = new DataPoint(date, dataList.get(i));
                //points[i] = new DataPoint(i, dataList.get(i));
            } else if(dataList2 != null){
                points[i] = new DataPoint(date, dataList2.get(i));
                //points[i] = new DataPoint(i, dataList2.get(i));
            }
        }

        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(points);

        series.setColor(Color.GREEN);
        series.setDrawDataPoints(true);
        series.setDataPointsRadius(10);
        series.setThickness(8);

        /** set date label formatter */
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(this){
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if(isValueX){
                    Date date = new Date((long)value);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy\nMM.dd");
                    return sdf.format(date);
                }else{
                    return super.formatLabel(value,isValueX);
                }
            }
        });

        // x축 갯수
        if(dateList.size() < 3){
            graph.getGridLabelRenderer().setNumHorizontalLabels(dateList.size());
        }else{
            graph.getGridLabelRenderer().setNumHorizontalLabels(3);
        }

        graph.addSeries(series);

        /** set manual x bounds to have nice steps */
        try {
            Date srtDate = df.parse(dateList.get(0));
            Date endDate = df.parse(dateList.get(dateList.size()-1));

            Log.d("시작일: ", srtDate.toString());
            Log.d("종료일: ", endDate.toString());

            graph.getViewport().setMinX((double)srtDate.getTime());
            graph.getViewport().setMaxX((double)endDate.getTime());

            graph.getViewport().setXAxisBoundsManual(true);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        /*if(dateList.size() < 5){
            graph.getGridLabelRenderer().setNumHorizontalLabels(dateList.size()); // only 4 because of the space
        }*/




        /** 그래프 탭 리스너 */
        series.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                DecimalFormat form = new DecimalFormat("#.##");
                Date date = new Date((long)dataPoint.getX());
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
                String danwi = "";

                switch (type){
                    case 0: //kg
                        danwi = "kg";
                        break;
                    case 1: //kg/m²
                        danwi = "kg/m²";
                        break;
                    case 2: //%
                        danwi = "%";
                        break;
                    case 3: //level
                        danwi = "level";
                        break;
                    case 4: //kcal
                        danwi = "kcal";
                        break;
                }
                Toast.makeText(InBodyViewGraphActivity.this, sdf.format(date) + " : "  + form.format(dataPoint.getY())  + danwi, Toast.LENGTH_SHORT).show();
            }
        });

        /** Y축 최대 데이터 값 설정 */
        if(dataList != null){
            int max1 = getMaxInt(dataList);
            graph.getViewport().setMaxY((double)max1 + 10);
        } else if(dataList2 != null){
            float max2 = getMaxFloat(dataList2);;
            graph.getViewport().setMaxY(max2 + 10);
        }

        /** enable scaling and scrolling */
        /*raph.getViewport().setScalable(true);
        graph.getViewport().setScalableY(true);*/

        graph.getViewport().setScrollable(true); // enables horizontal scrolling
        graph.getViewport().setScrollableY(true); // enables vertical scrolling
        graph.getViewport().setScalable(true); // enables horizontal zooming and scrolling
        graph.getViewport().setScalableY(true); // enables vertical zooming and scrolling
    }

    /** 데이터의 최대값 가져오기 */
    public int getMaxInt(ArrayList<Integer> dataList){
        int max = 0;

        for(int i = 0; i < dataList.size(); i++) {
            if(i != 0) {
                if(max < dataList.get(i)){
                    max = dataList.get(i);
                }
            }
            else{
                max = dataList.get(i);
            }
        }
        return max;
    }

    public float getMaxFloat(ArrayList<Float> dataList2){
        float max = 0;

        for(int i = 0; i < dataList2.size(); i++) {
            if(i != 0) {
                if(max < dataList2.get(i)){
                    max = dataList2.get(i);
                }
            }
            else{
                max = dataList2.get(i);
            }
        }
        return max;
    }

    /** 그래프를 그리기 위한 데이터 가져오기 */
    private class GetInBodyForGraphCall extends AsyncTask<Call, Void, String> {
        private retrofit2.Response<InBodyForGraph> response;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("onPreExecute", "11");
        }

        @Override
        protected String  doInBackground(Call[] params) {
            try {
                Call<InBodyForGraph> call = params[0];
                response = call.execute();

                if(response.body() != null){
                    inBodyForGraph = response.body();
                }

                return response.body().toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            /** 기존에 있던 데이터 지워줌*/
            binding.graphWeight.removeAllSeries();
            binding.graphMuscleMass.removeAllSeries();
            binding.graphBodyFat.removeAllSeries();
            binding.graphBmi.removeAllSeries();
            binding.graphBodyFatPercent.removeAllSeries();
            binding.graphFatLevel.removeAllSeries();
            binding.graphBasalMetabolicRate.removeAllSeries();

            ArrayList<String> inBodyIdList = inBodyForGraph.getInBodyIdList();
            ArrayList<String> inBodyDateList = inBodyForGraph.getInBodyDateList();
            ArrayList<String> inBodyDateTimeList = inBodyForGraph.getInBodyDateTimeList();
            ArrayList<Float> weightList = inBodyForGraph.getWeightList();
            ArrayList<Float> heightList = inBodyForGraph.getHeightList();
            ArrayList<Float> bodyFatList = inBodyForGraph.getBodyFatList();
            ArrayList<Float> muscleMassList = inBodyForGraph.getMuscleMassList();
            ArrayList<Float> bmiList = inBodyForGraph.getBmiList();
            ArrayList<Float> bodyFatPercentList = inBodyForGraph.getBodyFatPercentList();
            ArrayList<Float> basalMetabolicRateList = inBodyForGraph.getBasalMetabolicRateList();
            ArrayList<Integer> fatLevelList = inBodyForGraph.getFatLevelList();

            /*Log.d("그래프 데이터 잘 가져오나?", "" + inBodyIdList.get(0));
            Log.d("그래프 데이터 잘 가져오나?", "" + inBodyDateList.get(0));
            Log.d("그래프 데이터 잘 가져오나?", "" + inBodyDateTimeList.get(0));
            Log.d("그래프 데이터 잘 가져오나?", "" + weightList.get(0));
            Log.d("그래프 데이터 잘 가져오나?", "" + heightList.get(0));
            Log.d("그래프 데이터 잘 가져오나?", "" + bodyFatList.get(0));
            Log.d("그래프 데이터 잘 가져오나?", "" + muscleMassList.get(0));
            Log.d("그래프 데이터 잘 가져오나?", "" + bmiList.get(0));
            Log.d("그래프 데이터 잘 가져오나?", "" + bodyFatPercentList.get(0));
            Log.d("그래프 데이터 잘 가져오나?", "" + basalMetabolicRateList.get(0));
            Log.d("그래프 데이터 잘 가져오나?", "" + fatLevelList.get(0));*/


            // 체중 그래프
            drawGraph(binding.graphWeight, inBodyDateList, null,weightList,0);
            // 근육량 그래프
            drawGraph(binding.graphMuscleMass, inBodyDateList, null,muscleMassList,0);
            // 체지방량 그래프
            drawGraph(binding.graphBodyFat, inBodyDateList, null,bodyFatList,0);
            // BMI 그래프
            drawGraph(binding.graphBmi, inBodyDateList, null,bmiList,1);
            // 체지방률 그래프
            drawGraph(binding.graphBodyFatPercent, inBodyDateList, null,bodyFatPercentList,2);
            // 내장지방 그래프
            drawGraph(binding.graphFatLevel, inBodyDateList, fatLevelList, null,3);
            // 기초대사량 그래프
            drawGraph(binding.graphBasalMetabolicRate, inBodyDateList, null,basalMetabolicRateList,4);
        }
    }
}