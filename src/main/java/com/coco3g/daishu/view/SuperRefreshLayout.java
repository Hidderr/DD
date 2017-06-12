package com.coco3g.daishu.view;

import android.content.Context;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AbsListView;
import android.widget.ListView;

import com.coco3g.daishu.R;


@SuppressWarnings("unused")
public class SuperRefreshLayout extends SwipeRefreshLayout implements AbsListView.OnScrollListener, SwipeRefreshLayout.OnRefreshListener {
    private Context mContext;
    private ListView mListView;

    private int mTouchSlop;

    private SuperRefreshLayoutListener mListener;

    private boolean mIsOnLoading = false;

    private boolean mCanLoadMore = false;

    private int mYDown;

    private int mLastY;

    private int mTextColor;
    private int mFooterBackground;
    private boolean mIsMoving = false;

    private View mFooterView;

    public SuperRefreshLayout(Context context) {
        this(context, null);
    }

    public SuperRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        setOnRefreshListener(this);
        setColorSchemeResources(R.color.swiperefresh_color1, R.color.swiperefresh_color2, R.color.swiperefresh_color3, R.color.swiperefresh_color4);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        // 滚动时到了最底部也可以加载更多
        if (canLoad()) {
            loadData();
        }
    }

    @Override
    public void onRefresh() {
        if (mListener != null && !mIsOnLoading) {
            mListener.onRefreshing();
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        // 初始化ListView对象
        if (mListView == null) {
            getListView();
        } else {
            if (mCanLoadMore && mFooterView == null) {
                int listviewHeight = mListView.getHeight();
                int count = mListView.getChildCount();
                int totalHeight = 0;
                for (int i = 0; i < count; i++) {
                    totalHeight = totalHeight + mListView.getChildAt(i).getMeasuredHeight();
                }
                // 只有在listview中Item总高度超出屏幕时，才会添加footerview控件
                if (totalHeight > listviewHeight) {
//                    LayoutInflater lay = LayoutInflater.from(mContext);
//                    mFooterView = lay.inflate(R.layout.view_listview_footer, null);
//                    mListView.addFooterView(mFooterView);
                } else {
//                    setNoMoreData();
                }
            }
        }
    }

    /**
     * 获取ListView
     */
    private void getListView() {
        int child = getChildCount();
        if (child > 0) {
            View childView = getChildAt(0);
            if (childView instanceof ListView) {
                mListView = (ListView) childView;
                // 设置滚动监听器给ListView, 使得滚动的情况下也可以自动加载
                mListView.setOnScrollListener(this);
            }
        }
    }


    public void setCanLoadMore() {
        this.mCanLoadMore = true;
    }

    public void setNoMoreData() {
        this.mCanLoadMore = false;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                // 按下
                mYDown = (int) event.getRawY();
                break;

            case MotionEvent.ACTION_MOVE:
                // 移动
                mIsMoving = true;
                mLastY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_UP:
                mIsMoving = false;
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    /**
     * 是否可以加载更多, 条件是到了最底部, listview不在加载中, 且为上拉操作.
     *
     * @return 是否可以加载更多
     */
    private boolean canLoad() {
        return isInBottom() && !mIsOnLoading && isPullUp() && mCanLoadMore;
    }

    /**
     * 如果到了最底部,而且是上拉操作.那么执行onLoad方法
     */
    private void loadData() {
        if (mListener != null) {
            setIsOnLoading(true);
            mListener.onLoadMore();
            //
            showFooterView();
        }
    }

    /**
     * 是否是上拉操作
     *
     * @return 是否是上拉操作
     */
    private boolean isPullUp() {
        return (mYDown - mLastY) >= mTouchSlop;
    }

    /**
     * 设置正在加载
     *
     * @param loading loading
     */
    public void setIsOnLoading(boolean loading) {
        mIsOnLoading = loading;
        if (!mIsOnLoading) {
            mYDown = 0;
            mLastY = 0;
        }
    }


    /**
     * 判断是否到了最底部
     */
    private boolean isInBottom() {
        boolean listbottom = false;
        int listviewHeight = mListView.getHeight();
        int count = mListView.getChildCount();
        int totalHeight = 0;
        for (int i = 0; i < count; i++) {
            totalHeight = totalHeight + mListView.getChildAt(i).getMeasuredHeight();
        }
        if (totalHeight > listviewHeight) {
            listbottom = true;
        }
        return (mListView != null && mListView.getAdapter() != null) && listbottom
                && mListView.getLastVisiblePosition() == (mListView.getAdapter().getCount() - 1);
    }

    /**
     * 隐藏footerview
     */
    private void hideFooterView() {
        if (mFooterView != null) {
//            mFooterView.setVisibility(View.GONE);
//            mFooterView.setPadding(0, -mFooterView.getHeight(), 0, 0);
            mListView.removeFooterView(mFooterView);
            mFooterView = null;
        }

    }

    /**
     * 显示footerview
     */
    private void showFooterView() {
        if (mFooterView == null) {
//            mFooterView.setVisibility(View.VISIBLE);
//            mFooterView.setPadding(0, 0, 0, 0);
            LayoutInflater lay = LayoutInflater.from(mContext);
            mFooterView = lay.inflate(R.layout.view_listview_footer, null);
            mListView.addFooterView(mFooterView);
        }
    }

    public interface SuperRefreshLayoutListener {
        void onRefreshing();

        void onLoadMore();
    }

    /**
     * 自动刷新并实现OnRefreshing方法回调
     */
    public void setRefreshingLoad() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setRefreshing(true);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mListener.onRefreshing();
                    }
                }, 500);
            }
        }, 300);
    }

    /**
     * 加载结束记得调用
     */
    public void onLoadComplete() {
        setIsOnLoading(false);
        setRefreshing(false);
        //
        hideFooterView();
    }

    /**
     * set
     *
     * @param loadListener loadListener
     */
    public void setSuperRefreshLayoutListener(SuperRefreshLayoutListener loadListener) {
        mListener = loadListener;
    }

    public boolean isMoving() {
        return mIsMoving;
    }
}
