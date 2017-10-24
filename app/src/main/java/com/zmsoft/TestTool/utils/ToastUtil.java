package com.zmsoft.TestTool.utils;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.zmsoft.TestTool.application.MyApplication;

import com.zmsoft.TestTool.R;


public class ToastUtil {

    private static Toast mToast;
    private static Handler mHandler = new Handler();
    private static Runnable r = new Runnable() {
        public void run() {
            mToast.cancel();
        }
    };
    private static final int DURATION = 2000;

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
        View view = LayoutInflater.from(mContext).inflate(R.layout.toast_item,null);
        TextView textView = view
                .findViewById(R.id.floating_ball_app_tv_toast);
        textView.setText(text);
        showToast(mContext,view,duration);
    }
    public static ToastUtil showToast(Context mContext,View view, int duration) {
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

    public static void showToast(String text) {
        MyApplication mContext = MyApplication.getInstance();
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

    public static ToastUtil showToast(Context mContext, int id, int duration) {

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

    public static void show() {
        mToast.show();
    }

}