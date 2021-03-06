package com.zmsoft.TestTool.utils;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.zmsoft.TestTool.R;
import com.zmsoft.TestTool.application.MyApplication;


public class ToastUtil {

    private static Toast mToast;
    private static Handler mHandler = new Handler();
    private static Runnable r = new Runnable() {
        public void run() {
            mToast.cancel();
        }
    };
    private static final int DURATION = 2000;
    private static Context mContext = MyApplication.getInstance();

    public static void close() {
        if (null != mToast) {
            mToast.cancel();
        }
    }

    private static class ToastHolder {
        static final ToastUtil TOAST_UTIL = new ToastUtil();
    }

    public static ToastUtil getInstance() {
        return ToastHolder.TOAST_UTIL;
    }

    public static void showToast(Context mContext, String text, int duration) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.toast_item, null);
        TextView textView = view
                .findViewById(R.id.floating_ball_app_tv_toast);
        textView.setText(text);
        showToast(view, duration);
    }

    public static void showToast(View view, int duration) {
        mHandler.removeCallbacks(r);
        if (mToast != null)
            mToast.setView(view);
        else
            mToast = new Toast(mContext);
        mToast.setView(view);

        long time = duration;
        if (duration < 3) {
            if (duration == 1) {
                time = 3000l;
            } else {
                time = 2000l;
            }
        }
        mHandler.postDelayed(r, time);
        mToast.show();
    }

    public static void showToast(String text) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.toast_item,
                null);
        TextView textView = view
                .findViewById(R.id.floating_ball_app_tv_toast);
        textView.setText(text);
        mHandler.removeCallbacks(r);
        if (mToast != null)
            mToast.setView(view);
        else
            mToast = new Toast(mContext);
        mToast.setView(view);

        long time = DURATION;
        if (DURATION < 3) {
            if (DURATION == 1) {
                time = 3000l;
            } else {
                time = 2000l;
            }
        }
        mHandler.postDelayed(r, time);
        mToast.show();
    }

    public static ToastUtil showToast(int id, int duration) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.toast_item,
                null);
        TextView textView = view
                .findViewById(R.id.floating_ball_app_tv_toast);
        textView.setText(mContext.getResources().getString(id));
        mHandler.removeCallbacks(r);
        if (mToast != null)
            mToast.setView(view);
        else
            mToast = new Toast(mContext);
        mToast.setView(view);
        long time = duration;
        if (duration < 3) {
            if (duration == 1) {
                time = 3000l;
            } else {
                time = 2000l;
            }
        }
        mHandler.postDelayed(r, time);
        mToast.show();
        return getInstance();
    }

}
