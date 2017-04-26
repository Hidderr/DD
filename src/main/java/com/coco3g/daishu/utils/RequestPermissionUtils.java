package com.coco3g.daishu.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public class RequestPermissionUtils {
	Context mContext;

	public RequestPermissionUtils(Context context) {
		mContext = context;
		// aleraPermission(alertmsg, permissiontype);
	}

	public void aleraPermission(final String permissiontype, final int resultcode) {
		if (ContextCompat.checkSelfPermission(mContext, permissiontype) == PackageManager.PERMISSION_DENIED) {
			// boolean isget =
			// ActivityCompat.shouldShowRequestPermissionRationale((Activity)
			// mContext, permissiontype);
			// if (isget) {
			// AlertDialog dialog = new AlertDialog.Builder(mContext).create();
			// dialog.setMessage("需要授予拨打电话的权限 ,才能使用该功能");
			// dialog.setButton(AlertDialog.BUTTON_POSITIVE, "确定", new
			// DialogInterface.OnClickListener() {
			// @Override
			// public void onClick(DialogInterface dialog, int which) {
			//
			// }
			// });
			// dialog.show();
			// // ActivityCompat.requestPermissions((Activity) mContext, new
			// // String[] { permissiontype }, resultcode);
			// }
			ActivityCompat.requestPermissions((Activity) mContext, new String[] { permissiontype }, resultcode);
		} else {
		}
	}

}
