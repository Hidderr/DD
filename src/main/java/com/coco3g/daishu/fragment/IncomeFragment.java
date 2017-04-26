package com.coco3g.daishu.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.coco3g.daishu.R;

/**
 * Created by jason on 2017/4/26.
 */

public class IncomeFragment extends Fragment {
    private View mHomeView;
    private Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        mHomeView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_income, null);
        return mHomeView;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public void onResume() {
        super.onResume();
    }

}
