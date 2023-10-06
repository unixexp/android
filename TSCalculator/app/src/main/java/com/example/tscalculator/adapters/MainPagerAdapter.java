package com.example.tscalculator.adapters;

import android.app.Activity;
import android.view.ViewGroup;

import androidx.annotation.DrawableRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.tscalculator.R;
import com.example.tscalculator.fragments.FSFragment;
import com.example.tscalculator.fragments.OverviewFragment;
import com.example.tscalculator.fragments.QTSFragment;
import com.example.tscalculator.fragments.SummaryFragment;
import com.example.tscalculator.fragments.UMFragment;
import com.example.tscalculator.fragments.VASFragment;

public class MainPagerAdapter extends FragmentStatePagerAdapter {

    public enum Tab {
        INFO(R.drawable.button_info),
        FS(R.drawable.button_fs),
        UM(R.drawable.button_um),
        QTS(R.drawable.button_qts),
        VAS(R.drawable.button_vas),
        SPEAKER(R.drawable.button_speaker);

        private final int icon;

        Tab(@DrawableRes int drwIcon) {
            icon = drwIcon;
        }

        public int getIcon() {
            return icon;
        }
    }

    int COUNT = 6; // This value should be incremented if new fragment added
    private Activity activity;
    private FragmentManager fragmentManager;
    private Fragment[] fragments = new Fragment[COUNT];
    private final Tab[] mTabs = Tab.values();

    public MainPagerAdapter(FragmentManager fm, Activity activity) {
        super(fm);
        this.fragmentManager = fm;
        this.activity = activity;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return OverviewFragment.newInstance(activity);
            case 1:
                return FSFragment.newInstance(activity);
            case 2:
                return UMFragment.newInstance(activity);
            case 3:
                return QTSFragment.newInstance(activity);
            case 4:
                return VASFragment.newInstance(activity);
            case 5:
                return SummaryFragment.newInstance(activity);
        }
        throw new IllegalArgumentException("Unhandled position: " + position);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Object object = super.instantiateItem(container, position);

        if (object instanceof Fragment) {
            Fragment fragment = (Fragment) object;
            fragments[position] = fragment;
        }

        return object;
    }

    @Override
    public int getCount() {
        return COUNT;
    }

    public Fragment getFragment(int position) {
        try {
            return fragments[position];
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    public Fragment[] getFragments() {
        return fragments;
    }

    public int getTabIcon (int position) {
        return mTabs[position].getIcon();
    }

}
