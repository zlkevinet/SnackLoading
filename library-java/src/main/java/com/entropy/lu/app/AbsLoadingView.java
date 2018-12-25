package com.entropy.lu.app;

import android.arch.lifecycle.LifecycleOwner;
import android.content.Context;
import com.entropy.lu.ILoadingHolder;
import com.entropy.lu.ILoadingable;
import com.entropy.lu.LoadingConfig;

/**
 * <p>
 *     简单loading页面
 * </p>
 *
 * @author kevinet created at 2018/12/20
 */
public abstract class AbsLoadingView implements ILoadingable, ILoadingHolder {

    private LoadingDelegate loadingDelegate;

    public AbsLoadingView(Context context) {
        loadingDelegate = new LoadingDelegate(context, this);
    }

    public void showMaskLayerLoading() {
        loadingDelegate.showMaskLayerLoading();
    }

    @Override
    public void showLoading() {
        loadingDelegate.showLoading();
    }

    @Override
    public void showLoading(LoadingConfig config) {
        loadingDelegate.showLoading(config);
    }

    @Override
    public void hideLoading() {
        loadingDelegate.hideLoading();
    }

    @Override
    public boolean isLoading() {
        return loadingDelegate.isLoading();
    }

    @Override
    public LoadingConfig getLoadingConfig() {
        return LoadingConfig.create().build();
    }

    @Override
    public LifecycleOwner getLifecycleOwner() {
        return null;
    }
}
