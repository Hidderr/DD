package com.coco3g.daishu.model;

import com.coco3g.daishu.listener.IBaseDataListener;

import java.util.HashMap;

/**
 * Created by lisen on 2017/3/6.
 */

public interface IBaseDataModel {

    void loadData(String url, HashMap<String, String> param, String msg, IBaseDataListener listener);

    void uploadFiles(String url, HashMap<String, String> param, String files, String msg, IBaseDataListener listener);

}
