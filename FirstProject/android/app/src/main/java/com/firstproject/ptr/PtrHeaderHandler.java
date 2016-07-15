package com.firstproject.ptr;

/**
 * Created by lijie on 16/7/13.
 */
public interface PtrHeaderHandler {

    /**
     * 当下拉位置发生改变
     *
     * @param offset 偏移
     * @param state  状态
     */
    void onPositionUpdate(int offset, PtrState state);

    void onRefreshing();

    /**
     * 完成刷新
     *
     * @param state 刷新状态
     */
    void onCompleteRefreshing(PtrState state);


    /**
     * 获取刷新的下拉高度
     *
     * @return
     */
    int getRefreshHeight();

}
