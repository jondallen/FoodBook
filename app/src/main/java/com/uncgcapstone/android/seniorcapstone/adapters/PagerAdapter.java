package com.uncgcapstone.android.seniorcapstone.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.uncgcapstone.android.seniorcapstone.fragments.DetailCommentsFragment;
import com.uncgcapstone.android.seniorcapstone.fragments.DetailRecipeFragment;

/**
 * Created by jon on 10/14/2016.
 */

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                DetailRecipeFragment tab1 = DetailRecipeFragment.newInstance();
                return tab1;
            case 1:
                DetailCommentsFragment tab2 = DetailCommentsFragment.newInstance();
                return tab2;
            default:
                DetailRecipeFragment tab3 = DetailRecipeFragment.newInstance();
                return tab3;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}