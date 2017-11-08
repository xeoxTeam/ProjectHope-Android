package com.example.luke.projecthopeandroid;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HelpActivity extends AppCompatActivity {
    //Declaration and Initailisation
    private ViewPager viewPager;                    //Viewpager
    private MyViewPagerAdapter myViewPagerAdapter;  //Adapter
    private LinearLayout dotsLayout;                //Linear layout
    private TextView[] dots;                        //Textview dots
    private int[] layouts;                          //Layouts
    private Button btnSkip, btnNext;                //Buttons
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        SharedPreferences myPrefs = getSharedPreferences("PREFERENCE", MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = myPrefs.edit();
        prefsEditor.putString("firstTime", "No");                  //Saving first time login
        prefsEditor.commit();
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);

        layouts = new int[]{                                                // layouts of all welcome sliders
                R.layout.log_in_help,
                R.layout.register_help,
                R.layout.home_screen_help,
                R.layout.profile_page_help,
                R.layout.search_page_help};

        addBottomDots(0);                           // adding bottom dots

        myViewPagerAdapter = new MyViewPagerAdapter();  // making notification bar transparent
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        btnNext = (Button)findViewById(R.id.btn_next);      //Assigning the buttons
        btnSkip = (Button)findViewById(R.id.btn_skip);

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // checking for last page
                // if last page home screen will be launched
                int current = getItem(+1);
                if (current < layouts.length) {
                    // move to next screen
                    viewPager.setCurrentItem(current);
                } else {
                    finish();
                }
            }
        });

        getSupportActionBar().hide();
    }

    private void addBottomDots(int currentPage) {           //Method to add dots at the bottom of the screen
        dots = new TextView[layouts.length];

        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);      //Active dots
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);  //Inactive dots

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {                                         //Setting dots
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorsInactive[currentPage]);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(colorsActive[currentPage]);
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {     //Method
            View view = (View) object;
            container.removeView(view);
        }
    }           //Pager adapter
}
