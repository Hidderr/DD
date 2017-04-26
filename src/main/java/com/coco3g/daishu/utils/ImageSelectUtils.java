package com.coco3g.daishu.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.RelativeLayout;

import com.coco3g.daishu.data.Global;
import com.lqr.imagepicker.ImagePicker;
import com.lqr.imagepicker.bean.ImageItem;
import com.lqr.imagepicker.view.PhotosPupopwindow;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by coco3g on 17/2/28.
 */

public class ImageSelectUtils {
    public PhotosPupopwindow mPopupwindow;
    private Context mContext;
    //
    public static String protraitPath = ""; // 需要剪裁的图片路径

    //压缩图片的集合(原图)
    private ArrayList<ImageItem> mImageList = new ArrayList<>();
    //裁剪压缩后的图片保存路径
    private ArrayList<String> mUploadImageList = new ArrayList<>();

    //压缩图片完成的监听
    OnImageCompressFinishedListener onImageCompressFinishedListener;


    public ImageSelectUtils(Context context) {
        this.mContext = context;
    }

    /**
     * @param context
     * @param mRelativeRoot Activity布局的根目录
     */
    //选择头像
    public void chooseImagesPop(final Context context, RelativeLayout mRelativeRoot) {
        mContext = context;
        mPopupwindow = new PhotosPupopwindow(mContext, mRelativeRoot);
        mPopupwindow.setOnPopupWindowClickListener(new PhotosPupopwindow.OnPopupWindowClickListener() {
            @Override
            public void choosePhotos(int result) {  //0:拍照  1:相册  2:取消
                if (result == 0) {//0:拍照
                    ImagePicker.getInstance().takePicture((Activity) mContext, ImagePicker.REQUEST_CODE_TAKE);
                } else if (result == 1) {// 1:相册
                    ImagePicker.getInstance().setShowCamera(false)
                            .setMultiMode(false)
                            .setCrop(true)
                            .start((Activity) mContext);
                }
            }
        });
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            if (requestCode == ImagePicker.REQUEST_CODE_SELECT_IMAGES) {  //照相的返回码
                startActionCrop(Uri.fromFile(ImagePicker.getInstance().getTakeImageFile()));  //图片裁剪
            } else if (requestCode == ImagePicker.REQUEST_CODE_CROP) {     //裁剪的返回码

            }
        }
    }

    /**
     * 拍照后裁剪
     *
     * @param uri 原始图片
     */
    public void startActionCrop(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("output", getUploadTempFile(uri));
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);// 裁剪框比例
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 800);// 输出图片大小
        intent.putExtra("outputY", 800);
        intent.putExtra("scale", true);// 去黑边
        intent.putExtra("scaleUpIfNeeded", true);// 去黑边
        ((Activity) mContext).startActivityForResult(intent, ImagePicker.REQUEST_CODE_CROP);
    }

    // 裁剪头像的绝对路径
    private Uri getUploadTempFile(Uri uri) {
        String filePath = Global.getPath(mContext) + File.separator + Global.localThumbPath;
        File savedir = new File(filePath);
        if (!savedir.exists()) {
            savedir.mkdirs();
        }
        String timeStamp = DateTime.getDateFormated("yyyyMMddHHmmss");
        String thePath = ImageUtils.getAbsolutePathFromNoStandardUri(uri);

        // 如果是标准Uri
        if (TextUtils.isEmpty(thePath)) {
            thePath = ImageUtils.getAbsoluteImagePath((Activity) mContext, uri);
        }
        String ext = Global.getFileFormat(thePath);
        ext = TextUtils.isEmpty(ext) ? "jpg" : ext;
        // 照片命名
        String cropFileName = "daling_crop_" + timeStamp + "." + ext;
        // 裁剪头像的绝对路径
        protraitPath = filePath + cropFileName;
        File protraitFile = new File(protraitPath);
        Uri cropUri = Uri.fromFile(protraitFile);
        return cropUri;
    }


    //压缩图片
    public void compressImage(ArrayList<ImageItem> imageList) {
        mImageList = imageList;
        new LoadThumbThread().start();
    }


    //对头像图片做处理
    class LoadThumbThread extends Thread {
        @Override
        public void run() {
            super.run();
            mUploadImageList.clear();
            for (ImageItem item : mImageList) {
                String temppath = Global.getPath(mContext) + File.separator + Global.localThumbPath;
                File f = new File(temppath);
                if (!f.exists()) {
                    f.mkdirs();
                }
                temppath = temppath + File.separator + "thumb_" + Global.getFileName(item.path);
                f = new File(temppath);
                if (!f.exists()) {
                    Bitmap bitmap = ImageUtils.getPicThumbnail(item.path, 1000, ImageUtils.getExifOrientation(item.path));
                    ImageUtils.saveBitmap(temppath, bitmap, 100);
                    if (bitmap != null && !bitmap.isRecycled()) {
                        bitmap.recycle();
                        bitmap = null;
                    }
                }
                mUploadImageList.add(temppath);
            }
            ((Activity) mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onImageCompressFinishedListener.imageCompressFinished(mUploadImageList);
                }
            });
        }
    }


    public interface OnImageCompressFinishedListener {
        void imageCompressFinished(ArrayList<String> list);
    }

    public void setOnImageCompressFinishedListener(OnImageCompressFinishedListener onImageCompressFinishedListener) {
        this.onImageCompressFinishedListener = onImageCompressFinishedListener;
    }


}
