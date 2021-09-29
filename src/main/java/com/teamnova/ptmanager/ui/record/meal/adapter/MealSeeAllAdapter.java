package com.teamnova.ptmanager.ui.record.meal.adapter;

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
import androidx.recyclerview.widget.RecyclerView;

import com.teamnova.ptmanager.R;
import com.teamnova.ptmanager.model.record.meal.MealHistoryInfo;
import com.teamnova.ptmanager.model.userInfo.FriendInfoDto;
import com.teamnova.ptmanager.ui.changehistory.eyebody.adapter.EyeBodyChangeHistoryByDayAdapter;
import com.teamnova.ptmanager.util.GetDate;

import java.util.ArrayList;

/**
 * 식사기록데이터를 담는 리사이클러뷰의 아답터
 * */
public class MealSeeAllAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private ArrayList<MealHistoryInfo> mealHistoryInfoList;
    private Context context;
    private ActivityResultLauncher<Intent> startActivity;
    private FriendInfoDto memberInfo;


    public MealSeeAllAdapter(ArrayList<MealHistoryInfo> mealHistoryInfoList, Context context, ActivityResultLauncher<Intent> startActivity, FriendInfoDto memberInfo){
        this.mealHistoryInfoList = mealHistoryInfoList;
        this.context = context;
        this.startActivity = startActivity;
        this.memberInfo = memberInfo;
    }

    // 일별 식사정보를 담을 뷰홀더
    public class MealHistoryViewHolder extends RecyclerView.ViewHolder {
        TextView mealDate;
        RecyclerView recyclerviewMealByDay;

        public MealHistoryViewHolder(View itemView) {
            super(itemView);
            mealDate = itemView.findViewById(R.id.meal_date);
            recyclerviewMealByDay = itemView.findViewById(R.id.recyclerview_meal_by_day);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemEyeBody = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_meal_history_list, parent, false);
        MealHistoryViewHolder vh = new MealHistoryViewHolder(itemEyeBody);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MealHistoryInfo mealHistoryInfo = mealHistoryInfoList.get(position);

        Log.d("리스트는 가져오나?", "" + mealHistoryInfo.getMealListByDay().size());

        /**
         * 식사기록 이미지 셋
         * */
        // 날짜
        ((MealHistoryViewHolder)holder).mealDate.setText(GetDate.getDateWithYMDAndDot(mealHistoryInfo.getMealDate()) + "(" + GetDate.getDayOfWeek(mealHistoryInfo.getMealDate()) + ")");


        // 리사이클러뷰
        RecyclerView recyclerView = ((MealHistoryViewHolder)holder).recyclerviewMealByDay;
        MealSeeAllByDayAdapter adapter = new MealSeeAllByDayAdapter(mealHistoryInfo.getMealListByDay(), context, memberInfo);

        // 클릭 시 상세화면으로 이동하기 위한 객체
        adapter.setStartActivity(startActivity);
        adapter.setParentRecPosition(position);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(context,3));

        //recyclerView.setLayoutManager(new LinearLayoutManager(context));
    }

    @Override
    public int getItemCount() {
        return mealHistoryInfoList.size();
    }


    // 식사 기록 수정
    public void modifyMealList(int position, MealHistoryInfo mealHistoryInfo){
        mealHistoryInfoList.set(position,mealHistoryInfo);
        notifyItemChanged(position);
    }

    // 식사 기록 삭제
    public void deleteMealList(int position){
        mealHistoryInfoList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mealHistoryInfoList.size());
    }


    public ArrayList<MealHistoryInfo> getMealInfoList(){
        return mealHistoryInfoList;
    }
}
