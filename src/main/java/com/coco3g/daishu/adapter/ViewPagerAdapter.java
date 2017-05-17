package com.coco3g.daishu.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by coco3g on 17/4/24.
 */

public class ViewPagerAdapter extends PagerAdapter {
    private ArrayList<View> mList = new ArrayList<>();
    private String[] tabTitles;

    public ViewPagerAdapter() {
    }

    public ViewPagerAdapter(ArrayList<View> mList, String[] mTabTitle) {
        this.mList = mList;
        this.tabTitles = mTabTitle;
    }

    public void setList(ArrayList<View> mList) {
        this.mList = mList;
        notifyDataSetChanged();
    }

    public ArrayList<View> getList() {
        return mList;
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
