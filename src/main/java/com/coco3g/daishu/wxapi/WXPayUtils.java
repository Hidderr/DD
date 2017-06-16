package com.coco3g.daishu.wxapi;

import android.content.Context;
import android.util.Log;
import android.util.Xml;

import com.coco3g.daishu.data.Constants;
import com.coco3g.daishu.wxapi.MD5;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.xmlpull.v1.XmlPullParser;

import java.io.StringReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class WXPayUtils {
	Context mContext;
	PayReq req;
	final IWXAPI msgApi;
	String prepayID;

	public WXPayUtils(Context context, String prepayid) {
		mContext = context;
		msgApi = WXAPIFactory.createWXAPI(mContext, null);
		msgApi.registerApp(Constants.WEIXIN_APP_ID);
		req = new PayReq();
		prepayID = prepayid;

	}

	public void genPayReq() {
		req.appId = Constants.WEIXIN_APP_ID;
		req.partnerId = Constants.MCH_ID;
		req.prepayId = prepayID;
		req.packageValue = "Sign=WXPay";
		req.nonceStr = genNonceStr();
		req.timeStamp = String.valueOf(genTimeStamp());
		List<NameValuePair> signParams = new LinkedList<NameValuePair>();
		signParams.add(new BasicNameValuePair("appid", req.appId));
		signParams.add(new BasicNameValuePair("noncestr", req.nonceStr));
		signParams.add(new BasicNameValuePair("package", req.packageValue));
		signParams.add(new BasicNameValuePair("partnerid", req.partnerId));
		signParams.add(new BasicNameValuePair("prepayid", req.prepayId));
		signParams.add(new BasicNameValuePair("timestamp", req.timeStamp));
		req.sign = genAppSign(signParams);
	}

	/**
	 * 调起支付
	 * 
	 */
	public void sendPayReq() {
		msgApi.registerApp(Constants.WEIXIN_APP_ID);
		msgApi.sendReq(req);
	}

	/**
	 * 生成sign
	 * 
	 * @param params
	 * @return
	 */
	private String genAppSign(List<NameValuePair> params) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < params.size(); i++) {
			sb.append(params.get(i).getName());
			sb.append('=');
			sb.append(params.get(i).getValue());
			sb.append('&');
		}
		sb.append("key=");
		sb.append(Constants.API_KEY);
		String appSign = MD5.getMessageDigest(sb.toString().getBytes()).toUpperCase();
		return appSign;
	}

	/**
	 * 生成随机字符串 noncestr
	 * 
	 * @return
	 */
	private String genNonceStr() {
		Random random = new Random();
		return MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
	}

	/**
	 * 生成时间戳 timestamp
	 * 
	 * @return
	 */
	private long genTimeStamp() {
		return System.currentTimeMillis() / 1000;
	}

	// public class GetPrepayIdTask extends AsyncTask<Void, Void, Map<String,
	// String>> {
	//
	// private ProgressDialog dialog;
	//
	// @Override
	// protected void onPreExecute() {
	// dialog = ProgressDialog.show(mContext, "提示", "稍等");
	// }
	//
	// @Override
	// protected void onPostExecute(Map<String, String> result) {
	// if (dialog != null) {
	// dialog.dismiss();
	// }
	// String prepayID = result.get("prepay_id");
	// if (!TextUtils.isEmpty(prepayID)) {
	// genPayReq();
	// sendPayReq();
	// }
	// }
	//
	// @Override
	// protected void onCancelled() {
	// super.onCancelled();
	// }
	//
	// @Override
	// protected Map<String, String> doInBackground(Void... params) {
	// String url =
	// String.format("https://api.mch.weixin.qq.com/pay/unifiedorder");
	// String entity = genProductArgs();
	// byte[] buf = Util.httpPost(url, entity);
	// String content = new String(buf);
	// Map<String, String> xml = decodeXml(content);
	// return xml;
	// }
	// }

	public Map<String, String> decodeXml(String content) {

		try {
			Map<String, String> xml = new HashMap<String, String>();
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(new StringReader(content));
			int event = parser.getEventType();
			while (event != XmlPullParser.END_DOCUMENT) {
				String nodeName = parser.getName();
				switch (event) {
				case XmlPullParser.START_DOCUMENT:
					break;
				case XmlPullParser.START_TAG:
					if ("xml".equals(nodeName) == false) {
						// 实例化student对象
						xml.put(nodeName, parser.nextText());
					}
					break;
				case XmlPullParser.END_TAG:
					break;
				}
				event = parser.next();
			}

			return xml;
		} catch (Exception e) {
			Log.e("orion", e.toString());
		}
		return null;

	}

	// //
	// private String genProductArgs() {
	// // StringBuffer xml = new StringBuffer();
	//
	// try {
	// String nonceStr = genNonceStr();
	//
	// // xml.append("</xml>");
	// List<NameValuePair> packageParams = new LinkedList<NameValuePair>();
	// packageParams.add(new BasicNameValuePair("appid", Constants.APP_ID));
	// packageParams.add(new BasicNameValuePair("body", "aaa"));
	// packageParams.add(new BasicNameValuePair("mch_id", Constants.MCH_ID));
	// packageParams.add(new BasicNameValuePair("nonce_str", nonceStr));
	// packageParams.add(new BasicNameValuePair("notify_url",
	// "http://192.168.1.111:8082/testPay/aa.html"));
	// packageParams.add(new BasicNameValuePair("out_trade_no",
	// genOutTradNo()));
	// packageParams.add(new BasicNameValuePair("spbill_create_ip",
	// "127.0.0.1"));
	// packageParams.add(new BasicNameValuePair("total_fee", "10"));
	// packageParams.add(new BasicNameValuePair("trade_type", "APP"));
	//
	// String sign = genPackageSign(packageParams);
	// packageParams.add(new BasicNameValuePair("sign", sign));
	//
	// // String xmlstring = toXml(packageParams);
	// String xmlstring = new String(toXml(packageParams).getBytes(),
	// "ISO8859-1");
	//
	// return xmlstring;
	//
	// } catch (Exception e) {
	// Log.e("eee", "genProductArgs fail, ex = " + e.getMessage());
	// return null;
	// }
	//
	// }
	//
	// private String genOutTradNo() {
	// Random random = new Random();
	// return
	// MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
	// }
	//
	// /**
	// * 生成签名
	// */
	//
	// private String genPackageSign(List<NameValuePair> params) {
	// StringBuilder sb = new StringBuilder();
	//
	// for (int i = 0; i < params.size(); i++) {
	// sb.append(params.get(i).getName());
	// sb.append('=');
	// sb.append(params.get(i).getValue());
	// sb.append('&');
	// }
	// sb.append("key=");
	// sb.append(Constants.API_KEY);
	//
	// String packageSign =
	// MD5.getMessageDigest(sb.toString().getBytes()).toUpperCase();
	// Log.e("orion", packageSign);
	// return packageSign;
	// }
	//
	// private String toXml(List<NameValuePair> params) {
	// StringBuilder sb = new StringBuilder();
	// sb.append("<xml>");
	// for (int i = 0; i < params.size(); i++) {
	// sb.append("<" + params.get(i).getName() + ">");
	//
	// sb.append(params.get(i).getValue());
	// sb.append("</" + params.get(i).getName() + ">");
	// }
	// sb.append("</xml>");
	//
	// Log.e("orion", sb.toString());
	// return sb.toString();
	// }
}
