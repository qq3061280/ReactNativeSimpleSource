package com.firstproject.utils;

import android.graphics.Bitmap;

import com.firstproject.MainApplication;
import com.firstproject.R;
import com.nostra13.universalimageloader.cache.memory.impl.LRULimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;

/**
 * Created by plu on 2015/12/15.
 * <p/>
 * 初始化图片加载类
 * <p/>
 * 主要参数：默认加载图，是否缓存本地，imageScaleType
 * <p/>
 * https://github.com/nostra13/Android-Universal-Image-Loader
 */
public class ImageHelper {

    public static DisplayImageOptions.Builder initImageBuilder() {
        return initImageBuilder(R.mipmap.no_pic_horizontal, true, null);
    }

    public static DisplayImageOptions.Builder initImageBuilder(boolean isDiskCache) {
        return initImageBuilder(R.mipmap.no_pic_horizontal, isDiskCache, null);
    }

    public static DisplayImageOptions.Builder initImageBuilder(int defDrawableId, boolean isDiskCache) {
        return initImageBuilder(defDrawableId, isDiskCache, null);
    }

    public static DisplayImageOptions.Builder initImageBuilder(int defDrawableId, boolean isDiskCache, ImageScaleType scaleType) {
        DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder()
                .bitmapConfig(Bitmap.Config.RGB_565)
                // 设置图片的解码类型
                .showImageOnLoading(defDrawableId)
                .showImageForEmptyUri(defDrawableId) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(defDrawableId) // 设置图片加载/解码过程中错误时候显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(isDiskCache); // 设置下载的图片是否缓存在SD卡中

        if (scaleType != null && scaleType instanceof ImageScaleType) {
            builder.imageScaleType(scaleType); // 设置ImageView的显示属性
        }
        return builder;
    }

    public static DisplayImageOptions.Builder initImageBuilder(int defDrawableId, boolean inMemoryCache, boolean isDiskCache, ImageScaleType scaleType) {
        DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder()
                .bitmapConfig(Bitmap.Config.RGB_565)
                        // 设置图片的解码类型
                .showImageOnLoading(defDrawableId)
                .showImageForEmptyUri(defDrawableId) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(defDrawableId) // 设置图片加载/解码过程中错误时候显示的图片
                .cacheInMemory(inMemoryCache) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(isDiskCache); // 设置下载的图片是否缓存在SD卡中

        if (scaleType != null && scaleType instanceof ImageScaleType) {
            builder.imageScaleType(scaleType); // 设置ImageView的显示属性
        }
        return builder;
    }



    public static ImageLoaderConfiguration.Builder initImageConfigBuilder() {
        File cacheDir = StorageUtils.getOwnCacheDirectory(
                MainApplication.getInstance().getApplicationContext(), "imageloader/Cache");
//        PluLogUtil.log("-----cacheDir is " + cacheDir);
        ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(MainApplication.getInstance().getApplicationContext())
                .denyCacheImageMultipleSizesInMemory()
                .memoryCacheSizePercentage(50) //availableMemoryPercent，设置内存缓存使用占应最大内存的比例
                .memoryCache(new LRULimitedMemoryCache(5 * 1024 * 1024)) // 弱引用自动管理内存，达到上限时先remove最不常用的
//                .diskCache(new LruDiskCache(cacheDir, new Md5FileNameGenerator(), 50 * 1024 * 1024))
//                .memoryCacheExtraOptions(480, 800) // 设定内存图片缓存大小限制，不设置默认为屏幕的宽高
//                .memoryCacheSize(5 * 1024 * 1024) // 缓存到内存的最大数据
//                .memoryCache(new UsingFreqLimitedMemoryCache(10 * 1024 * 1024)) // 设定内存缓存为弱缓存
                /*.diskCacheFileCount(1000) // 缓存文件最大数量
                .diskCacheSize(50 * 1024 * 1024) // 缓存到文件的最大数据
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .diskCache(new LimitedAgeDiskCache(cacheDir,10*60))//设置图片的磁盘缓存时间为10min，缓存时间超过１０分钟就删除 */                   //    .diskCache(new UnlimitedDiskCache(cacheDir))
//                .writeDebugLogs() // LOG打包时要去掉
                .threadPoolSize(3)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .tasksProcessingOrder(QueueProcessingType.LIFO); // 线程优先级：last in first out


        return builder;
    }

}
