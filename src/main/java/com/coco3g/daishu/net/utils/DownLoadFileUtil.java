package com.coco3g.daishu.net.utils;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import com.coco3g.daishu.R;
import com.coco3g.daishu.view.MyProgressDialog;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class DownLoadFileUtil {
	Context mContext;
	String mUrl;
	private DownloadManager downloadManager;
	private SharedPreferences prefs;
	private static final String DL_ID = "downloadId";
	IntentFilter filterComplete = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
	//
	MyProgressDialog mProgress = null;

	// IntentFilter filterClick = new
	// IntentFilter(DownloadManager.ACTION_NOTIFICATION_CLICKED);

	public DownLoadFileUtil(Context context) {
		mContext = context;
		downloadManager = (DownloadManager) mContext.getSystemService(Activity.DOWNLOAD_SERVICE);
		prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
	}

	public void addDownLoadUrl(String url) {
		mUrl = url;
		mProgress = MyProgressDialog.show(mContext, "正在下载，请稍候...", false, false);
		startDownLoad();
	}

	private void startDownLoad() {
		// 开始下载
		Uri resource = Uri.parse(encodeGB(mUrl));
		Request request = new Request(resource);
		request.setAllowedNetworkTypes(Request.NETWORK_MOBILE | Request.NETWORK_WIFI);
		request.setAllowedOverRoaming(false);
		// 设置文件类型
		MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
		String mimeString = mimeTypeMap.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(mUrl));
		request.setMimeType(mimeString);
		// 在通知栏中显示
		request.setShowRunningNotification(true);
		request.setVisibleInDownloadsUi(true);
		// sdcard的目录下的download文件夹
		String filename = mUrl.substring(mUrl.lastIndexOf("/") + 1);
		if (TextUtils.isEmpty(filename)) {
			filename = "daling.apk";
		}
		request.setDestinationInExternalPublicDir("/download/", filename);
		request.setTitle(mContext.getResources().getString(R.string.app_name) + "正在下载");
		long id = downloadManager.enqueue(request);
		// 保存id
		prefs.edit().putLong(DL_ID, id).commit();
		// queryDownloadStatus();
		//
		mContext.registerReceiver(receiver, filterComplete);
		// mContext.registerReceiver(receiver, filterClick);
	}

	/**
	 * 如果服务器不支持中文路径的情况下需要转换url的编码。
	 * 
	 * @param string
	 * @return
	 */
	public String encodeGB(String string) {
		// 转换中文编码
		String split[] = string.split("/");
		for (int i = 1; i < split.length; i++) {
			try {
				split[i] = URLEncoder.encode(split[i], "GB2312");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			split[0] = split[0] + "/" + split[i];
		}
		split[0] = split[0].replaceAll("\\+", "%20");// 处理空格
		return split[0];
	}

	private BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			queryDownloadStatus(intent);
		}
	};

	private void queryDownloadStatus(Intent intent) {
		// DownloadManager.Query query = new DownloadManager.Query();
		// query.setFilterById(prefs.getLong(DL_ID, 0));
		// Cursor c = downloadManager.query(query);
		// if (c.moveToFirst()) {
		// int status =
		// c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
		// switch (status) {
		// case DownloadManager.STATUS_PAUSED:
		// Log.e("down", "STATUS_PAUSED");
		// case DownloadManager.STATUS_PENDING:
		// Log.e("down", "STATUS_PENDING");
		// case DownloadManager.STATUS_RUNNING:
		// // 正在下载，不做任何事情
		// Log.e("down", "STATUS_RUNNING");
		// break;
		// case DownloadManager.STATUS_SUCCESSFUL:
		// // 完成
		// Log.e("down", "下载完成");
		// long myDwonloadID =
		// intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
		// if (prefs.getLong(DL_ID, 0) == myDwonloadID) {
		// Intent install = new Intent(Intent.ACTION_VIEW);
		// Uri downloadFileUri =
		// downloadManager.getUriForDownloadedFile(myDwonloadID);
		// install.setDataAndType(downloadFileUri,
		// "application/vnd.android.package-archive");
		// install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// mContext.startActivity(install);
		// }
		// break;
		// case DownloadManager.STATUS_FAILED:
		// // 清除已下载的内容，重新下载
		// Log.e("down", "STATUS_FAILED");
		// break;
		// }
		// }
		if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction())) {
			DownloadManager.Query query = new DownloadManager.Query();
			// 在广播中取出下载任务的id
			long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
			query.setFilterById(id);
			Cursor c = downloadManager.query(query);
			if (c.moveToFirst()) {
				// 获取文件下载路径
				String filename = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
				if (filename != null) {
					Intent intentInstall = new Intent(Intent.ACTION_VIEW);
					intentInstall.setDataAndType(Uri.fromFile(new File(filename)), "application/vnd.android.package-archive");
					mContext.startActivity(intentInstall);
					mProgress.cancel();
				}
			}
		}
		// else if
		// (DownloadManager.ACTION_NOTIFICATION_CLICKED.equals(intent.getAction()))
		// {
		// long[] ids =
		// intent.getLongArrayExtra(DownloadManager.EXTRA_NOTIFICATION_CLICK_DOWNLOAD_IDS);
		// // 点击通知栏取消下载
		// downloadManager.remove(ids);
		// Global.showToast("取消下载", mContext);
		// }
	}
}
