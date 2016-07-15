package com.firstproject;

import android.app.Application;
import android.content.Context;

import com.facebook.react.ReactApplication;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.ReactPackage;
import com.facebook.react.shell.MainReactPackage;
import com.firstproject.app.AppReactPackage;
import com.firstproject.utils.ImageHelper;
import com.microsoft.codepush.react.CodePush;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

public class MainApplication extends Application implements ReactApplication {


  private static MainApplication mApp;
  private static Context appContext;

  public static MainApplication getInstance() {
    return mApp;
  }

  public static Context getAppContext() {
    return appContext;
  }

  /**
   *  Name       │ Deployment Key                        │
   ├────────────┼───────────────────────────────────────┤
   │ Production │ m-riu3kK1__kGboK4WPIwBYs7ZwzEygDCEa5e │
   ├────────────┼───────────────────────────────────────┤
   │ Staging    │ zi1vEIim9-i5bkMl9fQ32EA_4LBxEygDCEa5e │
   */


  @Override
  public void onCreate() {
    super.onCreate();
    if (mApp == null) {
      mApp = this;
      appContext = getApplicationContext();
      initImgLoader();

    }
  }

  public  void initImgLoader() {
    ImageLoaderConfiguration imageLoaderConfig = ImageHelper.initImageConfigBuilder().build();
    ImageLoader.getInstance().init(imageLoaderConfig);
  }

  public static ImageLoader initImageLoader() {
    ImageLoader imageLoader = ImageLoader.getInstance();
//        imageLoader.resume(); // v3.0前防止图片加载线程被pause后所有图片都加载不了；v3.5每次都getInstance()不需要resume
    return imageLoader;
  }


  private final ReactNativeHost mReactNativeHost = new ReactNativeHost(this) {
    @Override
    protected boolean getUseDeveloperSupport() {
      return false;
    }

    @Override
    protected List<ReactPackage> getPackages() {
      return Arrays.<ReactPackage>asList(
          new MainReactPackage(),
          new AppReactPackage(),
          new CodePush("m-riu3kK1__kGboK4WPIwBYs7ZwzEygDCEa5e", MainApplication.this, false)
      );
    }

    @Nullable
    @Override
    protected String getJSBundleFile() {
      return CodePush.getJSBundleFile();
    }
  };

  @Override
  public ReactNativeHost getReactNativeHost() {
      return mReactNativeHost;
  }
}
