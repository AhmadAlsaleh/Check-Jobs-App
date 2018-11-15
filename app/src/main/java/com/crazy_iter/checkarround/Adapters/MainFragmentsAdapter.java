package com.crazy_iter.checkarround.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.crazy_iter.checkarround.MainFragments.MainFragment0;
import com.crazy_iter.checkarround.MainFragments.MainFragment1;
import com.crazy_iter.checkarround.MainFragments.MainFragment2;
import com.crazy_iter.checkarround.MainFragments.MainFragment3;
import com.crazy_iter.checkarround.MainFragments.MainFragment4;

/**
 * Created by CrazyITer on 4/19/2018.
 */

public class MainFragmentsAdapter extends FragmentStatePagerAdapter {

    private int numberOfFragment;

    public MainFragmentsAdapter(FragmentManager fm, int numberOfFragment) {
        super(fm);
        this.numberOfFragment = numberOfFragment;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new MainFragment0();
            case 1:
                return new MainFragment1();
            case 2:
                return new MainFragment2();
            case 3:
                return new MainFragment3();
            case 4:
                return new MainFragment4();
        }
        return null;
    }

    @Override
    public int getCount() {
        return numberOfFragment;
    }
}
