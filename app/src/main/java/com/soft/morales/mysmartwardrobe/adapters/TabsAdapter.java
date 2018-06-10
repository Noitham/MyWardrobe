package com.soft.morales.mysmartwardrobe.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class TabsAdapter extends FragmentPagerAdapter {

    /**
     * CustomAdapter for our Tabs.
     * It will show a different Fragment depending on the position of tab we are.
     */

    // List of fragments
    private final List<Fragment> mFragments = new ArrayList<>();

    // Constructor
    public TabsAdapter(FragmentManager fm) {
        super(fm);
    }

    // Method that'll add a new fragment
    public void addFragment(Fragment fragment) {
        mFragments.add(fragment);
    }

    // Method that'll get the fragment by its position
    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    // Method that returns the count of our fragments
    @Override
    public int getCount() {
        return mFragments.size();
    }

}