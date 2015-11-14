/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.listviewaddheader.cache;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.View;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.example.listviewaddheader.R;
import com.listviewaddheader.common.PathCommonDefines;
import com.listviewaddheader.utils.BitmapTool;
import com.listviewaddheader.utils.Logger;
import com.listviewaddheader.utils.ScreenUtil;
import com.listviewaddheader.utils.ScreenUtil.Screen;

/**
 * This helper class download images from the Internet and binds those with the
 * provided ImageView.
 * 
 * <p>
 * It requires the INTERNET permission, which should be added to your
 * application's manifest file.
 * </p>
 * 
 * A local cache of downloaded images is maintained internally to improve
 * performance.
 */
public class ImageDownloader {
	private static final String TAG = ImageDownloader.class.getSimpleName();
	private static final String DEFAULT_BITMAP_CACHE = "default_bitmap_cache";

	private static final int HARD_CACHE_CAPACITY = 2;// 缓存中Bitmap强引用的个数
	private static final int SOFT_CACHE_CAPACITY = 4;// 缓存中Bitmap弱引用的个数
	private static final int TASK_CACHE_CAPACITY = 10;// 缓存中Task弱引用的个数
	private static final int DELAY_BEFORE_PURGE = 10 * 1000; // 30秒清空一次缓存
	private static final int IMAGEVIEW_DELAY_BEFORE_PURGE = 10 * 1000; // 20秒清空一次ImageView缓存
	public static final int PHOTO_LOADING_TYPE = 1;
	public static final int ICON_LOADING_TYPE = 2;
	private int mLoadingDefaultPicType = 1;
	private ImageSDCacher mImageSDCacher;
	private Context mContext;
	private ExecutorService mExecutorService = null;
	private boolean isImageViewCache = true;
	private ScaleType mScaleType;
	private Screen mScreen = null;

	public ImageDownloader(Context context) {
		mImageSDCacher = ImageSDCacher.getImageSDCacher();
		mContext = context;
		mScreen = ScreenUtil.getScreenPix(context);
		mExecutorService = Executors.newFixedThreadPool(5);
	}

	// Soft cache for bitmap kicked out of hard cache
	public final static HashMap<String, SoftReference<Bitmap>> mSoftBitmapCache = new LinkedHashMap<String, SoftReference<Bitmap>>(
			SOFT_CACHE_CAPACITY / 2) {
		private static final long serialVersionUID = 1L;

		@Override
		protected boolean removeEldestEntry(
				java.util.Map.Entry<String, SoftReference<Bitmap>> eldest) {
			// Logger.e(TAG, "SoftBitmapCache key:" + eldest.getKey()
			// + " Bitmap size:" + eldest.getValue().get().getByteCount());
			// Logger.e(TAG, "SoftBitmapCache size():" +
			// mSoftBitmapCache.size());
			if (size() > SOFT_CACHE_CAPACITY) {
				System.gc();
				return true;
			} else
				return false;
		}
	};
	// Hard cache, with a fixed maximum capacity and a life duration
	public static final HashMap<String, Bitmap> mHardBitmapCache = new LinkedHashMap<String, Bitmap>(
			HARD_CACHE_CAPACITY / 2, 0.75f, true) {
		private static final long serialVersionUID = 1L;

		@Override
		protected boolean removeEldestEntry(
				LinkedHashMap.Entry<String, Bitmap> eldest) {
			// Logger.e(TAG, "HardBitmapCache key:" + eldest.getKey()
			// + " Bitmap size:" + eldest.getValue().getByteCount());
			// Logger.e(TAG, "HardBitmapCache size():" +
			// mSoftBitmapCache.size());

			if (size() > HARD_CACHE_CAPACITY) {

				// 如果强引用的缓存数量超过了规定的量,则转存到软引用中,避免内存使用过多
				mSoftBitmapCache.put(eldest.getKey(),
						new SoftReference<Bitmap>(eldest.getValue()));

				return true;
			} else
				return false;
		}
	};

	// Task cache for bitmap kicked out of hard cache
	// public final static HashMap<String, BitmapDownloaderTask>
	// mBitmapDownloaderTaskCache = new LinkedHashMap<String,
	// BitmapDownloaderTask>() {
	// protected boolean removeEldestEntry(
	// java.util.Map.Entry<String, BitmapDownloaderTask> eldest) {
	// if (size() > TASK_CACHE_CAPACITY) {
	// BitmapDownloaderTask task = eldest.getValue();
	// if (!task.isCancelled()) {
	// task.cancel(true);
	// }
	// return true;
	// } else
	// return false;
	// };
	// };

	/**
	 * Download the specified image from the Internet and binds it to the
	 * provided ImageView. The binding is immediate if the image is found in the
	 * cache and will be done asynchronously otherwise. A null bitmap will be
	 * associated to the ImageView if an error occurs.
	 * 
	 * @param url
	 *            The URL of the image to download.
	 * @param imageView
	 *            The ImageView to bind the downloaded image to.
	 */
	// public void download(String url, ImageView imageView) {
	// Logger.d(TAG, "prepare to download url " + url);
	// download(url, imageView, null);
	// }

	/**
	 * 重载,可以根据给定的特殊类型,显示特定的默认图片
	 * 
	 * @param url
	 * @param imageView
	 * @param defaultPicType
	 *            默认图片类型
	 */
	public void download(String url, ImageView imageView, int defaultPicType) {
		// Logger.d(TAG, "开始调用下载方法:" + System.currentTimeMillis());
		// Logger.d(TAG, "prepare to download url " + url);
		mLoadingDefaultPicType = defaultPicType;
		download(url, imageView, null, defaultPicType);
		// Logger.d(TAG, "结束调用下载方法:" + System.currentTimeMillis());
	}

	public void download(String url, ImageView imageView, ScaleType scaleType,
			boolean isDownload) {

		if (isDownload) {

			download(url, imageView, scaleType);

		} else {

			if (scaleType != null) {

				imageView.setScaleType(scaleType);

			}

			imageView.setImageBitmap(getDefaultBitmap(mContext));

		}

	}

	public void download(String url, ImageView imageView, ScaleType scaleType) {
		mScaleType = scaleType;
		download(url, imageView, 0);
	}

	/**
	 * Same as {@link #download(String, ImageView)}, with the possibility to
	 * provide an additional cookie that will be used when the image will be
	 * retrieved.
	 * 
	 * @param url
	 *            The URL of the image to download.
	 * @param imageView
	 *            The ImageView to bind the downloaded image to.
	 * @param cookie
	 *            A cookie String that will be used by the http connection.
	 */
	public void download(String url, ImageView imageView, String cookie,
			int defaultPicType) {

		// Logger.i(TAG, "download url:" + url);

		// 异步清理软引用和强引用
		resetPurgeTimer();

		// Logger.d(TAG, "缓存开始时间:" + System.currentTimeMillis());
		Bitmap bitmap = getBitmapFromCache(url);// 缓存
		// Logger.d(TAG, "缓存结束时间:" + System.currentTimeMillis());

		if (bitmap == null) {
			// Logger.d(TAG, "get image from SD card:" +
			// System.currentTimeMillis());
			bitmap = loadFromSDCache(url);// SD卡+
			// Logger.d(TAG, "SD卡结束时间:" + System.currentTimeMillis());
		}

		// else{
		// Logger.d(TAG, "get image from cache:" + System.currentTimeMillis());
		// }
		if (bitmap == null) {
			// Logger.d(TAG, "get image from network:" +
			// System.currentTimeMillis());
			forceDownload(url, imageView, cookie, defaultPicType);// 网络
			// Logger.d(TAG, "网络结束时间:" + System.currentTimeMillis());
		} else {
			// Logger.d(TAG,
			// "got image from SD card, cancel potential download:" +
			// System.currentTimeMillis());
			if (imageView != null) {
				cancelPotentialDownload(url, imageView);
			}

			// 留作备用,放大缩小图片时会用到
			// if (imageView instanceof MulitPointTouchImageView) {
			// ((MulitPointTouchImageView) imageView).setImageBitmap(bitmap);
			// } else {
			// imageView.setImageBitmap(bitmap);// 设置
			// }
			if (mScaleType != null && imageView != null) {
				imageView.setScaleType(mScaleType);
			}
			if (imageView != null) {
				imageView.setImageBitmap(bitmap);// 设置
			}

			// if (isImageViewCache) {
			// mImageViewCache.put(url, imageView);
			// }

		}

		// Log
		// Collection<Bitmap> hardValues = mHardBitmapCache.values();
		// int hardTotalSize = 0;
		// for (Bitmap hartbitmap : hardValues) {
		//
		// hardTotalSize += (hartbitmap.getHeight() * hartbitmap.getWidth() * 4)
		// / 1024;
		// Logger.d(TAG, "hartbitmap Height:" + hartbitmap.getHeight()
		// + "hartbitmap Width:" + hartbitmap.getWidth());
		// }

		// Collection<SoftReference<Bitmap>> softValues = mSoftBitmapCache
		// .values();
		// int softTotalSize = 0;
		// for (SoftReference<Bitmap> softbitmap : softValues) {
		//
		// if (softbitmap != null && softbitmap.get() != null) {
		// softTotalSize += (softbitmap.get().getHeight()
		// * softbitmap.get().getWidth() * 4) / 1024;
		// Logger.d(TAG, "softbitmap Height:"
		// + softbitmap.get().getHeight() + "softbitmap Width:"
		// + softbitmap.get().getWidth());
		// }
		//
		// }
		// Logger.d(TAG, "缓存容量==>download() bitmap 一级缓存容量:" + hardTotalSize
		// + "KB 二级缓存容量:" + softTotalSize + "KB");
	}

	/**
	 * get bitmap by local image
	 * 
	 * @param url
	 * @return
	 */
	public Bitmap loadFromSDCache(String url) {

		// 从普通图片缓存取bitmap
		Bitmap bitmap = mImageSDCacher.getBitmapByCachePath(url,
				PathCommonDefines.PHOTOCACHE_FOLDER);

		if (bitmap == null) {

			// 从我的收藏缓存取bitmap
			bitmap = mImageSDCacher.getBitmapByCachePath(url,
					PathCommonDefines.MY_FAVOURITE_FOLDER);

		}

		if (bitmap != null) {

			// Add to RAM cache
			synchronized (mHardBitmapCache) {
				mHardBitmapCache.put(url, bitmap);
				Logger.d(TAG, "");
			}
		}
		return bitmap;
	}

	/**
	 * Returns true if the current download has been canceled or if there was no
	 * download in progress on this image view. Returns false if the download in
	 * progress deals with the same url. The download is not stopped in that
	 * case.
	 */
	private static boolean cancelPotentialDownload(String url,
			ImageView imageView) {

		BitmapDownloaderTask bitmapDownloaderTask = getBitmapDownloaderTask(imageView);

		if (bitmapDownloaderTask != null) {
			String bitmapUrl = bitmapDownloaderTask.url;
			if ((bitmapUrl == null) || (!bitmapUrl.equals(url))) {
				bitmapDownloaderTask.cancel(true);
			} else {
				// The same URL is already being downloaded.
				return false;
			}
		}

		// mBitmapDownloaderTaskCache.remove(url);

		return true;
	}

	/*
	 * Same as download but the image is always downloaded and the cache is not
	 * used. Kept private at the moment as its interest is not clear. private
	 * void forceDownload(String url, ImageView view) { forceDownload(url, view,
	 * null); }
	 */

	/**
	 * Same as download but the image is always downloaded and the cache is not
	 * used. Kept private at the moment as its interest is not clear.
	 */
	private void forceDownload(String url, ImageView imageView, String cookie,
			int defaultPicType) {
		try {

			// State sanity: url is guaranteed to never be null in
			if (url == null && imageView != null) {
				// 设置默认图片
				imageView.setImageBitmap(getDefaultBitmap(mContext));
				return;
			}

			if (cancelPotentialDownload(url, imageView)) {

				// mBitmapDownloaderTaskCache.remove(url);

				BitmapDownloaderTask task = new BitmapDownloaderTask(imageView);

				DownloadedDrawable downloadedDrawable = new DownloadedDrawable(
						task, mContext, defaultPicType);

				// imageView.setImageDrawable(downloadedDrawable);

				imageView.setTag(downloadedDrawable);

				task.execute(url, cookie);

				// 每创建一个线程就加入到缓存中
				// mBitmapDownloaderTaskCache.put(url, task);
			}

		} catch (RejectedExecutionException localRejectedExecutionException) {
			Logger.w(TAG, "localRejectedExecutionException");
		}
	}

	private final Handler purgeHandler = new Handler();

	private final Runnable purger = new Runnable() {
		@Override
		public void run() {
			clearCache();
		}
	};

	/**
	 * Clears the image cache used internally to improve performance. Note that
	 * for memory efficiency reasons, the cache will automatically be cleared
	 * after a certain inactivity delay.
	 */
	public void clearCache() {
		mHardBitmapCache.clear();
		mSoftBitmapCache.clear();

	}

	private void resetPurgeTimer() {
		purgeHandler.removeCallbacks(purger);
		purgeHandler.postDelayed(purger, DELAY_BEFORE_PURGE);
	}

	/**
	 * @param imageView
	 *            Any imageView
	 * @return Retrieve the currently active download task (if any) associated
	 *         with this imageView. null if there is no such task.
	 */
	private static BitmapDownloaderTask getBitmapDownloaderTask(
			ImageView imageView) {
		if (imageView != null) {
			Object objDrawable = imageView.getTag();
			if (objDrawable != null
					&& objDrawable instanceof DownloadedDrawable) {
				DownloadedDrawable downloadedDrawable = (DownloadedDrawable) objDrawable;
				return downloadedDrawable.getBitmapDownloaderTask();
			}
		}
		return null;
	}

	/**
	 * @param url
	 *            The URL of the image that will be retrieved from the cache.
	 * @return The cached bitmap or null if it was not found.
	 */
	public Bitmap getBitmapFromCache(String url) {
		if (url == null || url.length() == 0)
			return null;
		// First try the hard reference cache
		synchronized (mHardBitmapCache) {
			final Bitmap bitmap = mHardBitmapCache.get(url);
			if (bitmap != null) {
				// Bitmap found in hard cache
				// Move element to first position, so that it is removed last
				mHardBitmapCache.remove(url);
				mHardBitmapCache.put(url, bitmap);
				// Logger.d(
				// "ImageDownloader",
				// "RAM HardReference Cache==>" + "Heap:"
				// + (Debug.getNativeHeapSize() / 1024) + "KB "
				// + "FreeHeap:"
				// + (Debug.getNativeHeapFreeSize() / 1024)
				// + "KB " + "AllHeap:"
				// + (Debug.getNativeHeapAllocatedSize() / 1024)
				// + "KB" + " url:" + url);
				return bitmap;
			}
		}

		// Then try the soft reference cache
		SoftReference<Bitmap> bitmapReference = mSoftBitmapCache.get(url);
		if (bitmapReference != null) {
			final Bitmap bitmap = bitmapReference.get();
			if (bitmap != null) {
				// Bitmap found in soft cache
				// Logger.d(
				// "ImageDownloader",
				// "RAM SoftReference Cache==>" + "Heap:"
				// + (Debug.getNativeHeapSize() / 1024) + "KB "
				// + "FreeHeap:"
				// + (Debug.getNativeHeapFreeSize() / 1024)
				// + "KB " + "AllHeap:"
				// + (Debug.getNativeHeapAllocatedSize() / 1024)
				// + "KB" + " url:" + url);
				return bitmap;
			} else {
				// Soft reference has been Garbage Collected
				mSoftBitmapCache.remove(url);
			}
		}

		return null;
	}

	/**
	 * 返回默认的加载图片
	 * 
	 * @param context
	 * @param defaultPicType
	 * @return
	 */
	private Bitmap getDefaultBitmap(Context context) {

		// 创建图片的Bitmap
		Bitmap defaultBitmap = getBitmapByResId(context,
				R.drawable.ic_launcher, DEFAULT_BITMAP_CACHE);
		// Bitmap defaultBitmap = null;

		// 返回默认图片的Bitmap
		return defaultBitmap;
	}

	/**
	 * The actual AsyncTask that will asynchronously download the image.
	 */
	class BitmapDownloaderTask extends AsyncTask<String, Void, Bitmap> {
		private static final int IO_BUFFER_SIZE = 4 * 1024;
		private String url;
		private final WeakReference<ImageView> imageViewReference;

		public BitmapDownloaderTask(ImageView imageView) {
			imageViewReference = new WeakReference<ImageView>(imageView);
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}

		/**
		 * Actual download method.
		 */
		@Override
		protected Bitmap doInBackground(String... params) {
			// 获得URL
			url = params[0];

			Bitmap bitmap = null;

			// 去网络端取图片
			try {

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

					outputStream = new BufferedOutputStream(
							byteArrayOutputStream, IO_BUFFER_SIZE);

					copy(inputStream, outputStream);

					outputStream.flush();

					// Logger.d(
					// TAG,
					// "Before download decodeStream==>"
					// + "Heap:"
					// + (Debug.getNativeHeapSize() / 1024)
					// + "KB "
					// + "FreeHeap:"
					// + (Debug.getNativeHeapFreeSize() / 1024)
					// + "KB "
					// + "AllocatedHeap:"
					// + (Debug.getNativeHeapAllocatedSize() / 1024)
					// + "KB" + " url:" + url);

					boolean isJpg = url.contains(".jpg");

					bitmap = BitmapTool.saveZoomBitmapToSDCard(
							byteArrayOutputStream, mScreen, url,
							PathCommonDefines.PHOTOCACHE_FOLDER, isJpg);

					// Logger.d(
					// TAG,
					// "doInbackground bitmap width:"
					// + bitmap.getWidth() + " height:"
					// + bitmap.getHeight());

					// Logger.d(
					// TAG,
					// "After download decodeStream==>"
					// + "Heap:"
					// + (Debug.getNativeHeapSize() / 1024)
					// + "KB "
					// + "FreeHeap:"
					// + (Debug.getNativeHeapFreeSize() / 1024)
					// + "KB "
					// + "AllocatedHeap:"
					// + (Debug.getNativeHeapAllocatedSize() / 1024)
					// + "KB" + " url:" + url);

					// if (client != null) {
					// client.close();
					// }
					return bitmap;
				} finally {
					if (inputStream != null) {
						inputStream.close();
					}
					if (outputStream != null) {
						outputStream.close();
					}
				}
				// }
			} catch (IOException e) {
				// getRequest.abort();
				Logger.w(TAG, "I/O error while retrieving bitmap from " + url,
						e);
			} catch (IllegalStateException e) {
				// getRequest.abort();
				Logger.w(TAG, "Incorrect URL: " + url);
			} catch (Exception e) {
				// getRequest.abort();
				Logger.w(TAG, "Error while retrieving bitmap from " + url, e);
			} catch (OutOfMemoryError e) {
				// TODO: handle exception
			} finally {

			}

			return null;

		}

		/**
		 * Once the image is downloaded, associates it to the imageView
		 */
		@Override
		protected void onPostExecute(Bitmap bitmap) {
			if (isCancelled()) {
				bitmap = null;
			}

			// Logger.d("ImageDownloader", "开始保存下载的图片");
			// Add bitmap to cache
			if (bitmap != null) {
				synchronized (mHardBitmapCache) {
					mHardBitmapCache.put(url, bitmap);
				}
			}
			// Logger.d("ImageDownloader", "结束保存下载的图片");

			if (imageViewReference != null) {
				ImageView imageView = imageViewReference.get();
				BitmapDownloaderTask bitmapDownloaderTask = getBitmapDownloaderTask(imageView);
				// Change bitmap only if this process is still associated with
				// it
				if (this == bitmapDownloaderTask && bitmap != null) {

					// if (imageView instanceof MulitPointTouchImageView) {
					// // ((MulitPointTouchImageView) imageView)
					// // .setImageBitmap(bitmap);
					// } else {
					// imageView.setImageBitmap(bitmap);// 设置
					// }
					if (mScaleType != null) {

						imageView.setScaleType(mScaleType);

					}
					imageView.setImageBitmap(bitmap);// 设置

					// if (isImageViewCache) {
					// mImageViewCache.put(url, imageView);
					// }
				}
			}
		}

		public void copy(InputStream in, OutputStream out) throws IOException {
			byte[] b = new byte[IO_BUFFER_SIZE];
			int read;
			while ((read = in.read(b)) != -1) {
				out.write(b, 0, read);
			}
		}
	}

	/**
	 * A fake Drawable that will be attached to the imageView while the download
	 * is in progress.
	 * 
	 * <p>
	 * Contains a reference to the actual download task, so that a download task
	 * can be stopped if a new binding is required, and makes sure that only the
	 * last started download process can bind its result, independently of the
	 * download finish order.
	 * </p>
	 */
	class DownloadedDrawable extends BitmapDrawable {
		private final WeakReference<BitmapDownloaderTask> bitmapDownloaderTaskReference;

		public DownloadedDrawable(BitmapDownloaderTask bitmapDownloaderTask,
				Context context, int defaultPicType) {
			// super();
			super(context.getResources(), getDefaultBitmap(context));//
			// 此位置可以改变图片的背景颜色----王阳
			bitmapDownloaderTaskReference = new WeakReference<BitmapDownloaderTask>(
					bitmapDownloaderTask);
		}

		public BitmapDownloaderTask getBitmapDownloaderTask() {
			return bitmapDownloaderTaskReference.get();
		}

	}

	/**
	 * 根据图片的资源ID创建Bitmap
	 * 
	 * @param context
	 * @param resId
	 * @return
	 */
	public Bitmap getBitmapByResId(Context context, int resId, String cacheKey) {

		// 从缓存中取默认图片
		Bitmap resBitmap = mHardBitmapCache.get(cacheKey);
		try {
			if (resBitmap == null) {

				// 创建图片的Bitmap
				resBitmap = BitmapFactory.decodeResource(
						context.getResources(), resId);

				// 再放到缓存中
				mHardBitmapCache.put(cacheKey, resBitmap);

			}

		} catch (OutOfMemoryError e) {
			Logger.e(TAG, "getBitmapByResId()", e);
			System.gc();
		}

		return resBitmap;
	}

	private HttpClient getHttpClient() {
		HttpParams params = new BasicHttpParams();

		HttpConnectionParams.setConnectionTimeout(params, 20 * 1000);
		HttpConnectionParams.setSoTimeout(params, 20 * 1000);
		HttpConnectionParams.setSocketBufferSize(params, 8192);
		HttpClientParams.setRedirecting(params, true);

		HttpClient client = new DefaultHttpClient(params);
		return client;
	}

	public interface ImageCallback {
		public void imageLoaded(Bitmap bmp);
	}

	/**
	 * 把bitmap设置到相应控件zho
	 * 
	 * @param view
	 * @param bitmap
	 */
	public void setBitmapByView(View view, Bitmap bitmap) {
		if (view instanceof ImageView) {
			ImageView imageView = (ImageView) view;
			imageView.setImageBitmap(bitmap);
		} else if (view instanceof ImageSwitcher) {
			ImageSwitcher imageSwitcher = (ImageSwitcher) view;
			imageSwitcher.setImageDrawable(new BitmapDrawable(bitmap));
		}
	}
}
