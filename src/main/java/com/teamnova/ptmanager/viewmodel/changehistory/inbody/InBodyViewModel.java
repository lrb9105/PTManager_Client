package com.teamnova.ptmanager.viewmodel.changehistory.inbody;

import android.os.Handler;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.teamnova.ptmanager.model.changehistory.eyebody.EyeBody;
import com.teamnova.ptmanager.model.changehistory.eyebody.EyeBodyCompare;
import com.teamnova.ptmanager.model.changehistory.eyebody.EyeBodyHistoryInfo;
import com.teamnova.ptmanager.model.changehistory.inbody.InBody;
import com.teamnova.ptmanager.repository.changehistory.eyebody.EyeBodyRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/** 눈바디 데이터를 저장할  viewModel
 * */
public class InBodyViewModel extends ViewModel {
    // 인바디관련데이터 처리를 담당할 저장소 객체
    private EyeBodyRepository eyeBodyRepository;

    // 인바디 정보: 인바디 변화 기록 화면에서 사용
    private MutableLiveData<HashSet<CalendarDay>> dateSet;

    // 초기화
    public InBodyViewModel() {
        eyeBodyRepository = new EyeBodyRepository();
        dateSet = new MutableLiveData<>();
    }

    // 눈바디 사진 저장
    public void registerEyeBodyInfo(Handler retrofitResultHandler, RequestBody userId, MultipartBody.Part eyeBodyFile){
        eyeBodyRepository.registerEyeBodyInfo(retrofitResultHandler, userId, eyeBodyFile);
    }

    // 인바디 날짜리스트 반환
    public MutableLiveData<HashSet<CalendarDay>> getDateSet() {
        return dateSet;
    }
}
