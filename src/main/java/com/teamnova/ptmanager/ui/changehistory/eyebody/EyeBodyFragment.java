package com.teamnova.ptmanager.ui.changehistory.eyebody;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.teamnova.ptmanager.R;
import com.teamnova.ptmanager.databinding.FragmentEyeBodyBinding;
import com.teamnova.ptmanager.databinding.ItemEyebodyChangeBinding;
import com.teamnova.ptmanager.databinding.ItemEyebodyCompareBinding;
import com.teamnova.ptmanager.model.changehistory.eyebody.EyeBody;
import com.teamnova.ptmanager.model.changehistory.eyebody.EyeBodyCompare;
import com.teamnova.ptmanager.model.changehistory.eyebody.EyeBodyHistoryInfo;
import com.teamnova.ptmanager.model.userInfo.FriendInfoDto;
import com.teamnova.ptmanager.network.RetrofitInstance;
import com.teamnova.ptmanager.network.changehistory.eyebody.EyeBodyService;
import com.teamnova.ptmanager.ui.changehistory.eyebody.adapter.EyeBodyChangeHistoryAdapter;
import com.teamnova.ptmanager.ui.home.trainer.TrainerHomeActivity;
import com.teamnova.ptmanager.util.GetDate;
import com.teamnova.ptmanager.viewmodel.changehistory.eyebody.EyeBodyViewModel;
import com.teamnova.ptmanager.viewmodel.friend.FriendViewModel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;

/**
 * 눈바디 프래그먼트
 * */
public class EyeBodyFragment extends Fragment implements View.OnClickListener{
    private FragmentEyeBodyBinding binding;

    // 눈비디 비교 등록 시 넘어오는 데이터 처리
    private ActivityResultLauncher<Intent> startActivityResult;

    // 눈비디 데이터 등록 시
    private ActivityResultLauncher<Intent> startActivityResult2;

    // 눈비디 데이터 등록
    private ActivityResultLauncher<Intent> startActivityResult3;

    // 눈비디 데이터 삭제
    private ActivityResultLauncher<Intent> startActivityResult4;

    // 눈비디 비교 데이터 삭제
    private ActivityResultLauncher<Intent> startActivityResult5;

    // 트레이너 정보를 가져오기 위한 viewModel
    private FriendViewModel friendViewModel;

    // 눈바디 정보를 다루기 위한 viewModel
    private EyeBodyViewModel eyeBodyViewModel;

    // 회원 정보
    private FriendInfoDto memberInfo;

    // 눈바디 변화기록 리스트
    private ArrayList<EyeBodyHistoryInfo> eyeBodyHistoryList;

    // 눈바디 변화기록
    private ArrayList<EyeBody> eyeBodyList;

    // 9개의 변화기록 imageView를 관리하기 위한 배열
    private ArrayList<ItemEyebodyChangeBinding> viewList;

    //눈바디 비교데이터 배열(그룹별) 리스트
    private ArrayList<EyeBodyCompare[]> eyeBodyCompareArrList;

    // 서버에서 가져온 눈바디 비교 리스트 - 연산을 위해 잠시 사용한다.
    private ArrayList<EyeBodyCompare> tempList;

    // 추가한 눈바디 비교정보
    private EyeBodyCompare[] addedEyeBodyCompareArr;

    // Retrofit 통신의 결과를 전달하는 핸들러
    private Handler resultHandler;

    public EyeBodyFragment() {}


    // TODO: Rename and change types and number of parameters
    public static EyeBodyFragment newInstance(String param1, String param2) {
        EyeBodyFragment fragment = new EyeBodyFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        resultHandler = new Handler(Looper.myLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                // 눈바디 저장 성공 성공
                if(msg.what == 0){
                    EyeBody addedEyeBody = (EyeBody)msg.obj;

                    if(eyeBodyList == null){
                        eyeBodyList = new ArrayList<>();
                    }
                    eyeBodyList.add(0,addedEyeBody);

                    eyeBodyViewModel.getEyeBodyList().setValue(eyeBodyList);
                }
            }
        };

        // 눈바디 비교 데이터 등록, 삭제 시
        startActivityResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Log.d("여길 들어오나11?","11");
                            int deletedEyeBodyCompareInfoPosition = result.getData().getIntExtra("deletedEyeBodyCompareInfoPosition", 99999);
                            addedEyeBodyCompareArr = (EyeBodyCompare[])result.getData().getSerializableExtra("addedEyeBodyCompareArr");

                            // 데이터를 삭제했다면
                            if(addedEyeBodyCompareArr == null){
                                Log.d("여길 들어오나22?","2");
                                Log.d("첫번째 데이터 삭제", "11");
                                eyeBodyCompareArrList.remove(deletedEyeBodyCompareInfoPosition);

                            } else { //눈바디 비교데이터 추가
                                Log.d("여길 들어오나33?","33");
                                Log.d("EyeFragment commonId:", addedEyeBodyCompareArr[0].getCommonCompareId());
                                eyeBodyCompareArrList.add(0,addedEyeBodyCompareArr);
                            }

                            eyeBodyViewModel.getEyeBodyCompareList().setValue(eyeBodyCompareArrList);
                        }
                    }
                });

        // 눈바디 변화기록 화면에서 데이터 등록 시
        startActivityResult2 = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Log.d("눈바디 변화 데이터 등록", "11");
                            /**
                             * 눈바디 변화 기록 리스트를 받아서 viewModel에 넣어준다.
                             * */
                            //eyeBodyHistoryList = (ArrayList<EyeBodyHistoryInfo>)result.getData().getSerializableExtra("eyeBodyHistoryList");
                            //eyeBodyViewModel.geteyeBodyHistoryList().setValue(eyeBodyHistoryList);

                            // 추가한 EyeBody객체
                            EyeBody addedEyeBody = (EyeBody)result.getData().getSerializableExtra("addedEyeBody");
                            // 삭제한 눈바디 데이터
                            EyeBody removedEyeBody = (EyeBody)result.getData().getSerializableExtra("removedEyeBody");

                            if(addedEyeBody != null){ //눈바디데이터를 추가했다면
                                if(eyeBodyList == null){
                                    eyeBodyList = new ArrayList<>();
                                }
                                eyeBodyList.add(0,addedEyeBody);

                                eyeBodyViewModel.getEyeBodyList().setValue(eyeBodyList);
                            } else if(removedEyeBody != null){ // 눈바디 데이터를 삭제했다면
                                for(int i = 0; i < eyeBodyList.size(); i++){
                                    // 리스트의 해당 인덱스의 데이터가 삭제된 데이터라면
                                    if(eyeBodyList.get(i).getEyeBodyChangeId().equals(removedEyeBody.getEyeBodyChangeId())){
                                        eyeBodyList.remove(i);
                                        eyeBodyViewModel.getEyeBodyList().setValue(eyeBodyList);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                });

        startActivityResult3 = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            //  눈바디 데이터 가져오기
                            String filePath = result.getData().getStringExtra("eyeBodyPath");

                            // 서버로 전송하기 위한 MultipartData 만들기
                            Bitmap selectedImage = BitmapFactory.decodeFile(filePath);
                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            // 사진을 압축
                            selectedImage.compress(Bitmap.CompressFormat.JPEG, 20, byteArrayOutputStream);

                            RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpg"), byteArrayOutputStream.toByteArray());
                            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

                            MultipartBody.Part uploadFile = MultipartBody.Part.createFormData("eyeBodyFileImg", "JPEG_" + timeStamp + "_.jpg" ,requestBody);

                            // 사용자 userId
                            String userId = memberInfo.getUserId();

                            RequestBody userIdReq = RequestBody.create(MediaType.parse("text/plain"),userId);

                            // 눈바디 정보 저장하기
                            eyeBodyViewModel.registerEyeBodyInfo(resultHandler, userIdReq, uploadFile);
                        }
                    }
                });

        // 눈바디 데이터 삭제
        startActivityResult4 = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // 삭제한 눈바디 정보를 가져옴
                            EyeBody removedEyeBody = (EyeBody)result.getData().getSerializableExtra("removedEyeBody");

                            for(int i = 0; i < eyeBodyList.size(); i++){
                                // 리스트의 해당 인덱스의 데이터가 삭제된 데이터라면
                                if(eyeBodyList.get(i).getEyeBodyChangeId().equals(removedEyeBody.getEyeBodyChangeId())){
                                    eyeBodyList.remove(i);
                                    eyeBodyViewModel.getEyeBodyList().setValue(eyeBodyList);
                                    break;
                                }
                            }
                        }
                    }
                });

        // 눈바디 비교 데이터 삭제
        startActivityResult5 = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            eyeBodyCompareArrList.remove(0);
                            eyeBodyViewModel.getEyeBodyCompareList().setValue(eyeBodyCompareArrList);
                        }
                    }
                });

        super.onCreate(savedInstanceState);

        // 뷰모델 초기화
        friendViewModel = new ViewModelProvider(requireActivity()).get(FriendViewModel.class);
        eyeBodyViewModel = new ViewModelProvider(requireActivity()).get(EyeBodyViewModel.class);

        // 로그인한 회원 정보 observe
        friendViewModel.getFriendInfo().observe(requireActivity(), memberInfo -> {
            this.memberInfo = memberInfo;
        });

        // 비교데이터 가져오기 - Async
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

       // View view = inflater.inflate(R.layout.fragment_eye_body, container, false);

        binding = FragmentEyeBodyBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // 회원의 경우
        if(TrainerHomeActivity.staticLoginUserInfo == null){
            binding.eyebodyChangeLayout1.image.setImageDrawable(getContext().getDrawable(R.drawable.camera));
            binding.eyebodyChangeLayout1.text.setText("");

            binding.eyebodyChangeLayout1.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivityResult3.launch(new Intent(requireActivity(), EyeBodySaveActivity.class));
                }
            });
        }

        // 레이아웃 초기화
        viewList = new ArrayList<>();

        viewList.add(binding.eyebodyChangeLayout1);
        viewList.add(binding.eyebodyChangeLayout2);
        viewList.add(binding.eyebodyChangeLayout3);
        viewList.add(binding.eyebodyChangeLayout4);
        viewList.add(binding.eyebodyChangeLayout5);
        viewList.add(binding.eyebodyChangeLayout6);
        viewList.add(binding.eyebodyChangeLayout7);
        viewList.add(binding.eyebodyChangeLayout8);
        viewList.add(binding.eyebodyChangeLayout9);

        setOnclickListener();

        // 눈바디 변화 리스트(9장)가 변경 됐을 때
        eyeBodyViewModel.getEyeBodyList().observe(requireActivity(), eyeBodyList -> {
            // if(isAddedEyeBodyInfo) -> 추가 혹은 삭제된 경우 모두 기본이미지로 변경(삭제된 경우 그대로 남아있을 수 있으므로 - 이전에 8개, 지금 7개일 때 삭제 된 경우 8번째가 그대로 나오게 된다).
            // 회원
            if(TrainerHomeActivity.staticLoginUserInfo == null){
                int length = 8;

                if(length >= eyeBodyList.size()){
                    length = eyeBodyList.size();
                }

                // 초기화
                for(int i = 0; i < length; i++){
                    viewList.get(i+1).text.setText("");
                    viewList.get(i+1).image.setImageDrawable(getContext().getDrawable(R.drawable.album));
                }

                for(int i = 0; i < length; i++){
                    // 사진 등록 날짜
                    viewList.get(i+1).text.setText(GetDate.getDateWithYMDAndDot(eyeBodyList.get(i).getCreDate()));
                    // 사진이미지
                    Glide.with(getContext()).load("http://15.165.144.216/" + eyeBodyList.get(i).getSavePath()).into(viewList.get(i+1).image);

                    int finalI = i;
                    viewList.get(i+1).image.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(requireActivity(), EyeBodyEnLargeActivity.class);
                            intent.putExtra("eyeBodyInfo", eyeBodyList.get(finalI));
                            startActivityResult4.launch(intent);
                        }
                    });
                }
            } else{
                int length = 9;

                if(length >= eyeBodyList.size()){
                    length = eyeBodyList.size();
                }

                // 초기화
                for(int i = 0; i < length; i++){
                    viewList.get(i).text.setText("");
                    viewList.get(i).image.setImageDrawable(getContext().getDrawable(R.drawable.album));
                }

                for(int i = 0; i < length; i++){
                    // 사진 등록 날짜
                    viewList.get(i).text.setText(GetDate.getDateWithYMDAndDot(eyeBodyList.get(i).getCreDate()));
                    // 사진이미지
                    Glide.with(getContext()).load("http://15.165.144.216/" + eyeBodyList.get(i).getSavePath()).into(viewList.get(i).image);

                    int finalI = i;
                    viewList.get(i).image.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(requireActivity(), EyeBodyEnLargeActivity.class);
                            intent.putExtra("savePath", eyeBodyList.get(finalI).getSavePath());
                            startActivity(intent);
                        }
                    });
                }
            }

        });

        // 눈바디 비교 리스트가 변경 됐을 때
        eyeBodyViewModel.getEyeBodyCompareList().observe(requireActivity(), eyeBodyCompareList -> {
            // 가장 최신 데이터를 보여줌
            ItemEyebodyCompareBinding eyeBodyCompareLayout = binding.eyebodyCompareLayout;

            // 가장 최신 데이터
            EyeBodyCompare[] eyebodyArr = eyeBodyCompareList.get(0);

            Log.d("날짜 비교 전 데이터1:", eyebodyArr[0].getCreDate());
            Log.d("날짜 비교 전 데이터2:", eyebodyArr[1].getCreDate());

            // 앞 순서의 날짜가 더 크다면
            if(GetDate.getDayDifference(eyebodyArr[0].getCreDate(),eyebodyArr[1].getCreDate()) < 0) {
                EyeBodyCompare tempObj = eyebodyArr[0];
                eyebodyArr[0] = eyebodyArr[1];
                eyebodyArr[1] = tempObj;
            }

            Log.d("날짜 비교 후 데이터1:", eyebodyArr[0].getCreDate());
            Log.d("날짜 비교 후 데이터2:", eyebodyArr[1].getCreDate());

            eyeBodyCompareLayout.dayDifference.setText("" + Math.abs((int)GetDate.getDayDifference(eyebodyArr[0].getCreDate(), eyebodyArr[1].getCreDate())) + "일간의 변화");

            // 날짜
            eyeBodyCompareLayout.text.setText(GetDate.getDateWithYMDAndDot(eyebodyArr[0].getCreDate()));
            eyeBodyCompareLayout.text2.setText(GetDate.getDateWithYMDAndDot(eyebodyArr[1].getCreDate()));

            // 이미지
            Glide.with(getContext()).load("http://15.165.144.216/" + eyebodyArr[0].getSavePath()).into(eyeBodyCompareLayout.image);
            Glide.with(getContext()).load("http://15.165.144.216/" + eyebodyArr[1].getSavePath()).into(eyeBodyCompareLayout.image2);
        });

        // 눈바디 비교데이터 가져오기
        if(eyeBodyCompareArrList == null){
            eyeBodyCompareArrList = new ArrayList<>();

            // 변화데이터 가져오기 - Async
            Retrofit retrofit= RetrofitInstance.getRetroClient();
            EyeBodyService service = retrofit.create(EyeBodyService.class);

            // http request 객체 생성
            Call<ArrayList<EyeBodyCompare>> call = service.getEyeBodyCompareList(memberInfo.getUserId());

            new GetEyeBodyCompareList().execute(call);
        }

        // 눈바디 변화 데이터가 없다면
        if(eyeBodyList == null){
            // 변화데이터 가져오기 - Async
            Retrofit retrofit= RetrofitInstance.getRetroClient();
            EyeBodyService service = retrofit.create(EyeBodyService.class);

            // http request 객체 생성
            Call<ArrayList<EyeBody>> call = service.getEyeBodyList(memberInfo.getUserId());

            new NetworkCall().execute(call);
        }

        return view;
    }

    /**
     * OnclickListener 등록
     * */
    public void setOnclickListener(){
        binding.eyebodyCompareMore.setOnClickListener(this);
        binding.eyebodyChangeMore.setOnClickListener(this);
        binding.eyebodyCompareLayout.layoutEyebodyCompare.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.eyebody_compare_more: // 눈바디 비교기록
                Intent intent = new Intent(getActivity(), EyeBodyCompareHistoryActivity.class);

                intent.putExtra("memberInfo", memberInfo);
                intent.putExtra("eyeBodyCompareArrList", eyeBodyCompareArrList);
                intent.putExtra("eyeBodyList", eyeBodyList);

                startActivityResult.launch(intent);
                break;

            case R.id.eyebody_change_more: // 눈바디 변화기록
                intent = new Intent(getActivity(), EyeBodyChangeHistoryActivity.class);
                intent.putExtra("memberInfo", memberInfo);
                intent.putExtra("eyeBodyList", eyeBodyList);
                startActivityResult2.launch(intent);
                break;
            case R.id.eyebody_compare_layout: // 눈바디 비교기록 확대
                intent = new Intent(getActivity(), EyeBodyCompareEnLargeActivity.class);
                intent.putExtra("eyeBodyCompareArr", eyeBodyCompareArrList.get(0));

                Log.d("데이터 넘어오나1", eyeBodyCompareArrList.get(0)[0].getSavePath());

                startActivityResult5.launch(intent);
                break;
        }
    }

    // 눈바디 정보 가져오기
    private class NetworkCall extends AsyncTask<Call, Void, String> {
        private retrofit2.Response<ArrayList<EyeBody>> response;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("onPreExecute", "11");
        }

        @Override
        protected String  doInBackground(Call[] params) {
            try {
                retrofit2.Call<ArrayList<EyeBody>> call = params[0];
                response = call.execute();

                if(response.body() != null){
                    eyeBodyList = response.body();
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
                // 눈바디 정보 세팅
                eyeBodyViewModel.getEyeBodyList().setValue(eyeBodyList);
            }
        }
    }

    // 눈바디 비교 정보 가져오기
    private class GetEyeBodyCompareList extends AsyncTask<Call, Void, String> {
        private retrofit2.Response<ArrayList<EyeBodyCompare>> response;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("onPreExecute", "11");
        }

        @Override
        protected String  doInBackground(Call[] params) {
            try {
                retrofit2.Call<ArrayList<EyeBodyCompare>> call = params[0];
                response = call.execute();

                if(response.body() != null){
                    // 서버에서 가져온 눈바디비교 정보 리스트
                    tempList = response.body();
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
                // 데이터 가공
                for(int i = 0; i < tempList.size(); i++){
                    EyeBodyCompare tempEyeBodyCompare = tempList.get(i);
                    // 0보다 크다면
                    if(i > 0){
                        String previousCommonId = tempList.get(i-1).getCommonCompareId();

                        // 앞의 데이터와 공통 아이디가 동일하지 않다면
                        if(!previousCommonId.equals(tempEyeBodyCompare.getCommonCompareId())){
                            EyeBodyCompare[] eyeBodyCompareArr = new EyeBodyCompare[2];
                            eyeBodyCompareArr[0] = tempEyeBodyCompare;

                            eyeBodyCompareArrList.add(eyeBodyCompareArr);
                        } else{ //동일하다면
                            EyeBodyCompare[] temp = eyeBodyCompareArrList.get((i-1)/2);
                            temp[1] = tempEyeBodyCompare;

                            // 앞 순서의 날짜가 더 크다면
                            if(GetDate.getDayDifference(temp[0].getCreDate(),temp[1].getCreDate()) < 0) {
                                EyeBodyCompare tempObj = temp[0];
                                temp[0] = temp[1];
                                temp[1] = tempObj;
                            }

                            eyeBodyCompareArrList.set((i-1)/2,temp);
                        }
                    } else{ // 0이라면
                        EyeBodyCompare[] eyeBodyCompareArr = new EyeBodyCompare[2];
                        eyeBodyCompareArr[0] = tempEyeBodyCompare;

                        eyeBodyCompareArrList.add(eyeBodyCompareArr);
                    }
                }

                // viewModel에서 관리
                eyeBodyViewModel.getEyeBodyCompareList().setValue(eyeBodyCompareArrList);

                Log.d("눈바디 비교 데이터 가공결과: ", "" + eyeBodyCompareArrList.size());
            }
        }
    }
}