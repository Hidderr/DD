package com.coco3g.daishu.presenter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.coco3g.daishu.bean.BaseDataBean;
import com.coco3g.daishu.data.Global;
import com.coco3g.daishu.listener.IBaseDataListener;
import com.coco3g.daishu.model.IBaseDataModel;
import com.coco3g.daishu.net.utils.Coco3gNetRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lisen on 2017/3/6.
 */

public class BaseDataPresenter implements IBaseDataModel {
    Context mContext;

    public BaseDataPresenter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void loadData(String url, HashMap<String, String> param, String msg, final IBaseDataListener listener) {
        boolean isshow = TextUtils.isEmpty(msg) ? false : true;
        new Coco3gNetRequest(mContext, "post", url, isshow, msg).addRequestModel(new BaseDataBean()).addRequestParams(param).setHandler(new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case Coco3gNetRequest.NET_RESULT_CODE_SUCCESS:
                        BaseDataBean resultdata = (BaseDataBean) msg.obj;
                        Object object = resultdata.response;
                        if (object instanceof Map) {
                            Map hashmap = (Map)object;
                            Object tempobject = hashmap.get("token");
                            if (tempobject instanceof String) {
                                String token = (String) tempobject;
                                Global.serializeData(mContext, token, Global.APP_CACHE);
                            }
                        }
                        listener.onSuccess((BaseDataBean) msg.obj);
                        break;
                    case Coco3gNetRequest.NET_RESULT_CODE_FAILURE:
                        BaseDataBean data = (BaseDataBean) msg.obj;
                        listener.onFailure(data);
                        break;
                    case Coco3gNetRequest.NET_RESULT_CODE_ERROR:
                        listener.onError();
                        break;
                }
            }
        }).run();
    }

    @Override
    public void uploadFiles(String url, HashMap<String, String> param, String files, String msg, final IBaseDataListener listener) {
        boolean isshow = TextUtils.isEmpty(msg) ? false : true;
        new Coco3gNetRequest(mContext, "upload", url, isshow, msg).addRequestModel(new BaseDataBean()).addRequestParams(param).addUploadFiles(files).setHandler(new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case Coco3gNetRequest.NET_RESULT_CODE_SUCCESS:
                        listener.onSuccess((BaseDataBean) msg.obj);
                        break;
                    case Coco3gNetRequest.NET_RESULT_CODE_FAILURE:
                        BaseDataBean data = (BaseDataBean) msg.obj;
                        listener.onFailure(data);
                        break;
                    case Coco3gNetRequest.NET_RESULT_CODE_ERROR:
                        listener.onError();
                        break;
                }
            }
        }).run();
    }
}
