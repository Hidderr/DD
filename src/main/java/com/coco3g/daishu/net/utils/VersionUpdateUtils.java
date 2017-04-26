package com.coco3g.daishu.net.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;

import com.coco3g.daishu.bean.BaseDataBean;
import com.coco3g.daishu.data.DataUrl;
import com.coco3g.daishu.data.Global;
import com.coco3g.daishu.listener.IBaseDataListener;
import com.coco3g.daishu.presenter.BaseDataPresenter;

import java.util.Map;

public class VersionUpdateUtils {
    Context mContext;

    public VersionUpdateUtils(Context context) {
        mContext = context;
    }

    /**
     * 新版本检测
     *
     * @param showLoading
     * @param loadingMsg
     */
    public void checkUpdate(final boolean showLoading, String loadingMsg) {
        new BaseDataPresenter(mContext).loadData(DataUrl.GET_NEW_VERSION, null, null, new IBaseDataListener() {
            @Override
            public void onSuccess(BaseDataBean data) {
                if (data.response == null && showLoading) {
                    AlertDialog dialog = new AlertDialog.Builder(mContext,
                            AlertDialog.THEME_DEVICE_DEFAULT_LIGHT).setTitle("信息").setMessage("已是最新版本")
                            .setPositiveButton("关闭", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            }).create();
                    dialog.show();
                    return;
                }
                final Map<String, String> hashmap = (Map<String, String>) data.response;
                int code = Integer.valueOf(hashmap.get("code"));
                if (code > Global.getAppVersionCode(mContext)) {
                    AlertDialog dialog = new AlertDialog.Builder(mContext, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT).setCancelable(false).setTitle("信息")
                            .setMessage("发现新版本，点击下载").setPositiveButton("下载", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (Global.getAndroidSDKVersion() < 11) {
                                        Uri uri = Uri.parse(hashmap.get("appurl"));
                                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                        mContext.startActivity(intent);
                                    } else {
                                        DownLoadFileUtil dlf = new DownLoadFileUtil(mContext);
                                        dlf.addDownLoadUrl(hashmap.get("appurl"));
                                    }
                                }
                            }).setNegativeButton("关闭", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ((Activity) mContext).finish();
                                }
                            }).create();

                    dialog.show();
                }
            }

            @Override
            public void onFailure(BaseDataBean data) {
                Global.showToast(data.msg, mContext);
            }

            @Override
            public void onError() {

            }
        });
    }

//    Handler mHandlerUpdate = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            // TODO Auto-generated method stub
//            super.handleMessage(msg);
//            final VersionUpdateData info = (VersionUpdateData) msg.obj;
//
//        }
//    };
}
