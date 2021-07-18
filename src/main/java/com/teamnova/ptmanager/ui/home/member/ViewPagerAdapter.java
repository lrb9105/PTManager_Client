package com.teamnova.ptmanager.ui.home.member;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.teamnova.ptmanager.ui.home.member.fragment.ChildFragment1;
import com.teamnova.ptmanager.ui.home.member.fragment.ChildFragment2;
import com.teamnova.ptmanager.ui.home.member.fragment.ChildFragment3;

import java.util.ArrayList;

public class ViewPagerAdapter extends FragmentStateAdapter {
    ArrayList<Fragment> fragList = new ArrayList<>();

    public ViewPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);

        fragList.add(new ChildFragment1());
        fragList.add(new ChildFragment2());
        fragList.add(new ChildFragment3());
    }

    public ViewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
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
        return 3;
    }
}
