package com.github.liosha2007.android.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;

import com.github.liosha2007.android.R;
import com.github.liosha2007.android.adapter.ViewPagerAdapter;
import com.github.liosha2007.android.common.Utils;
import com.github.liosha2007.android.popup.MessagePopup;

/**
 * Created by liosha on 13.11.13.
 */
public class MainActivity extends FragmentActivity {
    protected ViewPager viewPager;
    protected IBackPressed backPressed;
    public interface IBackPressed { boolean onBackPressed(); };

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
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setCurrentItem(1);
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

    public void setOnBackPressed(IBackPressed backPressed) { this.backPressed = backPressed; }
    public IBackPressed getOnBackPressed() { return this.backPressed; }
    public ViewPager getViewPager() {
        return viewPager;
    }
}
