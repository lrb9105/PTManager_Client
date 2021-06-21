package com.teamnova.ptmanager.ui.member;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.teamnova.ptmanager.R;
import com.teamnova.ptmanager.databinding.ActivityHomeBinding;
import com.teamnova.ptmanager.databinding.ActivityMemberAddBinding;
import com.teamnova.ptmanager.model.userInfo.FriendInfoDto;
import com.teamnova.ptmanager.model.userInfo.UserInfoDto;
import com.teamnova.ptmanager.ui.home.trainer.TrainerHomeActivity;
import com.teamnova.ptmanager.ui.home.trainer.fragment.TrainerHomeFragment;
import com.teamnova.ptmanager.ui.login.LoginActivity;
import com.teamnova.ptmanager.ui.register.RegisterActivity;
import com.teamnova.ptmanager.ui.schedule.lecture.pass.PassRegisterActivity;
import com.teamnova.ptmanager.viewmodel.friend.FriendViewModel;
import com.teamnova.ptmanager.viewmodel.login.LoginViewModel;

public class MemberAddActivity extends AppCompatActivity {
    private ActivityMemberAddBinding binding;

    // 뷰모델
    private FriendViewModel friendViewModel;

    // 친구정보
    private FriendInfoDto memberInfo;

    // Retrofit 통신의 결과를 전달하는 핸들러
    private Handler resultHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 해당 액티비티에서 사용할 xml파일에대한 클래스가 자동으로 생성되고 inflate함수를 이용해서 인스턴스를 생성한다.
        binding = ActivityMemberAddBinding.inflate(getLayoutInflater());

        // 기존에는 layout이 들어갔는데 대신 getRoot를 넣어준다.
        setContentView(binding.getRoot());

        initialize();
    }

    // 초기화
    public void initialize(){
        // 뷰모델 생성
        friendViewModel = new ViewModelProvider(this).get(FriendViewModel.class);

        // 소유자(액티비티티)가 활성화(ifecycle.State.STARTED, Lifecycle.State.RESUMED)상태에서 관찰하고 있는 LiveData의 값이 변경 시 콜백함수가 호출된다.
        friendViewModel.getFriendInfo().observe(this, friendInfo ->{
            // 입력한 친구정보가 존재한다면
            if(friendInfo.getLoginId() != null){
                // 액티비티에 넘겨주기 위해 memberInfo 변수에 담음
                memberInfo = friendInfo;

                /**
                 * 입력한 아이디에 해당하는 회원정보가 존재한다면 화면에 출력
                 * */
                // 회원정보를 보여주는 레이아웃의 상태 Visible 로 변경
                binding.layoutMemberAdd.setVisibility(View.VISIBLE);
                // 프로필이미지 넣기
                if(friendInfo.getProfileId() != null){
                    Glide.with(this).load("http://15.165.144.216" + friendInfo.getProfileId()).into(binding.userProfile);
                }
                // 조회해온 회원의 이름 넣어주기
                binding.memberName.setText(friendInfo.getUserName());
            } else { // 입력한 회원정보가 존재하지 않는다면
                /**
                 * 입력한 아이디에 해당하는 회원정보가 존재하지 않는다면
                 * */
                // 검색화면의 입력데이터 지우기
                binding.searchIdOrPhoneNum.setText("");
                binding.layoutMemberAdd.setVisibility(View.GONE);

                // 검색결과없음 출력 및 검색화면 화면 감추기
                binding.layoutMemberSearch.setVisibility(View.INVISIBLE);
                binding.layoutMemberNotFound.setVisibility(View.VISIBLE);
            }
        });

        // 검색 버튼 클릭 시
        binding.btnSearchMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(MemberAddActivity.this, TrainerHomeActivity.class);
                // 이동할 액티비티가 이미 작업에서 실행중이라면 기존 인스턴스를 가져오고 위의 모든 액티비티를 삭제
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("memberInfo",memberInfo);
                startActivity(intent);*/

                // 입력한 아이디가 존재하는지 검색
                String enteredId = binding.searchIdOrPhoneNum.getText().toString();

                // 결과객체가 FriendDto
                friendViewModel.getFriendInfo(enteredId);
            }
        });

        // 뒤로가기 버튼 클릭 시
        binding.btnBackToSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 검색화면 출력 및 검색결과없음 화면 감추기
                binding.layoutMemberSearch.setVisibility(View.VISIBLE);
                binding.layoutMemberNotFound.setVisibility(View.INVISIBLE);
            }
        });

        // 검색한 회원정보 클릭 시
        binding.layoutMemberAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 다이얼로그빌더
                AlertDialog.Builder builder = new AlertDialog.Builder(MemberAddActivity.this);

                builder.setMessage("선택한 회원을 회원목록에 추가하시겠습니까?");

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
                         * 1. 검색한 회원 목록에 추가
                         * 2. 파라미터: 핸들러, 소유자Id, 소유자타입(T:트레이너, M:멤버), 친구Id, 친구타입(T:트레이너, M:멤버)
                         * */
                        String ownerId = TrainerHomeActivity.staticLoginUserInfo.getLoginId();
                        String friendId = memberInfo.getLoginId();

                        friendViewModel.addToFriend(resultHandler, ownerId, "T", friendId,"M");
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        // Retrofit 통신 핸들러
        resultHandler = new Handler(Looper.myLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                // 회원정보 친구목록에 추가한 결과
                if(msg.what == 0){
                    Log.d("검색한 회원 친구목록에 추가한 결과", (String)msg.obj);

                    /**
                     *   친구 추가 완료 후 수강권 등록 가능
                     * 1. 확인: 수강권등록 화면으로 이동
                     * 2. 취소: 메인화면으로 바로 이동
                     * */
                    // 다이얼로그빌더
                    AlertDialog.Builder builder = new AlertDialog.Builder(MemberAddActivity.this);

                    builder.setMessage("해당 회원의 수강권을 등록하시겠습니까?");

                    // 취소버튼 클릭 - Home 화면으로 이동
                    builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(MemberAddActivity.this, TrainerHomeActivity.class);
                            // 이동할 액티비티가 이미 작업에서 실행중이라면 기존 인스턴스를 가져오고 위의 모든 액티비티를 삭제
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            intent.putExtra("id","memberAddActivity");
                            intent.putExtra("memberInfo",memberInfo);

                            startActivity(intent);
                        }
                    });

                    // 확인버튼 클릭 - 수강권등록 화면으로 이동
                    builder.setPositiveButton("확인", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int id)
                        {
                            Intent intent = new Intent(MemberAddActivity.this, PassRegisterActivity.class);
                            // 이동할 액티비티가 이미 작업에서 실행중이라면 기존 인스턴스를 가져오고 위의 모든 액티비티를 삭제
                            intent.putExtra("memberInfo",memberInfo);

                            startActivity(intent);

                            // 친구추가 액티비티는 종료
                            finish();
                        }
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            }
        };
    }
}