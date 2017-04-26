package com.coco3g.daishu.listener;

import com.coco3g.daishu.bean.BaseDataBean;

/**
 * Created by lisen on 2017/3/6.
 */

public interface IBaseDataListener {

    void onSuccess(BaseDataBean data);

    void onFailure(BaseDataBean data);

    void onError();

}
