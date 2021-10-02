package com.teamnova.ptmanager.ui.record.fitness.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.teamnova.ptmanager.R;
import com.teamnova.ptmanager.model.record.fitness.FitnessKinds;
import com.teamnova.ptmanager.model.record.fitness.FitnessRecord;
import com.teamnova.ptmanager.model.record.fitness.FitnessRecordDetail;
import com.teamnova.ptmanager.model.userInfo.FriendInfoDto;
import com.teamnova.ptmanager.ui.home.trainer.TrainerHomeActivity;
import com.teamnova.ptmanager.ui.record.fitness.FitnessModifyActivity;
import com.teamnova.ptmanager.util.GetDate;

import java.lang.reflect.Array;
import java.util.ArrayList;

/** 운동기록리스트 아답터*/
public class FitnessRecordListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    // 운동종류리스트
    //private ArrayList<FitnessKinds> fitnessList;

    // 운동기록을 관리하는 리스트
    private ArrayList<FitnessRecord> fitnessRecordList;

    // context
    private Context context;
    
    // 사용자 정보
    private FriendInfoDto memberInfo;

    // 운동일
    private String selectedDateYMD;


    // 운동기록 정보를 담을 뷰홀더
    public class FitnessRecordViewHolder extends RecyclerView.ViewHolder {
        TextView part;
        TextView fitnessKindName;
        RecyclerView recyclerviewSetNum;
        Button addSet;
        Button deleteSet;
        ImageView btnDeleteFitnessRecord;

        public FitnessRecordViewHolder(View itemView) {
            super(itemView);
            part = itemView.findViewById(R.id.part);
            fitnessKindName = itemView.findViewById(R.id.fitness_name);
            recyclerviewSetNum = itemView.findViewById(R.id.recyclerview_set_num);
            addSet = itemView.findViewById(R.id.add_set);
            deleteSet = itemView.findViewById(R.id.delete_set);
            btnDeleteFitnessRecord = itemView.findViewById(R.id.btn_delete_fitness_record);

            hideBtnForModifyOrDelete();
        }

        /** 트레이너의 경우 세트 추가/삭제/ 항목삭제 못하도록 관련 버튼 안보이게 하기*/
        public void hideBtnForModifyOrDelete() {
            // 트레이너 라면
            if (TrainerHomeActivity.staticLoginUserInfo != null) {
                addSet.setVisibility(View.GONE);
                deleteSet.setVisibility(View.GONE);
                btnDeleteFitnessRecord.setVisibility(View.GONE);
            }
        }
    }

    public FitnessRecordListAdapter(ArrayList<FitnessRecord> fitnessRecordList, Context context, FriendInfoDto memberInfo, String selectedDateYMD){
        this.fitnessRecordList = fitnessRecordList;
        this.context = context;
        this.memberInfo = memberInfo;
        this.selectedDateYMD = selectedDateYMD;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemInBody = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fitness_record, parent, false);
        FitnessRecordViewHolder vh = new FitnessRecordViewHolder(itemInBody);

        return vh;
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        // 특정위치의 운동기록 객체
        FitnessRecord fitnessRecordInfo = fitnessRecordList.get(position);

        // 운동부위
        ((FitnessRecordViewHolder)holder).part.setText(fitnessRecordInfo.getPart());
        // 운동명
        ((FitnessRecordViewHolder)holder).fitnessKindName.setText(fitnessRecordInfo.getFitnessKindName());
        // 운동기록 상세
        ArrayList<FitnessRecordDetail> fitnessRecordInfoDetail = fitnessRecordInfo.getFitnessRecordDetailList();

        // 리사이클러뷰
        FitnessRecordDetailListAdapter fitnessRecordDetailListAdapter = new FitnessRecordDetailListAdapter(fitnessRecordInfoDetail, context, Integer.parseInt(fitnessRecordInfo.getFitnessType()));
        RecyclerView recyclerView = ((FitnessRecordViewHolder)holder).recyclerviewSetNum;
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(fitnessRecordDetailListAdapter);

        // 세트추가 버튼 클릭
        ((FitnessRecordViewHolder)holder).addSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 세트리스트 객체의 참조 가져오기
                fitnessRecordDetailListAdapter.addFitnessDetailInfo();
            }
        });

        // 세트제거 버튼 클릭
        ((FitnessRecordViewHolder)holder).deleteSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fitnessRecordDetailListAdapter.deleteFitnessDetailInfo();
            }
        });

        // 아이템 삭제
        ((FitnessRecordViewHolder)holder).btnDeleteFitnessRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fitnessRecordDetailListAdapter.clearAllFitnessInfo();
                deleteInBodyInfo(position);
            }
        });

        // 운동타입 - 0: 근력운동, 1: 유산소운동 or 맨몸운동
        //String type = fitnessRecordInfo.getFitnessType();

        /*// 운동기록
        String record = "";
        for(FitnessRecordDetail detail : fitnessRecordInfo.getFitnessRecordDetailList()){
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
        }*/
    }

    @Override
    public int getItemCount() {
        return fitnessRecordList.size();
    }

    // 리사이클러뷰의 모든데이터 삭제
    public void clearAllFitnessInfo(){
        int size = fitnessRecordList.size();
        fitnessRecordList.clear();
        notifyItemRangeRemoved(0, size);
    }

    public ArrayList<FitnessRecord> getFitnessList() {
        return fitnessRecordList;
    }

    // 운동기록 추가
    public void addFitnessInfo(FitnessRecord fitnessRecord){
        fitnessRecordList.add(fitnessRecord);

        notifyItemInserted(fitnessRecordList.size());
    }

    // 운동기록 수정
    public void modifyFitnessInfo(int position, FitnessRecord fitnessRecord){
        fitnessRecordList.set(position,fitnessRecord);
        notifyItemChanged(position);
    }

    // 운동기록 삭제
    public void deleteInBodyInfo(int position){
        fitnessRecordList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, fitnessRecordList.size());
    }
}
