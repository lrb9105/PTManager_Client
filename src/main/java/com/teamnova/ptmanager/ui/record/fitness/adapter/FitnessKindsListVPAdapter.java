package com.teamnova.ptmanager.ui.record.fitness.adapter;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.teamnova.ptmanager.model.userInfo.FriendInfoDto;
import com.teamnova.ptmanager.ui.home.member.fragment.ChildFragment1;
import com.teamnova.ptmanager.ui.home.member.fragment.ChildFragment2;
import com.teamnova.ptmanager.ui.home.member.fragment.ChildFragment3;
import com.teamnova.ptmanager.ui.record.fitness.FitnessKindsListFragment;

import java.util.ArrayList;

public class FitnessKindsListVPAdapter extends FragmentStateAdapter {
    ArrayList<Fragment> fragList = new ArrayList<>();

    public FitnessKindsListVPAdapter(FragmentActivity activity, FriendInfoDto memberInfo) {
        super(activity);

        //즐겨찾기
        fragList.add(new FitnessKindsListFragment(1, memberInfo));
        // 전체
        fragList.add(new FitnessKindsListFragment(2, memberInfo));
        // 가슴
        fragList.add(new FitnessKindsListFragment(3, memberInfo));
        // 어깨
        fragList.add(new FitnessKindsListFragment(4, memberInfo));
        //등
        fragList.add(new FitnessKindsListFragment(5, memberInfo));
        //복근
        fragList.add(new FitnessKindsListFragment(6, memberInfo));
        //삼두
        fragList.add(new FitnessKindsListFragment(7, memberInfo));
        //이두
        fragList.add(new FitnessKindsListFragment(8, memberInfo));
        //엉덩이
        fragList.add(new FitnessKindsListFragment(9, memberInfo));
        //전신
        fragList.add(new FitnessKindsListFragment(10, memberInfo));
        //코어
        fragList.add(new FitnessKindsListFragment(11, memberInfo));
    }

    public FitnessKindsListVPAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Log.d("위치값: ", "" + position);

        return fragList.get(position);
    }

    @Override
    public int getItemCount() {
        return fragList.size();
    }
}
