package com.example.update;

/**
 * Created by lijie on 16/7/16.
 */
public class UpdateConstants {

    public static final String ASSETS_BUNDLE_PREFIX = "assets://HotUpdate/";
    public static final String DEFAULT_JS_BUNDLE_NAME = "index.android.bundle";
    /**存放 更新文件的文件夹名字*/
    public static final String PLU_RN_FOLDER_PREFIX = "PluRN";
    /**存放 当前与上一版本的 hash值*/
    public static final String STATUS_FILE = "status.json";
    /**当前应该显示文件的key*/
    public static final String CURRENT_PACKAGE_KEY = "currentPackage";

    public static final String PREVIOUS_PACKAGE_KEY = "previousPackage";

    /**当前版本包的信息存放json*/
    public static final String PACKAGE_FILE_NAME = "app.json";

    public static final String DOWNLOAD_FILE_NAME = "download.zip";

    public static final int DOWNLOAD_BUFFER_SIZE = 1024 * 256;

    public static final String UNZIPPED_FOLDER_NAME = "unzipped";


    /**bundle文件的相对路径key*/
    public static final String RELATIVE_BUNDLE_PATH_KEY = "bundlePath";

    public static final String PACKAGE_APP_VERSION="appVersion";
    public static final String PACKAGE_APP_LABEL="label";
    /**访问外网的HOST*/
    private static final String HOST="http://git.oschina.net/dshihouzaishuo/testRnVersion/raw/master/";
    /**检查最新的版本文件*/
    public static String getCheckLastestUrl(String appVersion){
        return HOST+appVersion+".json";
    }
    public static final String CHECK_INFO_LABEL="label";
    public static final String CHECK_INFO_DOWNLOAD_URL="downloadUrl";
    public static final String CHECK_INFO_ISMANDOTORY="isMandatory";
    public static final String CHECK_INFO_PACKAGEHASH="packageHash";
    public static final String CHECK_INFO_FAILED="isFailed";
    /**
     *  打包命令:react-native bundle --assets-dest HotUpdate --bundle-output HotUpdate/index.android.bundle --dev false --entry-file index.android.js --platform android
     */
}
