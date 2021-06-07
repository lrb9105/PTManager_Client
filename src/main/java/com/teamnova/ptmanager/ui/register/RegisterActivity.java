package com.teamnova.ptmanager.ui.register;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.teamnova.ptmanager.R;
import com.teamnova.ptmanager.databinding.ActivityRegisterBinding;
import com.teamnova.ptmanager.test.TestActivity;
import com.teamnova.ptmanager.test.TestViewModel;
import com.teamnova.ptmanager.ui.login.LoginActivity;
import com.teamnova.ptmanager.model.userInfo.UserInfoDto;
import com.teamnova.ptmanager.network.register.RegisterService;
import com.teamnova.ptmanager.ui.login.findpw.FindPw3Activity;
import com.teamnova.ptmanager.util.Sha256;
import com.teamnova.ptmanager.viewmodel.login.LoginViewModel;
import com.teamnova.ptmanager.viewmodel.login.ValidationCheckingViewModel;
import com.teamnova.ptmanager.viewmodel.register.RegisterViewModel;
import com.teamnova.ptmanager.viewmodel.sms.SmsViewModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 1. 클래스명: RegisterActivity
 2. 역할: 일반 회원가입 기능 제공
 */
public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    // 해당 액티비티에서 사용할 Binder
    private ActivityRegisterBinding binding;

    // registerViewModel
    private RegisterViewModel registerViewModel;

    // smsViewModel
    private SmsViewModel smsViewModel;

    // smsViewModel
    private ValidationCheckingViewModel validationCheckingViewModel;

    // loginViewModel
    private LoginViewModel loginViewModel;

    //타이머
    private Handler handler;
    private int minute;
    private int sec;
    private TimerThread t;

    // Retrofit 통신의 결과를 전달하는 핸들러
    private Handler resultHandler;

    // 유저타입 - 0: 트레이너, 1: 일반회원
    private int userType;

    // 생년월일
    private String birthDayForSave;
    
    // 사용할 변수
    private EditText name;
    private EditText id;
    private EditText pw;
    private EditText pwConfirm;
    private TextView birthDay;
    private EditText branchOffice;
    private EditText phoneNum;
    private EditText certificationNum;

    // 사용자 정보
    private UserInfoDto userInfoDto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 해당 액티비티에서 사용할 xml파일에대한 클래스가 자동으로 생성되고 inflate함수를 이용해서 인스턴스를 생성한다ㅣ.
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());

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
        // ViewModel 인스턴스 생성
        registerViewModel = new ViewModelProvider(this).get(RegisterViewModel.class);

        smsViewModel = new ViewModelProvider(this).get(SmsViewModel.class);

        validationCheckingViewModel = new ViewModelProvider(this).get(ValidationCheckingViewModel.class);

        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        // 사용할 변수
        name = binding.editTextName;
        id = binding.editTextTextId;
        pw = binding.editTextPw;
        pwConfirm = binding.editTextPwConfirm;
        birthDay = binding.editTextBirthDay;
        branchOffice = binding.editTextBranchOffice;
        phoneNum = binding.phoneNum;
        certificationNum = binding.certificationNum;

        // 온클릭 리스너 등록
        binding.btnTrainer.setOnClickListener(this);
        binding.btnIndividual.setOnClickListener(this);
        binding.btnRegisterComplete.setOnClickListener(this);
        birthDay.setOnClickListener(this);
        binding.btnCertification.setOnClickListener(this);
        binding.btnConfirm.setOnClickListener(this);
        binding.checkboxAllTerms.setOnClickListener(this);
        binding.checkboxPrivateInfo.setOnClickListener(this);
        binding.checkboxUsingTerms.setOnClickListener(this);
        binding.textViewPrivateInfo.setOnClickListener(this);
        binding.textViewUsingTerms.setOnClickListener(this);
        binding.btnBack.setOnClickListener(this);

        // 데이터 입력 이벤트
        onDataChanged();

        // 포커스 변경 이벤트
        onFocusChange();

        // 처음에 선택 되도록
        binding.btnIndividual.performClick();

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
        resultHandler = new Handler(Looper.myLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                // Sms 문자전송 결과
                if(msg.what == 0){
                    Log.d("문자전송결과: ", (String) msg.obj);

                    if(((String) msg.obj).contains("success")){
                        binding.certificationSend.setVisibility(View.VISIBLE);
                        binding.certificationSend.setText("인증번호를 발송했습니다.");
                        binding.certificationSend.setTextColor(ResourcesCompat.getColor(getResources(),R.color.green,null));

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
                    } else{
                        binding.certificationSend.setVisibility(View.VISIBLE);
                        binding.certificationSend.setText("이미 사용중인 전화번호입니다.");
                        binding.certificationSend.setTextColor(ResourcesCompat.getColor(getResources(),R.color.red,null));
                    }
                } else if(msg.what == 1){ //인증결과
                    Log.d("인증결과: ", (String) msg.obj);

                    if(((String) msg.obj).contains("success")){
                        // 인증완료 문구 출력
                        binding.certificationCompl.setVisibility(View.VISIBLE);
                        binding.certificationCompl.setText("인증에 성공했습니다.");
                        binding.certificationCompl.setTextColor(ResourcesCompat.getColor(getResources(),R.color.green,null));


                        // 쓰레드 종료
                        t.setRunning(false);

                        // 문자인증 관련 View들 disable
                        phoneNum.setEnabled(false);
                        binding.btnCertification.setEnabled(false);
                        certificationNum.setEnabled(false);
                        binding.btnConfirm.setEnabled(false);
                        binding.certificationSend.setVisibility(View.GONE);
                        validationCheckingViewModel.setValidPhoneNum(true);

                        Log.d("인증 완료", "11");

                        // 모든 유효성 검증이 완료 됐다면 버튼 활성화
                        if(validationCheckingViewModel.isValidationCompleted()){
                            binding.btnRegisterComplete.setEnabled(true);
                        }
                    } else{
                        validationCheckingViewModel.setValidPhoneNum(false);
                        binding.certificationCompl.setVisibility(View.VISIBLE);
                        binding.certificationCompl.setText("인증에 실패했습니다. 인증번호를 확인하세요");
                        binding.certificationCompl.setTextColor(ResourcesCompat.getColor(getResources(),R.color.red,null));
                    }
                } else if(msg.what == 2){ //아이디 중복체크
                    String loginId = (String)msg.obj;
                    // debug: db에 아이디가 없을 때 뭐라도 가져오는지 체크
                    Log.d("핸들러를 통해 뭐라도 가져옴",loginId);

                    Log.d("아이디중복확인: ", loginId);

                    /**
                     * 사용중인 아이디가 아니라면
                     * 1. 문구변경
                     * 2. 텍스트 색상변경
                     * 3. 유효성검증 완료
                     * */
                    if(loginId.contains("null")){
                        // debug:
                        Log.d("loginId.contains(null)","111");

                        binding.textViewRequirement1.setText("사용가능한 아이디입니다.");
                        binding.textViewRequirement1.setTextColor(ResourcesCompat.getColor(getResources(),R.color.green,null));
                        binding.textViewRequirement1.setVisibility(View.VISIBLE);

                        validationCheckingViewModel.setValidId(true);
                    } else{
                        // debug:
                        Log.d("loginId.contains(loginId)","111");

                        binding.textViewRequirement1.setText("이미 사용중인 아이디입니다.");
                        binding.textViewRequirement1.setTextColor(ResourcesCompat.getColor(getResources(),R.color.red,null));
                        binding.textViewRequirement1.setVisibility(View.VISIBLE);

                        validationCheckingViewModel.setValidId(false);
                    }
                }
            }
        };

        // 관찰로직
        observe();
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
        switch(v.getId()){
            case  R.id.btn_trainer: //트레이너 버튼
                /**
                 * 1. 트레이너버튼 파란색 변경
                 * 2. 일반회원버튼 회색 변경
                 * 3. 지점 로우(지점명 텍스트박스 및 찾기 버튼) 활성화
                 * 4. 생년월일 로우(생년월일 텍스트박스 및 남,녀 버튼) 비활성화
                 * 5. 생년월일 유효성체크 true
                 * 6. 생년월일 null로 변경
                 * */
                binding.btnTrainer.setBackgroundColor(ResourcesCompat.getColor(getResources(),R.color.blue,null));
                binding.btnIndividual.setBackgroundColor(ResourcesCompat.getColor(getResources(),R.color.white,null));

                binding.rowBranchOffice.setVisibility(View.VISIBLE);

                binding.rowBirthDay.setVisibility(View.GONE);
                binding.textViewRequirement6.setVisibility(View.GONE);

                validationCheckingViewModel.checkIsValidBirthday();

                binding.editTextBirthDay.setText("");

                userType = 0;

                break;
            case  R.id.btn_individual: //일반회원 버튼
                /**
                 * 1. 일반회원버튼 파란색 변경
                 * 2. 트레이너버튼 회색 변경
                 * 3. 생년월일 로우(생년월일 텍스트박스 및 남,녀 버튼) 활성화
                 * 4. 지점 로우(지점명 텍스트박스 및 찾기 버튼) 비활성화
                 * 5. 생년월일 유효성 검증 초기화(false)
                 * */

                binding.btnIndividual.setBackgroundColor(ResourcesCompat.getColor(getResources(),R.color.blue,null));
                binding.btnTrainer.setBackgroundColor(ResourcesCompat.getColor(getResources(),R.color.white,null));

                binding.rowBirthDay.setVisibility(View.VISIBLE);
                binding.textViewRequirement6.setVisibility(View.VISIBLE);

                binding.rowBranchOffice.setVisibility(View.GONE);

                userType = 1;

                validationCheckingViewModel.setValidBirthday(false);
                break;
            case R.id.editText_birth_day: //생년월일 버튼
                /**
                 * 1. datePicker
                 * */
                DatePickerDialog dialog = new DatePickerDialog(this);

                // 생년월일 설정 완료 시
                dialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String birthDayForDisplay = year + "-" + (month <10 ? "0"+(month+1) : (month+1)) + "-" + (dayOfMonth <10 ? "0" + dayOfMonth : dayOfMonth);
                        birthDayForSave = year + "" + (month <10 ? "0"+(month+1) : (month+1)) + (dayOfMonth <10 ? "0" + dayOfMonth : dayOfMonth);

                        // 화면에 출력
                        birthDay.setText(birthDayForDisplay);

                        validationCheckingViewModel.checkIsValidBirthday();

                        binding.textViewRequirement6.setVisibility(View.GONE);

                        // 모든 유효성 검증이 완료 됐다면 버튼 활성화
                        if(validationCheckingViewModel.isValidationCompleted()){
                            binding.btnRegisterComplete.setEnabled(true);
                        }
                    }
                });

                dialog.show();

                break;
            case R.id.btn_registerComplete:
                /**
                 * 1. 회원가입 완료
                 * */

                // 유저정보
                String loginId = id.getText().toString();
                String pw2 = Sha256.encrypt(pw.getText().toString());
                String userName = name.getText().toString();
                String phoneNum2 = phoneNum.getText().toString();
                String branchOffice2 = branchOffice.getText().toString();
                String birth = birthDayForSave;

                RadioButton selectedBtn = findViewById(binding.radioGroup1.getCheckedRadioButtonId());
                String selectedGenderVal = selectedBtn.getText().toString();

                int gender = selectedGenderVal.equals("남자") ? 0 : 1;

                // 유저정보 객체 생성
                userInfoDto = registerViewModel.makeRegisterInfo(userType, loginId, pw2, userName, phoneNum2, branchOffice2, birth, gender);

                // 유저정보 서버에 전달
                registerViewModel.registerUserInfo(userInfoDto);
                break;
            case R.id.btn_certification: //인증번호 받기 버튼 클릭
                // 테스트모드: 문자발송 안함
                validationCheckingViewModel.setValidPhoneNum(true);
                /**
                 * 서버에 문자발송 요청
                 * */
                //smsViewModel.smsSendReq(resultHandler, null, null, phoneNum.getText().toString());
                break;
            case R.id.btn_confirm: // 인증번호 확인
                if(minute != 0 && sec != 0){
                    // 전화번호, 인증번호
                    phoneNum2 = phoneNum.getText().toString();
                    String certificationNum2 = certificationNum.getText().toString();

                    smsViewModel.smsCertificationReq(resultHandler, phoneNum2, certificationNum2);
                } else{
                    binding.certificationCompl.setText("인증 가능시간이 지났습니다. 인증번호를 다시 받으세요");
                    binding.certificationCompl.setTextColor(ResourcesCompat.getColor(getResources(),R.color.red,null));
                }

                break;
            case R.id.checkbox_all_terms: // 전체동의

                Log.d("체크박스 체크 시 이벤트 발생하나?", "11");

                // 체크 되었다면
                if(((CheckBox)v).isChecked()){
                    // 둘다 체크
                    binding.checkboxPrivateInfo.setChecked(true);
                    binding.checkboxUsingTerms.setChecked(true);

                    validationCheckingViewModel.setAgree(true);

                    // 모든 유효성 검증이 완료 됐다면 버튼 활성화
                    if(validationCheckingViewModel.isValidationCompleted()){
                        binding.btnRegisterComplete.setEnabled(true);
                    }
                } else { // 체크 해제되었다면
                    // 둘다 해제
                    binding.checkboxPrivateInfo.setChecked(false);
                    binding.checkboxUsingTerms.setChecked(false);

                    validationCheckingViewModel.setAgree(false);
                }
                break;
            case R.id.checkbox_private_info: // 개인정보
                // 체크라면
                if(binding.checkboxPrivateInfo.isChecked()){
                    if(binding.checkboxUsingTerms.isChecked()){ //상대방이 체크되어있다면
                        validationCheckingViewModel.setAgree(true);

                        // 모든 유효성 검증이 완료 됐다면 버튼 활성화
                        if(validationCheckingViewModel.isValidationCompleted()){
                            binding.btnRegisterComplete.setEnabled(true);
                        }
                    } else{
                        validationCheckingViewModel.setAgree(false);
                    }
                }
                break;
            case R.id.checkbox_using_terms: // 이용약관
                // 체크라면
                if(binding.checkboxUsingTerms.isChecked()){
                    if(binding.checkboxPrivateInfo.isChecked()){ //상대방이 체크되어있다면
                        validationCheckingViewModel.setAgree(true);

                        // 모든 유효성 검증이 완료 됐다면 버튼 활성화
                        if(validationCheckingViewModel.isValidationCompleted()){
                            binding.btnRegisterComplete.setEnabled(true);
                        }
                    } else{
                        validationCheckingViewModel.setAgree(false);
                    }
                }
                break;
            case R.id.textView_private_info:

                Intent intent = new Intent(this, PrivateInfoActivity.class);
                startActivity(intent);
                break;
            case R.id.textView_using_terms:

                intent = new Intent(this, UsingTermsActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_back: //뒤로가기
                onBackPressed();
                break;
            default:
                break;
        }
    }

    /**
     * 데이터 입력 시 진행하는 이벤트
     * => 나중에 xml에 다 밀어넣기
     */
    public void onDataChanged(){
        // 이름
        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.textViewRequirement4.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // 아이디
        id.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.textViewRequirement1.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // 비밀번호
        pw.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.textViewRequirement2.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // 생년월일
        birthDay.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.textViewRequirement6.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    /**
     * 포커스 변경 시 진행하는 이벤트
     */
    public void onFocusChange() {
        // 이름
        name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.d("111","222");

                if(!hasFocus){
                    // 포커스 아웃 시 값이 존재한다면 유효섬 검증 완료
                    if(!name.getText().toString().equals("")){
                        validationCheckingViewModel.setValidName(true);
                        Log.d("포커스아웃","11");
                    } else{
                        Log.d("포커스아웃","22");
                        binding.textViewRequirement4.setVisibility(View.VISIBLE);
                        validationCheckingViewModel.setValidName(false);
                    }

                    // 모든 유효성 검증이 완료 됐다면 버튼 활성화
                    if(validationCheckingViewModel.isValidationCompleted()){
                        binding.btnRegisterComplete.setEnabled(true);
                    }
                }
            }
        });

        // 아이디 중복체크
        id.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    // 포커스 아웃 시 값이 존재한다면 유효한 아이디인지 체크
                    if (!id.getText().toString().equals("")) {
                        loginViewModel.checkIsValidId(resultHandler, id.getText().toString());
                    } else {
                        binding.textViewRequirement1.setVisibility(View.VISIBLE);
                        validationCheckingViewModel.setValidId(false);
                    }

                    // 모든 유효성 검증이 완료 됐다면 버튼 활성화
                    if(validationCheckingViewModel.isValidationCompleted()){
                        binding.btnRegisterComplete.setEnabled(true);
                    }
                }
            }
        });

        // 비밀번호
        pw.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    // 포커스 아웃 시 값이 존재한다면 유효섬 검증 완료
                    if (!pw.getText().toString().equals("")) {
                        validationCheckingViewModel.setValidPw(true);
                    } else {
                        binding.textViewRequirement2.setVisibility(View.VISIBLE);
                        validationCheckingViewModel.setValidPw(false);
                    }

                    // 모든 유효성 검증이 완료 됐다면 버튼 활성화
                    if(validationCheckingViewModel.isValidationCompleted()){
                        binding.btnRegisterComplete.setEnabled(true);
                    }
                }
            }
        });

        // 비밀번호 확인
        pwConfirm.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    // 포커스 아웃 시 비밀번호와 값이 일치한다면 검증완료
                    if (pw.getText().toString().equals(pwConfirm.getText().toString())) {
                        validationCheckingViewModel.setValidPwConfirm(true);
                        binding.textViewRequirement3.setVisibility(View.GONE);
                    } else {
                        validationCheckingViewModel.setValidPwConfirm(false);
                        binding.textViewRequirement3.setVisibility(View.VISIBLE);
                    }

                    // 모든 유효성 검증이 완료 됐다면 버튼 활성화
                    if(validationCheckingViewModel.isValidationCompleted()){
                        binding.btnRegisterComplete.setEnabled(true);
                    }
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(t != null){
            t.setRunning(false);
        }
    }

     /**
     *  1. 역할: 관찰할 변수에 대한 관찰
     * */
    public void observe(){
        // 소유자(액티비티티)가 활성화(ifecycle.State.STARTED, Lifecycle.State.RESUMED)상태에서 관찰하고 있는 LiveData의 값이 변경 시 콜백함수가 호출된다.
        registerViewModel.getRegisterResult().observe(this, registerResult ->{
            // 저장 완료 시
            if(registerResult.contains("success")){
                Toast.makeText(this, "성공: " + registerResult, Toast.LENGTH_SHORT).show();

                // 다이얼로그빌더
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setMessage("회원가입이 완료되었습니다.");

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
                        Intent intent;
                        intent = new Intent(RegisterActivity.this, LoginActivity.class);

                        intent.putExtra("userInfo", userInfoDto);

                        startActivity(intent);
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            } else if(registerResult.equals("fail")){ // 저장 실패시
                Toast.makeText(this, "실패: " + registerResult, Toast.LENGTH_SHORT).show();
            } else{
                Log.d("그외:", registerResult);

                Toast.makeText(this, "그외: " + registerResult, Toast.LENGTH_SHORT).show();
            }
        });
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