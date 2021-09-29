package com.teamnova.ptmanager.ui.changehistory.eyebody.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.teamnova.ptmanager.R;
import com.teamnova.ptmanager.model.changehistory.eyebody.EyeBodyCompare;
import com.teamnova.ptmanager.ui.home.member.ViewPagerAdapter;


public class EyeBodyCompareVPA  extends RecyclerView.Adapter<EyeBodyCompareVPA.MyViewHolder>{
    private EyeBodyCompare[] eyeBodyCompareArr;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public MyViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageview_eyebody_compare);
        }
    }

    public EyeBodyCompareVPA(Context context, EyeBodyCompare[] eyeBodyCompareArr) {
        this.eyeBodyCompareArr = eyeBodyCompareArr;
        this.context = context;
    }

    @NonNull
    @Override
    public EyeBodyCompareVPA.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_eyebody_compare_enlarge, parent, false);
        EyeBodyCompareVPA.MyViewHolder vh = new EyeBodyCompareVPA.MyViewHolder(itemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull EyeBodyCompareVPA.MyViewHolder holder, int position) {
        Log.d("데이터 넘어오나3", eyeBodyCompareArr[position].getSavePath());

        Glide.with(context).load("http://15.165.144.216" + eyeBodyCompareArr[position].getSavePath()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return eyeBodyCompareArr.length;
    }
}
