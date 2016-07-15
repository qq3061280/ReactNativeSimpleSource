package com.firstproject.module;

import android.view.LayoutInflater;
import android.view.View;

import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.firstproject.MainApplication;
import com.firstproject.R;
import com.joooonho.SelectableRoundedImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

/**
 * Created by lijie on 16/7/13.
 */
public class ReactRoundedImageView extends SimpleViewManager<SelectableRoundedImageView> {

    private static final String MODULE_NAME="AndroidRoundImage";

    private ImageLoader mImageLoader;
    private DisplayImageOptions options;

    @Override
    public String getName() {
        return MODULE_NAME;
    }

    @Override
    protected SelectableRoundedImageView createViewInstance(ThemedReactContext reactContext) {
        mImageLoader = MainApplication.initImageLoader();
        //   options = ImageHelper.initImageBuilder(R.drawable.no_pic_horizontal_col, true, ImageScaleType.EXACTLY ).build();
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.no_pic_horizontal_col)
                .showImageForEmptyUri(R.mipmap.no_pic_horizontal_col)
                .showImageOnFail(R.mipmap.no_pic_horizontal_col)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new SimpleBitmapDisplayer())
                .build();
        View rootView= LayoutInflater.from(reactContext).inflate(R.layout.layout_select_round,null);
        return (SelectableRoundedImageView) rootView;
    }

    @ReactProp(name = "url")
    public void setUrl(SelectableRoundedImageView simpleDraweeView, String url){
        mImageLoader.displayImage(url,simpleDraweeView, options);
    }


}
