package com.github.liosha2007.android.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;

import com.github.liosha2007.android.R;
import com.github.liosha2007.android.adapter.ViewPagerAdapter;
import com.github.liosha2007.android.common.Utils;

/**
 * Created by liosha on 13.11.13.
 */
public class MainActivity extends FragmentActivity {
    protected ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        configureViewPager();
    }

    private void configureViewPager() {
        if (viewPager == null) {
            Utils.err("viewPager is null");
            return;
        }
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setCurrentItem(1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater();
        return true;
    }

    public ViewPager getViewPager() {
        return viewPager;
    }
}
