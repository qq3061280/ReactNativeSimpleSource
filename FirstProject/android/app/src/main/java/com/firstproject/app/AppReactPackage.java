package com.firstproject.app;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.JavaScriptModule;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;
import com.firstproject.module.ReactPtrLayout;
import com.firstproject.module.ReactRoundedImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by lijie on 16/7/13.
 */
public class AppReactPackage implements ReactPackage {
    @Override
    public List<NativeModule> createNativeModules(ReactApplicationContext reactContext) {
        return Collections.emptyList();
    }

    @Override
    public List<Class<? extends JavaScriptModule>> createJSModules() {
        return Collections.emptyList();
    }

    @Override
    public List<ViewManager> createViewManagers(ReactApplicationContext reactContext) {
        List<ViewManager> result = new ArrayList<ViewManager>();
        result.add(new ReactRoundedImageView());
        result.add(new ReactPtrLayout());
        return result;
    }
}
