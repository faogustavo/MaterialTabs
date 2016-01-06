/*
 * Copyright 2014 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gustavofao.materialtabs;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import static com.gustavofao.materialtabs.TabType.TEXT_ONLY;

public class SlidingTabLayout extends HorizontalScrollView {

    private static final int TEXT_ONLY_TAB = R.layout.text_only;
    private static final int ICON_ONLY_TAB = R.layout.icon_only;
    private static final int TEXT_ICON_TAB = R.layout.text_icon;

    public static final int FOCUSED_WHITE       = 0xFFFFFFFF;
    public static final int NOT_FOCUSED_WHITE   = 0xB3FFFFFF;

    private static final int TEXT_ID = R.id.TabText;
    private static final int ICON_ID = R.id.TabImage;

    private int mTitleOffset;

    private boolean mDistributeEvenly;

    private int customFocusedColor;
    private int customUnfocusedColor;

    private TabType tabType;
    private ViewPager mViewPager;
    private ViewPager.OnPageChangeListener mViewPagerPageChangeListener;

    private final SlidingTabStrip mTabStrip;

    public SlidingTabLayout(Context context) {
        this(context, null);
    }

    public SlidingTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlidingTabLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        // Disable the Scroll Bar
        setHorizontalScrollBarEnabled(false);
        // Make sure that the Tab Strips fills this View
        setFillViewport(true);

        mTabStrip = new SlidingTabStrip(context);
        addView(mTabStrip, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    }

    /**
     * Sets a custom color for the item when it is not focused
     */
    public void setCustomUnfocusedColor(int customUnfocusedColor) {
        this.customUnfocusedColor = customUnfocusedColor;
    }

    /**
     * Sets a custom color for the item when it is focused
     */
    public void setCustomFocusedColor(int customFocusedColor) {
        this.customFocusedColor = customFocusedColor;
    }

    /**
     * Sets to all children objects ocuppates the same width.
     */
    public void setDistributeEvenly(boolean distributeEvenly) {
        mDistributeEvenly = distributeEvenly;
    }

    /**
     * Sets the type of tab and its content.
     */
    public void setTabType(TabType tabType) {
        this.tabType = tabType;
    }

    /**
     * Sets the colors to be used for indicating the selected tab. These colors are treated as a
     * circular array. Providing one color will mean that all tabs are indicated with the same color.
     */
    public void setSelectedIndicatorColors(int... colors) {
        mTabStrip.setSelectedIndicatorColors(colors);
    }

    /**
     * Set the {@link ViewPager.OnPageChangeListener}. When using {@link SlidingTabLayout} you are
     * required to set any {@link ViewPager.OnPageChangeListener} through this method. This is so
     * that the layout can update it's scroll position correctly.
     *
     * @see ViewPager#setOnPageChangeListener(ViewPager.OnPageChangeListener)
     */
    public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        mViewPagerPageChangeListener = listener;
    }

    /**
     * Sets the associated view pager. Note that the assumption here is that the pager content
     * (number of tabs and tab titles) does not change after this call has been made.
     */
    public void setViewPager(ViewPager viewPager) {
        mTabStrip.removeAllViews();

        if (tabType == null)
            tabType = TEXT_ONLY;

        mViewPager = viewPager;
        if (viewPager != null) {
            viewPager.setOnPageChangeListener(new InternalViewPagerListener());
            populateTabStrip();
        }
    }

    private void populateTabStrip() {
        final SlidingFragmentPagerAdapter adapter = (SlidingFragmentPagerAdapter) mViewPager.getAdapter();
        final OnClickListener tabClickListener = new TabClickListener();

        if (tabType == null)
            tabType = TEXT_ONLY;

        int focused_color = customFocusedColor != 0 ? customFocusedColor : FOCUSED_WHITE;
        int unfocused_color = customUnfocusedColor != 0 ? customUnfocusedColor : NOT_FOCUSED_WHITE;

        for (int i = 0; i < adapter.getCount(); i++) {
            View tabView = null;
            TextView tabTitleView = null;
            ImageView tabImageView = null;

            switch (tabType) {
                case TEXT_ONLY:
                    tabView = LayoutInflater.from(getContext()).inflate(TEXT_ONLY_TAB, mTabStrip, false);
                    tabTitleView = (TextView) tabView.findViewById(TEXT_ID);
                    break;

                case ICON_ONLY:
                    tabView = LayoutInflater.from(getContext()).inflate(ICON_ONLY_TAB, mTabStrip, false);
                    tabImageView = (ImageView) tabView.findViewById(ICON_ID);
                    break;

                case TEXT_ICON:
                    tabView = LayoutInflater.from(getContext()).inflate(TEXT_ICON_TAB, mTabStrip, false);
                    tabTitleView = (TextView) tabView.findViewById(TEXT_ID);
                    tabImageView = (ImageView) tabView.findViewById(ICON_ID);
                    break;
            }

            if (mDistributeEvenly) {
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) tabView.getLayoutParams();
                lp.width = 0;
                lp.weight = 1;
            }

            if (i == mViewPager.getCurrentItem())
                tabView.setSelected(true);

            if (tabTitleView != null && adapter.getPageTitle(i) != null) {
                tabTitleView.setText(adapter.getPageTitle(i));
                tabTitleView.setTextColor(i == mViewPager.getCurrentItem() ?
                        focused_color : unfocused_color);
            }

            if (tabImageView != null && adapter.getPageDrawable(i) != null) {
                tabImageView.setImageDrawable(adapter.getPageDrawable(i));
                tabImageView.setColorFilter(i == mViewPager.getCurrentItem() ?
                        focused_color : unfocused_color, PorterDuff.Mode.MULTIPLY);
            }

            tabView.setOnClickListener(tabClickListener);
            mTabStrip.addView(tabView);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (mViewPager != null) {
            scrollToTab(mViewPager.getCurrentItem(), 0);
        }
    }

    private void scrollToTab(int tabIndex, int positionOffset) {
        final int tabStripChildCount = mTabStrip.getChildCount();
        if (tabStripChildCount == 0 || tabIndex < 0 || tabIndex >= tabStripChildCount) {
            return;
        }

        View selectedChild = mTabStrip.getChildAt(tabIndex);
        if (selectedChild != null) {
            int targetScrollX = selectedChild.getLeft() + positionOffset;

            if (tabIndex > 0 || positionOffset > 0) {
                // If we're not at the first child and are mid-scroll, make sure we obey the offset
                targetScrollX -= mTitleOffset;
            }

            scrollTo(targetScrollX, 0);
        }
    }

    private class InternalViewPagerListener implements ViewPager.OnPageChangeListener {
        private int mScrollState;

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            int tabStripChildCount = mTabStrip.getChildCount();
            if ((tabStripChildCount == 0) || (position < 0) || (position >= tabStripChildCount)) {
                return;
            }

            mTabStrip.onViewPagerPageChanged(position, positionOffset);

            View selectedTitle = mTabStrip.getChildAt(position);
            int extraOffset = (selectedTitle != null)
                    ? (int) (positionOffset * selectedTitle.getWidth())
                    : 0;
            scrollToTab(position, extraOffset);

            if (mViewPagerPageChangeListener != null) {
                mViewPagerPageChangeListener.onPageScrolled(position, positionOffset,
                        positionOffsetPixels);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            mScrollState = state;

            if (mViewPagerPageChangeListener != null) {
                mViewPagerPageChangeListener.onPageScrollStateChanged(state);
            }
        }

        @Override
        public void onPageSelected(int position) {
            TextView tabTitleView = null;
            ImageView tabImageView = null;

            int focused_color = customFocusedColor != 0 ? customFocusedColor : FOCUSED_WHITE;
            int unfocused_color = customUnfocusedColor != 0 ? customUnfocusedColor : NOT_FOCUSED_WHITE;

            if (mScrollState == ViewPager.SCROLL_STATE_IDLE) {
                mTabStrip.onViewPagerPageChanged(position, 0f);
                scrollToTab(position, 0);
            }

            for (int i = 0; i < mTabStrip.getChildCount(); i++) {
                mTabStrip.getChildAt(i).setSelected(position == i);

                switch (tabType) {
                    case TEXT_ONLY:
                        tabTitleView = (TextView) mTabStrip.getChildAt(i).findViewById(TEXT_ID);
                        tabTitleView.setTextColor(i == mViewPager.getCurrentItem() ?
                                focused_color : unfocused_color);
                        break;

                    case ICON_ONLY:
                        tabImageView = (ImageView) mTabStrip.getChildAt(i).findViewById(ICON_ID);
                        tabImageView.setColorFilter(i == mViewPager.getCurrentItem() ?
                                focused_color : unfocused_color, PorterDuff.Mode.MULTIPLY);
                        break;

                    case TEXT_ICON:
                        tabTitleView = (TextView) mTabStrip.getChildAt(i).findViewById(TEXT_ID);
                        tabImageView = (ImageView) mTabStrip.getChildAt(i).findViewById(ICON_ID);
                        tabTitleView.setTextColor(i == mViewPager.getCurrentItem() ?
                                focused_color : unfocused_color);
                        tabImageView.setColorFilter(i == mViewPager.getCurrentItem() ?
                                focused_color : unfocused_color, PorterDuff.Mode.MULTIPLY);
                        break;
                }

            }
            if (mViewPagerPageChangeListener != null) {
                mViewPagerPageChangeListener.onPageSelected(position);
            }
        }

    }

    private class TabClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            for (int i = 0; i < mTabStrip.getChildCount(); i++) {
                if (v == mTabStrip.getChildAt(i)) {
                    mViewPager.setCurrentItem(i);
                    return;
                }
            }
        }
    }

}
