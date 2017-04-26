package com.coco3g.daishu.net.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.coco3g.daishu.R;
import com.coco3g.daishu.data.Constants;
import com.coco3g.daishu.data.Global;
import com.coco3g.daishu.utils.Coco3gBroadcastUtils;
import com.coco3g.daishu.utils.ImageUtils;
import com.coco3g.daishu.wxapi.Util;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.tencent.connect.share.QQShare;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import java.io.File;
import java.io.IOException;

public class ShareAppUtils {
    private static Context mContext;
    String mShareUrl, mShareTitle, mShareDesc, mShareImage;
    Bitmap shareBitmap;
    /* 微信相关 */
    private IWXAPI api;
    //    /* QQ相关 */
    public static Tencent TENCENT = null;
    /* 微博相关 */
//    public IWeiboShareAPI mWeiboShareAPI;
    int supportApiLevel, mShareFlag;
    DisplayImageOptions options;

    public ShareAppUtils(Context context, int shareFlag, String url, String title, String desc, String image) {
        mContext = context;
        mShareUrl = url;
        mShareTitle = title;
        mShareDesc = desc;
        mShareImage = image;
        mShareFlag = shareFlag;
        //
        options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.mipmap.ic_launcher)
                .showImageOnFail(R.mipmap.ic_launcher)
                .resetViewBeforeLoading(true)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .considerExifParams(true)
                .displayer(new FadeInBitmapDisplayer(300))
                .build();
        ImageLoader.getInstance().loadImage(mShareImage, options, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {

            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                shareBitmap = bitmap;
                switch (mShareFlag) {
                    case 1: // qq
                        shareToQQ();
                        break;
                    case 2: // 微信
                        shareToWeiXin();
                        break;
                    case 3: // 朋友圈
                        shareToMoment();
                        break;
                    case 4: // 微博
//                        shareToSina();
                        break;

                }
            }

            @Override
            public void onLoadingCancelled(String s, View view) {

            }
        });
    }

    /**
     * 分享到微信
     */
    private void shareToWeiXin() {
        api = WXAPIFactory.createWXAPI(mContext, Constants.WEIXIN_APP_ID, true);
        api.registerApp(Constants.WEIXIN_APP_ID);
        //
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = mShareUrl;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = mShareTitle;
        msg.description = mShareDesc;
        if (shareBitmap == null) {
            shareBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher);
        }
        msg.thumbData = Util.bmpToByteArray(shareBitmap, true);
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = "webpage";
        req.message = msg;
        req.scene = SendMessageToWX.Req.WXSceneSession;
        api.sendReq(req);
    }

    /**
     * 分享到微信朋友圈
     */
    private void shareToMoment() {
        api = WXAPIFactory.createWXAPI(mContext, Constants.WEIXIN_APP_ID, true);
        api.registerApp(Constants.WEIXIN_APP_ID);
        //
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = mShareUrl;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = mShareTitle;
        msg.description = mShareDesc;
        if (shareBitmap == null) {
            shareBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher);
        }
        msg.thumbData = Util.bmpToByteArray(shareBitmap, true);
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = "webpage";
        req.message = msg;
        req.scene = SendMessageToWX.Req.WXSceneTimeline;
        api.sendReq(req);
    }

    /**
     * 分享到QQ
     */
    public void shareToQQ() {
        TENCENT = Tencent.createInstance(Constants.QQ_APP_ID, mContext.getApplicationContext());
        final Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, mShareTitle);
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, mShareDesc);
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, mShareUrl);
        if (!TextUtils.isEmpty(mShareImage) && (mShareImage.startsWith("http") || mShareImage.startsWith("https"))) {
            params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, mShareImage);
        } else {
            String filepath = Global.getPath(mContext) + File.separator + Global.localThumbPath + File.separator + "sharepic.png";
            Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher);
            try {
                ImageUtils.saveImageToSD(mContext, filepath, bitmap, Bitmap.CompressFormat.PNG, 70);
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, filepath);
        }
        // params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, mShareImage);
        // params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL,
        // "http://imgcache.qq.com/qzone/space_item/pre/0/66768.gif");
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, mContext.getResources().getString(R.string.app_name));
        // params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, "其他附加功能");
//        TENCENT.shareToQQ(((Activity) mContext), params, new IUiListener() {
//            @Override
//            public void onError(UiError e) {
//                // TODO Auto-generated method stub
//                Global.showToast(e.errorMessage + "-" + e.errorCode, mContext);
//            }
//
//            @Override
//            public void onComplete(Object arg0) {
//                // TODO Auto-generated method stub
//                new Coco3gBroadcastUtils(mContext).sendBroadcast(Coco3gBroadcastUtils.SHARE_SUCCESS, null);
//            }
//
//            @Override
//            public void onCancel() {
//                // TODO Auto-generated method stub
//                Global.showToast("用户取消", mContext);
//            }
//        });
        TENCENT.shareToQQ(((Activity) mContext), params, qqShareListener);
    }

    public static IUiListener qqShareListener = new IUiListener() {
        @Override
        public void onCancel() {
            Global.showToast("用户取消", mContext);
        }

        @Override
        public void onComplete(Object response) {
            // TODO Auto-generated method stub
            new Coco3gBroadcastUtils(mContext).sendBroadcast(Coco3gBroadcastUtils.SHARE_SUCCESS, null);
        }

        @Override
        public void onError(UiError e) {
            // TODO Auto-generated method stub
            Global.showToast(e.errorMessage + "-" + e.errorCode, mContext);
        }
    };
//    /**
//     * 分享到微博
//     */
//    private void shareToSina() {
//        mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(mContext, Constants.WEIBO_APP_KEY);
//        mWeiboShareAPI.registerApp();
//        int supportApi = mWeiboShareAPI.getWeiboAppSupportAPI();
//        // if (supportApiLevel >= 10351) {
//        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
//        weiboMessage.mediaObject = getWebpageObj();
//        SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
//        // 用transaction唯一标识一个请求
//        request.transaction = String.valueOf(System.currentTimeMillis());
//        request.multiMessage = weiboMessage;
//        //
////        AuthInfo authInfo = new AuthInfo(mContext, Constants.WEIBO_APP_KEY, Constants.REDIRECT_URL, Constants.SCOPE);
////        // Oauth2AccessToken accessToken =
////        // AccessTokenKeeper.readAccessToken(getApplicationContext());
////        Oauth2AccessToken accessToken = null;
////        String token = "";
////        if (accessToken != null) {
////            token = accessToken.getToken();
////        }
//        mWeiboShareAPI.sendRequest(((Activity) mContext), request);
////        mWeiboShareAPI.sendRequest(((Activity) mContext), request, authInfo, token, new WeiboAuthListener() {
////            @Override
////            public void onWeiboException(WeiboException e) {
////                e.printStackTrace();
////            }
////
////            @Override
////            public void onComplete(Bundle bundle) {
////                // TODO Auto-generated method stub
////                Oauth2AccessToken newToken = Oauth2AccessToken.parseAccessToken(bundle);
////                Global.showToast("onAuthorizeComplete token = " + newToken.getToken(), mContext);
////            }
////
////            @Override
////            public void onCancel() {
////                Global.showToast("用户取消", mContext);
////            }
////        });
//
//    }
//
//    /**
//     * 创建多媒体（网页）消息对象。
//     *
//     * @return 多媒体（网页）消息对象。
//     */
//    private WebpageObject getWebpageObj() {
//        WebpageObject mediaObject = new WebpageObject();
//        mediaObject.identify = Utility.generateGUID();
//        mediaObject.title = mShareTitle;
//        if (TextUtils.isEmpty(mShareDesc)) {
//            mShareDesc = mContext.getString(R.string.app_name);
//        }
//        mediaObject.description = mShareDesc;
//        if (shareBitmap == null) {
//            shareBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher);
//        }
////        // 设置 Bitmap 类型的图片到视频对象里 设置缩略图。 注意：最终压缩过的缩略图大小不得超过 32kb。
//        mediaObject.setThumbImage(shareBitmap);
//        mediaObject.actionUrl = mShareUrl;
//        mediaObject.defaultText = mShareDesc;
//        return mediaObject;
//    }
}
