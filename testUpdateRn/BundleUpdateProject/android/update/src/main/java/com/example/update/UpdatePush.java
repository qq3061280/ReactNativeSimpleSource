package com.example.update;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by lijie on 16/7/16.
 */
public class UpdatePush {

    private static UpdatePush mCurrentInstance;

    private Context mContext;

    private String mAppVersion;

    private UpdateManager mUpdateManager;

    public UpdatePush(Context context){
        this.mContext=context;
        mUpdateManager=new UpdateManager(context.getFilesDir().getAbsolutePath());
        PackageInfo pInfo = null;
        try {
            pInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
            mAppVersion = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        mCurrentInstance=this;
    }


    public static String getJsBundle(){
        if (mCurrentInstance==null){
            throw new RuntimeException("should initialize this");
        }
        return mCurrentInstance.getJsBundleFile();
    }

    /**找到应该显示的版本号*/
    public String getJsBundleFile(){
        //放在asserts里面的bundle
        String binaryJsBundleUrl = UpdateConstants.ASSETS_BUNDLE_PREFIX + UpdateConstants.DEFAULT_JS_BUNDLE_NAME;
        //获取相应目录下的bundle
        String packageFilePath=mUpdateManager.getCurrentPackageBundlePath(UpdateConstants.DEFAULT_JS_BUNDLE_NAME);
        if (packageFilePath!=null){
            HashMap packageMetadata = this.mUpdateManager.getCurrentPackage();
            String packageAppVersion = UpdateUtils.tryGetString(packageMetadata, UpdateConstants.PACKAGE_APP_VERSION);
            if (mAppVersion.equals(packageAppVersion)){
                return packageFilePath;
            }
        }
//        isUsedAssertsBundle=true;
        return binaryJsBundleUrl;
    }

    private OkHttpClient okHttpClient;
    OkHttpClient getOkHttpClient(){
        if (okHttpClient==null){
            okHttpClient=new OkHttpClient();
        }
        return okHttpClient;
    }
    private BundleUpdateCallBack mBundleUpdateCallBack;

    private void checkUpdate(BundleUpdateCallBack bundleUpdateCallBack){
        this.mBundleUpdateCallBack=bundleUpdateCallBack;
        String url=UpdateConstants.getCheckLastestUrl(mAppVersion);
        Log.d("info","查询更新bundle的地址.."+url);
        Request request=new Request.Builder().url(url).build();
        Call call=getOkHttpClient().newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                handleBundleInfo(response);
            }
        });
    }

    /**检查版本号是否需要更新*/
    public static void checkRemoteBundleInfo(BundleUpdateCallBack callBack){
        if (mCurrentInstance==null){
            throw new RuntimeException("mCurrentInstance is null");
        }
        mCurrentInstance.checkUpdate(callBack);
    }

    public void rollBackCurrentPackage(){
        mUpdateManager.rollBackPreviousPackage();
    }

    private void handleBundleInfo(Response response){
        if (response.isSuccessful()){
            JSONObject jsonObject=null;
            try {
                jsonObject=new JSONObject(response.body().string());
                String label=jsonObject.getString(UpdateConstants.CHECK_INFO_LABEL);
                if (label.equals("v0")){ //v0版本表示底包,不需要升级
                    if (mBundleUpdateCallBack!=null){
                        mBundleUpdateCallBack.onBundleMandatory(false);
                        mBundleUpdateCallBack=null;
                    }
                    return;
                }
                String downloadUrl=jsonObject.getString(UpdateConstants.CHECK_INFO_DOWNLOAD_URL);
                boolean isMandatory=jsonObject.getBoolean(UpdateConstants.CHECK_INFO_ISMANDOTORY);
                String packageHash=jsonObject.getString(UpdateConstants.CHECK_INFO_PACKAGEHASH);
                if (!TextUtils.isEmpty(label)){
                    if (mBundleUpdateCallBack!=null){
                        mBundleUpdateCallBack.onBundleMandatory(isMandatory);
                        if (!isMandatory){
                            mBundleUpdateCallBack=null;
                        }
                    }
                        String packageFilePath=mUpdateManager.getCurrentPackageBundlePath(UpdateConstants.DEFAULT_JS_BUNDLE_NAME);
                        //确认文件是否存在
                        if (packageFilePath!=null){
                            //找到app.json的配置信息
                            HashMap packageMetadata = this.mUpdateManager.getCurrentPackage();
                            String packageAppVersion = UpdateUtils.tryGetString(packageMetadata, UpdateConstants.PACKAGE_APP_VERSION);
                            String packageAppLabel=UpdateUtils.tryGetString(packageMetadata,UpdateConstants.PACKAGE_APP_LABEL);

                            if (packageAppVersion.equals(mAppVersion)){
                                if (!packageAppLabel.equals(label)){
                                    Log.d("info","bundle有新的版本可下载");
                                    mUpdateManager.downloadBundleFile(downloadUrl,packageHash,jsonObject,mBundleUpdateCallBack);
                                }
                                else{
                                    if (mBundleUpdateCallBack!=null){
                                        mBundleUpdateCallBack.onBundleMandatory(false);
                                    }
                                    Log.d("info","bundle当前是最新版本");
                                }
                            }
                            else{
                                if (mBundleUpdateCallBack!=null){
                                    mBundleUpdateCallBack.onBundleMandatory(false);
                                }
                                Log.d("info","该bundle版本不适用于该APP版本");
                            }
                        }
                        else{
                            Log.d("info","需要下载bundle");
                            mUpdateManager.downloadBundleFile(downloadUrl,packageHash,jsonObject,mBundleUpdateCallBack);
                        }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
