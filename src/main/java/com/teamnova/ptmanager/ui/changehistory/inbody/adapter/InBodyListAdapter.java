package com.teamnova.ptmanager.ui.changehistory.inbody.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.teamnova.ptmanager.R;
import com.teamnova.ptmanager.model.changehistory.eyebody.EyeBody;
import com.teamnova.ptmanager.model.changehistory.eyebody.EyeBodyCompare;
import com.teamnova.ptmanager.model.changehistory.inbody.InBody;
import com.teamnova.ptmanager.model.lesson.LessonSchInfo;
import com.teamnova.ptmanager.model.userInfo.FriendInfoDto;
import com.teamnova.ptmanager.network.RetrofitInstance;
import com.teamnova.ptmanager.network.changehistory.inbody.InBodyService;
import com.teamnova.ptmanager.network.schedule.lesson.LessonService;
import com.teamnova.ptmanager.ui.changehistory.eyebody.EyeBodyCompareEnLargeActivity;
import com.teamnova.ptmanager.ui.changehistory.eyebody.EyeBodyCompareHistoryActivity;
import com.teamnova.ptmanager.ui.changehistory.inbody.InBodyModifyActivity;
import com.teamnova.ptmanager.ui.home.member.MemberHomeActivity;
import com.teamnova.ptmanager.util.DialogUtil;
import com.teamnova.ptmanager.util.GetDate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import retrofit2.Call;
import retrofit2.Retrofit;

public class InBodyListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private ArrayList<InBody> inBodyList;
    private Context context;
    private ActivityResultLauncher<Intent> startActivityResult;
    private FriendInfoDto memberInfo;
    private ItemDeleteListenerChild listener;
    private InBody deletedInfo;

    // 인바디 사진정보 담을 뷰홀더
    public class InBodyListViewHolder extends RecyclerView.ViewHolder {
        TextView creDate;
        TextView height;
        TextView weight;
        TextView bodyFat;
        TextView muscleMass;
        TextView bmi;
        TextView bodyFatPercent;
        TextView fatLevel;
        TextView basalMetabolicRate;
        LinearLayout layoutInBody;

        public InBodyListViewHolder(View itemView) {
            super(itemView);
            creDate = itemView.findViewById(R.id.inbody_check_date_input);
            height = itemView.findViewById(R.id.inbody_height_input);
            weight = itemView.findViewById(R.id.inbody_weight_input);
            bodyFat = itemView.findViewById(R.id.inbody_body_fat_input);
            muscleMass = itemView.findViewById(R.id.inbody_muscle_mass_input);
            bmi = itemView.findViewById(R.id.inbody_bmi_input);
            bodyFatPercent = itemView.findViewById(R.id.inbody_body_fat_percent_input);
            fatLevel = itemView.findViewById(R.id.inbbody_fat_level_input);
            basalMetabolicRate = itemView.findViewById(R.id.inbbody_basal_metabolic_rate_input);
            layoutInBody = itemView.findViewById(R.id.layout_inbody);
        }
    }

    public InBodyListAdapter(ArrayList<InBody> inBodyList, Context context, ActivityResultLauncher<Intent> startActivityResult, FriendInfoDto memberInfo, ItemDeleteListenerChild listener){
        this.inBodyList = inBodyList;
        this.context = context;
        this.startActivityResult = startActivityResult;
        this.memberInfo = memberInfo;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemInBody = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_inbody, parent, false);
        InBodyListViewHolder vh = new InBodyListViewHolder(itemInBody);

        return vh;
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        InBody inBodyInfo = inBodyList.get(position);

        String dateWithDot = "";
        String date = inBodyInfo.getCreDate();
        String dateYMDWithKorean = GetDate.getDateWithYMDAndDot(date);
        String dateDay = GetDate.getDayOfWeek(date);

        dateWithDot = dateYMDWithKorean + "(" + dateDay +")";

        ((InBodyListViewHolder)holder).creDate.setText(dateWithDot);
        ((InBodyListViewHolder)holder).height.setText("" + inBodyInfo.getHeight());
        ((InBodyListViewHolder)holder).weight.setText("" + Math.round(inBodyInfo.getWeight()*100)/100.0);
        ((InBodyListViewHolder)holder).bodyFat.setText("" + Math.round(inBodyInfo.getBodyFat()*100)/100.0);
        ((InBodyListViewHolder)holder).muscleMass.setText("" + Math.round(inBodyInfo.getMuscleMass()*100)/100.0);
        ((InBodyListViewHolder)holder).bmi.setText("" + Math.round(inBodyInfo.getBmi()*100)/100.0);
        ((InBodyListViewHolder)holder).bodyFatPercent.setText("" + Math.round(inBodyInfo.getBodyFatPercent()*100)/100.0);
        ((InBodyListViewHolder)holder).fatLevel.setText("" + inBodyInfo.getFatLevel());
        ((InBodyListViewHolder)holder).basalMetabolicRate.setText("" + (int)inBodyInfo.getBasalMetabolicRate());

        Log.d("인바디id", inBodyInfo.getInBodyId());

        ((InBodyListViewHolder)holder).layoutInBody.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder.setItems(new String[]{"수정하기", "삭제하기"}, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int pos)
                    {
                        if(pos == 0){
                            /** 인바디 수정화면으로 이동 */
                            Intent intent = new Intent(context, InBodyModifyActivity.class);

                            intent.putExtra("inBody", inBodyInfo);
                            intent.putExtra("memberInfo", memberInfo);

                            startActivityResult.launch(intent);
                        }else{
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);

                            builder.setMessage("인바디정보를 삭제하시겠습니까?");

                            builder.setPositiveButton("확인", new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog, int id)
                                {
                                    /** 인바디 데이터 삭제*/
                                    //인바디정보 삭제
                                    Retrofit retrofit= RetrofitInstance.getRetroClient();
                                    InBodyService service = retrofit.create(InBodyService.class);

                                    // http request 객체 생성
                                    Log.d("인바디id", inBodyInfo.getInBodyId());
                                    deletedInfo = inBodyInfo;
                                    Call<String> call = service.deleteInBodyInfo(inBodyInfo.getInBodyId());

                                    new DeleteInBodyInfoCall().execute(call);
                                }
                            }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {}
                            });

                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        }
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                /** 클릭 시 인바디 수정화면으로 이동*/
            }
        });

    }

    @Override
    public int getItemCount() {
        return inBodyList.size();
    }

    // 리사이클러뷰의 모든데이터 삭제
    public void clearAllInBodyInfo(){
        int size = inBodyList.size();
        inBodyList.clear();
        notifyItemRangeRemoved(0, size);
    }

    public ArrayList<InBody> getInBodyList() {
        return inBodyList;
    }

    // 인바디 수정
    public void modifyInBodyInfo(int position, InBody inBody){
        inBodyList.set(position,inBody);
        notifyItemChanged(position);
    }

    // 인바디 삭제
    public void deleteInBodyInfo(int position){
        inBodyList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, inBodyList.size());
    }

    /** 인바디 정보삭제 */
    public class DeleteInBodyInfoCall extends AsyncTask<Call, Void, String> {
        private retrofit2.Response<String> response;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("onPreExecute", "11");
        }

        @Override
        protected String  doInBackground(Call[] params) {
            try {
                Call<String> call = params[0];
                response = call.execute();

                return response.body().toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if(result.contains("DELETE FAIL")){
                // 눈바디정보 삭제를 실패했다면 - "DELETE FAIL"반환
                new DialogUtil().msgDialog(context,"인바디정보 삭제에 실패했습니다.");

            } else if(result.contains("DELETE SUCCESS")){
                Log.d("삭제성공!","1111");
                Log.d("에러", response.body());
                listener.onItemDeleted(deletedInfo);
            } else{
                Log.d("에러", result);
            }
        }
    }

    // 아이템 클릭 시 Adapter에 알려주는 리스터
    public interface ItemDeleteListenerChild{
        void onItemDeleted(InBody i);
    }
}
