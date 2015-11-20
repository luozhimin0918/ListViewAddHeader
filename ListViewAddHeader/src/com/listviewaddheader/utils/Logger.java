package com.listviewaddheader.utils;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

public class Logger {

    private static boolean ENABLE_LOG = true;// falseΪ������־��ӡ,true������־��ӡ
    private static boolean REDIRECTTO_FILE = false;

    public static String logFilePrefix = "YCdebug";

    public static void v(String tag, String msg) {
        if (ENABLE_LOG) {
            try {
                Log.v(tag, msg);
                logMsg(tag, msg);
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    }

    public static void i(String tag, String msg) {
        if (ENABLE_LOG) {
            try {
                Log.i(tag, msg);
                logMsg(tag, msg);
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    }

    public static void i(String tag, String msg, Throwable tr) {
        if (ENABLE_LOG) {
            try {
                Log.i(tag, msg, tr);
                logException(tag, msg, tr);
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    }

    public static void d(String tag, String msg) {
        if (ENABLE_LOG) {
            try {
                Log.d(tag, msg);
                logMsg(tag, msg);
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    }

    public static void d(String tag, String msg, Throwable tr) {
        if (ENABLE_LOG) {
            try {
                Log.d(tag, msg, tr);
                logException(tag, msg, tr);
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    }

    public static void w(String tag, String msg) {
        if (ENABLE_LOG) {
            try {

                Log.w(tag, msg);
                logMsg(tag, msg);
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    }

    public static void w(String tag, String msg, Throwable tr) {
        if (ENABLE_LOG) {
            try {
                Log.w(tag, msg, tr);
                logException(tag, msg, tr);
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    }

    public static void e(String tag, String msg) {
        if (ENABLE_LOG) {
            try {
                Log.e(tag, msg);
                logMsg(tag, msg);
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    }

    public static void e(String tag, String msg, Throwable tr) {
        if (ENABLE_LOG) {
            try {
                Log.e(tag, msg, tr);
                logException(tag, msg, tr);
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    }

    public static void initLog(Activity activity) {
        if (REDIRECTTO_FILE && ENABLE_LOG) {
            PrintStream out = null;
            try {
                out = new PrintStream(activity.openFileOutput(logFilePrefix
                                + System.currentTimeMillis() + ".log",
                        Context.MODE_WORLD_WRITEABLE), true);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            System.setOut(out);
            System.setErr(out);
        }
    }

    private static void logMsg(String tag, String msg) {
        if (REDIRECTTO_FILE) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
            String date = sdf.format(new Date());
            StringBuilder sb = new StringBuilder();
            sb.append(date);
            sb.append("|");
            sb.append("Thread|");
            sb.append(Thread.currentThread().getId());
            sb.append(":");
            sb.append(tag);
            sb.append("=>");
            sb.append(msg);

            System.out.println(sb.toString());
        }
    }

    private static void logException(String tag, String msg, Throwable tr) {
        if (REDIRECTTO_FILE) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
            String date = sdf.format(new Date());
            StringBuilder sb = new StringBuilder();
            sb.append(date);
            sb.append("|");
            sb.append("Thread|");
            sb.append(Thread.currentThread().getId());
            sb.append(":");
            sb.append(tag);
            sb.append("=>");
            sb.append(msg);
            sb.append("=>Exception:");
            sb.append(tr);

            System.out.println(sb.toString());
        }
    }

    @SuppressWarnings("rawtypes")
    public static void dumpMap(String tag, AbstractMap m) {
        d(tag, "dump map information");
        for (Object obj : m.keySet())
            d(tag, "key:" + obj + ";\tvalue:" + m.get(obj));
    }
}
