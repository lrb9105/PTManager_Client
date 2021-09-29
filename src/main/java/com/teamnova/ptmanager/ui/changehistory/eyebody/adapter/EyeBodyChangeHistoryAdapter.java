package com.teamnova.ptmanager.ui.changehistory.eyebody.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.teamnova.ptmanager.R;
import com.teamnova.ptmanager.model.changehistory.eyebody.EyeBodyHistoryInfo;
import com.teamnova.ptmanager.model.lecture.LectureInfoDto;
import com.teamnova.ptmanager.util.GetDate;

import java.util.ArrayList;

/**
 * 눈바디 변화기록데이터를 담는 리사이클러뷰의 아답터
 * */
public class EyeBodyChangeHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private ArrayList<EyeBodyHistoryInfo> eyebodyInfoist;
    private Context context;
    private int type;
    private EyeBodyChangeHistoryByDayAdapter.ItemClickListenerChild listenerChild;
    private ActivityResultLauncher<Intent> startActivity;

    // type: 0 - EyeBodychangeHistoryAct, 1 - EyeBodySelectAct
    public EyeBodyChangeHistoryAdapter(ArrayList<EyeBodyHistoryInfo> eyebodyPathList, Context context, int type, EyeBodyChangeHistoryByDayAdapter.ItemClickListenerChild i, ActivityResultLauncher<Intent> startActivity){
        this.eyebodyInfoist = eyebodyPathList;
        this.context = context;
        this.type = type;
        listenerChild = i;
        this.startActivity = startActivity;
    }

    // 일별 눈바디 사진정보를 담을 뷰홀더
    public class EyeBodyChangeHistoryViewHolder extends RecyclerView.ViewHolder {
        TextView eyeBodyDate;
        RecyclerView recyclerviewEyeBodyByDay;

        public EyeBodyChangeHistoryViewHolder(View itemView) {
            super(itemView);
            eyeBodyDate = itemView.findViewById(R.id.eyebody_date);
            recyclerviewEyeBodyByDay = itemView.findViewById(R.id.recyclerview_eyebody_by_day);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemEyeBody = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_eyebody_change_history, parent, false);
        EyeBodyChangeHistoryViewHolder vh = new EyeBodyChangeHistoryViewHolder(itemEyeBody);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        EyeBodyHistoryInfo eyeBodyHistoryInfo = eyebodyInfoist.get(position);
        /**
         * 눈바디 이미지 셋
         * */

        // 날짜
        ((EyeBodyChangeHistoryViewHolder)holder).eyeBodyDate.setText(GetDate.getDateWithYMDAndDot(eyeBodyHistoryInfo.getEyeBodyDate()));

        Log.d("여기도 들어오나?", eyeBodyHistoryInfo.getEyeBodyListByDay().get(0).getSavePath());

        // 리사이클러뷰
        RecyclerView recyclerView = ((EyeBodyChangeHistoryViewHolder)holder).recyclerviewEyeBodyByDay;
        EyeBodyChangeHistoryByDayAdapter adapter = new EyeBodyChangeHistoryByDayAdapter(eyeBodyHistoryInfo.getEyeBodyListByDay(), context, type);

        adapter.setItemClickListener(listenerChild);
        adapter.setStartActivity(startActivity);
        adapter.setParentRecPosition(position);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(context,2));

        //recyclerView.setLayoutManager(new LinearLayoutManager(context));
    }

    @Override
    public int getItemCount() {
        return eyebodyInfoist.size();
    }

    // 눈바디 기록추가
    public void addEyeBodyHistory(EyeBodyHistoryInfo eyeBodyHistoryInfo){
        eyebodyInfoist.add(0,eyeBodyHistoryInfo);

        notifyItemInserted(0);
    }

    // 눈바디 기록 수정
    public void modifyEyeBodyHistory(int position, EyeBodyHistoryInfo eyeBodyHistoryInfo){
        eyebodyInfoist.set(position,eyeBodyHistoryInfo);
        notifyItemChanged(position);
    }

    // 눈바디 기록 삭제
    public void deleteEyeBodyHistory(int position){
        eyebodyInfoist.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, eyebodyInfoist.size());
    }


    public ArrayList<EyeBodyHistoryInfo> getEyebodyInfoist(){
        return eyebodyInfoist;
    }
}
