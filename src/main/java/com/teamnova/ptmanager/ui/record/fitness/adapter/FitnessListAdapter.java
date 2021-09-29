package com.teamnova.ptmanager.ui.record.fitness.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.teamnova.ptmanager.R;
import com.teamnova.ptmanager.model.changehistory.eyebody.EyeBodyHistoryInfo;
import com.teamnova.ptmanager.model.changehistory.inbody.InBody;
import com.teamnova.ptmanager.model.record.fitness.FitnessRecord;
import com.teamnova.ptmanager.model.record.fitness.FitnessRecordDetail;
import com.teamnova.ptmanager.model.userInfo.FriendInfoDto;
import com.teamnova.ptmanager.network.RetrofitInstance;
import com.teamnova.ptmanager.network.changehistory.inbody.InBodyService;
import com.teamnova.ptmanager.ui.changehistory.inbody.InBodyModifyActivity;
import com.teamnova.ptmanager.ui.record.fitness.FitnessModifyActivity;
import com.teamnova.ptmanager.util.DialogUtil;
import com.teamnova.ptmanager.util.GetDate;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Retrofit;

public class FitnessListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private ArrayList<FitnessRecord> fitnessList;
    private Context context;
    // 수정, 삭제화면으로 이동
    private ActivityResultLauncher<Intent> startActivityResult;
    private FriendInfoDto memberInfo;

    
    // 인바디 사진정보 담을 뷰홀더
    public class FitnessListViewHolder extends RecyclerView.ViewHolder {
        TextView fitness_kind;
        TextView fitness_set_num;
        LinearLayout layout_fitness;

        public FitnessListViewHolder(View itemView) {
            super(itemView);
            fitness_kind = itemView.findViewById(R.id.fitness_kind);
            fitness_set_num = itemView.findViewById(R.id.fitness_set_num);
            layout_fitness = itemView.findViewById(R.id.layout_fitness);
        }
    }

    public FitnessListAdapter(ArrayList<FitnessRecord> fitnessList, Context context, ActivityResultLauncher<Intent> startActivityResult, FriendInfoDto memberInfo){
        this.fitnessList = fitnessList;
        this.context = context;
        this.startActivityResult = startActivityResult;
        this.memberInfo = memberInfo;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemInBody = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_fitness, parent, false);
        FitnessListViewHolder vh = new FitnessListViewHolder(itemInBody);

        return vh;
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        /*FitnessRecord fitnessInfo = fitnessList.get(position);

        // 운동명
        ((FitnessListViewHolder)holder).fitness_kind.setText(fitnessInfo.getFitnessKindName());

        *//** 세트, 횟수, 분 *//*
        // 운동타입 - 0: 근력운동, 1: 유산소운동 or 맨몸운동
        String type = fitnessInfo.getFitnessType();
        // 운동기록
        String record = "";

        for(FitnessRecordDetail detail : fitnessInfo.getFitnessRecordDetailList()){
            // 근력운동
            if(type.equals("0")){
                record += detail.getSetNum() + "세트" + "        " + detail.getWeight() + "kg" + " X " + detail.getNum() + "회\n";

                Log.d("세트는 제대로 나오나?", "" + detail.getSetNum() + " : " + record);

            } else { //유산소, 맨몸 운동 - kg이 없음, 시간에 값이 있다면 분, km에 값이 있다면 km로 하기
                if(detail.getFitnessTimeMinute() != 0){
                    record += detail.getSetNum() + "세트" + "        " + detail.getFitnessTimeMinute() + "분";
                }else{
                    record += detail.getSetNum() + "세트" + "        " + detail.getKm() + "km";
                }
            }
        }

        ((FitnessListViewHolder)holder).fitness_set_num.setText(record);

        // 클릭 시 운동 수정/삭제화면으로 이동
        ((FitnessListViewHolder)holder).layout_fitness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                *//** 운동 수정/삭제화면으로 이동 *//*
                Intent intent = new Intent(context, FitnessModifyActivity.class);

                intent.putExtra("fitnessRecord", fitnessInfo);
                intent.putExtra("memberInfo", memberInfo);

                startActivityResult.launch(intent);
            }
        });*/

    }

    @Override
    public int getItemCount() {
        return fitnessList.size();
    }

    // 리사이클러뷰의 모든데이터 삭제
    public void clearAllFitnessInfo(){
        int size = fitnessList.size();
        fitnessList.clear();
        notifyItemRangeRemoved(0, size);
    }

    public ArrayList<FitnessRecord> getFitnessList() {
        return fitnessList;
    }

    // 운동기록 추가
    public void addFitnessInfo(FitnessRecord fitnessRecord){
        fitnessList.add(fitnessRecord);

        notifyItemInserted(fitnessList.size());
    }

    // 운동기록 수정
    public void modifyFitnessInfo(int position, FitnessRecord fitnessRecord){
        fitnessList.set(position,fitnessRecord);
        notifyItemChanged(position);
    }

    // 운동기록 삭제
    public void deleteInBodyInfo(int position){
        fitnessList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, fitnessList.size());
    }
}