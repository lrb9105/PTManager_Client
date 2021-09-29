package com.teamnova.ptmanager.ui.record.fitness.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.teamnova.ptmanager.R;
import com.teamnova.ptmanager.model.record.fitness.FavoriteReq;
import com.teamnova.ptmanager.model.record.fitness.FitnessKinds;
import com.teamnova.ptmanager.model.record.fitness.FitnessRecord;
import com.teamnova.ptmanager.model.record.fitness.FitnessRecordDetail;
import com.teamnova.ptmanager.model.userInfo.FriendInfoDto;
import com.teamnova.ptmanager.network.RetrofitInstance;
import com.teamnova.ptmanager.network.record.fitness.FitnessService;
import com.teamnova.ptmanager.ui.record.fitness.FitnessEnterActivity;
import com.teamnova.ptmanager.ui.record.fitness.FitnessModifyActivity;
import com.teamnova.ptmanager.viewmodel.record.fitness.FitnessViewModel;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Retrofit;

public class FitnessKindsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {
    // 필터링 전 리스트
    private ArrayList<FitnessKinds> unFilteredList;
    // 필터링 된 리스트
    private ArrayList<FitnessKinds> filteredList;

    private Context context;

    // 운동기록 입력화면으로 이동
    private ActivityResultLauncher<Intent> startActivityResult;

    // 운동뷰모델
    private FitnessViewModel fitnessViewModel;

    // 사용자 정보
    private FriendInfoDto memberInfo;

    // 운동종류 담을 뷰홀더
    public class FitnessKindsListViewHolder extends RecyclerView.ViewHolder {
        TextView fitness_kind;
        LinearLayout layout_fitness;
        ImageView btnFavorite;

        public FitnessKindsListViewHolder(View itemView) {
            super(itemView);
            fitness_kind = itemView.findViewById(R.id.fitness_kind);
            layout_fitness = itemView.findViewById(R.id.layout_fitness_kind);
            btnFavorite = itemView.findViewById(R.id.btn_favorite);
        }
    }

    public FitnessKindsListAdapter(ArrayList<FitnessKinds> unFilteredList, Context context, ActivityResultLauncher<Intent> startActivityResult, FitnessViewModel fitnessViewModel, FriendInfoDto memberInfo){
        this.unFilteredList = unFilteredList;
        this.filteredList = unFilteredList;
        this.context = context;
        this.startActivityResult = startActivityResult;
        this.fitnessViewModel = fitnessViewModel;
        this.memberInfo = memberInfo;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemInBody = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fitness_kinds, parent, false);
        FitnessKindsListViewHolder vh = new FitnessKindsListViewHolder(itemInBody);

        return vh;
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        FitnessKinds fitnessKindInfo = filteredList.get(position);

        // 체크여부에 따라 배경색 변경
        if(fitnessKindInfo.isChecked()){ // 선택되어있다면
            ((FitnessKindsListViewHolder)holder).layout_fitness.setBackgroundColor(Color.GRAY);
        } else { // 선택되지 않았다면
            ((FitnessKindsListViewHolder)holder).layout_fitness.setBackgroundColor(Color.WHITE);
        }

        // 즐겨찾기 여부에 따라 배경색 변경
        if(fitnessKindInfo.isFavoriteChecked()){ // 선택되어있다면
            ((FitnessKindsListViewHolder)holder).btnFavorite.setImageDrawable(context.getDrawable(R.drawable.favorite_clicked));
        } else { // 선택되지 않았다면
            ((FitnessKindsListViewHolder)holder).btnFavorite.setImageDrawable(context.getDrawable(R.drawable.favorite_unclicked));
        }

        // 운동명
        ((FitnessKindsListViewHolder)holder).fitness_kind.setText(fitnessKindInfo.getFitnessKindName());

        // 텍스트 클릭 시 뷰모델에 리스트 추가
        ((FitnessKindsListViewHolder)holder).fitness_kind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 뷰모델에 추가
                ArrayList<FitnessKinds> tempList = fitnessViewModel.getSelectedFitnessKindsList().getValue();

                if(tempList == null){
                    tempList = new ArrayList<>();
                }

                // 이미 선택되어 있다면 선택 해제 && 리스트에서 제거
                if(fitnessKindInfo.isChecked()){
                    filteredList.get(position).setChecked(false);
                    tempList.remove(fitnessKindInfo);
                    ((FitnessKindsListViewHolder)holder).layout_fitness.setBackgroundColor(Color.WHITE);
                } else { //선택되어있지 않다면 선택 && 리스트에 추가
                    filteredList.get(position).setChecked(true);
                    tempList.add(fitnessKindInfo);
                    ((FitnessKindsListViewHolder)holder).layout_fitness.setBackgroundColor(Color.GRAY);
                }

                // 뷰모델에 추가
                fitnessViewModel.getSelectedFitnessKindsList().setValue(tempList);

                // 선택완료 후
                System.out.println("선택여부: " + fitnessKindInfo.isChecked());
            }
        });

        // 즐겨찾기 클릭 시 뷰모델에 리스트 추가
        ((FitnessKindsListViewHolder)holder).btnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 뷰모델에 추가
                ArrayList<FitnessKinds> tempFavoriteList = fitnessViewModel.getFavoriteList().getValue();

                if(tempFavoriteList == null){
                    tempFavoriteList = new ArrayList<>();
                }

                // 이미 선택되어 있다면 선택 해제 && 리스트에서 제거
                if(fitnessKindInfo.isFavoriteChecked()){
                    filteredList.get(position).setFavoriteChecked(false);
                    tempFavoriteList.remove(fitnessKindInfo);
                    ((FitnessKindsListViewHolder)holder).btnFavorite.setImageDrawable(context.getDrawable(R.drawable.favorite_unclicked));
                } else { //선택되어있지 않다면 선택 && 리스트에 추가
                    filteredList.get(position).setFavoriteChecked(true);
                    tempFavoriteList.add(fitnessKindInfo);
                    ((FitnessKindsListViewHolder)holder).btnFavorite.setImageDrawable(context.getDrawable(R.drawable.favorite_clicked));                }

                // 서버에 추가즐겨찾기 정보 전송하기
                modifyFavorite(fitnessKindInfo, fitnessKindInfo.isFavoriteChecked());

                // 뷰모델에 추가
                fitnessViewModel.getFavoriteList().setValue(tempFavoriteList);

                // 선택완료 후
                System.out.println("선택여부: " + fitnessKindInfo.isFavoriteChecked());
            }
        });

    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();
                Log.d("여기 오나?", charString);

                if(charString.isEmpty()) {
                    filteredList = unFilteredList;
                } else {
                    ArrayList<FitnessKinds> filteringList = new ArrayList<>();
                    for(FitnessKinds kinds : unFilteredList) {
                        if(kinds.getFitnessKindName().toLowerCase().contains(charString.toLowerCase())) {
                            filteringList.add(kinds);
                            Log.d("필터링된 ㄱ밧:", filteringList.get(0).getFitnessKindName());
                        }
                    }
                    filteredList = filteringList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                for(FitnessKinds k : (ArrayList<FitnessKinds>) results.values){
                    System.out.println(k.getFitnessKindName() + ",");
                }
                filteredList = (ArrayList<FitnessKinds>) results.values;

                notifyDataSetChanged();
            }
        };
    }

    /** 즐겨찾기 등록/해제 */
    public void modifyFavorite(FitnessKinds fitness, boolean isClicked){
        FavoriteReq favoriteReq = new FavoriteReq(fitness.getFitnessKindId(), memberInfo.getUserId(), "" + isClicked);
        Retrofit retrofit= RetrofitInstance.getRetroClient();
        FitnessService service = retrofit.create(FitnessService.class);

        // http request 객체 생성
        Call<String> call = service.modifyFavorite(favoriteReq);

        new ModifyFavorites().execute(call);
    }

    /** 즐겨찾기 등록/해제 */
    private class ModifyFavorites extends AsyncTask<Call, Void, String> {
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

                Log.d("에러", response.body());

                return response.body().toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
        }
    }
}
