package com.example.smarthealth.MedicalCentreFinder.UI_Elements;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class TabPagerAdapter extends FragmentStateAdapter {
    Fragment_Walk fragmentWalk;
    Fragment_drive fragmentDrive;
    Fragment_Transit fragmentTransit;
    public TabPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                fragmentDrive = new Fragment_drive();
                return fragmentDrive;
            case 1:
                fragmentWalk = new Fragment_Walk();
                return fragmentWalk;
            case 2:
                fragmentTransit = new Fragment_Transit();
                return fragmentTransit;
            default: throw new IllegalArgumentException("Invalid position");
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    public Fragment getPositionalFragment(int position){
        switch (position)
        {
            case 0: return fragmentDrive != null? fragmentDrive : new Fragment_drive() ;
            case 1: return fragmentWalk != null? fragmentWalk : new Fragment_Walk();
            case 2: return fragmentTransit != null? fragmentTransit : new Fragment_Transit();
            default: return fragmentDrive != null? fragmentDrive : new Fragment_drive();
        }
    }
}
