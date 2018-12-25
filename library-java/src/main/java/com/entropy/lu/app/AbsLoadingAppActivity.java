package com.entropy.lu.app;

import android.app.Activity;
import android.arch.lifecycle.LifecycleOwner;
import android.os.Bundle;
import android.support.annotation.Nullable;
import com.entropy.lu.ILoadingHolder;
import com.entropy.lu.ILoadingable;
import com.entropy.lu.LoadingConfig;

/**
 * @author kevinet created at 2018/11/05
 */
public abstract class AbsLoadingAppActivity extends Activity implements ILoadingable, ILoadingHolder {

    private LoadingDelegate loadingDelegate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadingDelegate = new LoadingDelegate(this, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideLoading();
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
