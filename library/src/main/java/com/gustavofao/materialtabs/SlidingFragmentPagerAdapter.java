package com.gustavofao.materialtabs;

import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public abstract class SlidingFragmentPagerAdapter extends FragmentPagerAdapter {

    private static final String EMPTY_TOOLBAR_TITLE = "";

    public SlidingFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Nullable
    public Drawable getPageDrawable(int position) {
        return null;
    }

    @NonNull
    public String getToolbarTitle(int position) {
        return EMPTY_TOOLBAR_TITLE;
    }
}
