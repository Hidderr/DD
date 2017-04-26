package com.android.volley.toolbox;

import android.text.TextUtils;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lisen on 2017/3/3.
 * 泛型定义为Gson类型，统一数据模型
 */

public class GsonRequest<T> extends Request<T> {
    private final Listener<T> mListener;
    private Gson mGson;
    private Class<T> mClass;
    private String mToken = "";

    public GsonRequest(int method, String url, Class<T> clazz, Listener<T> listener, Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        mGson = new Gson();
        mListener = listener;
        mClass = clazz;
    }

    public GsonRequest(String url, Class<T> clazz, Listener<T> listener, Response.ErrorListener errorListener) {
        this(Method.GET, url, clazz, listener, errorListener);
    }

    @Override
    protected void deliverResponse(T response) {
        mListener.onResponse(response);
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers, "UTF-8"));
            Log.e("json", jsonString);
            return Response.success(mGson.fromJson(jsonString, mClass), HttpHeaderParser.parseCacheHeaders(response));
        } catch (Exception e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected VolleyError parseNetworkError(VolleyError error) {
        try {
            String jsonString = new String(error.networkResponse.data, HttpHeaderParser.parseCharset(error.networkResponse.headers, "UTF-8"));
            Log.e("json", jsonString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.parseNetworkError(error);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        if (!TextUtils.isEmpty(mToken)) {
            Map<String, String> map = new HashMap<>();
            map.put("token", mToken);
            map.put("timestep", System.currentTimeMillis() + "");
            return map;
        }
        return super.getHeaders();
    }

    public GsonRequest setToken(String token) {
        mToken = token;
        return this;
    }
}
