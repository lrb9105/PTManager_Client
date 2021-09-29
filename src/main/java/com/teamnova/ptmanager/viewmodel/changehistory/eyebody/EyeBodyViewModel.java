package com.teamnova.ptmanager.viewmodel.changehistory.eyebody;

import android.os.Handler;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.teamnova.ptmanager.model.changehistory.eyebody.EyeBody;
import com.teamnova.ptmanager.model.changehistory.eyebody.EyeBodyCompare;
import com.teamnova.ptmanager.model.changehistory.eyebody.EyeBodyHistoryInfo;
import com.teamnova.ptmanager.model.userInfo.FriendInfoDto;
import com.teamnova.ptmanager.repository.changehistory.eyebody.EyeBodyRepository;
import com.teamnova.ptmanager.repository.friend.FriendRepository;

import java.util.ArrayList;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/** 눈바디 데이터를 저장할  viewModel
 * */
public class EyeBodyViewModel extends ViewModel {
    // 눈바디관련데이터 처리를 담당할 저장소 객체
    private EyeBodyRepository eyeBodyRepository;

    // 눈바디 변화기록 정보: 눈바디 변화 기록 화면에서 사용
    private MutableLiveData<ArrayList<EyeBodyHistoryInfo>> eyeBodyHistoryList;

    // 눈바디 변화 정보: 눈바디 홈에서 사용
    private MutableLiveData<ArrayList<EyeBody>> eyeBodyList;

    // 눈바디 비교 정보리스트: 눈바디 홈에서 사용
    private MutableLiveData<ArrayList<EyeBodyCompare[]>> eyeBodyCompareList;

    // 초기화
    public EyeBodyViewModel() {
        eyeBodyRepository = new EyeBodyRepository();
        eyeBodyHistoryList = new MutableLiveData<>();
        eyeBodyList = new MutableLiveData<>();
        eyeBodyCompareList = new MutableLiveData<>();

    }

    // 눈바디 사진 저장
    public void registerEyeBodyInfo(Handler retrofitResultHandler, RequestBody userId, MultipartBody.Part eyeBodyFile){
        eyeBodyRepository.registerEyeBodyInfo(retrofitResultHandler, userId, eyeBodyFile);
    }

    // 눈바디 변화기록리스트 반환
    public MutableLiveData<ArrayList<EyeBodyHistoryInfo>> getEyeBodyHistoryList() {
        return eyeBodyHistoryList;
    }

    // 눈바디 정보리스트 반환
    public MutableLiveData<ArrayList<EyeBody>> getEyeBodyList() {
        return eyeBodyList;
    }

    // 눈바디 비교리스트 반환
    public MutableLiveData<ArrayList<EyeBodyCompare[]>> getEyeBodyCompareList() {
        return eyeBodyCompareList;
    }
}
