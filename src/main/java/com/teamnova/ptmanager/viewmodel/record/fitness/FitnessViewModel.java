package com.teamnova.ptmanager.viewmodel.record.fitness;

import android.os.Handler;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.teamnova.ptmanager.model.record.fitness.FitnessKinds;
import com.teamnova.ptmanager.repository.changehistory.eyebody.EyeBodyRepository;

import java.util.ArrayList;
import java.util.HashSet;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/** 운동기록 데이터를 저장할  viewModel
 * */
public class FitnessViewModel extends ViewModel {
    // 운동 정보: 운동 기록 화면에서 사용
    private MutableLiveData<HashSet<CalendarDay>> dateSet;
    private MutableLiveData<ArrayList<FitnessKinds>> fitnessKindsList;
    private MutableLiveData<CharSequence> searchText;
    private MutableLiveData<ArrayList<FitnessKinds>> selectedFitnessKindsList;
    private MutableLiveData<ArrayList<FitnessKinds>> favoriteList;

    // 초기화
    public FitnessViewModel() {
        dateSet = new MutableLiveData<>();
        fitnessKindsList = new MutableLiveData<>();
        searchText = new MutableLiveData<>();
        selectedFitnessKindsList = new MutableLiveData<>();
        favoriteList = new MutableLiveData<>();
    }

    // 운동기록이 있는 날짜리스트 반환
    public MutableLiveData<HashSet<CalendarDay>> getDateSet() {
        return dateSet;
    }

    // 운동종류 리스트 반환
    public MutableLiveData<ArrayList<FitnessKinds>> getFitnessKindsList() {
        return fitnessKindsList;
    }

    // 즐겨찾기 리스트 반환
    public MutableLiveData<ArrayList<FitnessKinds>> getFavoriteList() {
        return favoriteList;
    }

    // 입력된 값 반환
    public MutableLiveData<CharSequence> getSearchText() {
        return searchText;
    }

    // 선택된 운동리스트 반환
    public MutableLiveData<ArrayList<FitnessKinds>> getSelectedFitnessKindsList() {
        return selectedFitnessKindsList;
    }
}
