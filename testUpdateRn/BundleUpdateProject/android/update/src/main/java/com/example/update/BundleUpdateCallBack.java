package com.example.update;

/**
 * Created by lijie on 16/7/18.
 */
public interface BundleUpdateCallBack {
    void onBundleMandatory(boolean isMandatory);

    void onReceiverByte(long receiverByte);

    void onBundleUpdateFinished();
}
