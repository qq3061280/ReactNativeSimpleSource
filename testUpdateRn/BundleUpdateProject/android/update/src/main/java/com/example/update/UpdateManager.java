package com.example.update;


import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.HashMap;

/**
 * Created by lijie on 16/7/16.
 */
public class UpdateManager {


    private String mDocumentsDirectory;

    public UpdateManager(String documentsDirectory){
        this.mDocumentsDirectory=documentsDirectory;
    }
    private String getDocumentsDirectory() {
        return mDocumentsDirectory;
    }

    /**存放 升级版本的文件夹*/
    private String getUpdateFilePath(){
        String updateFilePath=UpdateUtils.appendPathComponent(getDocumentsDirectory(),UpdateConstants.PLU_RN_FOLDER_PREFIX);
        return updateFilePath;
    }

    /**获取 状态文件的路径*/
    private String getStatusFilePath(){
        return UpdateUtils.appendPathComponent(getUpdateFilePath(),UpdateConstants.STATUS_FILE);
    }

    /**获取 更新配置文件的WritableMap对象 */
    public HashMap getCurrentPackageInfo(){
        String statusFilePath=getStatusFilePath();
        if (FileUtils.fileAtPathExists(statusFilePath)){
            try {
                return  UpdateUtils.getWritableMapFromFile(statusFilePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new HashMap();
    }

    /**根据文件夹名字获取绝对路径*/
    public String getPackageFolderPath(String packageHash){
        return UpdateUtils.appendPathComponent(getUpdateFilePath(),packageHash);
    }

    /**获取当前 应该显示的更新文件 路径*/
    public String getCurrentPackageFolderPath(){
        HashMap info=getCurrentPackageInfo();
        String packageHash=UpdateUtils.tryGetString(info,UpdateConstants.CURRENT_PACKAGE_KEY);
        if (packageHash!=null){
            return getPackageFolderPath(packageHash);
        }
        return null;
    }

    /**获取配置文件中当前更新文件的Hash值*/
    public String getCurrentPackageHash(){
        HashMap info=getCurrentPackageInfo();
        return UpdateUtils.tryGetString(info,UpdateConstants.CURRENT_PACKAGE_KEY);
    }


    /**返回当前版本包的配置信息 app.json*/
    public HashMap getCurrentPackage(){
        String packageHash=getCurrentPackageHash();
        if (packageHash==null){
            return null;
        }
        return getPackage(packageHash);
    }
    /**根据版本号hash值返回版本包的配置信息 app.json */
    public HashMap getPackage(String packageHash){
        String folderPath=getPackageFolderPath(packageHash);
        String packageFilePath=UpdateUtils.appendPathComponent(folderPath,UpdateConstants.PACKAGE_FILE_NAME);
        if (!FileUtils.fileAtPathExists(packageFilePath)){
            return null;
        }
        try {
            return UpdateUtils.getWritableMapFromFile(packageFilePath);
        } catch (IOException e) {
            return null;
        }
    }

    public String getCurrentPackageBundlePath(String bundleFileName){
        String packageFolder=getCurrentPackageFolderPath();
        if (packageFolder==null){
            return null;
        }
        HashMap currentPackage=getCurrentPackage();
        if (currentPackage==null){
            return null;
        }
        String relativeBundlePath=UpdateUtils.tryGetString(currentPackage,UpdateConstants.RELATIVE_BUNDLE_PATH_KEY);
        if (relativeBundlePath==null){
            return UpdateUtils.appendPathComponent(packageFolder,bundleFileName);
        }
        else {
            return UpdateUtils.appendPathComponent(packageFolder,relativeBundlePath);
        }
    }


    public void rollBackPreviousPackage(){
//        try {
//            Log.e("info","正在执行回滚操作.....");
//            //将status.json 文件的 currentPackageHash删除, 用 previousPackageHash 当做currentPackageHash
//            String statusContent=FileUtils.readFileToString(getStatusFilePath());
//            JSONObject jsonObject=new JSONObject(statusContent);
//            String previousPackageHash=jsonObject.getString(UpdateConstants.PREVIOUS_PACKAGE_KEY);
//            String currentPackageHash=jsonObject.getString(UpdateConstants.CURRENT_PACKAGE_KEY);
//            Log.e("info","正在执行回滚操作.....1");
////            Log.e("info",previousPackageHash+"-----previousPackageHash----");
//
//            if (TextUtils.isEmpty(previousPackageHash)){ //没有可回退的版本
//                FileUtils.writeStringToFile("",getStatusFilePath());
//                Log.e("info","正在执行回滚操作.....2");
//            }
//            else{
//                jsonObject.remove(UpdateConstants.PREVIOUS_PACKAGE_KEY);
//                jsonObject.put(UpdateConstants.CURRENT_PACKAGE_KEY,previousPackageHash);
//                FileUtils.writeStringToFile(jsonObject.toString(),getStatusFilePath());
//                Log.e("info","正在执行回滚操作.....3");
//            }
//
//            //将出错的bundle 的标志设置为 failed:true
//            String folderPath=getPackageFolderPath(currentPackageHash);
//            String packageFilePath=UpdateUtils.appendPathComponent(folderPath,UpdateConstants.PACKAGE_FILE_NAME);
//            String appJsonContent=FileUtils.readFileToString(packageFilePath);
//            JSONObject appJsonObject=new JSONObject(appJsonContent);
//            appJsonObject.put(UpdateConstants.CHECK_INFO_FAILED,true);
//            FileUtils.writeStringToFile(appJsonObject.toString(),packageFilePath);
//            Log.e("info","正在执行回滚操作.....4");
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }

    /**写入appJson*/
    private void saveAppJsonToSdCard(String appJson,File file){
        if (FileUtils.fileAtPathExists(file.getAbsolutePath())){
            file.delete();
        }
        try {
            FileUtils.writeStringToFile(appJson,file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void downloadBundleFile(String downloadUrlString,String packageHash,JSONObject appJson,BundleUpdateCallBack mBundleUpdateCallBack) throws IOException {
        HttpURLConnection connection = null;
        BufferedInputStream bin = null;
        FileOutputStream fos = null;
        BufferedOutputStream bout = null;
        File downloadFile = null;
        boolean isZip = false;

        try {
            URL downloadUrl = new URL(downloadUrlString);
            connection = (HttpURLConnection) (downloadUrl.openConnection());
            connection.setRequestProperty("Accept-Encoding", "identity");
            long totalBytes = connection.getContentLength();
            long receivedBytes = 0;
            bin = new BufferedInputStream(connection.getInputStream());
            File downloadFolder = new File(getUpdateFilePath());
            downloadFolder.mkdirs();
            downloadFile = new File(downloadFolder, UpdateConstants.DOWNLOAD_FILE_NAME);
            if (FileUtils.fileAtPathExists(downloadFile.getAbsolutePath())){
                downloadFile.delete();
            }
            fos = new FileOutputStream(downloadFile);
            bout = new BufferedOutputStream(fos, UpdateConstants.DOWNLOAD_BUFFER_SIZE);
            byte[] data = new byte[UpdateConstants.DOWNLOAD_BUFFER_SIZE];
            byte[] header = new byte[4];

            int numBytesRead = 0;
            while ((numBytesRead = bin.read(data, 0, UpdateConstants.DOWNLOAD_BUFFER_SIZE)) >= 0) {
                if (receivedBytes < 4) {
                    for (int i = 0; i < numBytesRead; i++) {
                        int headerOffset = (int)(receivedBytes) + i;
                        if (headerOffset >= 4) {
                            break;
                        }
                        header[headerOffset] = data[i];
                    }
                }
                receivedBytes += numBytesRead;
                bout.write(data, 0, numBytesRead);
                if (mBundleUpdateCallBack!=null){
                    mBundleUpdateCallBack.onReceiverByte(receivedBytes);
                }
//                progressCallback.call(new DownloadProgress(totalBytes, receivedBytes));
            }
            if (mBundleUpdateCallBack!=null){
                mBundleUpdateCallBack.onBundleUpdateFinished();
            }
            if (totalBytes != receivedBytes) {
                Log.d("info","receivedBytes="+receivedBytes+",expected="+totalBytes);
                // totalBytes 需要服务端支持 Content-Length
            }
            isZip = ByteBuffer.wrap(header).getInt() == 0x504b0304;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bout != null) bout.close();
                if (fos != null) fos.close();
                if (bin != null) bin.close();
                if (connection != null) connection.disconnect();
            } catch (IOException e) {
                throw new RuntimeException("Error closing IO resources.", e);
            }
        }
        if (isZip) {
            String newUpdateFolderPath=getPackageFolderPath(packageHash);
            String newUpdateMetadataPath = UpdateUtils.appendPathComponent(newUpdateFolderPath, UpdateConstants.PACKAGE_FILE_NAME);
            String unzippedFolderPath = getUnzippedFolderPath();
            FileUtils.unzipFile(downloadFile, unzippedFolderPath);
            FileUtils.deleteFileOrFolderSilently(downloadFile);

            FileUtils.copyDirectoryContents(unzippedFolderPath, newUpdateFolderPath);
            FileUtils.deleteFileAtPathSilently(unzippedFolderPath);

            String relativeBundlePath = UpdateUtils.findJSBundleInUpdateContents(newUpdateFolderPath, UpdateConstants.DEFAULT_JS_BUNDLE_NAME);
            if (relativeBundlePath==null){
                throw new RuntimeException("not found index.android.bundle");
            }
            else{
                //如果存在app.json 则删除
                if (FileUtils.fileAtPathExists(newUpdateMetadataPath)) {
                    File metadataFileFromOldUpdate = new File(newUpdateMetadataPath);
                    metadataFileFromOldUpdate.delete();
                }
                try {
                    //添加bundlePath 字段,标志index.android.bundle相对于app.json的路径
                    appJson.put(UpdateConstants.RELATIVE_BUNDLE_PATH_KEY,relativeBundlePath);
                    saveAppJsonToSdCard(appJson.toString(),new File(newUpdateMetadataPath));
                    updateStatusFile(packageHash);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void updateStatusFile(String packageHash){
        String statusFilePath=getStatusFilePath();

        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put(UpdateConstants.CURRENT_PACKAGE_KEY,packageHash);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //如果之前存在statusFile,则把之前的currentPackageHash,当previousPackageHash
        if (FileUtils.fileAtPathExists(statusFilePath)){
            try {
                String preContent=FileUtils.readFileToString(statusFilePath);
                if (!TextUtils.isEmpty(preContent)){
                    JSONObject preJson=new JSONObject(preContent);
                    String prePackageHash=preJson.getString(UpdateConstants.CURRENT_PACKAGE_KEY);
                    jsonObject.put(UpdateConstants.PREVIOUS_PACKAGE_KEY,prePackageHash);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            FileUtils.writeStringToFile(jsonObject.toString(),statusFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**解压文件夹 data/data/xxx/file/PluRn/unzipped/   */
    private String getUnzippedFolderPath() {
        return UpdateUtils.appendPathComponent(getUpdateFilePath(), UpdateConstants.UNZIPPED_FOLDER_NAME);
    }



}
