package com.listviewaddheader.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.listviewaddheader.common.PathCommonDefines;

public class HelperUtils {
	private static final String TAG = HelperUtils.class.getSimpleName();
	public static final int SINA_SHARE_SUCCESS = 10;
	public static final int SINA_SHARE_ERROR = 11;
	public static final int SINA_SHARE_EXCEPTION = 12;
	public static final int TENCENT_SHARE_SUCCESS = 13;
	public static final int TENCENT_SHARE_ERROR = 14;
	public static final int QZONE_SHARE_SUCCESS = 18;
	public static final int QZONE_SHARE_ERROR = 19;
	public static final int TENCENT_SHARE_EXCEPTION = 15;
	public static final int FAVORITE_IMAGE_SUCCESS = 16;
	public static final int FAVORITE_IMAGE_FAILE = 17;

	private static final String addr = "http://api.sms.cn/mt/";
	private static final String userid = "userid=";

	private static HelperUtils mInstance = null;

	public static HelperUtils getInstance() {
		if (mInstance == null) {
			mInstance = new HelperUtils();
		}
		return mInstance;
	}

	public static String getNumberFromString(String str) {
		String str2 = "";
		if (str != null && !"".equals(str)) {
			for (int i = 0; i < str.length(); i++) {
				if (str.charAt(i) >= 48 && str.charAt(i) <= 57) {
					str2 += str.charAt(i);
				}
			}
		}
		return str2;
	}



	public static String send(String msg, String phone) throws Exception {
		String massage = java.net.URLEncoder.encode(msg);
		String pwdString = encryption("xianshisong31112501");
		String straddr = addr + "?uid=31112501&pwd=" + pwdString + "&mobile="
				+ phone + "&encode=utf8&content=" + massage;
		StringBuffer sb = new StringBuffer(straddr);
		URL url = new URL(sb.toString());
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("POST");
		BufferedReader in = new BufferedReader(new InputStreamReader(
				url.openStream()));
		String inputline = in.readLine();
		System.out.println(inputline);
		return inputline;
	}

	/**
	 * ��ȡ��֤��
	 */
	public static String read(String str) throws IOException {
		File file = new File(str);
		FileInputStream fis = new FileInputStream(file);
		StringBuffer sb = new StringBuffer();

		BufferedInputStream bis = new BufferedInputStream(fis);
		BufferedReader read = new BufferedReader(new InputStreamReader(bis));
		int c = 0;
		while ((c = read.read()) != -1) {
			sb.append((char) c);
		}
		read.close();
		bis.close();
		fis.close();
		Log.i(TAG, sb.toString());
		String verify = sb.toString();
		return verify;
	}

	/**
	 * ��ȡ6λ�������
	 */
	public static int getSixNum() {
		int numcode = (int) ((Math.random() * 9 + 1) * 100000);
		return numcode;
	}

	/**
	 * MD5����
	 * 
	 * @param enc
	 * @return
	 */
	public static String encryption(String enc) {
		String md5 = null;
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			byte[] bs = enc.getBytes();
			digest.update(bs);
			md5 = byte2hex(digest.digest());
			Log.i("md5", md5);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return md5;
	}

	private static String byte2hex(byte[] b) {
		String hs = "";
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			stmp = (java.lang.Integer.toHexString(b[n] & 0xFF));
			if (stmp.length() == 1)
				hs = hs + "0" + stmp;
			else
				hs = hs + stmp;
		}
		return hs;
	}

	/**
	 * ��ȡ��ǰ����
	 */
	public static String getCurrentDate() {
		final Calendar c = Calendar.getInstance();
		int mYear = c.get(Calendar.YEAR); // ��ȡ��ǰ���
		int mMonth = c.get(Calendar.MONTH);// ��ȡ��ǰ�·�
		int mDay = c.get(Calendar.DAY_OF_MONTH);// ��ȡ��ǰ�·ݵ����ں���
		return mYear + "-" + mMonth + "-" + mDay;
	}

	/**
	 * ��ʽ���ļ�·��(���ڱ���Ƿ���·��)
	 * 
	 * @param filePath
	 *            �ļ�·��
	 * @return ��ʽ������ļ�·��
	 */
	public static String enCodeFilePath(String filePath) {
		filePath = "file:" + filePath;
		return filePath;
	}

	/**
	 * px to sp
	 * 
	 * @param pxValue
	 * @param fontScale
	 * @return
	 */
	public static int px2sp(float pxValue, Context context) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	public static String unEnCodeFilePath(String filePath) {
		if (filePath != null) {
			filePath = filePath.replace("file:", "");
		}
		return filePath;
	}

	/**
	 * �ж��Ƿ����ļ�·��
	 * 
	 * @param filePath
	 * @return
	 */
	public static boolean isFilePath(String filePath) {
		if (filePath != null) {
			if (filePath.contains("file:")) {
				return true;
			}
		}

		return false;
	}

	// �ж�ע�������Ƿ���д�淶
	public static boolean emailFormat(String email) {
		boolean tag = true;
		final String pattern1 = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
		final Pattern pattern = Pattern.compile(pattern1);
		final Matcher mat = pattern.matcher(email);
		if (!mat.find()) {
			tag = false;
		}
		return tag;
	}

	/**
	 * ����Ƿ����SDCard
	 * 
	 * @return
	 */
	public static boolean hasSdcard() {
		String state = Environment.getExternalStorageState();
		if (state.equals(Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}


	/**
	 * ���ð����ҹ��ģʽ
	 * 
	 * @param window
	 *            ��ǰ��Window����
	 * @param isNightModel
	 *            �Ƿ���ҹ��ģʽ
	 */
	public void setScreenBrightness(Window window, boolean isNightModel) {

		WindowManager.LayoutParams lp = window.getAttributes();

		if (isNightModel) {

			lp.screenBrightness = 0.4f;

		} else {

			lp.screenBrightness = 1.0f;

		}

		window.setAttributes(lp);

	}

	/**
	 * ��ʾ��ʾ(String)
	 * 
	 * @param context
	 * @param msg
	 */
	public void showToast(Context context, String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}

	/**
	 * ��ʾ��ʾ(resource id)
	 * 
	 * @param context
	 * @param msg
	 */
	public void showToast(Context context, int msg) {
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}

	/**
	 * ��תActivity
	 * 
	 * @param context
	 * @param to
	 */
	public static void jump(Context context, Class to) {
		Intent intent = new Intent(context, to);
		context.startActivity(intent);
	}

	/**
	 * ��֤�ַ����Ƿ�Ϊ�� �� ""
	 * 
	 * @param str
	 * @return
	 */
	public static boolean validateString(String str) {
		if (str != null && !"".equals(str.trim()))
			return false;

		return true;
	}

	/**
	 * �õ��汾��
	 * 
	 * @return
	 */
	public static String getVersionName(Context context) {
		PackageInfo pinfo;
		String versionName = "";
		try {
			pinfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(),
							PackageManager.GET_CONFIGURATIONS);
			versionName = pinfo.versionName;

		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		return versionName;
	}

	/**
	 * ���������(�ƹ�ƽ̨0=��վ,1=����,2=����,3=��׿�ٷ�)
	 * 
	 * @return
	 */
	public static String getChannelCode(Context context) {
		String channelCode = "0";
		try {
			ApplicationInfo ai = context.getPackageManager()
					.getApplicationInfo(context.getPackageName(),
							PackageManager.GET_META_DATA);
			Bundle bundle = ai.metaData;
			if (bundle != null) {

				Object obj = bundle.get("UMENG_CHANNEL");
				if (obj != null) {
					channelCode = obj.toString();
				}
				Logger.d(TAG, "channelCode:" + channelCode + " obj:" + obj);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return channelCode;
	}

	/***
	 * ��������Ƿ����
	 */
	public static boolean isConnect(Context context) {
		// ��ȡ�ֻ��������ӹ�����󣨰�����wi-fi,net�����ӵĹ���
		try {
			ConnectivityManager connectivity = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity != null) {
				// ��ȡ�������ӹ���Ķ���
				NetworkInfo info = connectivity.getActiveNetworkInfo();
				if (info != null && info.isConnected()) {
					// �жϵ�ǰ�����Ƿ��Ѿ�����
					if (info.getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			Log.v("error", e.toString());
		}
		return false;
	}

	// ����������ͼƬ�Ĵ�С�����жϣ����õ����ʵ����ű���������2��1/2,3��1/3
	public static int computeSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength,
				maxNumOfPixels);

		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}
		return roundedSize;
	}

	private static int computeInitialSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;

		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
				.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 80 : (int) Math.min(
				Math.floor(w / minSideLength), Math.floor(h / minSideLength));

		if (upperBound < lowerBound) {
			// return the larger one when there is no overlapping zone.
			return lowerBound;
		}

		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}

	/**
	 * ����ͼƬ����ɾ��ͼƬ
	 * 
	 * @param fileName
	 *            �ļ���
	 */
	public void deletePictureByFilePath(final Context context,
			final Handler handler, final String url) {

		new Thread() {
			@Override
			public void run() {
				if (url != null && !url.equals("")) {
					// urlת��HashCode
					String urlHashCode = convertUrlToFileName(url);

					// PictureSDUtil myPictureSDUtil = new
					// PictureSDUtil(context);
					File file = new File(PathCommonDefines.MY_FAVOURITE_FOLDER,
							urlHashCode + ".png");
					if (file != null) {
						file.delete();
					}
					Message message = new Message();
					message.what = 2;
					handler.sendMessage(message);

				}
			}
		}.start();
	}

	/**
	 * ����ͼƬ���ƿ���ͼƬ
	 * 
	 * @param fileName
	 *            �ļ���
	 */
	public void copyPictureByFilePath(final Handler handler, final String url) {

		new Thread() {
			@Override
			public void run() {
				try {

					if (url != null && !url.equals("")) {

						// urlת��HashCode
						String urlHashCode = convertUrlToFileName(url);

						copyFile(urlHashCode, urlHashCode);

						Message message = new Message();
						message.what = 3;
						handler.sendMessage(message);

					}
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}.start();
	}

	/**
	 * �ļ��ĸ���(��һ���ļ��и��Ƶ��µ��ļ���)
	 * 
	 * @param sourceFile
	 *            Դ�ļ�
	 * @param targetFile
	 *            Ŀ���ļ�
	 */
	public void copyFile(String filename, String urlHashCode)
			throws IOException {

		File myFavouriteFolder = new File(PathCommonDefines.MY_FAVOURITE_FOLDER);
		if (!myFavouriteFolder.exists()) {
			myFavouriteFolder.mkdirs();
		}

		// �½��ļ����������������л���
		FileInputStream input = new FileInputStream(
				PathCommonDefines.PHOTOCACHE_FOLDER + File.separator
						+ urlHashCode);
		BufferedInputStream inBuff = new BufferedInputStream(input);

		// �½��ļ���������������л���
		FileOutputStream output = new FileOutputStream(
				PathCommonDefines.MY_FAVOURITE_FOLDER + File.separator
						+ filename);
		BufferedOutputStream outBuff = new BufferedOutputStream(output);

		// ��������
		byte[] b = new byte[1024 * 5];
		int len;
		while ((len = inBuff.read(b)) != -1) {
			outBuff.write(b, 0, len);
		}
		// ˢ�´˻���������
		outBuff.flush();

		// �ر���
		inBuff.close();
		outBuff.close();
		output.close();
		input.close();
	}

	/**
	 * ��URLת����HashCode
	 * 
	 * @param url
	 * @return
	 */
	public String convertUrlToFileName(String url) {
		String fn = null;
		if (url != null && url.trim().length() > 0) {
			if (url.contains(".png")) {

				fn = String.valueOf(url.hashCode()) + ".png";

			} else {

				fn = String.valueOf(url.hashCode()) + ".jpg";

			}
		}
		return fn;
	}

	/**
	 * ��md5��ʽ�����ַ���
	 * 
	 * @param content
	 *            �ַ���
	 * @param length
	 *            ���صĳ���,֧��16λ��32λ,��length=16,length=32
	 * @return ���ؼ��ܺ���ַ���
	 */
	public static String getMd5(String content, int length) {
		try {
			MessageDigest bmd5 = MessageDigest.getInstance("MD5");
			bmd5.update(content.getBytes());
			int i;
			StringBuffer buf = new StringBuffer();
			byte[] b = bmd5.digest();
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			String md5Content = buf.toString();
			switch (length) {
			case 16:
				md5Content = md5Content.substring(0, 16);
				break;
			case 32:
			default:
				break;
			}
			return md5Content;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}

	public ByteArrayOutputStream getByteArrayOutputStreamByInputStream(
			InputStream inputStream) throws Exception {

		byte[] buffer = new byte[1024];
		int len = -1;
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		while ((len = inputStream.read(buffer)) != -1) {
			byteArrayOutputStream.write(buffer, 0, len);
		}
		return byteArrayOutputStream;
	}
}
