package com.zmsoft.TestTool.basees;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zmsoft.TestTool.modle.GoodSInfo;
import com.zmsoft.TestTool.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * 提供Fragment最基础的扩展
 * onAttach -> onCreate -> onCreateView ->onViewCreated -> onActivityCreated -> onViewStateRestored -> onStart -> onResume
 */
public abstract class BaseFragment extends Fragment {
    protected Activity mContext;
    public View mView;
    /**
     * 标题
     */
    private String myTitle;
    private LayoutInflater inflater;
    private ViewGroup container;
    private Unbinder unbinder;
    public List<GoodSInfo.OneLevelBean> oneLevel = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    public View getView(LayoutInflater inflater, int layoutId, ViewGroup container) {
        if (null == mView) {
            mView = inflater.inflate(layoutId, container, false);
        }
        unbinder = ButterKnife.bind(this, mView);
        return mView;
    }

    public void refresh() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (null != unbinder) {
            unbinder.unbind();
        }
    }

    protected void showToast(String string) {
        ToastUtil.showToast(mContext, string, 0);
    }

    protected static BaseFragment getInstance(Class<? extends BaseFragment> clz) {
        try {
            return clz.newInstance();
        } catch (java.lang.InstantiationException e) {
            Log.e("BaseFragment error", e.toString());
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            Log.e("BaseFragment error", e.toString());
            e.printStackTrace();
        }
        return null;
    }

    public String getMyTitle() {
        return myTitle;
    }

    public void setMyTitle(String myTitle) {
        this.myTitle = myTitle;
    }


    public <T extends View> T getView(int viewId) {
        return (T) mView.findViewById(viewId);
    }

}
