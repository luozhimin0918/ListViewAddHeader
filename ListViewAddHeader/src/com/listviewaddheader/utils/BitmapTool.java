package com.listviewaddheader.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.listviewaddheader.cache.ImageSDCacher;
import com.listviewaddheader.utils.ScreenUtil.Screen;

public class BitmapTool {

    private final static String TAG = BitmapTool.class.getSimpleName();

    /**
     * ����ͼ��URL����Bitmap
     *
     * @param url URL��ַ
     * @return bitmap
     */
    public Bitmap CreateImage(String url) {
        // Logger.d("ImageDownloader",
        // "��ʼ����CreateImage():" + System.currentTimeMillis());
        Bitmap bitmap = null;
        if (url == null || url.equals("")) {
            return null;
        }
        try {
            // Logger.d(
            // "ImageDownloader",
            // "C Before SDCard decodeStream==>" + "Heap:"
            // + (Debug.getNativeHeapSize() / 1024) + "KB "
            // + "FreeHeap:"
            // + (Debug.getNativeHeapFreeSize() / 1024) + "KB "
            // + "AllocatedHeap:"
            // + (Debug.getNativeHeapAllocatedSize() / 1024)
            // + "KB" + " url:" + url);

            FileInputStream fis = new FileInputStream(url);
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
            opts.inTempStorage = new byte[100 * 1024];
            opts.inPurgeable = true;
            opts.inInputShareable = true;
            bitmap = BitmapFactory.decodeStream(fis, null, opts);

            // Logger.d(
            // "ImageDownloader",
            // "C After SDCard decodeStream==>" + "Heap:"
            // + (Debug.getNativeHeapSize() / 1024) + "KB "
            // + "FreeHeap:"
            // + (Debug.getNativeHeapFreeSize() / 1024) + "KB "
            // + "AllocatedHeap:"
            // + (Debug.getNativeHeapAllocatedSize() / 1024)
            // + "KB" + " url:" + url);
        } catch (OutOfMemoryError e) {
            Logger.e(TAG, "OutOfMemoryError", e);
            System.gc();
        } catch (FileNotFoundException e) {
            Logger.e(TAG, "FileNotFoundException", e);
        }
        // Logger.d("ImageDownloader",
        // "��������CreateImage():" + System.currentTimeMillis());
        return bitmap;
    }

    /**
     * ͼƬ���Ŵ���,�����浽SDCard
     *
     * @param byteArrayOutputStream ͼƬ�ֽ���
     * @param screen                ��Ļ���
     * @param url                   ͼƬ����·��
     * @param cachePath             ���ػ��游·��</br>PathCommonDefines.PHOTOCACHE_FOLDER ���򻺴�ͼƬ·��;</br>
     *                              PathCommonDefines.MY_FAVOURITE_FOLDER �ҵ��ղ�ͼƬ·��
     * @param isJpg                 �Ƿ���Jpg
     * @return ���ź��ͼƬbitmap
     */
    public static Bitmap saveZoomBitmapToSDCard(
            ByteArrayOutputStream byteArrayOutputStream, Screen screen,
            String url, String cachePath, boolean isJpg) {

        Bitmap bitmap = null;
        try {

            byte[] byteArray = byteArrayOutputStream.toByteArray();

            BitmapFactory.Options options = new BitmapFactory.Options();

            options.inTempStorage = new byte[16 * 1024];

            // ֻ����ͼƬ�ı߽�
            options.inJustDecodeBounds = true;

            // ��ȡBitmap��Ϣ
            BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length,
                    options);

            // ��ȡ��Ļ�Ŀ�͸�
            int screenWidth = screen.widthPixels;
            int screenHeight = screen.heightPixels;

            // ��Ļ������ظ���
            int maxNumOfPixels = screenWidth * screenHeight;

            // ���������
            int sampleSize = computeSampleSize(options, -1, maxNumOfPixels);

            options.inSampleSize = sampleSize;

            options.inJustDecodeBounds = false;

            // ���¶���ͼƬ,��ʱΪ���ź��ͼƬ
            bitmap = BitmapFactory.decodeByteArray(byteArray, 0,
                    byteArray.length, options);

            // ѹ������
            int quality = 100;

            // �ж��Ƿ���Jpg,png������ѹ��,���Բ��ý�������ѹ��
            if (bitmap != null && isJpg) {

                ByteArrayOutputStream saveBaos = new ByteArrayOutputStream();

                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, saveBaos);

                // ѭ���ж����ѹ����ͼƬ�Ƿ����100kb,���ڼ���ѹ��
                while (saveBaos.toByteArray().length / 1024 > 100) {

                    // ����saveBaos�����saveBaos
                    saveBaos.reset();

                    // ÿ�ζ�����10
                    quality -= 10;

                    // ����ѹ��optionsNum%����ѹ��������ݴ�ŵ�saveBaos��
                    bitmap.compress(Bitmap.CompressFormat.JPEG, quality,
                            saveBaos);

                }
                // ��ѹ���������ByteArrayOutputStream��ŵ�ByteArrayInputStream��
                ByteArrayInputStream saveBais = new ByteArrayInputStream(
                        saveBaos.toByteArray());

                bitmap = BitmapFactory.decodeStream(saveBais, null, null);

            }

            // ���浽SDCard
            ImageSDCacher.getImageSDCacher().saveBitmapToSDCard(bitmap, url,
                    cachePath, isJpg, quality);

        } catch (Exception e) {
            Log.e("saveZoomBitmapToSDCard", "" + e);
        }

        return bitmap;
    }

    /**
     * ͼƬ���Ŵ���,�����浽SDCard
     *
     * @param screen    ��Ļ���
     * @param bitmap    ͼƬbitmap
     * @param cachePath ���ػ��游·��</br>PathCommonDefines.PHOTOCACHE_FOLDER ���򻺴�ͼƬ·��;</br>
     *                  PathCommonDefines.MY_FAVOURITE_FOLDER �ҵ��ղ�ͼƬ·��
     * @param isJpg     �Ƿ���Jpg
     * @return ���ź��ͼƬbitmap
     */
    public static Bitmap saveZoomBitmapToSDCard(Bitmap bitmap, Screen screen,
                                                String url, String cachePath, boolean isJpg) {
        Bitmap tempBitmap = null;
        byte[] byteArray = bitmap2Bytes(bitmap);
        try {

            BitmapFactory.Options options = new BitmapFactory.Options();

            // ��ȡ��Ļ�Ŀ�͸�
            int screenWidth = screen.widthPixels;
            int screenHeight = screen.heightPixels;

            // ��Ļ������ظ���
            int maxNumOfPixels = screenWidth * screenHeight;

            // ���������
            int sampleSize = computeSampleSize(options, -1, maxNumOfPixels);

            options.inSampleSize = sampleSize;

            options.inJustDecodeBounds = false;

            // ���¶���ͼƬ,��ʱΪ���ź��ͼƬ
            tempBitmap = BitmapFactory.decodeByteArray(byteArray, 0,
                    byteArray.length, options);

            // ѹ������
            int quality = 100;

            // �ж��Ƿ���Jpg,png������ѹ��,���Բ��ý�������ѹ��
            if (bitmap != null && isJpg) {

                ByteArrayOutputStream saveBaos = new ByteArrayOutputStream();

                tempBitmap.compress(Bitmap.CompressFormat.JPEG, quality,
                        saveBaos);

                // ѭ���ж����ѹ����ͼƬ�Ƿ����100kb,���ڼ���ѹ��
                while (saveBaos.toByteArray().length / 1024 > 100) {

                    // ����saveBaos�����saveBaos
                    saveBaos.reset();

                    // ÿ�ζ�����10
                    quality -= 10;

                    // ����ѹ��optionsNum%����ѹ��������ݴ�ŵ�saveBaos��
                    tempBitmap.compress(Bitmap.CompressFormat.JPEG, quality,
                            saveBaos);

                }
                // ��ѹ���������ByteArrayOutputStream��ŵ�ByteArrayInputStream��
                ByteArrayInputStream saveBais = new ByteArrayInputStream(
                        saveBaos.toByteArray());

                tempBitmap = BitmapFactory.decodeStream(saveBais, null, null);

            }

            // ���浽SDCard
            ImageSDCacher.getImageSDCacher().saveBitmapToSDCard(tempBitmap,
                    url, cachePath, isJpg, quality);

        } catch (Exception e) {
            Log.e("", e.getMessage());
        }

        return tempBitmap;
    }

    public static byte[] bitmap2Bytes(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    // Recycle the resource of the Image
    public void recycleImage(Bitmap bitmap) {
        try {
            if (bitmap != null && !bitmap.isMutable() && !bitmap.isRecycled()) {
                bitmap.recycle();
                System.gc();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logger.e(TAG, "bitmap recycle excpetion");
        }
    }

    /**
     * �滻�����ַ�
     *
     * @param fileName ͼƬ�Ĵ���ǰ������
     * @return ͼƬ����������
     */
    public static String renameUploadFile(String fileName) {

        String result = "yepcolor";

        if (fileName != null && !fileName.equals("")) {

            result = fileName.hashCode() + "";// ����ļ����Ƶ�hashcodeֵ

        }
        return result;
        // ֻ������ĸ������
        // String regEx = "[^a-zA-Z0-9]";
        // ��������������ַ�
        // String regEx =
        // "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~��@#��%����&*��������+|{}������������������������]";
        // Pattern p = Pattern.compile(regEx);
        // Matcher m = p.matcher(fileName);
        // result = m.replaceAll("").trim();

    }

    /**
     * ���������
     *
     * @param options
     * @param minSideLength
     * @param maxNumOfPixels
     * @return
     */
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

    /**
     * �����ʼ������
     *
     * @param options
     * @param minSideLength
     * @param maxNumOfPixels
     * @return
     */
    private static int computeInitialSampleSize(BitmapFactory.Options options,
                                                int minSideLength, int maxNumOfPixels) {

        double w = options.outWidth;

        double h = options.outHeight;

        int lowerBound = (maxNumOfPixels == -1) ? 1 :

                (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));

        int upperBound = (minSideLength == -1) ? 128 :

                (int) Math.min(Math.floor(w / minSideLength),

                        Math.floor(h / minSideLength));

        if (upperBound < lowerBound) {

            // return the larger one when there is no overlapping zone.

            return lowerBound;

        }

        if ((maxNumOfPixels == -1) &&

                (minSideLength == -1)) {

            return 1;

        } else if (minSideLength == -1) {

            return lowerBound;

        } else {

            return upperBound;

        }

    }
    /**
     * ����ͼƬ��·����ȡͼƬ�Ĵ�С
     *
     * @param item
     */
    // public static void getBitmapSize(Items item) {
    // URL url;
    // try {
    // url = new URL(item.getPicUrl());
    // URLConnection conn = url.openConnection();
    // conn.connect();
    // InputStream is = conn.getInputStream();
    // BitmapFactory.Options options = new BitmapFactory.Options();
    // BitmapFactory.decodeStream(is, null, options);
    // options.inJustDecodeBounds = true;
    // int height = options.outHeight;
    // int width = options.outWidth;
    // item.setImageWidth(width);
    // item.setImageHeight(height);
    // } catch (MalformedURLException e) {
    // e.printStackTrace();
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    //
    // }

}
