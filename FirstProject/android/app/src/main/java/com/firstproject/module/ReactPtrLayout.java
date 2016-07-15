package com.firstproject.module;

import android.view.LayoutInflater;
import android.view.View;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.common.SystemClock;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.UIManagerModule;
import com.facebook.react.uimanager.ViewGroupManager;
import com.firstproject.R;
import com.firstproject.ptr.PtrFrameLayout;
import com.firstproject.ptr.PtrState;
import com.firstproject.ptr.RefreshEvent;

import java.util.Map;

import javax.annotation.Nullable;

/**
 * Created by lijie on 16/7/13.
 */
public class ReactPtrLayout extends ViewGroupManager<PtrFrameLayout> {

    private static final int STOP_REFRESH=1;

    @Override
    public String getName() {
        return "PtrFrameLayout";
    }

    @Override
    protected PtrFrameLayout createViewInstance(ThemedReactContext reactContext) {
        final PtrFrameLayout rootView= (PtrFrameLayout)LayoutInflater.from(reactContext).inflate(R.layout.ptr_layout,null);
        return  rootView;
    }

    @Nullable
    @Override
    public Map<String, Integer> getCommandsMap() {
        return MapBuilder.of("stop_refresh",STOP_REFRESH);
    }

    @Override
    public void receiveCommand(PtrFrameLayout root, int commandId, @Nullable ReadableArray args) {
        switch (commandId){
            case STOP_REFRESH:
                root.completeRefresh(PtrState.REFRESH_SUCCESS);
                return;
        }
    }

    @Override
    protected void addEventEmitters(final ThemedReactContext reactContext, final PtrFrameLayout view) {
        super.addEventEmitters(reactContext, view);
        view.setOnRefreshListener(new PtrFrameLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reactContext.getNativeModule(UIManagerModule.class).getEventDispatcher()
                        .dispatchEvent(new RefreshEvent(view.getId(), SystemClock.nanoTime()));
            }
        });
    }

    @Override
    public Map<String, Object> getExportedCustomDirectEventTypeConstants() {
        return MapBuilder.<String, Object>builder()
                .put("topRefresh", MapBuilder.of("registrationName", "onRefresh"))
                .build();
    }


    @Override
    public void addView(PtrFrameLayout parent, View child, int index) {
        super.addView(parent, child, index);
        parent.updateLayout();
    }

}
