package com.entropy.lu;

import android.arch.lifecycle.LifecycleOwner;
import android.view.View;

/**
 * 可提供loading的配置
 *
 * @author kevinet created at 2018/11/01
 */
public interface ILoadingHolder {
    /**
     * 设置可挂靠View
     *
     * @return View
     */
    View getFallbackView();

    /**
     * 设置Loading的配置模型
     *
     * @return 配置模型
     */
    LoadingConfig getLoadingConfig();

    /**
     * 设置生命周期管理者
     *
     * @return 生命周期管理者
     */
    LifecycleOwner getLifecycleOwner();
}
