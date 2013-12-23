package com.github.liosha2007.android.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;

import com.github.liosha2007.android.R;
import com.github.liosha2007.android.adapter.ViewPagerAdapter;
import com.github.liosha2007.android.common.Utils;
import com.github.liosha2007.android.fragment.BaseFragment;
import com.github.liosha2007.android.popup.MessagePopup;

/**
 * Created by liosha on 13.11.13.
 */
public class MainActivity extends FragmentActivity {
    protected ViewPager viewPager;
    protected IBackPressed backPressed;

    public interface IBackPressed {
        boolean onBackPressed();
    }

    ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        MessagePopup.initialize(this);
        configureViewPager();
    }

    private void configureViewPager() {
        if (viewPager == null) {
            Utils.err("viewPager is null");
            return;
        }
        final ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setCurrentItem(1);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {
            }

            @Override
            public void onPageSelected(int i) {
                Fragment focusFragment = viewPagerAdapter.getItem(i);
                if (focusFragment instanceof BaseFragment) {
                    ((BaseFragment) focusFragment).onFragmentFocused();
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater();
        return true;
    }

    @Override
    public void onBackPressed() {
        if (backPressed == null || backPressed.onBackPressed()) {
            super.onBackPressed();
        }
    }

    public void setOnBackPressed(IBackPressed backPressed) {
        this.backPressed = backPressed;
    }

    public IBackPressed getOnBackPressed() {
        return this.backPressed;
    }

    public ViewPager getViewPager() {
        return viewPager;
    }
}
