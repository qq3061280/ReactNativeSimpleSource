package com.example.update;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by lijie on 16/7/16.
 */
public class UpdateUtils {

    /**根据key获取map的value值*/
    public static String tryGetString(HashMap map, String key){
        try {
            return (String) map.get(key);
        }catch (Exception e){
            return null;
        }
    }

    /**获取文件绝对路径*/
    public static String appendPathComponent(String basePath,String appendPathComponent){
        return new File(basePath, appendPathComponent).getAbsolutePath();
    }

    /**从指定路径中获取WritableMap*/
    public static HashMap getWritableMapFromFile(String filePath) throws IOException {
        String content = FileUtils.readFileToString(filePath);
        try {
            JSONObject json=new JSONObject(content);
            return convertJsonObjectToWritable(json);
        } catch (JSONException e) {
            throw new RuntimeException("failed to parse json");
        }
    }

//    /**将JsonArray对象转换成WritableArray*/
//    public static HashMap convertJsonArrayToWritable(JSONArray jsonArr){
////        WritableArray arr= Arguments.createArray();
//        HashMap arr=new HashMap();
//        for (int i=0; i<jsonArr.length(); i++) {
//            Object obj = null;
//            try {
//                obj = jsonArr.get(i);
//            } catch (JSONException jsonException) {
//
//            }
//
//
////            if (obj instanceof JSONObject)
////                arr.pushMap(convertJsonObjectToWritable((JSONObject) obj));
////            else if (obj instanceof JSONArray)
////                arr.pushArray(convertJsonArrayToWritable((JSONArray) obj));
////            else if (obj instanceof String)
////                arr.pushString((String) obj);
////            else if (obj instanceof Double)
////                arr.pushDouble((Double) obj);
////            else if (obj instanceof Integer)
////                arr.pushInt((Integer) obj);
////            else if (obj instanceof Boolean)
////                arr.pushBoolean((Boolean) obj);
////            else if (obj == null)
////                arr.pushNull();
////            else
////                throw new RuntimeException("Unrecognized object: " + obj);
//        }
//
//        return arr;
//    }

    /**将 JsonObject对象转换成Writable对象*/
    public static HashMap convertJsonObjectToWritable(JSONObject jsonObject){
        HashMap map=new HashMap();
        Iterator<String> it=jsonObject.keys();
        while (it.hasNext()){
            String key=it.next();
            Object obj=null;
            try {
                obj=jsonObject.get(key);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (obj instanceof String){
                map.put(key,(String)obj);
            }
            else if(obj instanceof Boolean){
                map.put(key,(Boolean)obj);
            }
            else if (obj instanceof Double){
                map.put(key,(Double)obj);
            }
            else if (obj instanceof Integer){
                map.put(key,(Integer)obj);
            }
            else if (obj ==null){
                map.put(key,null);
            }
            else {
                throw new RuntimeException("Unrecognized object: " + obj);
            }
        }
        return map;
    }


    public static String findJSBundleInUpdateContents(String folderPath, String expectedFileName) {
        File folder = new File(folderPath);
        File[] folderFiles = folder.listFiles();
        for (File file : folderFiles) {
            String fullFilePath = UpdateUtils.appendPathComponent(folderPath, file.getName());
            if (file.isDirectory()) {
                String mainBundlePathInSubFolder = findJSBundleInUpdateContents(fullFilePath, expectedFileName);
                if (mainBundlePathInSubFolder != null) {
                    return UpdateUtils.appendPathComponent(file.getName(), mainBundlePathInSubFolder);
                }
            } else {
                String fileName = file.getName();
                if (fileName.equals(expectedFileName)) {
                    return fileName;
                }
            }
        }
        return null;
    }

}
