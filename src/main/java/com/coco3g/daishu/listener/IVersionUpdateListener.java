package com.coco3g.daishu.listener;

import com.coco3g.daishu.bean.VersionUpdateDataBean;

/**
 * Created by lisen on 16/4/13.
 */
public interface IVersionUpdateListener extends IBaseDataListener {
    void onSuccess(VersionUpdateDataBean data);
}
