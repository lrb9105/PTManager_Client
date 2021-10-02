package com.teamnova.ptmanager.ui.record.fitness.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

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
import com.teamnova.ptmanager.ui.record.fitness.FitnessKindListActivity;
import com.teamnova.ptmanager.ui.record.fitness.FitnessModifyActivity;
import com.teamnova.ptmanager.viewmodel.record.fitness.FitnessViewModel;
import com.whygraphics.multilineradiogroup.MultiLineRadioGroup;

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

    // 나만의 운동 만들기 다이얼로그
    private Dialog customFitnessModifyDeleteDialog;

    // 커스텀 운동 파라미터
    private String customFitnessName;
    private String fitnessType;
    private String fitnessType2;

    // 수정 전 커스텀 운동
    private FitnessKinds preFitnessKind;

    // 운동종류 담을 뷰홀더
    public class FitnessKindsListViewHolder extends RecyclerView.ViewHolder {
        TextView fitness_kind;
        LinearLayout layout_fitness;
        ImageView btnFavorite;
        ImageView btnMore;

        public FitnessKindsListViewHolder(View itemView) {
            super(itemView);
            fitness_kind = itemView.findViewById(R.id.fitness_kind);
            layout_fitness = itemView.findViewById(R.id.layout_fitness_kind);
            btnFavorite = itemView.findViewById(R.id.btn_favorite);
            btnMore = itemView.findViewById(R.id.btn_more);
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

        // 커스텀 운동인 경우 더보기 버튼 생성
        if(fitnessKindInfo.getUserId()!= null && !fitnessKindInfo.getUserId().equals("")){
            ((FitnessKindsListViewHolder)holder).btnMore.setVisibility(View.VISIBLE);
            ((FitnessKindsListViewHolder)holder).btnMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 수정 다이얼로그
                    createDialog(fitnessKindInfo.getFitnessKindId());
                }
            });
        } else{
            ((FitnessKindsListViewHolder)holder).btnMore.setVisibility(View.GONE);
        }

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

    // 다이얼로그 생성
    public void createDialog(String fitnessKindsId){
        customFitnessModifyDeleteDialog = new Dialog(context);
        customFitnessModifyDeleteDialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀 제거
        customFitnessModifyDeleteDialog.setContentView(R.layout.dialog_custom_fitness);
        customFitnessModifyDeleteDialog.show();
        
        // 커스텀 운동정보 가져오기
        getCustomFitness(fitnessKindsId);
        // 수정, 삭제 버튼 보이게
        customFitnessModifyDeleteDialog.findViewById(R.id.layout_custom_dialog_modify_delete).setVisibility(View.VISIBLE);
        
        // 저장버튼 안보이게
        customFitnessModifyDeleteDialog.findViewById(R.id.btn_register_custom_fitness).setVisibility(View.GONE);

        // 타이틀 변경
        customFitnessModifyDeleteDialog.findViewById(R.id.create_custom_fitness).setVisibility(View.GONE);
        customFitnessModifyDeleteDialog.findViewById(R.id.modify_custom_fitness).setVisibility(View.VISIBLE);

        // 커스텀 운동정보 가져오기
        
        customFitnessModifyDeleteDialog.findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customFitnessModifyDeleteDialog.dismiss();
            }
        });

        // 운동타입1 근력운동으로 초기화
        customFitnessName = "";
        fitnessType = "근력운동";
        fitnessType2 = "";

        // 근력 운동 선택 시
        RadioGroup grp1 = customFitnessModifyDeleteDialog.findViewById(R.id.radio_grp1);

        MultiLineRadioGroup grp2 = customFitnessModifyDeleteDialog.findViewById(R.id.radio_grp2);
        LinearLayout layout2 = customFitnessModifyDeleteDialog.findViewById(R.id.layout_radio_2);

        grp1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
                RadioButton select = radioGroup.findViewById(id);
                String selectStr = select.getText().toString();

                // 근력운동 선택 시 상세 라디오 그룹 표출
                if(selectStr.equals("근력운동")){
                    layout2.setVisibility(View.VISIBLE);
                } else{
                    layout2.setVisibility(View.GONE);
                }

                fitnessType = selectStr;
                Log.d("운동타입1:", fitnessType);

            }
        });

        grp2.setOnCheckedChangeListener(new MultiLineRadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ViewGroup group, RadioButton button) {
                fitnessType2 = button.getText().toString();
                Log.d("운동타입3:", fitnessType);
            }
        });

        // 운동 수정 시
        customFitnessModifyDeleteDialog.findViewById(R.id.btn_modify_custom_fitness).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((EditText)(customFitnessModifyDeleteDialog.findViewById(R.id.editText_custom_fitness_name))).getText().toString().equals("")){
                    Toast.makeText(context, "운동이름을 입력하세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                Log.d("운동타입2:", fitnessType);

                // 운동명
                customFitnessName = ((EditText)(customFitnessModifyDeleteDialog.findViewById(R.id.editText_custom_fitness_name))).getText().toString();

                // 운동타입 숫자로 변경
                if(fitnessType.equals("유산소운동")){
                    fitnessType = "0";
                    fitnessType2 = "유산소";
                } else if(fitnessType.equals("근력운동")) {
                    fitnessType = "1";
                } else if(fitnessType.equals("맨몸운동")) {
                    fitnessType = "2";
                    fitnessType2 = "맨몸";
                }

                modifyCustomFitness(customFitnessName, fitnessType, fitnessType2);

                customFitnessModifyDeleteDialog.dismiss();
            }
        });

        // 운동 삭제 시
        customFitnessModifyDeleteDialog.findViewById(R.id.btn_delete_custom_fitness).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //System.out.println("이전 아이디: " + preFitnessKind.getFitnessKindId());
                deleteCustomFitness(preFitnessKind.getFitnessKindId());

                customFitnessModifyDeleteDialog.dismiss();
            }
        });
    }

    /** 나만의 운동 수정*/
    public void modifyCustomFitness(String customFitnessName, String fitnessType, String fitnessType2){
        Retrofit retrofit= RetrofitInstance.getRetroClient();
        FitnessService service = retrofit.create(FitnessService.class);

        // 커스텀 운동
        FitnessKinds customFitness = new FitnessKinds(preFitnessKind.getFitnessKindId(), fitnessType2, customFitnessName, fitnessType, memberInfo.getUserId());
        customFitness.setFavoriteChecked(false);
        customFitness.setIsFavoriteYn("0");
        customFitness.setChecked(false);

        // http request 객체 생성
        Call<String> call = service.modifyCustomFitness(customFitness);

        new ModifyCustomFitness(customFitness).execute(call);
    }

    /** 나만의 운동 삭제*/
    public void deleteCustomFitness(String fitnessKindsId){
        Retrofit retrofit= RetrofitInstance.getRetroClient();
        FitnessService service = retrofit.create(FitnessService.class);

        // http request 객체 생성
        Call<String> call = service.deleteCustomFitness(fitnessKindsId);

        new DeleteCustomFitness(preFitnessKind).execute(call);
    }

    /** 나만의 운동 조회*/
    public void getCustomFitness(String fitnessKindsId){
        Retrofit retrofit= RetrofitInstance.getRetroClient();
        FitnessService service = retrofit.create(FitnessService.class);

        System.out.println(fitnessKindsId);

        // http request 객체 생성
        //Call<String> call = service.getCustomFitness(fitnessKindsId);
        Call<FitnessKinds> call = service.getCustomFitness(fitnessKindsId);

        new RetrieveCustomFitness().execute(call);
    }

    /** 나만의 운동 수정 */
    private class ModifyCustomFitness extends AsyncTask<Call, Void, String> {
        private retrofit2.Response<String> response;
        private FitnessKinds customFitness;

        public ModifyCustomFitness(FitnessKinds customFitness) {
            this.customFitness = customFitness;
        }

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
            // viewModel에 커스텀 운동 추가!

            Log.d("결과: ", result);
            ArrayList<FitnessKinds> currentList = fitnessViewModel.getFitnessKindsList().getValue();

            for(FitnessKinds f : currentList){
                if(f.getFitnessKindId().equals(preFitnessKind.getFitnessKindId())){
                    currentList.remove(f);
                    break;
                }
            }

            currentList.add(customFitness);

            fitnessViewModel.getFitnessKindsList().setValue(currentList);
            customFitnessModifyDeleteDialog.dismiss();
        }
    }

    /** 나만의 운동 삭제 */
    private class DeleteCustomFitness extends AsyncTask<Call, Void, String> {
        private retrofit2.Response<String> response;
        private FitnessKinds customFitness;

        public DeleteCustomFitness(FitnessKinds customFitness) {
            this.customFitness = customFitness;
        }

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
            // viewModel에 커스텀 운동 추가!
            ArrayList<FitnessKinds> currentList = fitnessViewModel.getFitnessKindsList().getValue();

            System.out.println("이전 사이즈: " + currentList.size());

            for(FitnessKinds f : currentList){
                if(f.getFitnessKindId().equals(preFitnessKind.getFitnessKindId())){
                    currentList.remove(f);
                    break;
                }
            }

            System.out.println("현재 사이즈: " + currentList.size());

            fitnessViewModel.getFitnessKindsList().setValue(currentList);
            customFitnessModifyDeleteDialog.dismiss();
        }
    }

    /** 나만의 운동 가져오기 */
    private class RetrieveCustomFitness extends AsyncTask<Call, Void, String> {
        private retrofit2.Response<FitnessKinds> response;
        //private retrofit2.Response<String> response;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("onPreExecute", "11");
        }

        @Override
        protected String  doInBackground(Call[] params) {
            try {
                //Call<String> call = params[0];
                Call<FitnessKinds> call = params[0];
                response = call.execute();

                //Log.d("결과:ㅣ ", response.body());

                preFitnessKind = response.body();
                return response.body().toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            // dialog에 세팅
            // 운동명
            ((EditText)customFitnessModifyDeleteDialog.findViewById(R.id.editText_custom_fitness_name)).setText(preFitnessKind.getFitnessKindName());

            // 구분1
            String type1 = preFitnessKind.getFitnessType();
            RadioButton radioButton = null;
            switch (type1){
                case "0":
                    radioButton = customFitnessModifyDeleteDialog.findViewById(R.id.radio_cardio);
                    break;
                case "1":
                    radioButton = customFitnessModifyDeleteDialog.findViewById(R.id.radio_weight);
                    break;
                case "2":
                    radioButton = customFitnessModifyDeleteDialog.findViewById(R.id.radio_body);
                    break;
            }
            ((RadioGroup)customFitnessModifyDeleteDialog.findViewById(R.id.radio_grp1)).check(radioButton.getId());

            // 구분2
            String type2 = preFitnessKind.getPart();
            MultiLineRadioGroup mrg = ((MultiLineRadioGroup)customFitnessModifyDeleteDialog.findViewById(R.id.radio_grp2));
            switch (type2){
                case "등":
                    mrg.checkAt(0);
                    break;
                case "가슴":
                    mrg.checkAt(1);
                    break;
                case "어깨":
                    mrg.checkAt(2);
                    break;
                case "복근":
                    mrg.checkAt(3);
                    break;
                case "삼두":
                    mrg.checkAt(4);
                    break;
                case "이두":
                    mrg.checkAt(5);
                    break;
                case "엉덩이":
                    mrg.checkAt(6);
                    break;
                case "전신":
                    mrg.checkAt(7);
                    break;
                case "코어":
                    mrg.checkAt(8);
                    break;
                case "기타":
                    mrg.checkAt(9);
                    break;
            }

        }
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
