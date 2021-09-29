package com.teamnova.ptmanager.ui.changehistory.eyebody.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.teamnova.ptmanager.R;
import com.teamnova.ptmanager.model.changehistory.eyebody.EyeBody;
import com.teamnova.ptmanager.model.changehistory.eyebody.EyeBodyCompare;
import com.teamnova.ptmanager.model.userInfo.FriendInfoDto;
import com.teamnova.ptmanager.ui.changehistory.eyebody.EyeBodyCompareEnLargeActivity;
import com.teamnova.ptmanager.ui.changehistory.eyebody.EyeBodyEnLargeActivity;
import com.teamnova.ptmanager.util.GetDate;

import java.util.ArrayList;

public class EyeBodyCompareHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private ArrayList<EyeBodyCompare[]> eyebodyBodyCompareList;
    private Context context;
    private ActivityResultLauncher<Intent> startActivity;

    // 눈바디 사진정보 담을 뷰홀더
    public class EyeBodyCompareViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layoutEyebodyCompare;
        TextView dayDifference;
        TextView text;
        TextView text2;
        ImageView image;
        ImageView image2;

        public EyeBodyCompareViewHolder(View itemView) {
            super(itemView);
            dayDifference = itemView.findViewById(R.id.day_difference);
            text = itemView.findViewById(R.id.text);
            text2 = itemView.findViewById(R.id.text2);
            image = itemView.findViewById(R.id.image);
            image2 = itemView.findViewById(R.id.image2);
            layoutEyebodyCompare = itemView.findViewById(R.id.layout_eyebody_compare);
        }
    }

    public EyeBodyCompareHistoryAdapter(ArrayList<EyeBodyCompare[]> eyebodyBodyCompareList, Context context, ActivityResultLauncher<Intent> startActivity){
        this.eyebodyBodyCompareList = eyebodyBodyCompareList;
        this.context = context;
        this.startActivity = startActivity;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemEyeBody = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_eyebody_compare, parent, false);
        EyeBodyCompareViewHolder vh = new EyeBodyCompareViewHolder(itemEyeBody);

        return vh;
    }



    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        EyeBodyCompare[] eyeBodyCompareArr = eyebodyBodyCompareList.get(position);

        // 앞 순서의 날짜가 더 크다면
        if(GetDate.getDayDifference(eyeBodyCompareArr[0].getCreDate(),eyeBodyCompareArr[1].getCreDate()) < 0) {
            EyeBodyCompare tempObj = eyeBodyCompareArr[0];
            eyeBodyCompareArr[0] = eyeBodyCompareArr[1];
            eyeBodyCompareArr[1] = tempObj;
        }

        Log.d("들어오나?", eyeBodyCompareArr[0].getCommonCompareId() + " : " + eyeBodyCompareArr[0].getCommonCompareId() + " : "  + eyeBodyCompareArr[0].getCreDate());
        Log.d("들어오나222?", eyeBodyCompareArr[1].getCommonCompareId() + " : " + eyeBodyCompareArr[1].getCommonCompareId() + " : " + eyeBodyCompareArr[1].getCreDate());

        ((EyeBodyCompareViewHolder)holder).dayDifference.setText("" + (int) GetDate.getDayDifference(eyeBodyCompareArr[0].getCreDate(), eyeBodyCompareArr[1].getCreDate()) + "일간의 변화");

        // 날짜
        ((EyeBodyCompareViewHolder)holder).text.setText(GetDate.getDateWithYMDAndDot(eyeBodyCompareArr[0].getCreDate()));
        ((EyeBodyCompareViewHolder)holder).text2.setText(GetDate.getDateWithYMDAndDot(eyeBodyCompareArr[1].getCreDate()));

        // 이미지
        Glide.with(context).load("http://15.165.144.216/" + eyeBodyCompareArr[0].getSavePath()).into(((EyeBodyCompareViewHolder)holder).image);
        Glide.with(context).load("http://15.165.144.216/" + eyeBodyCompareArr[1].getSavePath()).into(((EyeBodyCompareViewHolder)holder).image2);

        // 이미지 클릭 시 이미지 확대화면으로 이동
        ((EyeBodyCompareViewHolder)holder).layoutEyebodyCompare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EyeBodyCompareEnLargeActivity.class);
                intent.putExtra("eyeBodyCompareArr", eyeBodyCompareArr);

                // 부모와 자식 리사이클러뷰의 position값 넘겨줌
                intent.putExtra("parentPosition",position);

                startActivity.launch(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return eyebodyBodyCompareList.size();
    }

    // 눈바디비교 배열 데이터 추가
    public void addEyeBodyCompareInfo(EyeBodyCompare[] eyeBodyCompares){
        Log.d("눈바디 비교 배열 추가한 공통ID: ", eyeBodyCompares[0].getCommonCompareId());

        eyebodyBodyCompareList.add(0,eyeBodyCompares);
        notifyItemInserted(0);
    }

    public ArrayList<EyeBodyCompare[]> getEyebodyBodyCompareList() {
        return eyebodyBodyCompareList;
    }

    // 눈바디 기록 삭제
    public void deleteEyeBodyCompare(int position){
        Log.d("position: ", "" + position);
        Log.d("getItemCount: ", "" + getItemCount());

        eyebodyBodyCompareList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, eyebodyBodyCompareList.size());
    }
}
