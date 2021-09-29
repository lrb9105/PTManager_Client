package com.teamnova.ptmanager.ui.changehistory.eyebody.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.teamnova.ptmanager.R;
import com.teamnova.ptmanager.model.changehistory.eyebody.EyeBody;
import com.teamnova.ptmanager.ui.changehistory.eyebody.EyeBodyChangeHistoryActivity;
import com.teamnova.ptmanager.ui.changehistory.eyebody.EyeBodyEnLargeActivity;
import com.teamnova.ptmanager.ui.changehistory.eyebody.EyeBodySelectActivity;
import com.teamnova.ptmanager.ui.schedule.lecture.pass.PassRegisterActivity;
import com.teamnova.ptmanager.ui.schedule.lesson.LessonRegisterActivity;
import com.teamnova.ptmanager.util.GetDate;

import java.lang.reflect.Array;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class EyeBodyChangeHistoryByDayAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private ArrayList<EyeBody> eyebodyBodyList;
    private Context context;
    private int type;
    private ItemClickListenerChild listener;
    private ActivityResultLauncher<Intent> startActivity;
    private int parentRecPosition;

    // 눈바디 사진정보 담을 뷰홀더
    public class EyeBodyChangeViewHolder extends RecyclerView.ViewHolder {
        ImageView eyebodyImage;
        ImageView img_click;
        TextView time;

        public EyeBodyChangeViewHolder(View itemView) {
            super(itemView);
            eyebodyImage = itemView.findViewById(R.id.eyebody_img);
            time =  itemView.findViewById(R.id.time);
            img_click =  itemView.findViewById(R.id.img_click);
        }
    }

    public EyeBodyChangeHistoryByDayAdapter(ArrayList<EyeBody> eyebodyBodyList, Context context, int type){
        this.eyebodyBodyList = eyebodyBodyList;
        this.context = context;
        this.type = type;
        notifyDataSetChanged();

        Log.d("순서","11");
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemEyeBody = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_eyebody, parent, false);
        EyeBodyChangeViewHolder vh = new EyeBodyChangeViewHolder(itemEyeBody);
        Log.d("순서","22");
        return vh;
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Log.d("순서","33");
        Log.d("여기 들어오나?", "" + position + " : " + eyebodyBodyList.get(position).getSavePath());

        ImageView img = ((EyeBodyChangeViewHolder)holder).eyebodyImage;
        Glide.with(context).load("http://15.165.144.216" + eyebodyBodyList.get(position).getSavePath()).into(img);



        // EyeBodySelectAct인 경우에만!
        if(type == 1){
            // 이미지 클릭 시 배경변경 & act로 데이터 전달
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(((EyeBodyChangeViewHolder)holder).img_click.getVisibility() == View.VISIBLE){
                        ((EyeBodyChangeViewHolder)holder).img_click.setVisibility(View.GONE);

                        EyeBodySelectActivity.checkedEyeBodyInfoSize--;
                        Log.d("11","" + EyeBodySelectActivity.checkedEyeBodyInfoSize);

                        EyeBody e = eyebodyBodyList.get(position);
                        listener.onItemClicked(e,false);
                    } else{
                        if(EyeBodySelectActivity.checkedEyeBodyInfoSize == 2){
                            // 다이얼로그 출력
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);

                            builder.setMessage("눈바디 이미지는 2장까지 선택할 수 있습니다.");

                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog, int id)
                                {}
                            });

                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        } else{
                            ((EyeBodyChangeViewHolder)holder).img_click.setVisibility(View.VISIBLE);
                            EyeBody e = eyebodyBodyList.get(position);

                            listener.onItemClicked(e,true);
                            EyeBodySelectActivity.checkedEyeBodyInfoSize++;
                        }
                        Log.d("22","" + EyeBodySelectActivity.checkedEyeBodyInfoSize);
                    }

                }
            });
        }else{ // 눈바디변화기록 화면
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, EyeBodyEnLargeActivity.class);
                    intent.putExtra("eyeBodyInfo", eyebodyBodyList.get(position));

                    // 부모와 자식 리사이클러뷰의 position값 넘겨줌
                    intent.putExtra("parentPosition",parentRecPosition);
                    intent.putExtra("childPosition",position);

                    startActivity.launch(intent);
                    //context.startActivity(intent);
                }
            });
        }

        String creDateIme = eyebodyBodyList.get(position).getCreDateTime();
        String[] time = creDateIme.split(" ")[1].split(":");
        String amPm = GetDate.getAmPmTime(Integer.parseInt(time[0]), Integer.parseInt(time[1]));
        ((EyeBodyChangeViewHolder) holder).time.setText(amPm);
    }

    @Override
    public int getItemCount() {
        return eyebodyBodyList.size();
    }

    public void setItemClickListener(ItemClickListenerChild itemClickListener){
        Log.d("순서","44");
        this.listener = itemClickListener;

    }

    // 아이템 클릭 시 Adapter에 알려주는 리스터
    public interface ItemClickListenerChild{
        void onItemClicked(EyeBody e, boolean isClicked);
    }

    // startActivity 세팅
    public void setStartActivity(ActivityResultLauncher<Intent> startActivity){
        this.startActivity = startActivity;
    }

    public void setParentRecPosition(int parentRecPosition) {
        this.parentRecPosition = parentRecPosition;
    }
}
