package com.teamnova.ptmanager.ui.member.memberinfo.adapter;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.teamnova.ptmanager.ui.changehistory.eyebody.EyeBodyFragment;
import com.teamnova.ptmanager.ui.changehistory.inbody.InBodyFragment;
import com.teamnova.ptmanager.ui.home.member.fragment.ChildFragment2;
import com.teamnova.ptmanager.ui.member.memberinfo.MemberBasicInfoFragment;
import com.teamnova.ptmanager.ui.record.meal.MealFragment;

import java.util.ArrayList;

public class MemberInfoVPAdapter extends FragmentStateAdapter {
    ArrayList<Fragment> fragList = new ArrayList<>();

    public MemberInfoVPAdapter(FragmentActivity activity) {
        super(activity);

        fragList.add(new MemberBasicInfoFragment());
        fragList.add(new EyeBodyFragment());
        fragList.add(new InBodyFragment());
        fragList.add(new MealFragment());
        fragList.add(new ChildFragment2());
    }

    public MemberInfoVPAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
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
