package com.coco3g.daishu.view;

import android.app.ProgressDialog;
import android.content.Context;

public class MyProgressDialog extends ProgressDialog {

	public MyProgressDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public MyProgressDialog(Context context, int theme) {
		super(context, theme);
		// TODO Auto-generated constructor stub
	}

	public static MyProgressDialog show(Context context, String message, boolean indeterminate, boolean cancelable) {
		try {
			MyProgressDialog dialog = new MyProgressDialog(context, ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
			dialog.setMessage(message);
			dialog.setIndeterminate(indeterminate);
			dialog.setCancelable(cancelable);
			dialog.show();
			return dialog;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
