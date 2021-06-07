package com.teamnova.ptmanager.ui.login.findpw;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.ViewModelProvider;

import com.teamnova.ptmanager.R;
import com.teamnova.ptmanager.databinding.ActivityFindPw2Binding;
import com.teamnova.ptmanager.ui.login.findid.FindIdActivity;
import com.teamnova.ptmanager.viewmodel.sms.SmsViewModel;

public class FindPw2Activity extends AppCompatActivity implements View.OnClickListener{
    private ActivityFindPw2Binding binding;

    // smsViewModel
    private SmsViewModel smsViewModel;

    // Retrofit 통신의 결과를 전달하는 핸들러
    private Handler smsResultHandler;

    //타이머
    private Handler handler;
    private int minute;
    private int sec;
    private TimerThread t;

    // 로그인아이디
    private String loginId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 해당 액티비티에서 사용할 xml파일에대한 클래스가 자동으로 생성되고 inflate함수를 이용해서 인스턴스를 생성한다ㅣ.
        binding = ActivityFindPw2Binding.inflate(getLayoutInflater());

        // 기존에는 layout이 들어갔는데 대신 getRoot를 넣어준다.
        setContentView(binding.getRoot());

        initialize();
    }

    /**
     * 컴포넌트 등록
     * */

    /**
     *  1. 역할: 컴포넌트 초기화
     * */
    public void initialize(){
        Intent intent = getIntent();
        loginId = intent.getStringExtra("loginId").replace("\"","");

        binding.id.setText(loginId.replace("\"",""));

        // 뷰모델 초기화
        smsViewModel = new ViewModelProvider(this).get(SmsViewModel.class);

        binding.btnCertification.setOnClickListener(this);
        binding.btnConfirm.setOnClickListener(this);
        binding.btnBack.setOnClickListener(this);

        //타이머 핸들러
        handler = new Handler(Looper.myLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                // 타이머 쓰레드에서 1초마다 메세지를 전송하면 그때마다 시간을 1초씩 줄인다.
                if(msg.what == 0){
                    sec--;

                    if(minute != 0 && sec == 0){
                        minute--;
                        sec = 60;
                    }
                    if(sec >=0){
                        binding.timer.setText("0" + minute + ":" + (sec < 10?"0" + sec : sec));
                    }

                    if(minute == 0 && sec == 0){
                        handler.removeMessages(0);
                        t.setRunning(false);
                        return;
                    }
                }
            }
        };

        // Retrofit 통신 핸들러
        smsResultHandler = new Handler(Looper.myLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                // Sms 문자전송 결과
                if(msg.what == 0){
                    Log.d("문자전송결과: ", (String) msg.obj);

                    if(((String) msg.obj).contains("success")){
                        binding.certificationSend.setVisibility(View.VISIBLE);

                        /**
                         * 타이머 쓰레드 작동
                         * */
                        // 시간 5분세팅
                        binding.timer.setText("05:00");

                        minute = 4;
                        sec = 60;
                        // 기존에 실행되고 있는 스레드가 있다면 종료
                        if(t != null && (t.getState() == Thread.State.RUNNABLE || t.getState() == Thread.State.TIMED_WAITING)){
                            t.setRunning(false);
                        }

                        t = new TimerThread(handler);
                        t.start();

                        Log.d("완료", "11");
                    } else{
                        binding.certificationSend.setText("인증번호 발송에 실패했습니다.");
                        binding.certificationSend.setTextColor(ResourcesCompat.getColor(getResources(),R.color.red,null));
                        binding.certificationSend.setVisibility(View.VISIBLE);
                        Log.d("안완료: ", "22");
                    }
                } else if(msg.what == 1){ //인증결과
                    Log.d("인증결과: ", (String) msg.obj);

                    if(((String) msg.obj).contains("success")){
                        // 인증완료 문구 출력
                        binding.certificationCompl.setVisibility(View.VISIBLE);

                        // 쓰레드 종료
                        t.setRunning(false);

                        // 문자인증 관련 View들 disable
                        binding.phoneNum.setEnabled(false);
                        binding.btnCertification.setEnabled(false);
                        binding.certificationNum.setEnabled(false);
                        binding.btnConfirm.setEnabled(false);
                        binding.certificationSend.setVisibility(View.GONE);
                        binding.certificationCompl.setVisibility(View.GONE);

                        Intent intent = new Intent(FindPw2Activity.this, FindPw3Activity.class);
                        intent.putExtra("loginId",loginId);

                        startActivity(intent);

                        Log.d("인증 완료", "11");

                    } else{
                        binding.certificationCompl.setText("인증번호가 일치하지 않습니다.");
                        binding.certificationCompl.setTextColor(ResourcesCompat.getColor(getResources(),R.color.red,null));

                        Log.d("인증 안완료: ", "22");
                    }
                }
            }
        };

        binding.btnNext2.setOnClickListener(this);
    }


    /**
     * 이벤트 처리
     * */

    /**
     *  1. 이벤트 종류: onClick
     *  2. 역할: 트레이너/일반회원 버튼 클릭 시 발생하는 이벤트 정의
     *  3. 종류
     *      1) R.id.btn_login: //로그인버튼
     *          1> id/pw 유효성체크
     *          2> 입력한 id에 해당하는 pw 서버에서 가져와서 입력한 pw와 동일한지 확인 후 로그인
     *          3> HomeActivity로 이동
     *      2) R.id.textView_register: //회원가입
     *          1> RegisterActivity로 이동
     *      3) R.id.textView_find_id_pw: //아이디비밀번호찾기 링크
     *          1> FindIdAndPwActivity로 이동
     * */
    @Override
    public void onClick(View v) {
        Intent intent;

        switch(v.getId()){
            case R.id.btn_certification: //인증번호 받기 버튼 클릭
                /**
                 * 서버에 문자발송 요청
                 * */
                smsViewModel.smsSendReq(smsResultHandler, loginId, null, binding.phoneNum.getText().toString());
                break;
            case  R.id.btn_next2: // 인증요청
                if(minute != 0 && sec != 0){
                    String phoneNum2 = binding.phoneNum.getText().toString();
                    String certificationNum = binding.certificationNum.getText().toString();

                    if(!certificationNum.equals("") && !phoneNum2.equals("")){
                        smsViewModel.smsCertificationReq(smsResultHandler, phoneNum2, certificationNum);
                    }
                } else{
                    binding.certificationCompl.setText("인증 가능시간이 지났습니다. 인증번호를 다시 받으세요");
                    binding.certificationCompl.setTextColor(ResourcesCompat.getColor(getResources(),R.color.red,null));
                }
                break;
            case R.id.btn_back: //뒤로가기
                onBackPressed();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(t != null){
            t.setRunning(false);
        }
    }

    /**
     * 타이머 쓰레드
     * 역할: 1초마다 다른 쓰레드에 메시지를 전달.
     * */
    class TimerThread extends Thread {
        private Handler handler;
        private boolean isRunning = true;

        public TimerThread(Handler handler){
            this.handler = handler;
        }

        @Override
        public void run() {
            // 실행중 상태일 때 1초마다 handler에게 메시지 전달
            while(isRunning){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                handler.sendEmptyMessage(0);
            }
        }

        public void setRunning(boolean running) {
            isRunning = running;
        }
    }
}