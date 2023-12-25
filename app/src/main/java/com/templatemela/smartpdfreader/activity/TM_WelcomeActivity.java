package com.templatemela.smartpdfreader.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.templatemela.smartpdfreader.R;
import com.templatemela.smartpdfreader.language.LangSupportBaseActivity;
import com.templatemela.smartpdfreader.util.ThemeUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TM_WelcomeActivity extends LangSupportBaseActivity {
    @BindView(R.id.btn_skip)
    public Button mBtnSkip;
    @BindView(R.id.layoutDots)
    public LinearLayout mDotsLayout;

    public int[] mLayouts;
    @BindView(R.id.view_pager)
    public ViewPager mViewPager;


    private final ViewPager.OnPageChangeListener mViewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrollStateChanged(int state) {
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);
            if (position == 2) {
                mBtnSkip.setText(getText(R.string.done));
            } else {
                mBtnSkip.setText(getText(R.string.skip_text));
            }
        }
    };

    @Override
    public void onCreate(Bundle bundle) {
        ThemeUtils.getInstance().setThemeApp(this);
        super.onCreate(bundle);
        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);
        mLayouts = new int[]{R.layout.fragment_step_create_pdf, R.layout.fragment_step_view_file, R.layout.fragment_step_merge_pdf, };
        addBottomDots(0);
        mViewPager.setAdapter(new MyViewPagerAdapter());
        mViewPager.addOnPageChangeListener(mViewPagerPageChangeListener);
        mViewPager.setOffscreenPageLimit(3);
    }

    @OnClick({R.id.btn_skip})
    public void openMainActivity() {
        finish();
    }

    public void addBottomDots(int i) {
        int length = mLayouts.length;
        TextView[] textViewArr = new TextView[length];
        int[] intArray = getResources().getIntArray(R.array.array_dot_active);
        int[] intArray2 = getResources().getIntArray(R.array.array_dot_inactive);
        mDotsLayout.removeAllViews();

        for (int i2 = 0; i2 < length; i2++) {
            textViewArr[i2] = new TextView(this);
            textViewArr[i2].setText(Html.fromHtml("&#8226;"));
            textViewArr[i2].setTextSize(35.0f);
            textViewArr[i2].setTextColor(intArray2[i]);
            mDotsLayout.addView(textViewArr[i2]);
        }
        if (length > 0) {
            textViewArr[i].setTextColor(intArray[i]);
        }
    }

    class MyViewPagerAdapter extends PagerAdapter {
        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }

        MyViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup viewGroup, int position) {
            View inflate = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(mLayouts[position], viewGroup, false);
            if (position == 9) {
                inflate.findViewById(R.id.getStarted).setOnClickListener(view ->
                        openMainActivity()
                );
            }
            viewGroup.addView(inflate);
            return inflate;
        }

        @Override
        public int getCount() {
            return mLayouts.length;
        }

        @Override
        public void destroyItem(ViewGroup viewGroup, int i, Object obj) {
            viewGroup.removeView((View) obj);
        }
    }
}
