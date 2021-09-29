package com.teamnova.ptmanager.ui.changehistory.inbody;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Telephony;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;

import com.teamnova.ptmanager.R;
import com.teamnova.ptmanager.databinding.ActivityInBodyModifyBinding;
import com.teamnova.ptmanager.databinding.ActivityRegisterInBodyBinding;
import com.teamnova.ptmanager.model.changehistory.inbody.InBody;
import com.teamnova.ptmanager.model.userInfo.FriendInfoDto;
import com.teamnova.ptmanager.network.RetrofitInstance;
import com.teamnova.ptmanager.network.changehistory.inbody.InBodyService;
import com.teamnova.ptmanager.ui.home.member.MemberHomeActivity;
import com.teamnova.ptmanager.util.GetDate;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Retrofit;

public class InBodyModifyActivity extends AppCompatActivity implements View.OnClickListener{
    private ActivityInBodyModifyBinding binding;
    /** 데이터 */
    private String dateToServer;
    private InBody inBody;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 해당 액티비티에서 사용할 xml파일에대한 클래스가 자동으로 생성되고 inflate함수를 이용해서 인스턴스를 생성한다.
        binding = ActivityInBodyModifyBinding.inflate(getLayoutInflater());

        // 기존에는 layout이 들어갔는데 대신 getRoot를 넣어준다.
        setContentView(binding.getRoot());

        initialize();
    }

    // 초기화
    public void initialize(){
        /** inBody객체 받아오기 */
        inBody = (InBody)getIntent().getSerializableExtra("inBody");

        /** onClickListener 등록 */
        setOnclickListener();

        /** 인바디 정보 뿌려주기 */
        setInBodyInfo(inBody);
    }

    // 온클릭리스너 등록
    public void setOnclickListener(){
        binding.btnInbodyModify.setOnClickListener(this);
        binding.btnBack.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {

        switch(v.getId()){
            case  R.id.btn_inbody_modify: // 인바디 수정완료 버튼
                /** 수정완료 버튼 클릭*/
                AlertDialog.Builder builder = new AlertDialog.Builder(InBodyModifyActivity.this);

                builder.setMessage("인바디정보를 수정하시겠습니까?");

                builder.setPositiveButton("확인", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {

                        Log.d("인바디 아이디: ", inBody.getInBodyId());

                        //인바디 id
                        inBody.setInBodyId(inBody.getInBodyId());

                        // 몸무게
                        inBody.setWeight(Float.parseFloat(binding.inbodyWeightInput.getText().toString()));
                        // 키
                        inBody.setHeight(Float.parseFloat(binding.inbodyHeightInput.getText().toString()));
                        // 체지방량
                        inBody.setBodyFat(Float.parseFloat(binding.inbodyBodyFatInput.getText().toString()));
                        // 근육량
                        inBody.setMuscleMass(Float.parseFloat(binding.inbodyMuscleMassInput.getText().toString()));
                        // 내장지방레벨
                        inBody.setFatLevel(Integer.parseInt(binding.inbbodyFatLevelInput.getText().toString()));

                        //인바디정보 등록
                        Retrofit retrofit= RetrofitInstance.getRetroClient();
                        InBodyService service = retrofit.create(InBodyService.class);


                        // http request 객체 생성
                        Call<InBody> call = service.modifyInBodyInfo(inBody);
                        //Call<String> call = service.modifyInBodyInfo(inBody);

                        new ModifyInBodyCall().execute(call);
                    }
                }).setNegativeButton("취소",new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int id)
                            { }
                        }
                );

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

    /** 가져온 인바디 정보 화면에 뿌려주기*/
    public void setInBodyInfo(InBody inBodyInfo){
        /** 검사일시 형태 변환 */
        String day = inBodyInfo.getCreDate();
        String todayYMDWithKorean = GetDate.getDateWithYMD(day);
        String today = GetDate.getDayOfWeek(day);

        binding.inbodyCheckDateInput.setText(todayYMDWithKorean + "(" + today + ")");
        binding.inbodyWeightInput.setText("" + Math.round(inBodyInfo.getWeight()*100)/100.0);
        binding.inbodyHeightInput.setText("" + inBodyInfo.getHeight());
        binding.inbodyBodyFatInput.setText("" + Math.round(inBodyInfo.getBodyFat()*100)/100.0);
        binding.inbodyMuscleMassInput.setText("" + Math.round(inBodyInfo.getMuscleMass()*100)/100.0);
        binding.inbbodyFatLevelInput.setText("" + inBodyInfo.getFatLevel());

    }

    /** 인바디 정보수정 */
    private class ModifyInBodyCall extends AsyncTask<Call, Void, String> {
        private retrofit2.Response<InBody> response;
        //private retrofit2.Response<String> response;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("onPreExecute", "11");
        }

        @Override
        protected String  doInBackground(Call[] params) {
            try {
                Call<InBody> call = params[0];
                //Call<String> call = params[0];
                response = call.execute();

                inBody = response.body();

               /* modifiedInBody = response.body();

                Log.d("인바디정보후: ", "" + response.body().getHeight());
                Log.d("인바디정보후: ", "" + response.body().getWeight());
                Log.d("인바디정보후: ", "" + response.body().getFatLevel());
                Log.d("인바디정보후: ", "" + response.body().getMuscleMass());*/

                return response.body().toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            // 인바디 홈으로 이동
            Intent intent = new Intent(InBodyModifyActivity.this, MemberHomeActivity.class);

            // 눈바디 정보리스트
            intent.putExtra("inBody", inBody);

            setResult(Activity.RESULT_OK, intent);
            finish();

        }
    }
}