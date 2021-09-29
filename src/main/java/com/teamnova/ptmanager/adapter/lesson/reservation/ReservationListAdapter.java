package com.teamnova.ptmanager.adapter.lesson.reservation;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.teamnova.ptmanager.R;
import com.teamnova.ptmanager.model.reservation.ReservationInfo;
import com.teamnova.ptmanager.model.userInfo.FriendInfoDto;
import com.teamnova.ptmanager.util.GetDate;

import java.util.ArrayList;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 예약목록 보여주는 리사이클러뷰 아답터
 * */
public class ReservationListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    // ViewHolder에 매칭시키기위한 회원목록 데이터
    private ArrayList<ReservationInfo> reservationList;

    private Context context;

    // 예약정보를 담을 뷰홀더
    public class ReservationInfoViewHolder extends RecyclerView.ViewHolder {
        CircleImageView user_profile;
        ConstraintLayout memberLayout;
        TextView name_reserve_type;
        TextView reserve_date;
        TextView cancel_req_reason;
        CheckBox reserve_approve_check;

        public ReservationInfoViewHolder(View itemView) {
            super(itemView);
            user_profile = itemView.findViewById(R.id.user_profile);
            memberLayout = itemView.findViewById(R.id.layout_friend);
            name_reserve_type = itemView.findViewById(R.id.name_reserve_type);
            reserve_date = itemView.findViewById(R.id.reserve_date);
            cancel_req_reason = itemView.findViewById(R.id.cancel_req_reason);
            reserve_approve_check = itemView.findViewById(R.id.reserve_approve_check);
        }
    }


    // Adapter를 생성할 때 받아오는 데이터와 컨텍스트
    public ReservationListAdapter(ArrayList<ReservationInfo> reservationList, Context context) {
        this.reservationList = reservationList;
        this.context = context;
    }

    // Adapter를 생성할 때 받아오는 데이터와 컨텍스트
    public ReservationListAdapter(Context context) {
        this.context = context;
    }

    // 새로운 뷰홀더를 생성한다.
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 새로운 뷰(아이템(xml))을 인스턴스화한다.test
        View itemReservation = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reserved_member, parent, false);

        ReservationListAdapter.ReservationInfoViewHolder vh = new ReservationListAdapter.ReservationInfoViewHolder(itemReservation);
        Log.d("여기 들어오나? ", " 12");

        return vh;
    }

    // 생성한 뷰홀더의 각각의 컴포넌트에 받아온 데이터를 매칭
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        ReservationInfo reservationInfo = reservationList.get(position);
        Log.d("여기 들어오나? ", " 11111");


        // 체크박스 보이게
        CheckBox check = ((ReservationInfoViewHolder)holder).reserve_approve_check;
        // viewHolder를 재활용하므로 체크가 되어있을 수 있음 따라서 해제해줌.
        check.setChecked(false);

        // 해당 리사이클러뷰가 체크되어있던 경우에만 다시 체크함
        if(reservationInfo.getCheck() != null){
            if(reservationInfo.getCheck().equals("1")){
                check.setChecked(true);
            }
        }


        // 체크라면 memberInfo에 값(1) 넣기 체크해제라면 값(0) 넣기
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("클릭된 체크박스 번호: ", "" + position);

                if(check.isChecked()){ // 체크되었다면
                    reservationInfo.setCheck("1");
                } else{ //체크해제되었다면
                    reservationInfo.setCheck("0");
                }
                // 리스트에 체크값이 수정된 memberInfo 수정
                reservationList.set(position, reservationInfo);
            }
        });


        // 회원 프로필 이미지
        if(reservationInfo.getProfileId() != null){
            Glide.with(context).load("http://15.165.144.216" + reservationInfo.getProfileId()).into(((ReservationInfoViewHolder)holder).user_profile);
        }

        String reservationType = null;

        // [예약타입]이름
        if(reservationInfo.getCancelReqDatetime() != null){// 취소요청일에 값이있다면 - 예약취소
            reservationType = "[예약취소]";
        } else{
            reservationType = "[예약]";
        }
        ((ReservationInfoViewHolder)holder).name_reserve_type.setText(reservationType + reservationInfo.getUserName());

        // 예약날짜
        String lessonDate = GetDate.getDateWithYMD(reservationInfo.getLessonDate());
        lessonDate += "(" + GetDate.getDayOfWeek(reservationInfo.getLessonDate()) + ")\n";
        String srtTime = reservationInfo.getLessonSrtTime();
        String endTime = reservationInfo.getLessonEndTime();

        if(srtTime.endsWith(":0")){
            srtTime = srtTime.replace(":0",":00");
        }

        if(endTime.endsWith(":0")){
            endTime = endTime.replace(":0",":00");
        }

        lessonDate += srtTime + "~" + endTime;

        ((ReservationInfoViewHolder)holder).reserve_date.setText(lessonDate);
        
        // 예약취소 요청 사유
        if(reservationInfo.getCancelReason() != null && !reservationInfo.getCancelReason().equals("")){// 취소요청사유가 있다면
            ((ReservationInfoViewHolder)holder).cancel_req_reason.setText(reservationInfo.getCancelReason());
        }

    }

    @Override
    public int getItemCount() {
        Log.d("리사이클러뷰 사이즈12434123: ", "" + reservationList.size());
        return reservationList.size();
    }

    public ArrayList<ReservationInfo> getReservationList(){
        return this.reservationList;
    }

    public void setReservationList(ArrayList<ReservationInfo> reservationList) {
        this.reservationList = reservationList;
    }
}
