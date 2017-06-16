package com.coco3g.daishu.net.utils;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.coco3g.daishu.activity.App;
import com.coco3g.daishu.bean.BaseDataBean;
import com.coco3g.daishu.bean.ChatItemDataBean;
import com.coco3g.daishu.bean.RongMessageBaseBean;
import com.coco3g.daishu.data.DataUrl;
import com.coco3g.daishu.data.Global;
import com.coco3g.daishu.data.TypevauleGotoDictionary;
import com.coco3g.daishu.listener.IBaseDataListener;
import com.coco3g.daishu.presenter.BaseDataPresenter;
import com.coco3g.daishu.utils.Coco3gBroadcastUtils;
import com.coco3g.daishu.utils.DateTime;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import io.rong.imkit.RongIM;
import io.rong.imkit.manager.IUnReadMessageObserver;
import io.rong.imkit.userInfoCache.RongUserInfoManager;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.location.message.RealTimeLocationStartMessage;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.UserInfo;
import io.rong.message.FileMessage;
import io.rong.message.ImageMessage;
import io.rong.message.InformationNotificationMessage;
import io.rong.message.LocationMessage;
import io.rong.message.RecallNotificationMessage;
import io.rong.message.TextMessage;
import io.rong.message.VoiceMessage;

/**
 * Created by coco3g on 17/4/1.
 */

public class RongUtils implements RongIM.ConversationBehaviorListener {
    private Context mContext;

    public RongUtils(Context context) {
        mContext = context;
    }

    public void init() {
        Global.RONG_TOKEN = (String) Global.readSerializeData(mContext, Global.RONGTOKEN_INFO);
//        Global.RONG_TOKEN = "PLNT/CzpxIbJa620PjxCcbcZzDCMcD72O/MkwBXRDiFsujHvO+4hRT9ECMhCwG3rxqPnbu3e42Spl03U1UgVIA==";
        if (!TextUtils.isEmpty(Global.RONG_TOKEN)) {
            connect(Global.RONG_TOKEN);   // 连接融云
        } else {
            getRongCloudToken(); // 获取token
        }
    }


    //连接融云
    private void connect(String token) {

        if (mContext.getApplicationInfo().packageName.equals(App.getCurProcessName(mContext.getApplicationContext()))) {

            RongIM.connect(token, new RongIMClient.ConnectCallback() {
                /**
                 * Token 错误。可以从下面两点检查 1.  Token 是否过期，如果过期您需要向 App Server 重新请求一个新的 Token
                 *                  2.  token 对应的 appKey 和工程里设置的 appKey 是否一致
                 */
                @Override
                public void onTokenIncorrect() {
                    Log.e("融云连接", "token错误");
                    getRongCloudToken();
                }

                /**
                 * 连接融云成功
                 * @param userid 当前 token 对应的用户 id
                 */
                @Override
                public void onSuccess(String userid) {
                    Log.e("融云连接", "onSuccess   " + "userid " + userid);
//                    setUserInfo();

                    /*设置用户头像*/
                    RongIM.getInstance().setCurrentUserInfo(new UserInfo(Global.USERINFOMAP.get("id"), Global.USERINFOMAP.get("realname"),
                            Uri.parse(Global.USERINFOMAP.get("avatar"))));
                    RongIM.getInstance().setMessageAttachedUserInfo(true);

                    RongIM.setConversationBehaviorListener(RongUtils.this);
                    //接收消息的监听
                    setOnreceiveMessageListener();

                     /*设置总的未读消息监听*/
                    setUnReadListener();

                    //设置文本消息的未读消息的气泡提示功能
                    RongIM.getInstance().enableNewComingMessageIcon(true);//显示新消息提醒
                    RongIM.getInstance().enableUnreadMessageIcon(true);//显示未读消息数目
                    //
                    // 接收消息监听器
                    RongIM.setOnReceiveMessageListener(new RongIMClient.OnReceiveMessageListener() {
                        @Override
                        public boolean onReceived(Message message, int c) {
                            MessageContent messageContent = message.getContent();
                            final ChatItemDataBean itemdata = PackaingItem(message.getSenderUserId(), message, messageContent);
//                            Intent intent = new Intent();
//                            intent.setAction("receive_new_message");
//                            intent.putExtra("itemdata", itemdata);
//                            mContext.sendBroadcast(intent);
                            App app = (App) ((Activity) mContext).getApplication();
                            app.setmChatItemDataBean(itemdata);
                            new Coco3gBroadcastUtils(mContext).sendBroadcast(Coco3gBroadcastUtils.RECEVICE_RONG_MESSAGE_FLAG, null);
                            //
                            ((Activity) mContext).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // TODO Auto-generated method stub
                                    createChat(itemdata.userid, itemdata.lastcontent);
                                }
                            });
                            //
//                            showNotice(itemdata.lastcontent);
                            return false;
                        }
                    });
                    if (RongIM.getInstance() != null) {
                        //设置自己发出的消息监听器。
                        RongIM.getInstance().setSendMessageListener(new RongIM.OnSendMessageListener() {
                            @Override
                            public Message onSend(Message message) {
                                message.getContent().setUserInfo(new UserInfo(Global.USERINFOMAP.get("id"), Global.USERINFOMAP.get("nickname"),
                                        Uri.parse(Global.USERINFOMAP.get("avatar"))));
//                                TextMessage tm = (TextMessage) message.getContent();
//                                SpannableString temContent = faceConversionUtil.getExpressionString(mContext, tm.getContent());
//                                TextMessage myTextMessage = TextMessage.obtain("aaaaa" + "");
//                                message.setContent(myTextMessage);
                                return message;
                            }

                            @Override
                            public boolean onSent(Message message, RongIM.SentMessageErrorCode sentMessageErrorCode) {
                                if (message.getSentStatus() == Message.SentStatus.FAILED) { // 发送失败
                                    return false;
                                } else {
                                    String chatid = "";
                                    try {
                                        chatid = message.getTargetId();
                                        final ChatItemDataBean itemdata = PackaingItem(chatid, message, message.getContent());
//                                            TextMessage textMessage = (TextMessage) message.getContent();
//                                            String msg = textMessage.getContent();
//                                        noticeServerChatContent(chatid, itemdata.lastcontent);
                                        //
//                                        Intent intent = new Intent();
//                                        intent.setAction("receive_new_message");
//                                        intent.putExtra("itemdata", itemdata);
//                                        mContext.sendBroadcast(intent);
                                        Bundle b = new Bundle();
                                        b.putSerializable("targetID", chatid);
                                        new Coco3gBroadcastUtils(mContext).sendBroadcast(Coco3gBroadcastUtils.SEND_RONG_MESSAGE_FLAG, b);
                                        createChat(chatid, itemdata.lastcontent);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }
                                return false;
                            }
                        });
                    }
                    // 连接状态监听
                    if (RongIM.getInstance() != null) {
                        RongIM.getInstance().setConnectionStatusListener(new RongIMClient.ConnectionStatusListener() {
                            @Override
                            public void onChanged(final ConnectionStatus connectionStatus) {
                                ((Activity) mContext).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // TODO Auto-generated method stub
                                        // 断开连接，发送广播
                                        Bundle bundle = new Bundle();
                                        bundle.putInt("connection_state", connectionStatus.getValue());
                                        new Coco3gBroadcastUtils(mContext).sendBroadcast(Coco3gBroadcastUtils.RONGIM_DISCONNECTION_FLAG, bundle);
                                    }
                                });
                            }
                        });
                    }
                }

                /**
                 * 连接融云失败
                 * @param errorCode 错误码，可到官网 查看错误码对应的注释
                 */
                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {
                    Log.e("融云连接", "onError  " + errorCode);
                }
            });
        }
    }

    //断开连接
    public void disConnect() {
        if (RongIM.getInstance() != null) {
            RongIM.getInstance().disconnect();
        }
    }


    //开启回话列表
    public void startConversation(String username, String userid) {
        if (RongIM.getInstance() != null) {
            RongIM.getInstance().startPrivateChat(mContext, userid, username);
        }
    }


    //获取token
    private void getRongCloudToken() {
        HashMap<String, String> params = new HashMap<>();
        new BaseDataPresenter(mContext).loadData(DataUrl.GET_RONGCLOUD_TOKEN, params, null, new IBaseDataListener() {
            @Override
            public void onSuccess(BaseDataBean data) {
                Map<String, String> tokenMap = (Map<String, String>) data.response;
                Global.RONG_TOKEN = tokenMap.get("rc_token");
                Global.serializeData(mContext, tokenMap.get("rc_token"), Global.RONGTOKEN_INFO);
                connect(Global.RONG_TOKEN);
                Log.e("获取token", "成功" + Global.RONG_TOKEN);
            }

            @Override
            public void onFailure(BaseDataBean data) {
                Log.e("获取token", "onFailure");
            }

            @Override
            public void onError() {
                Log.e("获取token", "onError");
            }
        });
    }

    /**
     * 与某用户创建聊天会话
     *
     * @param uid
     */
    private void createChat(String uid, String content) {
        HashMap<String, String> params = new HashMap<>();
        params.put("chatid", uid);
        params.put("msg", content);
        new BaseDataPresenter(mContext).loadData(DataUrl.ADD_CHAT_ITEM, params, null, new IBaseDataListener() {
            @Override
            public void onSuccess(BaseDataBean data) {

            }

            @Override
            public void onFailure(BaseDataBean data) {

            }

            @Override
            public void onError() {

            }


        });
    }


    //刷新用户信息
    public void refreshUserInfo() {
        if (RongIM.getInstance() != null) {
            RongIM.getInstance().refreshUserInfoCache(new UserInfo(Global.USERINFOMAP.get("id"), Global.USERINFOMAP.get("realname"),
                    Uri.parse(Global.USERINFOMAP.get("avatar"))));
        }
    }


    /**
     * 清空与某人的聊天记录
     *
     * @param userid
     */
    public void deleteChatByUserID(String userid) {
        RongIM.getInstance().getRongIMClient().clearMessages(Conversation.ConversationType.PRIVATE, userid);
        ignoreUnreadMessage(userid);
    }



    public void ignoreUnreadMessage(String targetID) {
        RongIM.getInstance().getRongIMClient().clearMessagesUnreadStatus(Conversation.ConversationType.PRIVATE, targetID);
    }



    /**
     * 封装消息实体
     */
    private ChatItemDataBean PackaingItem(String uid, Message message, MessageContent messageContent) {
        ChatItemDataBean itemdata = new ChatItemDataBean();
        if (messageContent instanceof RealTimeLocationStartMessage) {
            itemdata.lastcontent = "[位置共享]";
        } else if (messageContent instanceof InformationNotificationMessage) {
            InformationNotificationMessage inm = (InformationNotificationMessage) messageContent;
            itemdata.lastcontent = inm.getMessage();
        } else if (messageContent instanceof TextMessage) {//文本消息
            TextMessage tm = (TextMessage) messageContent;
            itemdata.lastcontent = tm.getContent();
        } else if (messageContent instanceof RecallNotificationMessage) {
            itemdata.lastcontent = "[撤回了一条信息]";
        } else if (messageContent instanceof ImageMessage) {//图片消息
            itemdata.lastcontent = "[图片]";
            itemdata = getRongUserInfo(itemdata);
        } else if (messageContent instanceof VoiceMessage) {//语音消息
            itemdata.lastcontent = "[语音]";
        } else if (messageContent instanceof LocationMessage) { // 位置消息
            itemdata.lastcontent = "[位置]";
        } else if (messageContent instanceof FileMessage) { // 文件消息
            itemdata.lastcontent = "[文件]";
        }
        //
        itemdata.userid = uid;
        itemdata = getRongUserInfo(itemdata);
        itemdata.lasttime = DateTime.getDateFormated("yy-MM-dd HH:mm", message.getReceivedTime());
        int count = RongIM.getInstance().getRongIMClient().getUnreadCount(Conversation.ConversationType.PRIVATE, itemdata.userid); // 未读消息个数
        itemdata.unreadcount = count + "";
        return itemdata;
    }

    public ChatItemDataBean getChatInfoByUserID(String userid) {
        if (RongIM.getInstance() == null) {
            return null;
        }
        try {
            Conversation conversion = RongIM.getInstance().getConversation(Conversation.ConversationType.PRIVATE, userid);
            if (conversion != null) {
                MessageContent messageContent = conversion.getLatestMessage();
                ChatItemDataBean itemdata = new ChatItemDataBean();
                itemdata.userid = userid;
                if (messageContent instanceof RealTimeLocationStartMessage) {
                    itemdata.lastcontent = "[位置共享]";
                } else if (messageContent instanceof InformationNotificationMessage) {
                    InformationNotificationMessage inm = (InformationNotificationMessage) messageContent;
                    itemdata.lastcontent = inm.getMessage();
                } else if (messageContent instanceof RecallNotificationMessage) {
                    itemdata.lastcontent = "[撤回了一条信息]";
                } else if (messageContent instanceof TextMessage) {//文本消息
                    TextMessage tm = (TextMessage) messageContent;
                    itemdata.lastcontent = tm.getContent();
                    itemdata = getRongUserInfo(itemdata);
                } else if (messageContent instanceof ImageMessage) {//图片消息
                    itemdata.lastcontent = "[图片]";
                    itemdata = getRongUserInfo(itemdata);
                } else if (messageContent instanceof VoiceMessage) {//语音消息
                    itemdata.lastcontent = "[语音]";
                    itemdata = getRongUserInfo(itemdata);
                } else if (messageContent instanceof FileMessage) { // 文件
                    itemdata.lastcontent = "[文件]";
                    itemdata = getRongUserInfo(itemdata);
                } else if (messageContent instanceof LocationMessage) { // 位置消息
                    itemdata.lastcontent = "[位置]";
                    itemdata = getRongUserInfo(itemdata);
                }
                itemdata.lasttime = DateTime.getDateFormated("yy-MM-dd HH:mm", conversion.getReceivedTime());
                itemdata.unreadcount = conversion.getUnreadMessageCount() + "";
                itemdata.receivestate = conversion.getReceivedStatus().isRead();
                itemdata.sendstate = conversion.getSentStatus().getValue();
                return itemdata;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public ChatItemDataBean getChatInfoByObject(Map<String, String> userinfo) {
        if (RongIM.getInstance() == null) {
            return null;
        }
        try {
            Conversation conversion = RongIM.getInstance().getConversation(Conversation.ConversationType.PRIVATE, userinfo.get("userid"));
            if (conversion != null) {
                MessageContent messageContent = conversion.getLatestMessage();
                ChatItemDataBean itemdata = new ChatItemDataBean();
                itemdata.userid = userinfo.get("userid");
                if (messageContent instanceof RealTimeLocationStartMessage) {
                    itemdata.lastcontent = "[位置共享]";
                } else if (messageContent instanceof InformationNotificationMessage) {
                    InformationNotificationMessage inm = (InformationNotificationMessage) messageContent;
                    itemdata.lastcontent = inm.getMessage();
                } else if (messageContent instanceof RecallNotificationMessage) {
                    itemdata.lastcontent = "[撤回了一条信息]";
                } else if (messageContent instanceof TextMessage) {//文本消息
                    TextMessage tm = (TextMessage) messageContent;
                    itemdata.lastcontent = tm.getContent();
                    itemdata = getRongUserInfo(itemdata);
                } else if (messageContent instanceof ImageMessage) {//图片消息
                    itemdata.lastcontent = "[图片]";
                    itemdata = getRongUserInfo(itemdata);
                } else if (messageContent instanceof VoiceMessage) {//语音消息
                    itemdata.lastcontent = "[语音]";
                    itemdata = getRongUserInfo(itemdata);
                } else if (messageContent instanceof FileMessage) { // 文件
                    itemdata.lastcontent = "[文件]";
                    itemdata = getRongUserInfo(itemdata);
                } else if (messageContent instanceof LocationMessage) { // 位置消息
                    itemdata.lastcontent = "[位置]";
                    itemdata = getRongUserInfo(itemdata);
                }
                itemdata.lasttime = DateTime.getDateFormated("yy-MM-dd HH:mm", conversion.getReceivedTime());
                itemdata.unreadcount = conversion.getUnreadMessageCount() + "";
                itemdata.receivestate = conversion.getReceivedStatus().isRead();
                itemdata.sendstate = conversion.getSentStatus().getValue();
                return itemdata;
            } else {
                ChatItemDataBean itemdata = new ChatItemDataBean();
                itemdata.userid = userinfo.get("userid");
                itemdata.avatar = userinfo.get("avatar");
                itemdata.nickname = userinfo.get("username") + "";
                itemdata.lasttime = "";
                itemdata.lastcontent = "";
                itemdata.unreadcount = "0";
                return itemdata;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private ChatItemDataBean getRongUserInfo(ChatItemDataBean data) {
        UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(data.userid);
        if (userInfo != null) {
            data.nickname = userInfo.getName();
            data.avatar = userInfo.getPortraitUri().toString();
            if (!(data.avatar.startsWith("http://") || data.avatar.startsWith("https://"))) {
                data.avatar = DataUrl.BASE_URL + data.avatar;
            }
        }
        return data;
    }


    //未读消息总数的监听
    public void setUnReadListener() {
        if (RongIM.getInstance() == null) {
            return;
        }
        RongIM.getInstance().addUnReadMessageCountChangedObserver(new IUnReadMessageObserver() {
            @Override
            public void onCountChanged(int i) {
                //先查看是否有系统消息
                Conversation conversion = RongIM.getInstance().getConversation(Conversation.ConversationType.PRIVATE, Global.USERINFOMAP.get("adminid"));

                //
                Bundle bundle = new Bundle();
                if (conversion != null) {
                    int systemUnread = conversion.getUnreadMessageCount();
                    bundle.putInt("unreadcount", i - systemUnread);
                    if (systemUnread > 0) {
                        new Coco3gBroadcastUtils(mContext).sendBroadcast(Coco3gBroadcastUtils.RONG_UNREAD_MSG_SYSTEM, null);
                    }
                } else {
                    bundle.putInt("unreadcount", i);
                }
                new Coco3gBroadcastUtils(mContext).sendBroadcast(Coco3gBroadcastUtils.RONG_UNREAD_MSG, bundle);
            }
        }, Conversation.ConversationType.PRIVATE);
    }

    //设置与某个人的未读消息的监听
    public void setOnreceiveMessageListener() {
        RongIM.setOnReceiveMessageListener(new RongIMClient.OnReceiveMessageListener() {
            @Override
            public boolean onReceived(Message message, int i) {
                String userid = message.getSenderUserId();
                //
                Conversation conversion = RongIM.getInstance().getConversation(Conversation.ConversationType.PRIVATE, userid);
                String unread = conversion.getUnreadMessageCount() + "";
                Bundle bundle = new Bundle();
                bundle.putString("unread", unread);
                bundle.putString("userid", userid);
                Log.e("单个未读消息111", "数量 " + unread + "      userid  " + userid);
                new Coco3gBroadcastUtils(mContext).sendBroadcast(Coco3gBroadcastUtils.PERSONAL_RONG_UNREAD_MSG, bundle);
                if (userid.equals(Global.USERINFOMAP.get("adminid"))) {  //系统消息
                    new Coco3gBroadcastUtils(mContext).sendBroadcast(Coco3gBroadcastUtils.RONG_UNREAD_MSG_SYSTEM, null);
                }
                return false;
            }
        });
    }


    //获取单聊最后一条数据
    public static String getLastTxtMessageContent(String userid) {
        if (RongIM.getInstance() == null) {
            return null;
        }
        String lastcontent = "";
        try {
            Conversation conversion = RongIM.getInstance().getConversation(Conversation.ConversationType.PRIVATE, userid);
            if (conversion != null) {
                MessageContent messageContent = conversion.getLatestMessage();
                if (messageContent instanceof RealTimeLocationStartMessage) {
                    lastcontent = "[位置共享]";
                } else if (messageContent instanceof InformationNotificationMessage) {
                    InformationNotificationMessage inm = (InformationNotificationMessage) messageContent;
                    lastcontent = inm.getMessage();
                } else if (messageContent instanceof RecallNotificationMessage) {
                    lastcontent = "[撤回了一条信息]";
                } else if (messageContent instanceof TextMessage) {//文本消息
                    TextMessage tm = (TextMessage) messageContent;
                    lastcontent = tm.getContent();
                } else if (messageContent instanceof ImageMessage) {//图片消息
                    ImageMessage im = (ImageMessage) messageContent;
                    lastcontent = "[图片]";
                } else if (messageContent instanceof VoiceMessage) {//语音消息
                    VoiceMessage voiceMessage = (VoiceMessage) messageContent;
                    lastcontent = voiceMessage.getUri().toString();
                } else if (messageContent instanceof FileMessage) { // 文件
                    lastcontent = "[文件]";
                } else if (messageContent instanceof LocationMessage) { // 位置消息
                    lastcontent = "[位置]";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return lastcontent;
    }

    @Override
    public boolean onUserPortraitClick(Context context, Conversation.ConversationType conversationType, UserInfo userInfo) {
//        Intent intent = new Intent(mContext, WebActivity.class);
//        intent.putExtra("url", DataUrl.PERSONAL_INFO + userInfo.getUserId());
//        mContext.startActivity(intent);
        return true;
    }

    @Override
    public boolean onUserPortraitLongClick(Context context, Conversation.ConversationType conversationType, UserInfo userInfo) {
        return false;
    }

    @Override
    public boolean onMessageClick(Context context, View view, Message message) {
        if (message.getContent() instanceof TextMessage) {
            try {
                TextMessage textMessage = (TextMessage) message.getContent();
                String extra = textMessage.getExtra();
                Gson gson = new Gson();
                RongMessageBaseBean data = gson.fromJson(extra, RongMessageBaseBean.class);
                new TypevauleGotoDictionary(mContext).gotoViewChoose(data.url);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
        return false;
    }

    @Override
    public boolean onMessageLinkClick(Context context, String s) {
        return false;
    }

    @Override
    public boolean onMessageLongClick(Context context, View view, Message message) {
        return false;
    }

}
