package com.teamnova.ptmanager.ui.record;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.teamnova.ptmanager.ui.record.fitness.FitnessFragment;
import com.teamnova.ptmanager.ui.record.meal.MealFragment;

import java.util.ArrayList;

public class MemberRecordVPAdapter extends FragmentStateAdapter {
    ArrayList<Fragment> fragList = new ArrayList<>();

    public MemberRecordVPAdapter(@NonNull Fragment fragment) {
        super(fragment);

        fragList.add(new MealFragment());
        fragList.add(new FitnessFragment());
    }

    public MemberRecordVPAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
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
