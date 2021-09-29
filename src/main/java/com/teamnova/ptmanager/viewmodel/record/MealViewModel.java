package com.teamnova.ptmanager.viewmodel.record;

import android.os.Handler;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.teamnova.ptmanager.repository.changehistory.eyebody.EyeBodyRepository;

import java.util.HashSet;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/** 식사 데이터를 저장할  viewModel
 * */
public class MealViewModel extends ViewModel {
    // 식사 데이터가 있는 날짜 정보
    private MutableLiveData<HashSet<CalendarDay>> dateSet;

    // 초기화
    public MealViewModel() {
        dateSet = new MutableLiveData<>();
    }

    // 인바디 날짜리스트 반환
    public MutableLiveData<HashSet<CalendarDay>> getDateSet() {
        return dateSet;
    }
}
