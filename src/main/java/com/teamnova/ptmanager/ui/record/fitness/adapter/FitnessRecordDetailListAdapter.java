package com.teamnova.ptmanager.ui.record.fitness.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.teamnova.ptmanager.R;
import com.teamnova.ptmanager.model.record.fitness.FitnessRecord;
import com.teamnova.ptmanager.model.record.fitness.FitnessRecordDetail;
import com.teamnova.ptmanager.model.userInfo.FriendInfoDto;
import com.teamnova.ptmanager.ui.record.fitness.FitnessModifyActivity;

import java.net.InterfaceAddress;
import java.util.ArrayList;

/** 운동기록상세 리스트 아답터*/
public class FitnessRecordDetailListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    // 운동상세기록을 관리하는 리스트
    private ArrayList<FitnessRecordDetail> fitnessRecordDetailList;

    // context
    private Context context;

    // 운동타입
    private int type;

    // 운동타입:0 인 경우(유산소 운동) 뷰홀더
    public class CardioExerciseViewHolder extends RecyclerView.ViewHolder implements EnterFitnessRecord {
        TextView set_num;
        EditText running_time;


        public CardioExerciseViewHolder(View itemView) {
            super(itemView);
            set_num = itemView.findViewById(R.id.set_num);
            running_time = itemView.findViewById(R.id.running_time);
            possibleWhenMember(running_time, null);
        }

        @Override
        public void possibleWhenMember(EditText e1, EditText e2) {
            e1.setEnabled(false);
            if(e2 != null){
                e2.setEnabled(false);
            }
        }
    }

    // 운동타입:1 인 경우(근력 운동) 뷰홀더
    public class WeightViewHolder extends RecyclerView.ViewHolder implements EnterFitnessRecord {
        TextView set_num;
        EditText fitness_kg;
        EditText fitness_num;


        public WeightViewHolder(View itemView) {
            super(itemView);
            set_num = itemView.findViewById(R.id.set_num);
            fitness_kg = itemView.findViewById(R.id.fitness_kg);
            fitness_num = itemView.findViewById(R.id.fitness_num);
            possibleWhenMember(fitness_kg, fitness_num);
        }

        @Override
        public void possibleWhenMember(EditText e1, EditText e2) {
            e1.setEnabled(false);
            if(e2 != null){
                e2.setEnabled(false);
            }
        }
    }

    // 운동타입:2 인 경우(맨몸 운동) 뷰홀더
    public class BodyExerciseViewHolder extends RecyclerView.ViewHolder implements EnterFitnessRecord {
        TextView set_num;
        EditText fitness_num;


        public BodyExerciseViewHolder(View itemView) {
            super(itemView);
            set_num = itemView.findViewById(R.id.set_num);
            fitness_num = itemView.findViewById(R.id.fitness_num);
            possibleWhenMember(fitness_num, null);
        }

        @Override
        public void possibleWhenMember(EditText e1, EditText e2) {
            e1.setEnabled(false);
            if(e2 != null){
                e2.setEnabled(false);
            }
        }
    }


    public FitnessRecordDetailListAdapter(ArrayList<FitnessRecordDetail> fitnessRecordDetailList, Context context, int type){
        this.fitnessRecordDetailList = fitnessRecordDetailList;
        this.context = context;
        this.type = type;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemInBody = null;
        RecyclerView.ViewHolder vh = null;

        System.out.println("타입: " +  viewType);

        if(viewType == 0){
            itemInBody = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fitness_detail_cardio_exercise, parent, false);
            vh = new CardioExerciseViewHolder(itemInBody);
        } else if(viewType == 1){
            itemInBody = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fitness_detail_weight, parent, false);
            vh = new WeightViewHolder(itemInBody);
        } else if(viewType == 2){
            itemInBody = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fitness_detail_body_exercise, parent, false);
            vh = new BodyExerciseViewHolder(itemInBody);
        }

        return vh;
    }


    // 데이터 가져올 때 여기서 건 이벤트때문에 모든 데이터가 동일하게 나올 수 있음 => 확인하기
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        // 각 운동의 세트정보
        FitnessRecordDetail fitnessRecordDetailInfo = fitnessRecordDetailList.get(position);

        if(holder instanceof  FitnessRecordDetailListAdapter.CardioExerciseViewHolder){
            // 세트 수
            ((CardioExerciseViewHolder)holder).set_num.setText(fitnessRecordDetailInfo.getSetNum() + "세트");
            // 분
            ((CardioExerciseViewHolder)holder).running_time.setText("" + fitnessRecordDetailInfo.getFitnessTimeMinute());

            // 입력 시 객체에 값 넣어주기
            ((CardioExerciseViewHolder)holder).running_time.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    String data = s.toString();
                    if(!data.equals("")){
                        fitnessRecordDetailInfo.setFitnessTimeMinute(Integer.parseInt(s.toString()));
                    }
                }
            });
        } else if(holder instanceof  FitnessRecordDetailListAdapter.WeightViewHolder){
            // 세트 수
            ((WeightViewHolder)holder).set_num.setText(fitnessRecordDetailInfo.getSetNum() + "세트");
            // kg
            ((WeightViewHolder)holder).fitness_kg.setText("" + (int) fitnessRecordDetailInfo.getWeight());
            // 입력 시 객체에 값 넣어주기
            ((WeightViewHolder)holder).fitness_kg.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    String data = s.toString();
                    if(!data.equals("")){
                        fitnessRecordDetailInfo.setWeight(Integer.parseInt(s.toString()));
                    }
                }
            });
            // 회
            ((WeightViewHolder)holder).fitness_num.setText("" + fitnessRecordDetailInfo.getNum());
            // 입력 시 객체에 값 넣어주기
            ((WeightViewHolder)holder).fitness_num.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    String data = s.toString();
                    if(!data.equals("")){
                        fitnessRecordDetailInfo.setNum(Integer.parseInt(s.toString()));
                    }
                }
            });
        } else if(holder instanceof  FitnessRecordDetailListAdapter.BodyExerciseViewHolder){
            // 세트 수
            ((BodyExerciseViewHolder)holder).set_num.setText(fitnessRecordDetailInfo.getSetNum() + "세트");
            // 회
            ((BodyExerciseViewHolder)holder).fitness_num.setText("" + fitnessRecordDetailInfo.getNum());
            // 입력 시 객체에 값 넣어주기
            ((BodyExerciseViewHolder)holder).fitness_num.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    String data = s.toString();
                    if(!data.equals("")){
                        fitnessRecordDetailInfo.setNum(Integer.parseInt(s.toString()));
                    }
                }
            });
        }

        fitnessRecordDetailList.set(position, fitnessRecordDetailInfo);

        System.out.println(fitnessRecordDetailList.get(position));
    }

    @Override
    public int getItemCount() {
        return fitnessRecordDetailList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return type;
    }

    // 리사이클러뷰의 모든데이터 삭제
    public void clearAllFitnessInfo(){
        int size = fitnessRecordDetailList.size();
        fitnessRecordDetailList.clear();
        notifyItemRangeRemoved(0, size);
    }

    // 세트 리스트의 참조 반환
    public ArrayList<FitnessRecordDetail> getFitnessRecordDetailList() {
        return fitnessRecordDetailList;
    }

    // 운동기록 추가
    public void addFitnessDetailInfo(){
        FitnessRecordDetail fitnessRecordDetail = null;
        int size = this.getItemCount();

        if(size == 0){
            System.out.println("111");
            fitnessRecordDetail = new FitnessRecordDetail(null,1,0,0,0,0);
        } else { //0이 아니라면 이전 객체의 값 그대로 가져오기
            System.out.println("222");
            int setNum = fitnessRecordDetailList.get(size-1).getSetNum();
            float weight = fitnessRecordDetailList.get(size-1).getWeight();
            int num = fitnessRecordDetailList.get(size-1).getNum();
            int fitnessTimeMinute = fitnessRecordDetailList.get(size-1).getFitnessTimeMinute();
            float km = fitnessRecordDetailList.get(size-1).getKm();

            System.out.println(weight);
            System.out.println(num);

            fitnessRecordDetail = new FitnessRecordDetail(null, setNum+1, weight, num, fitnessTimeMinute, km);
        }
        fitnessRecordDetailList.add(fitnessRecordDetail);

        notifyItemInserted(fitnessRecordDetailList.size());
    }

    // 운동기록 수정
    public void modifyFitnessDetailInfo(int position, FitnessRecordDetail fitnessRecordDetail){
        fitnessRecordDetailList.set(position,fitnessRecordDetail);
        notifyItemChanged(position);
    }

    // 운동기록 삭제
    public void deleteFitnessDetailInfo(){
        int size = this.getItemCount();
        if(size != 0){
            fitnessRecordDetailList.remove(size - 1);
            notifyItemRemoved(size - 1);
            notifyItemRangeChanged(size - 1, fitnessRecordDetailList.size());
        }
    }
}

interface EnterFitnessRecord{
    void possibleWhenMember(EditText e1, EditText e2);
}
