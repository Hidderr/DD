package com.android.volley.toolbox;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lisen on 2017/3/8.
 */

public class MultipartRequest<T> extends Request<T> {
    private MultipartEntity entity = new MultipartEntity();
    private final Response.Listener<T> mListener;
    private List<File> mFileParts;
    private String mFilePartName;
    private Map<String, String> mParams;
    private Gson mGson;
    private Class<T> mClass;

    /**
     * 单个文件上传
     *
     * @param url
     * @param listener
     * @param errorListener
     * @param filePartName
     * @param file
     * @param params
     */
    public MultipartRequest(String url, Class clazz, Response.Listener<T> listener, Response.ErrorListener errorListener, String filePartName, File file,
                            Map<String, String> params) {
        super(Method.POST, url, errorListener);
        mGson = new Gson();
        mFileParts = new ArrayList<>();
        if (file != null) {
            mFileParts.add(file);
        }
        mFilePartName = filePartName;
        mListener = listener;
        mParams = params;
        mClass = clazz;
        buildMultipartEntity();
    }

//    /**
//     * 多个文件，对应一个key
//     *
//     * @param url
//     * @param errorListener
//     * @param listener
//     * @param filePartName
//     * @param files
//     * @param params
//     */
//    public MultipartRequest(String url, Response.Listener<String> listener, Response.ErrorListener errorListener, String filePartName, List<File> files,
//                            Map<String, String> params) {
//        super(Method.POST, url, errorListener);
//        mFilePartName = filePartName;
//        mListener = listener;
//        mFileParts = files;
//        mParams = params;
//        buildMultipartEntity();
//    }

    private void buildMultipartEntity() {
        if (mFileParts != null && mFileParts.size() > 0) {
            for (File file : mFileParts) {
                entity.addPart(mFilePartName, new FileBody(file));
            }
        }
        try {
            if (mParams != null && mParams.size() > 0) {
                for (Map.Entry<String, String> entry : mParams.entrySet()) {
                    entity.addPart(entry.getKey(), new StringBody(entry.getValue(), Charset.forName("UTF-8")));
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBodyContentType() {
        return entity.getContentType().getValue();
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            entity.writeTo(bos);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bos.toByteArray();
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        String jsonString = "";
        try {
            jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e) {
            jsonString = new String(response.data);
        }
        Log.e("json", jsonString);
        return Response.success(mGson.fromJson(jsonString, mClass), HttpHeaderParser.parseCacheHeaders(response));
//        return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
    }

    /*
     * (non-Javadoc)
     *
     * @see com.android.volley.Request#getHeaders()
     */
    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = super.getHeaders();
        if (headers == null || headers.equals(Collections.emptyMap())) {
            headers = new HashMap<>();
        }
        return headers;
    }

    @Override
    protected void deliverResponse(T response) {
        mListener.onResponse(response);
    }

    @Override
    protected VolleyError parseNetworkError(VolleyError error) {
        try {
            String jsonString = new String(error.networkResponse.data, HttpHeaderParser.parseCharset(error.networkResponse.headers));
            Log.e("json", jsonString);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return super.parseNetworkError(error);
    }
}