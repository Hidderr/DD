package com.coco3g.daishu.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by lisen on 2016/10/27.
 */

public class SplashPicListBean extends BaseDataBean {

    public ArrayList<SplashPicData> data;

    public static class SplashPicData implements Serializable {

        public String content_id;

        public String thumb;

        public String title;

        public String linkurl;

        public int goods_id;

    }

}
