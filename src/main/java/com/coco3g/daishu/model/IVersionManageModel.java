package com.coco3g.daishu.model;

import com.coco3g.daishu.listener.IVersionUpdateListener;

/**
 * Created by lisen on 16/4/13.
 */
public interface IVersionManageModel {
    public void checkNewVersion(boolean showLoading, String loadingMsg, IVersionUpdateListener listener);
}
