package com.listviewaddheader.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class ScreenUtil {
    /**
     * ��ȡ��Ļ�Ĵ�С
     *
     * @param context
     * @return
     */
    public static Screen getScreenPix(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);
        return new Screen(dm.widthPixels, dm.heightPixels);
    }

    /**
     * ��ȡ��Ļ�Ŀ�
     *
     * @param context
     * @return
     */
    public static int getScreenWidthPix(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);
        return new Screen(dm.widthPixels, dm.heightPixels).widthPixels;
    }

    /**
     * ��ȡ��Ļ�ĸ�
     *
     * @param context
     * @return
     */
    public static int getScreenHeightPix(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);
        return new Screen(dm.widthPixels, dm.heightPixels).heightPixels;
    }

    public static class Screen {
        public int widthPixels;
        public int heightPixels;

        public Screen() {
        }

        public Screen(int widthPixels, int heightPixels) {
            this.widthPixels = widthPixels;
            this.heightPixels = heightPixels;
        }

        @Override
        public String toString() {
            return "(" + widthPixels + "," + heightPixels + ")";
        }
    }
}
