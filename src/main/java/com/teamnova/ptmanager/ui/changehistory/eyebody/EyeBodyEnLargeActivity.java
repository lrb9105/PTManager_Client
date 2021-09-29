package com.teamnova.ptmanager.ui.changehistory.eyebody;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.teamnova.ptmanager.R;
import com.teamnova.ptmanager.databinding.ActivityEyeBodyChangeHistoryBinding;
import com.teamnova.ptmanager.databinding.ActivityEyeBodyEnLargeBinding;
import com.teamnova.ptmanager.model.changehistory.eyebody.EyeBody;
import com.teamnova.ptmanager.model.changehistory.inbody.InBody;
import com.teamnova.ptmanager.model.reservation.ReservationInfo;
import com.teamnova.ptmanager.network.RetrofitInstance;
import com.teamnova.ptmanager.network.changehistory.eyebody.EyeBodyService;
import com.teamnova.ptmanager.network.schedule.lesson.LessonService;
import com.teamnova.ptmanager.ui.changehistory.inbody.adapter.InBodyListAdapter;
import com.teamnova.ptmanager.ui.home.member.MemberHomeActivity;
import com.teamnova.ptmanager.ui.home.trainer.TrainerHomeActivity;
import com.teamnova.ptmanager.ui.member.MemberAddActivity;
import com.teamnova.ptmanager.util.DialogUtil;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Retrofit;

public class EyeBodyEnLargeActivity extends AppCompatActivity {
    private ActivityEyeBodyEnLargeBinding binding;
    private EyeBody eyeBody;
    private int parentPosition;
    private int childPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityEyeBodyEnLargeBinding.inflate(getLayoutInflater());

        // 기존에는 layout이 들어갔는데 대신 getRoot를 넣어준다.
        setContentView(binding.getRoot());

        super.onCreate(savedInstanceState);

        initialize();


    }

    /** 초기화 */
    public void initialize(){
        // 눈바디 정보 가져옴
        Intent intent = getIntent();
        eyeBody = (EyeBody)intent.getSerializableExtra("eyeBodyInfo");
        parentPosition = intent.getIntExtra("parentPosition",999999);
        childPosition = intent.getIntExtra("childPosition",999999);

        setImage();

        // 삭제버튼 클릭 시
        binding.deleteEyebodyImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 다이얼로그빌더
                AlertDialog.Builder builder = new AlertDialog.Builder(EyeBodyEnLargeActivity.this);

                builder.setMessage("해당 이미지를 삭제하세겠습니까?");

                // 취소버튼 클릭
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                // 확인버튼 클릭
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
                        /**
                         * 1. 서버에 삭제요청
                         * */
                        Retrofit retrofit= RetrofitInstance.getRetroClient();
                        EyeBodyService service = retrofit.create(EyeBodyService.class);

                        // http request 객체 생성
                        Call<String> call = service.deleteEyeBodyInfo(eyeBody.getEyeBodyChangeId());

                        new DeleteEyeBodyInfoCall().execute(call);
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }

    /** 클릭한 이미지 보여주기*/
    public void setImage(){
        Glide.with(this).load("http://15.165.144.216/" + eyeBody.getSavePath()).into(binding.imageView2);
    }

    /** 눈바디 정보 삭제하기 */
    private class DeleteEyeBodyInfoCall extends AsyncTask<Call, Void, String> {
        private retrofit2.Response<String> response;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("onPreExecute", "11");
        }

        @Override
        protected String  doInBackground(Call[] params) {
            try {
                Call<String> call = params[0];
                response = call.execute();

                return response.body();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            // 눈바디 정보를 EYEBODY_COMPARE 테이블에서 사용중이라면 - "CAN NOT DELETE"반환
            if(result.contains("CAN NOT DELETE")){
                new DialogUtil().msgDialog(EyeBodyEnLargeActivity.this,"눈바디 비교화면에서 사용중인 데이터는 삭제할 수 없습니다.");

            } else if(result.contains("DELETE FAIL")){
                // 눈바디정보 삭제를 실패했다면 - "DELETE FAIL"반환
                new DialogUtil().msgDialog(EyeBodyEnLargeActivity.this,"눈바디 데이터 삭제에 실패했습니다.");

            } else if(result.contains("DELETE SUCCESS")){
                // 눈바디 홈화면에서 넘어온 경우
                if(parentPosition == 999999){
                    // 눈바디 정보를 삭제했다면: id반환
                    Intent intent = new Intent(EyeBodyEnLargeActivity.this, MemberHomeActivity.class);
                    intent.putExtra("removedEyeBody", eyeBody);

                    setResult(Activity.RESULT_OK, intent);
                    finish();
                } else { // 눈바디 더보기 화면에서 넘어온 경우
                    // 눈바디 정보를 삭제했다면: id반환
                    Intent intent = new Intent(EyeBodyEnLargeActivity.this, EyeBodyChangeHistoryActivity.class);
                    intent.putExtra("removedEyeBody", eyeBody);
                    intent.putExtra("parentPosition", parentPosition);
                    intent.putExtra("childPosition", childPosition);

                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
            } else{
                Log.d("에러", result);
            }
        }
    }
}