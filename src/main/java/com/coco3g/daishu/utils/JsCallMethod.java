package com.coco3g.daishu.utils;

/**
 * Created by lisen on 16/7/20.
 */
public class JsCallMethod {
    OnJsReturnInterface onjsreturninterface;

    public void configMenu(String json, String callback) {
        returnData(json, callback);
    }

    public void setOnJsReturnDataListener(OnJsReturnInterface onjsreturninterface) {
        this.onjsreturninterface = onjsreturninterface;
    }

    public interface OnJsReturnInterface {
        void returnData(String json, String callback);
    }

    private void returnData(String json, String callback) {
        if (onjsreturninterface != null) {
            onjsreturninterface.returnData(json, callback);
        }
    }

}
