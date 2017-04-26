package com.coco3g.daishu.net.utils;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by lisen on 2017/3/6.
 */

public class VolleyController {
    private Context mContext;
    public static VolleyController mInstance;
    private RequestQueue mReqQueue;

    public VolleyController(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * 获取Volley工具的实例
     *
     * @return
     */
    public static VolleyController getInstance(Context context) {
        if (mInstance == null) {
            synchronized (VolleyController.class) {
                if (mInstance == null) {
                    mInstance = new VolleyController(context);
                }
            }
        }
        return mInstance;
    }

    /**
     * 获取请求队列的实例
     *
     * @return
     */
    private RequestQueue getRequestQueue() {
        if (mReqQueue == null) {
            synchronized ((VolleyController.class)) {
                if (mReqQueue == null) {
                    mReqQueue = Volley.newRequestQueue(mContext);
                }
            }
        }
        return mReqQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

}
