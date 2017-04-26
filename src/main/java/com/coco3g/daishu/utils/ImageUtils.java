package com.coco3g.daishu.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import com.coco3g.daishu.data.Global;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;

/**
 * Created by lisen on 16/1/10.
 */
public class ImageUtils {
    public final static String SDCARD_MNT = "/mnt/sdcard";
    public final static String SDCARD = "/sdcard";
    static SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 图片转换二进制流
     *
     * @param path
     * @return
     */
    public static byte[] Bitmap2Bytes(String path) {
        Bitmap bm = BitmapFactory.decodeFile(path);
        if (bm != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.PNG, 70, baos);
            return baos.toByteArray();
        } else {
            return null;
        }
    }

    /**
     * Bitmap转换为数据流
     *
     * @param bitmap
     * @return
     */
    public byte[] bitmapToByte(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        bitmap.recycle();
        return baos.toByteArray();
    }

    /**
     * 向sdcard中写入文件
     *
     * @param c
     * @param filename
     * @param b
     * @param dir
     * @return
     */
    public static String saveBitmapToSDCard(Context c, String filename, byte[] b, String dir) {
        File f = new File(Global.getPath(c) + File.separator + dir);
        if (!f.exists()) {
            f.mkdirs();
        }
        String path = f.getAbsolutePath() + File.separator + filename + ".jpg";
        File file = new File(path);
        if (!file.exists()) {
            try {
                OutputStream out = new FileOutputStream(file);
                out.write(b);
                out.flush();
                out.close();
                return file.getAbsolutePath();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return path;
        }

    }

    /**
     * 写图片文件到SD卡
     *
     * @param context
     * @param filePath
     * @param bitmap
     * @param quality
     * @return
     */
    public static String saveImageToSD(Context context, String filePath, Bitmap bitmap, int quality) {
        try {
            if (bitmap != null) {
                File file = new File(filePath.substring(0, filePath.lastIndexOf(File.separator)));
                if (!file.exists()) {
                    file.mkdirs();
                }
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos);
                bos.flush();
                bos.close();
                if (context != null) {
                    scanPhoto(context, filePath);
                }
                return filePath;
            } else {
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }


    /**
     * 写图片文件到SD卡
     *
     * @throws IOException
     */
    public static String saveImageToSD(Context context, String filePath, Bitmap bitmap, Bitmap.CompressFormat format, int quality) throws IOException {
        try {
            if (bitmap != null) {
                File file = new File(filePath.substring(0, filePath.lastIndexOf(File.separator)));
                if (!file.exists()) {
                    file.mkdirs();
                }
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos);
                bos.flush();
                bos.close();
                if (context != null) {
                    scanPhoto(context, filePath);
                }
                return filePath;
            } else {
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 让Gallery上能马上看到该图片
     */
    private static void scanPhoto(Context ctx, String imgFileName) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File file = new File(imgFileName);
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        ctx.sendBroadcast(mediaScanIntent);
    }
//    public static Bitmap getPicThumbnail(String path, int newHeight) { 
//        BitmapFactory.Options options = new BitmapFactory.Options(); 
//        options.inPreferredConfig = Bitmap.Config.RGB_565; 
//        options.inPurgeable = true; 
//        options.inInputShareable = true; 
//        options.inJustDecodeBounds = true; 
//        // bm暂时为null 
//        Bitmap bm = BitmapFactory.decodeFile(path, options); 
//        int height = options.outHeight; 
//        int sampleSize = (int) (height / (float) newHeight); 
//        if (sampleSize <= 0) { 
//            sampleSize = 1; 
//        } 
//        options.inSampleSize = sampleSize; 
//        options.inJustDecodeBounds = false; 
//        bm = BitmapFactory.decodeFile(path, options);  
//        return bm; 
//    }

    public static Bitmap loadImgThumbnail(String filePath, int w, int h) {
        Bitmap bitmap = getBitmapByPath(filePath);
        return zoomBitmap(bitmap, w, h);
    }

    /**
     * 获取bitmap
     *
     * @param filePath
     * @return
     */
    public static Bitmap getBitmapByPath(String filePath) {
        return getBitmapByPath(filePath, null);
    }

    public static Bitmap getBitmapByPath(String filePath, BitmapFactory.Options opts) {
        FileInputStream fis = null;
        Bitmap bitmap = null;
        try {
            File file = new File(filePath);
            fis = new FileInputStream(file);
            bitmap = BitmapFactory.decodeStream(fis, null, opts);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } finally {
            try {
                fis.close();
            } catch (Exception e) {
            }
        }
        return bitmap;
    }

    /**
     * 下载图片
     *
     * @param url      下载地址
     * @param dirpath  下载目录
     * @param filename 文件名F
     * @return
     */
    public static String downloadBitmap(String url, String dirpath, String filename) {
        if (url == null || url.equalsIgnoreCase(""))
            return null;
        //
        File dir = new File(dirpath);
        if (!dir.exists())
            dir.mkdirs();
        File f = new File(dir.getAbsoluteFile() + File.separator + ".nomedia");
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        String path = dir.getAbsolutePath() + File.separator + filename;
        f = new File(path);
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            f.delete();
            try {
                f.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        byte[] b = null;
        try {
            b = getBlob(url);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        FileOutputStream fileoutputstream = null;
        if (b != null) {
            try {
                fileoutputstream = new FileOutputStream(path);
                fileoutputstream.write(b);
                fileoutputstream.flush();
                return path;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            } finally {
                try {
                    if (fileoutputstream != null)
                        fileoutputstream.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        } else {
            return null;
        }
    }

    public static byte[] getBlob(String url) {
        if (url == null)
            return null;
        HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 5000);
        HttpConnectionParams.setSoTimeout(httpParams, 5000);
        HttpClient httpclient = new DefaultHttpClient(httpParams);
        byte[] buf = null;
        HttpGet httpget = null;
        try {
            httpget = new HttpGet(url);
            HttpResponse mResponse;
            HttpEntity mEntity;
            try {
                mResponse = httpclient.execute(httpget);
                int statusCode = mResponse.getStatusLine().getStatusCode();
                if (statusCode != 200)
                    return null;
            } catch (ClientProtocolException e) {
                return null;
            } catch (Exception e) {
                return null;
            }
            mEntity = mResponse.getEntity();
            if (mEntity == null)
                return null;
            try {
                buf = EntityUtils.toByteArray(mEntity);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return buf;
    }

    /**
     * 放大缩小图片
     *
     * @param bitmap
     * @param w
     * @param h
     * @return
     */
    public static Bitmap zoomBitmap(Bitmap bitmap, int w, int h) {
        Bitmap newbmp = null;
        if (bitmap != null) {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            Matrix matrix = new Matrix();
            float scaleWidht = ((float) w / width);
            float scaleHeight = ((float) h / height);
            matrix.postScale(scaleWidht, scaleHeight);
            newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        }
        return newbmp;
    }

    /**
     * 获取图片缩略图
     *
     * @param path
     * @param newHeight
     * @return
     */
    public static Bitmap getPicThumbnail(String path, int newHeight, int angle) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inJustDecodeBounds = true;
        Bitmap bm = BitmapFactory.decodeFile(path, options);
        int height = options.outHeight;
        int sampleSize = (int) (height / (float) newHeight);
        if (sampleSize <= 0) {
            sampleSize = 1;
        }
        options.inSampleSize = sampleSize;
        options.inJustDecodeBounds = false;
        bm = BitmapFactory.decodeFile(path, options);
        if (angle != 0) {
            bm = rotaingImageView(angle, bm);
        }
        return bm;
    }

    /**
     * 旋转图片
     *
     * @param angle
     * @param bitmap
     * @return
     */
    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        //旋转图片 动作
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizedBitmap;
    }

    /**
     * 判断当前Url是否标准的content://样式，如果不是，则返回绝对路径
     *
     * @param mUri
     * @return
     */
    public static String getAbsolutePathFromNoStandardUri(Uri mUri) {
        String filePath = null;

        String mUriString = mUri.toString();
        mUriString = Uri.decode(mUriString);

        String pre1 = "file://" + SDCARD + File.separator;
        String pre2 = "file://" + SDCARD_MNT + File.separator;

        if (mUriString.startsWith(pre1)) {
            filePath = Environment.getExternalStorageDirectory().getPath() + File.separator + mUriString.substring(pre1.length());
        } else if (mUriString.startsWith(pre2)) {
            filePath = Environment.getExternalStorageDirectory().getPath() + File.separator + mUriString.substring(pre2.length());
        }
        return filePath;
    }

    /**
     * 通过uri获取文件的绝对路径
     *
     * @param uri
     * @return
     */
    @SuppressWarnings("deprecation")
    public static String getAbsoluteImagePath(Activity context, Uri uri) {
        String imagePath = "";
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.managedQuery(uri, proj, // Which columns to
                // return
                null, // WHERE clause; which rows to return (all rows)
                null, // WHERE clause selection arguments (none)
                null); // Order-by clause (ascending by name)

        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                imagePath = cursor.getString(column_index);
            }
        }

        return imagePath;
    }

    /**
     * 保存图片到本地
     *
     * @param bitmapPath
     * @param mBitmap
     * @param quality
     * @return
     */
    public static String saveBitmap(String bitmapPath, Bitmap mBitmap, int quality) {
        // File f = new File(Global.getPath(context) + File.separator +
        // Global.CASE_IMAGE);
        File file = new File(bitmapPath);
        if (file.exists()) {
            try {
                file.delete();
                file.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        //
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "";
        }
        mBitmap.compress(Bitmap.CompressFormat.JPEG, quality, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        try {
            fOut.close();
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }

    }

    public static int getExifOrientation(String filepath) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(filepath);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }
}
