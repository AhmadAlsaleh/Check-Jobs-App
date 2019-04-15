package com.crazy_iter.checkjobs.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.crazy_iter.checkjobs.SearchFragments.AllJobsFragment;
import com.crazy_iter.checkjobs.SearchFragments.NearYouFragment;
import com.crazy_iter.checkjobs.SearchFragments.TimeFragment;
import com.crazy_iter.checkjobs.SearchFragments.TopSalaryFragment;

/**
 * Created by CrazyITer on 3/23/2018.
 */

public class SlidePagerSearchAdapter extends FragmentStatePagerAdapter {

    private int numberOfTabs;

    public SlidePagerSearchAdapter(FragmentManager fm, int numberOfTabs) {
        super(fm);
        this.numberOfTabs = numberOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new AllJobsFragment();
            case 1:
                return new NearYouFragment();
            case 2:
                return new TopSalaryFragment();
            case 3:
                return new TimeFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return numberOfTabs;
    }
}
