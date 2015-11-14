package com.listviewaddheader.cache;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpStatus;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.listviewaddheader.common.PathCommonDefines;
import com.listviewaddheader.utils.BitmapTool;
import com.listviewaddheader.utils.HelperUtils;
import com.listviewaddheader.utils.Logger;
import com.listviewaddheader.utils.ScreenUtil;
import com.listviewaddheader.utils.ScreenUtil.Screen;

public class BitmapDownloader {
	private static final String TAG = "AsyncImageLoader";
	private static final int IO_BUFFER_SIZE = 4 * 1024;
	private HashMap<String, SoftReference<Bitmap>> mImageCacheHashMap;
	private BlockingQueue<Runnable> mBlockingQueue;
	private ThreadPoolExecutor executor;
	private Screen mScreen = null;

	public BitmapDownloader(Context context) {
		mImageCacheHashMap = new HashMap<String, SoftReference<Bitmap>>();
		mScreen = ScreenUtil.getScreenPix(context);
		// �̳߳أ����50����ÿ��ִ�У�1���������߳̽����ĳ�ʱʱ�䣺180��
		mBlockingQueue = new LinkedBlockingQueue<Runnable>();
		executor = new ThreadPoolExecutor(1, 50, 180, TimeUnit.SECONDS,
				mBlockingQueue);
	}

	public ThreadPoolExecutor getThreadPoolExecutor() {
		return executor;
	}

	public Bitmap loadImage(final String url, final ImageCallback imageCallback) {
		
		if (url == null || (url != null && url.equals(""))) {
			return null;
		}
		
		if (mImageCacheHashMap.containsKey(url)) {
			SoftReference<Bitmap> softReference = mImageCacheHashMap.get(url);
			Bitmap bitmap = softReference.get();
			if (bitmap != null) {
				return bitmap;
			}
		}
		final Handler handler = new Handler() {
			public void handleMessage(Message message) {
				if (imageCallback != null) {
					imageCallback.imageLoaded((Bitmap) message.obj, url);
				}

			}
		};

		Bitmap bitmap = fromLocalUrl(url);

		if (bitmap == null) {

			// ���̳߳���������ͼƬ������
			executor.execute(new Runnable() {
				@Override
				public void run() {
					Bitmap bitmap = fromNetWorkUrl(url);
					mImageCacheHashMap.put(url, new SoftReference<Bitmap>(
							bitmap));
					Message message = handler.obtainMessage(0, bitmap);
					handler.sendMessage(message);
				}
			});

		}

		return bitmap;
	}

	/**
	 * ����ͼƬ
	 * 
	 * @param imageUrl
	 * @return
	 */
	public Bitmap fromLocalUrl(String imageUrl) {
		Bitmap bitmap = null;

		try {
			if (imageUrl == null)
				return null;
			String imagePath = "";
			String fileName = "";

			// ��ȡurl��ͼƬ���ļ������׺
			if (imageUrl != null && imageUrl.length() != 0) {
				fileName = HelperUtils.getInstance().convertUrlToFileName(
						imageUrl);
			}

			// ͼƬ���ֻ����صĴ��·��,ע�⣺fileNameΪ�յ����
			imagePath = PathCommonDefines.PHOTOCACHE_FOLDER + "/" + fileName;

			// Log.i(TAG,"imagePath = " + imagePath);
			File file = new File(imagePath);// �����ļ�
			// Log.i(TAG,"file.toString()=" + file.toString());
			if (file.exists()) {

				bitmap = BitmapFactory.decodeFile(imagePath);

				mImageCacheHashMap.put(imageUrl, new SoftReference<Bitmap>(
						bitmap));

				return bitmap;

			}
		} catch (Exception e) {
			Logger.e(TAG, "e:" + e.getMessage());
		}
		return bitmap;
	}

	/**
	 * ����ͼƬ
	 * 
	 * @param url
	 * @return
	 */
	public Bitmap fromNetWorkUrl(String url) {
		Bitmap bitmap = null;
		try {

			if (url == null)
				return null;
			String imagePath = "";
			String fileName = "";

			// ��ȡurl��ͼƬ���ļ������׺
			if (url != null && url.length() != 0) {
				fileName = HelperUtils.getInstance().convertUrlToFileName(url);
			}

			// ͼƬ���ֻ����صĴ��·��,ע�⣺fileNameΪ�յ����
			imagePath = PathCommonDefines.PHOTOCACHE_FOLDER + "/" + fileName;

			// Log.i(TAG,"imagePath = " + imagePath);

			// �����ļ�
			File file = new File(imagePath);

			URL httpUrl = new URL(url);

			HttpURLConnection httpURLConnection = (HttpURLConnection) httpUrl
					.openConnection();

			httpURLConnection.setDoInput(true);

			httpURLConnection.connect();

			int statusCode = httpURLConnection.getResponseCode();
			if (statusCode != HttpStatus.SC_OK) {
				Logger.w("ImageDownloader", "Error " + statusCode
						+ " while retrieving bitmap from " + url);
				return null;
			}

			InputStream inputStream = null;
			OutputStream outputStream = null;
			try {
				inputStream = httpURLConnection.getInputStream();
				final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				outputStream = new BufferedOutputStream(byteArrayOutputStream,
						IO_BUFFER_SIZE);
				copy(inputStream, outputStream);
				outputStream.flush();

				boolean isJpg = url.contains(".jpg");

				// ����ͼƬ������ͼƬ
				bitmap = BitmapTool.saveZoomBitmapToSDCard(byteArrayOutputStream, mScreen,
						url, PathCommonDefines.PHOTOCACHE_FOLDER, isJpg);
			} finally {
				if (inputStream != null) {
					inputStream.close();
				}
				if (outputStream != null) {
					outputStream.close();
				}
			}
			return bitmap;
		} catch (IOException e) {
			Log.e(TAG, e.toString() + "ͼƬ���ؼ�����ʱ�����쳣��");
		}
		return null;
	}

	public void copy(InputStream in, OutputStream out) throws IOException {
		byte[] b = new byte[IO_BUFFER_SIZE];
		int read;
		while ((read = in.read(b)) != -1) {
			out.write(b, 0, read);
		}
	}

	public interface ImageCallback {
		public void imageLoaded(Bitmap bitmap, String imageUrl);
	}

}
