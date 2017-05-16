package com.coco3g.daishu.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.coco3g.daishu.R;
import com.coco3g.daishu.adapter.IncomeAdapter;
import com.coco3g.daishu.data.Global;

import static com.coco3g.daishu.R.id.listview_income_frag;

/**
 * Created by jason on 2017/4/26.
 */

public class IncomeFragment extends Fragment {
    private View mIncomeView;
    private Context mContext;
    private ListView mListView;
    private IncomeAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        mIncomeView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_income, null);
        intview();
        return mIncomeView;
    }

    private void intview() {
        mListView = (ListView) mIncomeView.findViewById(listview_income_frag);
        mAdapter = new IncomeAdapter(mContext);
        mListView.setAdapter(mAdapter);


    }


    @Override
    public void onResume() {
        super.onResume();

        if (Global.USERINFOMAP == null) {

        } else {

        }

    }

}
