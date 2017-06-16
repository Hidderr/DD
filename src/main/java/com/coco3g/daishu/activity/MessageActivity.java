package com.coco3g.daishu.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.coco3g.daishu.R;
import com.coco3g.daishu.adapter.MessageAdapter;
import com.coco3g.daishu.bean.BaseDataBean;
import com.coco3g.daishu.bean.ChatItemDataBean;
import com.coco3g.daishu.data.DataUrl;
import com.coco3g.daishu.data.Global;
import com.coco3g.daishu.listener.IBaseDataListener;
import com.coco3g.daishu.net.utils.RongUtils;
import com.coco3g.daishu.presenter.BaseDataPresenter;
import com.coco3g.daishu.utils.Coco3gBroadcastUtils;
import com.coco3g.daishu.view.SuperRefreshLayout;
import com.coco3g.daishu.view.TopBarView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by coco3g on 17/3/30.
 */

public class MessageActivity extends BaseFragmentActivity {
    private TopBarView mTopBar;
    SuperRefreshLayout mSuperRefresh;
    ListView mListView;
    MessageAdapter mAdapter;
    ArrayList<Map<String, String>> mCurrChatList = new ArrayList<>();
    Coco3gBroadcastUtils mReceviceBoardcast, mSendBoardcast;
    private static String CURR_SELECT_USERID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        initView();
    }


    private void initView() {
        mTopBar = (TopBarView) findViewById(R.id.topbar_activity_message);
        mTopBar.setTitle("消息");
        mListView = (ListView) findViewById(R.id.listview_msg_frag);
        mSuperRefresh = (SuperRefreshLayout) findViewById(R.id.sr_msg_frag);
        //
        mAdapter = new MessageAdapter(MessageActivity.this);
        mListView.setAdapter(mAdapter);
        //
        mSuperRefresh.setSuperRefreshLayoutListener(new SuperRefreshLayout.SuperRefreshLayoutListener() {
            @Override
            public void onRefreshing() {
                mAdapter.clearList();
                getChatList();
            }

            @Override
            public void onLoadMore() {

            }
        });
        //
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                new RongUtils(MessageActivity.this).startConversation(mCurrChatList.get(position).get("username"), mCurrChatList.get(position - 1).get("userid"));
                CURR_SELECT_USERID = mCurrChatList.get(position - 1).get("userid");
            }
        });
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                remindDailog(position);
                return true;
            }
        });
        //
        mReceviceBoardcast = new Coco3gBroadcastUtils(MessageActivity.this).receiveBroadcast(Coco3gBroadcastUtils.RECEVICE_RONG_MESSAGE_FLAG);
        mReceviceBoardcast.setOnReceivebroadcastListener(new Coco3gBroadcastUtils.OnReceiveBroadcastListener() {
            @Override
            public void receiveReturn(Intent intent) {
//                App app = (App) getActivity().getApplication();
//                ChatItemDataBean itemdata = app.getmChatItemDataBean();
//                updateItem(itemdata);
                mSuperRefresh.setRefreshingLoad();
            }
        });
        mSendBoardcast = new Coco3gBroadcastUtils(MessageActivity.this).receiveBroadcast(Coco3gBroadcastUtils.SEND_RONG_MESSAGE_FLAG);
        mSendBoardcast.setOnReceivebroadcastListener(new Coco3gBroadcastUtils.OnReceiveBroadcastListener() {
            @Override
            public void receiveReturn(Intent intent) {
//                Bundle b = intent.getBundleExtra("data");
//                String targetID = (String) b.getSerializable("targetID");
                mSuperRefresh.setRefreshingLoad();
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        if (mAdapter.getList() == null || mAdapter.getList().size() <= 0) {
            mSuperRefresh.setRefreshingLoad();
        }
        chatReturnRefersh();
    }


    /**
     * 获取联系人列表
     */
    public void getChatList() {
        HashMap<String, String> params = new HashMap<>();
        new BaseDataPresenter(MessageActivity.this).loadData(DataUrl.GET_CONTACT_LIST, params, null, new IBaseDataListener() {
            @Override
            public void onSuccess(BaseDataBean data) {
                mCurrChatList = (ArrayList<Map<String, String>>) data.response;
                ArrayList<ChatItemDataBean> showlist = new ArrayList<>();
                RongUtils rong = new RongUtils(MessageActivity.this);
                for (int i = 0; i < mCurrChatList.size(); i++) {
                    ChatItemDataBean itemdata = rong.getChatInfoByObject(mCurrChatList.get(i));
                    if (itemdata == null) {
                        continue;
                    }
                    itemdata.id = mCurrChatList.get(i).get("id");
                    itemdata.userid = mCurrChatList.get(i).get("userid");
                    itemdata.avatar = mCurrChatList.get(i).get("avatar");
                    itemdata.nickname = mCurrChatList.get(i).get("username");
                    itemdata.updatetime = mCurrChatList.get(i).get("updatetime");
                    showlist.add(itemdata);
                }
                mAdapter.setList(showlist);
                mSuperRefresh.onLoadComplete();
            }

            @Override
            public void onFailure(BaseDataBean data) {
                Global.showToast(data.msg, MessageActivity.this);
                mSuperRefresh.onLoadComplete();
            }

            @Override
            public void onError() {
                mSuperRefresh.onLoadComplete();
            }
        });
    }

    /**
     * 进入会话界面并返回后，刷新当前用户的融云数据（主要用来清空当前用的融云未读消息）
     */
    private void chatReturnRefersh() {
        if (!TextUtils.isEmpty(CURR_SELECT_USERID)) {
            ChatItemDataBean userdata = new RongUtils(MessageActivity.this).getChatInfoByUserID(CURR_SELECT_USERID);
            if (userdata != null) {
                updateItem(userdata);
            }
            CURR_SELECT_USERID = null;
        }
    }

    public void updateItem(ChatItemDataBean data) {
        ArrayList<ChatItemDataBean> list = mAdapter.getList();
        if (list != null && list.size() > 0) {
            boolean tag = false;
            for (int i = 0; i < list.size(); i++) {
                ChatItemDataBean tempdata = list.get(i);
                if (tempdata != null && tempdata.userid.equalsIgnoreCase(data.userid)) {
                    list.get(i).lastcontent = data.lastcontent;
                    list.get(i).lasttime = data.lasttime;
                    list.get(i).unreadcount = data.unreadcount;
                    mAdapter.notifyDataSetChanged();
                    tag = true;
                    break;
                }
            }
            if (!tag) {
                mAdapter.addItem(data);
            }
        } else {
            list.add(data);
            mAdapter.setList(list);
            mListView.setAdapter(mAdapter);
        }
    }

    //删除某个对话的提示框
    public void remindDailog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MessageActivity.this);
        builder.setTitle("提示");
        builder.setMessage("您确定要删除该对话吗？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                delConversation(position);
            }
        });
        builder.setNegativeButton("取消", null);
        builder.create().show();
    }


    //删除某个对话
    public void delConversation(final int position) {
        HashMap<String, String> params = new HashMap<>();
        params.put("id", mAdapter.getList().get(position).id);
        new BaseDataPresenter(MessageActivity.this).loadData(DataUrl.DEL_CONTACT_RECORD, params, null, new IBaseDataListener() {
            @Override
            public void onSuccess(BaseDataBean data) {
                mAdapter.getList().remove(position);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(BaseDataBean data) {
                Global.showToast(data.msg, MessageActivity.this);
            }

            @Override
            public void onError() {
            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mReceviceBoardcast != null) {
            mReceviceBoardcast.unregisterBroadcast();
        }
        if (mSendBoardcast != null) {
            mSendBoardcast.unregisterBroadcast();
        }
    }


}
