## SnakeLoading

### 引入方式
```
repositories {
	maven {
		url  "https://entropy.bintray.com/library"
	}
}

dependencies {
    implementation 'com.entropy.lu:snack-loading:1.0.0'
}
```

### 功能支持
1. 支持单页面显示多个loading
2. 支持loading样式自定义
3. 类似SnackBar自动绑定位置
4. 支持非阻塞和阻塞操作
5. 支持Lottie动画库

### 使用限制
1. Loading相对于Fallback的显示位置只支持Gravity.Top和Gravity.Bottom两种配置
2. Lottie Loading存在Path的不同比例缩放问题(后续可能需要对loading资源进行更换)
3. 挂靠View的父类只能是FrameLayout、ConstraintLayout、RelativeLayout

### 版本历史
##### v1.1.x-dev
- 支持LifeCycle生命周期控制
- 支持按钮上Loading
- 支持Gravity.Center
- 增加Margin和Padding的调整
- 阻塞式Loading跟随Fallback高度变化而变化

##### v1.0.x
- 定义Loading的基础业务接入层
- 定义ILoadingable的抽象接口层，提供业务层调用
- 从ILoadingable接口分离出ILoadingHolder
- 支持Loading的位置设置，目前只支持Gravity.Top和Gravity.Bottom两种配置
- 支持单页面显示多个loading
- 支持loading样式自定义
- 支持挂靠式自动绑定位置
- 支持非阻塞和阻塞操作
- 支持Lottie动画库

### Loading类型

- Animation (默认\推荐)
- ProgressBar
- Lottie

### 使用方式

##### 全局配置
```
Bundle renderParams = new Bundle();
renderParams.putInt(XXXXRender.kXXXResourceId, R.drawable.XXX);
LoadingUtil.configRender(LoadingRenderType.ANIMATION, XXXRender.class, renderParams);
```
##### LoadingConfig管理
```
LoadingConfig newConfig = LoadingConfig.create()
                                    .setRenderType(LoadingRenderType.Animation)
                                    .setLoadingGravity(Gravity.Bottom)
                                    .setWithMaskLayer(true)
                                    .setMaskBackgroundColor(0x4c00ff00)
                                    .setRenderParams(bundle)
                                    .setStartDelay(300)
                                    .build();
```
##### 显示Loading
```
//显示默认类型的Loading
Loading.make(context, fallbackView).show()

//显示ANIMATION类型的Loading
Loading.make(context, LoadingRenderType.ANIMATION, fallbackView).show()

or
//显示ANIMATION类型、带有更新参数的Loading
Loading.make(context, LoadingRenderType.ANIMATION, params, fallbackView).show()

or
//显示ANIMATION类型、带有更新参数、带有阻塞式蒙层的Loading
Loading.make(context, LoadingRenderType.ANIMATION, params, fallbackView, true).show()

or
//显示ANIMATION类型、带有更新参数、带有阻塞式蒙层、位置设置的Loading
Loading.make(context, LoadingRenderType.ANIMATION, params, fallbackView, true, Gravity.Bottom).show()

or
//显示指定LoadingConfig的Loading
Loading.make(context, fallbackView, loadingConfig).show()
```

##### 隐藏Loading
```
//隐藏指定View的Loading
Loading.hide(fallbackView)

or
//隐藏所有Loading
Loading.hide()
```
##### Demo样例

###### 1. 直接使用
```
class MainActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        View fallbackView = findViewById(R.id.loading);
        Loading.make(this, fallbackView).show()
    }

    @Override
    public void onDestroy() {
        super.onDestroy()
        Loading.hide()
    }
}
```
###### 2. 集成业务接入层
```
//Activity
class MainActivity extends AbsLoadingActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        View fallbackView = findViewById(R.id.loading);
        showLoading();
    }

    @Override
    public void onDestroy() {
        super.onDestroy()
        hideLoading();
    }

    @Override
    public View getFallbackView() {
        return findViewById(R.id.loading);
    }
}

//Fragment
class DemoFragment extends AbsLoadingFragment {
    public void loadData() {
        showLoading();
        Cloud.requestData(new RemoteCallback(){
            void onFinish(){
                hideLoading()
            }
        });
    }

    @Override
    public View getFallbackView() {
        return findViewById(R.id.loading);
    }
}

//View or Business
class DataView extends AbsLoadingView {

    public void loadData() {
        showLoading();
        Cloud.requestData(new RemoteCallback(){
            void onFinish(){
                hideLoading()
            }
        });
    }

    @Override
    public View getFallbackView() {
        return mContentLayout;
    }
}
```
### 自定义Loading

##### 实现自定义ProgressBar Loading

##### 实现自定义Animation Loading

##### 实现自定义Lottie Loading


### 后续计划

1. 缓存相同的Loading显示展示过程，避免每次对ViewTree进行刷新
2. 支持MaskLayer的自定义背景色和样式
3. 整体提取公共Render参数
4. 阻塞式Loading增加margin和padding参数
5. 阻塞式Loading跟随Fallback变化
6. 增加生命周期控制