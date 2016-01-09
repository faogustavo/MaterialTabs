package com.gustavofao.materialtablayout.sample;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gustavofao.materialtabs.SlidingFragmentPagerAdapter;
import com.gustavofao.materialtabs.SlidingTabLayout;
import com.gustavofao.materialtabs.TabType;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private SlidingTabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tabLayout = (SlidingTabLayout) findViewById(R.id.tab_host);
        viewPager = (ViewPager) findViewById(R.id.view_pager);

        setSupportActionBar(toolbar);

        viewPager.setAdapter(new TabAdapter(getSupportFragmentManager(), this)); // Necessario

        tabLayout.setDistributeEvenly(true);
        tabLayout.setTabType(TabType.ICON_ONLY);
        tabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.colorAccent));
//        tabLayout.setCustomFocusedColor(getResources().getColor(R.color.colorAccent));
//        tabLayout.setCustomUnfocusedColor(getResources().getColor(R.color.colorAccent70));

        tabLayout.setViewPager(viewPager); // Necessário
    }

    public static class TabFragment extends Fragment {

        public static final String POSITION = "position";

        private View view;
        private TextView positionText;

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            this.view = inflater.inflate(R.layout.fragment_tab, container, false);
            return view;
        }

        @Override
        public void onResume() {
            super.onResume();
            positionText = (TextView) this.findViewById(R.id.FragmentTabText);

            int position = getArguments().getInt(POSITION);
            positionText.setText("Position " + position);
        }

        private View findViewById(int id) {
            return view.findViewById(id);
        }
    }

    public class TabAdapter extends SlidingFragmentPagerAdapter {

        private String[] titles = {
                "Contacts",
                "Favorites",
                "Groups",
        };

        private int[] icons = {
                R.drawable.account,
                R.drawable.star,
                R.drawable.account_multiple
        };
        private Context context;

        public TabAdapter(FragmentManager fm, Context context) {
            super(fm);
            this.context = context;
        }

        @Override
        public Fragment getItem(int position) {
            Bundle bundle = new Bundle();
            bundle.putInt(TabFragment.POSITION, position + 1);

            Fragment fragment = new TabFragment();
            fragment.setArguments(bundle);

            return fragment;
        }

        @Override
        public int getCount() {
            return icons.length == titles.length ? icons.length : 0;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public Drawable getPageDrawable(int position) {
            return context.getResources().getDrawable(icons[position]);
        }

        @Override
        public boolean hasIndicator(int position) {
            return position == 1;
        }
    }
}
