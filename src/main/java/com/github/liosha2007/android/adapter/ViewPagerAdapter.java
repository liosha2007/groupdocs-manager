package com.github.liosha2007.android.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.github.liosha2007.android.fragment.ActionFragment;
import com.github.liosha2007.android.fragment.SettingsFragment;
import com.github.liosha2007.android.fragment.DashboardFragment;

import java.util.Arrays;
import java.util.List;

/**
 * Created by liosha on 13.11.13.
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {
    protected List<Fragment> viewList = Arrays.<Fragment>asList(
        new SettingsFragment(),
        new DashboardFragment(),
        new ActionFragment()
    );

    public ViewPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int i) {
        return viewList.get(i);
    }

    @Override
    public int getCount() {
        return viewList.size();
    }
}
