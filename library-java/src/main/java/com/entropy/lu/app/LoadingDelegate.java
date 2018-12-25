package com.entropy.lu.app;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Context;
import com.entropy.lu.ILoadingHolder;
import com.entropy.lu.ILoadingable;
import com.entropy.lu.Loading;
import com.entropy.lu.LoadingConfig;

/**
 * <p> 公共Loading代理
 *
 * @author kevinet created at 2018/11/05
 */
public class LoadingDelegate implements ILoadingable, LifecycleObserver {
    private Context mContext;
    private ILoadingHolder mLoadingHolder;

    public LoadingDelegate(Context context, ILoadingHolder holder) {
        mContext = context;
        mLoadingHolder = holder;
    }

    @Override
    public void showLoading() {
        showLoading(LoadingConfig.create().build());
    }

    public void showMaskLayerLoading() {
        showLoading(LoadingConfig.create().setWithMaskLayer(true).build());
    }

    @Override
    public void showLoading(LoadingConfig config) {
        if (mLoadingHolder == null) {
            return;
        }

        LifecycleOwner owner = mLoadingHolder.getLifecycleOwner();
        if (owner != null) {
            owner.getLifecycle().removeObserver(this);
            owner.getLifecycle().addObserver(this);
        }

        if (mLoadingHolder.getFallbackView() != null) {
            LoadingConfig defaultConfig = mLoadingHolder.getLoadingConfig();
            defaultConfig.merge(config);
            Loading.make(mContext, mLoadingHolder.getFallbackView(), defaultConfig).show();
        }

    }

    @Override
    @OnLifecycleEvent(value = Lifecycle.Event.ON_DESTROY)
    public void hideLoading() {
        if (mContext != null && mLoadingHolder != null) {
            Loading.hide(mLoadingHolder.getFallbackView());
            LifecycleOwner owner = mLoadingHolder.getLifecycleOwner();
            if (owner != null) {
                owner.getLifecycle().removeObserver(this);
            }
        }
    }

    @Override
    public boolean isLoading() {
        if (mContext != null
                && mLoadingHolder != null) {
            return Loading.isShowing(mLoadingHolder.getFallbackView());
        }
        return false;
    }
}
