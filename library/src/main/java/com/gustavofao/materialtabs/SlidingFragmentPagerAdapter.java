package com.gustavofao.materialtabs;

import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public abstract class SlidingFragmentPagerAdapter extends FragmentPagerAdapter {

    public SlidingFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public Drawable getPageDrawable(int position) {
        return null;
    }

    public boolean hasIndicator(int position) {
        return false;
    }

}
