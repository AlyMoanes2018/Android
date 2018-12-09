package com.android.agzakhanty.sprints.two.views;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;

/**
 * Created by Aly on 01/07/2018.
 */

public class CustomTabLayout extends TabLayout {

    private Typeface typeface;


    public CustomTabLayout(Context context) {
        super(context);
        init();
    }

    public CustomTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        typeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/medium.ttf");
    }

    @Override
    public void setupWithViewPager(@Nullable ViewPager viewPager) {
        super.setupWithViewPager(viewPager);

        if (typeface != null){
            this.removeAllTabs();
            ViewGroup slidingTabStrip = (ViewGroup) getChildAt(0);
            PagerAdapter adapter = viewPager.getAdapter();

            for(int i = 0, count = adapter.getCount(); i < count; i++){
                Tab tab = this.newTab();
                this.addTab(tab.setText(adapter.getPageTitle(i)));
                AppCompatTextView view = (AppCompatTextView) ((ViewGroup) slidingTabStrip.getChildAt(i)).getChildAt(1);
                view.setTypeface(typeface);
            }
        }
        else Log.d("TEST_FONT", "Null font");
    }
}
