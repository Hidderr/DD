package com.coco3g.daishu.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.coco3g.daishu.R;
import com.coco3g.daishu.data.Global;
import com.coco3g.daishu.utils.DisplayImageOptionsUtils;
import com.coco3g.daishu.view.MyWebView;
import com.coco3g.daishu.view.TopBarView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lisen on 2017/3/25.
 */

public class TabViewWebActivity extends BaseActivity {
    TopBarView mTopBar;
    TabLayout mTabLayout;
    ViewPager mViewPager;
    ArrayList<HashMap<String, String>> mListData = new ArrayList<>();
    ArrayList<HashMap<String, String>> mTopbarRightData = new ArrayList<>();
    //
    String mTitle = "";
    private String[] mVPTitles;
    private ArrayList<View> mViewList = new ArrayList<>();
    //
    private int currentPosition = 0;  //当前打开的哪个pager

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabview_web);
        mTitle = getIntent().getStringExtra("title");
        String position = getIntent().getStringExtra("default");
        if (!TextUtils.isEmpty(position)) {
            currentPosition = Integer.parseInt(position);
        }
        mListData = (ArrayList<HashMap<String, String>>) getIntent().getSerializableExtra("data");
        mTopbarRightData = (ArrayList<HashMap<String, String>>) getIntent().getSerializableExtra("rightBtnList");
        if (mListData != null && mListData.size() > 0) {
            int count = mListData.size();
            mVPTitles = new String[count];
            for (int i = 0; i < count; i++) {
                mVPTitles[i] = mListData.get(i).get("title");
                MyWebView webview = new MyWebView(this, null);
                webview.loadUrl(mListData.get(i).get("url"));
                mViewList.add(webview);
            }
        }
        initView();
    }

    private void initView() {
        mTopBar = (TopBarView) findViewById(R.id.topbar_my_tabview_webview);
        if (!TextUtils.isEmpty(mTitle)) {
            mTopBar.setTitle(mTitle);
        }
        if (mTopbarRightData != null) {
            setTopbarRightView(mTopbarRightData);
        }
        mTabLayout = (TabLayout) findViewById(R.id.tablayout_fragment_web);
        mViewPager = (ViewPager) findViewById(R.id.viewpager_fragment_web);
        //
        MyPagerAdapter mAdapter = new MyPagerAdapter(mViewList, mVPTitles);
        mViewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabsFromPagerAdapter(mAdapter);
        //
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        mViewPager.setCurrentItem(currentPosition, true);
    }

    /**
     * Created by coco3g on 16/9/26.
     */
    public class MyPagerAdapter extends PagerAdapter {
        private ArrayList<View> mList = new ArrayList<>();
        private String[] tabTitles;

        public MyPagerAdapter(ArrayList<View> mList, String[] mTabTitle) {
            this.mList = mList;
            this.tabTitles = mTabTitle;
        }

        public void setList(ArrayList<View> mList) {
            this.mList = mList;
            notifyDataSetChanged();
        }


        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mList.get(position));
            return mList.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            if (position < mList.size()) {
                container.removeView(mList.get(position));
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }
    }


    //配置topbar右上角的view
    public void setTopbarRightView(ArrayList<HashMap<String, String>> topbarRightList) {
        if (topbarRightList == null) {
            return;
        }
        for (int i = 0; i < topbarRightList.size(); i++) {
            Map<String, String> oneView = topbarRightList.get(i);
            String title = oneView.get("title");
            final String url = oneView.get("url");
            if (title.startsWith("http://") || title.startsWith("https://")) {
                ImageView imageView = new ImageView(this);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(TabViewWebActivity.this, WebActivity.class);
                        intent.putExtra("url", url);
                        startActivity(intent);
                    }
                });
                ImageLoader.getInstance().displayImage(title, imageView, new DisplayImageOptionsUtils().init());
                mTopBar.setRightView(imageView);

            } else {

                TextView rightView = new TextView(this);
                rightView.setText(title);
                rightView.setTextSize(14);
                rightView.setTextColor(Color.WHITE);
                rightView.setPadding(0, Global.dipTopx(this, 3f), Global.dipTopx(this, 10f), Global.dipTopx(this, 3f));
                rightView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(TabViewWebActivity.this, WebActivity.class);
                        intent.putExtra("url", url);
                        startActivity(intent);
                    }
                });
                mTopBar.setRightView(rightView);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mViewList != null) {
            for (int i = 0; i < mViewList.size(); i++) {
                MyWebView web = (MyWebView) mViewList.get(i);
                web.unRegisterBroadcast();
            }
        }
    }
}
