package com.coco3g.daishu.net.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.GsonRequest;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.MultipartRequest;
import com.coco3g.daishu.bean.BaseDataBean;
import com.coco3g.daishu.data.Global;
import com.coco3g.daishu.view.MyProgressDialog;
import com.google.gson.Gson;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lisen on 2017/3/6.
 */

public class Coco3gNetRequest {
    private int mTimeout = 20 * 1000; // 超时时间20s
    Context mContext;
    private String mRequestMethod = "";
    private String mUrl = "";
    private Object mObject;
    private HashMap<String, String> mParams = null;
    private String mUploadFiles = "";
    private Handler mHandler = null;
    private int mValue1 = 0;
    private int mValue2 = -1;
    //
    public final static int NET_RESULT_CODE_SUCCESS = 100; // 接口访问正确，返回正确
    public final static int NET_RESULT_CODE_FAILURE = 101; // 接口访问正确，返回错误
    public final static int NET_RESULT_CODE_ERROR = 102; // 接口访问错误
    //
    MyProgressDialog mProgress;

    public Coco3gNetRequest(Context mContext, String requestMethod, String url, boolean isShowLoading, String loadingMsg) {
        this.mContext = mContext;
        mRequestMethod = requestMethod;
        mUrl = url;
        if (isShowLoading) {
            if (TextUtils.isEmpty(loadingMsg)) {
                loadingMsg = "正在加载...";
            }
            mProgress = MyProgressDialog.show(mContext, loadingMsg, false, false);
        }
    }

    /**
     * 添加请求参数
     *
     * @param params
     * @return
     */
    public Coco3gNetRequest addRequestParams(HashMap<String, String> params) {
        mParams = params;
        return this;
    }

    /**
     * 添加请求的数据模型
     *
     * @param object
     * @return
     */
    public Coco3gNetRequest addRequestModel(Object object) {
        mObject = object;
        return this;
    }

    /**
     * 添加要上传的文件
     *
     * @param filepath
     * @return
     */
    public Coco3gNetRequest addUploadFiles(String filepath) {
        mUploadFiles = filepath;
        return this;
    }

    public Coco3gNetRequest setHandler(Handler handler) {
        mHandler = handler;
        return this;
    }

    public Coco3gNetRequest setValue1(int value1) {
        this.mValue1 = value1;
        return this;
    }

    public Coco3gNetRequest setValue2(int value2) {
        this.mValue2 = value2;
        return this;
    }

    private void sendMessage(int what, int arg1, int arg2, Object obj) {
        if (mHandler != null) {
            Message msg = new Message();
            msg.what = what;
            msg.arg1 = arg1;
            msg.arg2 = arg2;
            msg.obj = obj;
            mHandler.sendMessage(msg);
        }
    }

    public void run() {
        if (mRequestMethod.equalsIgnoreCase("get")) {
            requestJson(mUrl);
        } else if (mRequestMethod.equalsIgnoreCase("post")) {
            requestJson(mUrl, mParams);
        } else if (mRequestMethod.equalsIgnoreCase("upload")) {
            uploadRequest(mUrl, mParams, mUploadFiles);
        }
    }

    /**
     * get访问
     *
     * @param url
     * @return
     */
    public void requestJson(String url) {
        request(url);
    }

    /**
     * post访问
     *
     * @param connurl
     * @param params
     * @return
     */
    public void requestJson(String connurl, HashMap<String, String> params) {
        request(connurl, params);
    }

    /**
     * 上传文件请求
     *
     * @param url
     * @param params
     * @param path
     */
    public void uploadRequest(String url, HashMap<String, String> params, String path) {
        uploadFiles(url, params, path);
    }

    /**
     * get请求
     *
     * @param url
     */
    private void request(String url) {
        GsonRequest gsonReq = new GsonRequest(url, mObject.getClass(), new Response.Listener<Object>() {
            @Override
            public void onResponse(Object object) {
                sendMessage(NET_RESULT_CODE_SUCCESS, mValue1, mValue2, object);
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        if (mProgress != null) {
                            mProgress.cancel();
                        }
                    }
                });
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        if (mProgress != null) {
                            mProgress.cancel();
                        }
                    }
                });
                try {
                    String jsonString = new String(error.networkResponse.data, HttpHeaderParser.parseCharset(error.networkResponse.headers));
                    Gson gson = new Gson();
                    BaseDataBean data = gson.fromJson(jsonString, new BaseDataBean().getClass());
                    sendMessage(NET_RESULT_CODE_FAILURE, mValue1, mValue2, data);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    Global.showToast("服务器返回错误或网络连接失败，请检查后重试...", mContext);
                }
            }
        });
        gsonReq.setRetryPolicy(new DefaultRetryPolicy(mTimeout, 1, 1.0f));
        VolleyController.getInstance(mContext).addToRequestQueue(gsonReq);
    }

    /**
     * post请求
     *
     * @param url
     * @param params
     */
    private void request(String url, final HashMap<String, String> params) {
        GsonRequest gsonReq = new GsonRequest(Request.Method.POST, url, mObject.getClass(), new Response.Listener<Object>() {
            @Override
            public void onResponse(Object object) {
                sendMessage(NET_RESULT_CODE_SUCCESS, mValue1, mValue2, object);
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        if (mProgress != null) {
                            mProgress.cancel();
                        }
                    }
                });
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        if (mProgress != null) {
                            mProgress.cancel();
                        }
                    }
                });
                try {
                    String jsonString = new String(error.networkResponse.data, HttpHeaderParser.parseCharset(error.networkResponse.headers));
                    Gson gson = new Gson();
                    BaseDataBean data = gson.fromJson(jsonString, new BaseDataBean().getClass());
                    sendMessage(NET_RESULT_CODE_FAILURE, mValue1, mValue2, data);
                } catch (Exception e) {
                    e.printStackTrace();
                    Global.showToast("服务器返回错误或网络连接失败，请检查后重试...", mContext);
                    sendMessage(NET_RESULT_CODE_ERROR, mValue1, mValue2, null);
                }
            }

        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }

//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                HashMap<String, String> map = new HashMap<>();
//                // 从字符串中得到公钥
//                // PublicKey publicKey = RSAUtils.loadPublicKey(PUCLIC_KEY);
//                // 从文件中得到公钥
//                String source = "123456";
//                String afterencrypt = "";
//                try {
//                    InputStream inPublic = mContext.getResources().getAssets().open("rsa_public_key.pem");
//                    PublicKey publicKey = RSAUtils.loadPublicKey(inPublic);
//                    // 加密
//                    byte[] encryptByte = RSAUtils.encryptData(source.getBytes(), publicKey);
//                    // 为了方便观察吧加密后的数据用base64加密转一下，要不然看起来是乱码,所以解密是也是要用Base64先转换
//                    afterencrypt = Base64Utils.encode(encryptByte);
//                    map.put("user-info", afterencrypt);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                //
//                try {
//                    // 从字符串中得到私钥
//                    // PrivateKey privateKey = RSAUtils.loadPrivateKey(PRIVATE_KEY);
//                    // 从文件中得到私钥
//                    InputStream inPrivate = mContext.getResources().getAssets().open("rsa_private_key.pem");
//                    PrivateKey privateKey = RSAUtils.loadPrivateKey(inPrivate);
//                    // 因为RSA加密后的内容经Base64再加密转换了一下，所以先Base64解密回来再给RSA解密
//                    byte[] decryptByte = RSAUtils.decryptData(Base64Utils.decode(afterencrypt), privateKey);
//                    String decryptStr = new String(decryptByte);
//                    Log.e("sasas", decryptStr + "--");
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                return map;
//            }

        };
        gsonReq.setToken((String) Global.readSerializeData(mContext, Global.APP_CACHE));
        gsonReq.setRetryPolicy(new DefaultRetryPolicy(mTimeout, 1, 1.0f));
        VolleyController.getInstance(mContext).addToRequestQueue(gsonReq);
    }

    /**
     * 文件上传
     *
     * @param url
     * @param params
     * @param filepath
     */
    private void uploadFiles(String url, HashMap<String, String> params, String filepath) {
        File file = new File(filepath);
        if (!file.exists()) {
            Toast.makeText(mContext, "文件不存在", Toast.LENGTH_SHORT).show();
            sendMessage(NET_RESULT_CODE_ERROR, mValue1, mValue2, null);
            ((Activity) mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    if (mProgress != null) {
                        mProgress.cancel();
                    }
                }
            });
            return;
        }
        MultipartRequest uploadrequest = new MultipartRequest(url, mObject.getClass(), new Response.Listener<Object>() {
            @Override
            public void onResponse(Object object) {
                sendMessage(NET_RESULT_CODE_SUCCESS, mValue1, mValue2, object);
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        if (mProgress != null) {
                            mProgress.cancel();
                        }
                    }
                });
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        if (mProgress != null) {
                            mProgress.cancel();
                        }
                    }
                });
                try {
                    String jsonString = new String(error.networkResponse.data, HttpHeaderParser.parseCharset(error.networkResponse.headers));
                    Gson gson = new Gson();
                    BaseDataBean data = gson.fromJson(jsonString, new BaseDataBean().getClass());
                    sendMessage(NET_RESULT_CODE_FAILURE, mValue1, mValue2, data);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }, "Filedata", file, params);
        uploadrequest.setRetryPolicy(new DefaultRetryPolicy(mTimeout, 1, 1.0f));
        VolleyController.getInstance(mContext).addToRequestQueue(uploadrequest);
    }
}
