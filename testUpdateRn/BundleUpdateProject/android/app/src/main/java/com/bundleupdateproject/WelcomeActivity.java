package com.bundleupdateproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.example.update.BundleUpdateCallBack;
import com.example.update.UpdatePush;
import com.orhanobut.logger.Logger;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


/**
 * Created by lijie on 16/7/18.
 */
public class WelcomeActivity extends Activity {

    private long time_load;//加载时间

    private static int JUMP_TIME = 1000 * 3;//开屏启动时间

    private boolean isBundleMandatory;//检查bundle版本是否为强制更新版

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        time_load = System.currentTimeMillis();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {//4.4之下
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        setContentView(R.layout.activity_welcome);
        UpdatePush.checkRemoteBundleInfo(new BundleUpdateCallBack() {
            @Override
            public void onBundleMandatory(boolean isMandatory) {
                isBundleMandatory=isMandatory;
                if (!isMandatory){ //强制更新的版本
                    jump();
                }
            }

            @Override
            public void onReceiverByte(long receiverByte) {
                Log.d("info","receiverByte--"+receiverByte);
            }

            @Override
            public void onBundleUpdateFinished() {
                if (isBundleMandatory){
                    jump();
                }
            }
        });
    }

    private void jump(){
        time_load = System.currentTimeMillis() - time_load;
        Logger.d("启动APP与访问接口耗时..."+time_load);
        long time_delayed = JUMP_TIME - time_load;//需要延迟加载的时间，防止二次启动过快
        Logger.d("延迟跳转时间:"+time_delayed);
        if (time_delayed < 0) time_delayed = 0;
        Observable.timer(time_delayed, TimeUnit.MILLISECONDS).observeOn(Schedulers.io()).subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Long>() {
            @Override
            public void call(Long aLong) {
                startActivity(new Intent(WelcomeActivity.this,MainActivity.class));
            }
        });
    }

}
