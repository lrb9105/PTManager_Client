package com.teamnova.ptmanager.ui.changehistory;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.teamnova.ptmanager.ui.changehistory.eyebody.EyeBodyFragment;
import com.teamnova.ptmanager.ui.changehistory.inbody.InBodyFragment;
import com.teamnova.ptmanager.ui.home.member.fragment.ChildFragment1;
import com.teamnova.ptmanager.ui.home.member.fragment.ChildFragment2;
import com.teamnova.ptmanager.ui.home.member.fragment.ChildFragment3;

import java.util.ArrayList;

public class MemberChangeHistoryVPAdapter extends FragmentStateAdapter {
    ArrayList<Fragment> fragList = new ArrayList<>();

    public MemberChangeHistoryVPAdapter(@NonNull Fragment fragment) {
        super(fragment);

        fragList.add(new EyeBodyFragment());
        fragList.add(new InBodyFragment());
    }

    public MemberChangeHistoryVPAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        return fragList.get(position);
    }

    @Override
    public int getItemCount() {
        return fragList.size();
    }
}
