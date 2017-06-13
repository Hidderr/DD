package com.coco3g.daishu.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by lisen on 16/2/17 23:23.
 */
public class BaseDataBean implements Serializable {
    public int code;

    public String msg;

    public Object response;

    public HashMap<Object, Object> request;

    public ArrayList<String> other;

}
