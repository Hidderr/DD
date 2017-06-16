package com.coco3g.daishu.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coco3g.daishu.R;
import com.coco3g.daishu.bean.ChatItemDataBean;
import com.coco3g.daishu.data.Global;
import com.coco3g.daishu.utils.DateTime;
import com.coco3g.daishu.utils.DisplayImageOptionsUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Created by lisen on 2017/5/2.
 */

public class MessageAdapter extends BaseAdapter {
    Context mContext;

    ArrayList<ChatItemDataBean> messageList = new ArrayList<>();
    RelativeLayout.LayoutParams avatar_lp;

    public MessageAdapter(Context mContext) {
        this.mContext = mContext;
        avatar_lp = new RelativeLayout.LayoutParams(Global.screenWidth / 10, Global.screenWidth / 10);
        avatar_lp.addRule(RelativeLayout.CENTER_VERTICAL);
    }


    public void setList(ArrayList<ChatItemDataBean> orderList) {
        this.messageList = orderList;
    }

    public void addList(ArrayList<ChatItemDataBean> orderList) {
        this.messageList.addAll(orderList);
    }

    public void clearList() {
        if (messageList != null && messageList.size() > 0) {
            messageList.clear();
        }
    }

    public void addItem(ChatItemDataBean itemdata) {
        if (messageList != null) {
            messageList.add(1, itemdata);
        }
        notifyDataSetChanged();
    }

    public ArrayList<ChatItemDataBean> getList() {
        return messageList;
    }

    @Override
    public int getCount() {
        if (messageList == null) {
            return 0;
        }
        return messageList.size();
    }

    @Override
    public Object getItem(int i) {
        return messageList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    ViewHolder viewHolder;

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            viewHolder = new ViewHolder();
            LayoutInflater lay = LayoutInflater.from(mContext);
            view = lay.inflate(R.layout.a_contacts_item, null);
            viewHolder.mImageAvatar = (ImageView) view.findViewById(R.id.image_contacts_item_avater);
            viewHolder.mImageAvatar.setLayoutParams(avatar_lp);
            viewHolder.mTxtName = (TextView) view.findViewById(R.id.tv_contact_item_name);
            viewHolder.mTxtLastMsg = (TextView) view.findViewById(R.id.tv_contact_item_last_msg);
            viewHolder.mTxtUnread = (TextView) view.findViewById(R.id.tv_contact_item_unread);
            viewHolder.mTxtLastTime = (TextView) view.findViewById(R.id.tv_contact_item_last_time);
            viewHolder.mRelativeUnread = (RelativeLayout) view.findViewById(R.id.relative_contact_item_unread);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        ChatItemDataBean itemdate = messageList.get(i);
        //头像
        ImageLoader.getInstance().displayImage(itemdate.avatar, viewHolder.mImageAvatar, new
                DisplayImageOptionsUtils().circleImageInit());
        //用户名字
        viewHolder.mTxtName.setText(itemdate.nickname);
        // 最新聊天记录
        if (TextUtils.isEmpty(itemdate.lastcontent)) {
            viewHolder.mTxtLastMsg.setText("没有任何消息");
        } else {
            viewHolder.mTxtLastMsg.setText(itemdate.lastcontent);
        }
        // 最近聊天时间
        if (!TextUtils.isEmpty(itemdate.updatetime)) {
            try {
                viewHolder.mTxtLastTime.setText(DateTime.getDateFormated("MM-dd HH:mm", Long.parseLong(itemdate.updatetime) * 1000));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // 未读消息
        if ("0".equals(itemdate.unreadcount)) {
            viewHolder.mRelativeUnread.setVisibility(View.INVISIBLE);
            viewHolder.mTxtUnread.setVisibility(View.INVISIBLE);
        } else {
            viewHolder.mRelativeUnread.setVisibility(View.VISIBLE);
            viewHolder.mTxtUnread.setVisibility(View.VISIBLE);
            viewHolder.mTxtUnread.setText(itemdate.unreadcount);
        }
        return view;
    }

    private class ViewHolder {
        public RelativeLayout mRelativeUnread;
        public ImageView mImageAvatar;
        public TextView mTxtName, mTxtLastMsg, mTxtUnread, mTxtLastTime;
    }


}
