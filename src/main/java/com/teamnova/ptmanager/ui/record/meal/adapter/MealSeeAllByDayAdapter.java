package com.teamnova.ptmanager.ui.record.meal.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.teamnova.ptmanager.R;
import com.teamnova.ptmanager.model.changehistory.eyebody.EyeBody;
import com.teamnova.ptmanager.model.record.meal.Meal;
import com.teamnova.ptmanager.model.userInfo.FriendInfoDto;
import com.teamnova.ptmanager.ui.changehistory.eyebody.EyeBodyEnLargeActivity;
import com.teamnova.ptmanager.ui.changehistory.eyebody.EyeBodySelectActivity;
import com.teamnova.ptmanager.ui.record.meal.MealDetailActivity;
import com.teamnova.ptmanager.util.GetDate;

import java.util.ArrayList;

public class MealSeeAllByDayAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private ArrayList<Meal> mealList;
    private Context context;
    private ActivityResultLauncher<Intent> startActivity;
    private int parentRecPosition;
    private FriendInfoDto memberInfo;

    // 눈바디 사진정보 담을 뷰홀더
    public class MealViewHolder extends RecyclerView.ViewHolder {
        ImageView mealImage;
        TextView mealType;

        public MealViewHolder(View itemView) {
            super(itemView);
            mealImage = itemView.findViewById(R.id.meal_img);
            mealType =  itemView.findViewById(R.id.meal_type);
        }
    }

    public MealSeeAllByDayAdapter(ArrayList<Meal> mealList, Context context, FriendInfoDto memberInfo){
        this.mealList = mealList;
        this.context = context;
        this.memberInfo = memberInfo;

        //notifyDataSetChanged();

        Log.d("순서","11");
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemMeal = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_meal, parent, false);
        MealViewHolder vh = new MealViewHolder(itemMeal);
        Log.d("순서","22");
        return vh;
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        ImageView img = ((MealViewHolder)holder).mealImage;
        if(mealList.get(position).getSavePath() != null){
            Glide.with(context).load("http://15.165.144.216" + mealList.get(position).getSavePath()).into(img);
        } else{
            img.setImageDrawable(context.getDrawable(R.drawable.profile_boy));
        }

        // 클릭 시 상세화면으로 이동
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MealDetailActivity.class);

                intent.putExtra("meal", mealList.get(position));
                intent.putExtra("memberInfo", memberInfo);

                // 부모와 자식 리사이클러뷰의 position값 넘겨줌
                intent.putExtra("parentPosition",parentRecPosition);
                intent.putExtra("childPosition",position);

                startActivity.launch(intent);
            }
        });

        String mealType = "";

        switch (mealList.get(position).getMealType()){
            case 0:
                mealType = "아침";
                break;
            case 1:
                mealType = "간식(아침-점심)";
                break;
            case 2:
                mealType = "점심";
                break;
            case 3:
                mealType = "간식(점심-저녁)";
                break;
            case 4:
                mealType = "저녁";
                break;
            case 5:
                mealType = "야식";
                break;
        }

        ((MealViewHolder) holder).mealType.setText(mealType);
    }

    @Override
    public int getItemCount() {
        return mealList.size();
    }


    // startActivity 세팅
    public void setStartActivity(ActivityResultLauncher<Intent> startActivity){
        this.startActivity = startActivity;
    }

    public void setParentRecPosition(int parentRecPosition) {
        this.parentRecPosition = parentRecPosition;
    }
}
