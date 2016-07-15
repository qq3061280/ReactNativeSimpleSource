package com.firstproject.utils;

import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.facebook.react.views.view.ReactViewGroup;

/**
 * Created by lijie on 16/7/13.
 */
public class UIHelper {
    private static final int SCROLL_UP = -1;

    private static final int SCROLL_DOWN = 1;
    /**
     * 是否可以下拉
     *
     * @param view
     * @return
     */
    public static boolean canScrollDown(View view) {
        boolean isFirst = true;
        //对listView进行适配，判断是否滚至顶部
        if (view instanceof ListView) {
            isFirst = false;
            ListView listView = (ListView) view;
            if (listView.getChildCount() == 0) {
                isFirst = true;
            } else {
                View child = listView.getChildAt(0);
                if (listView.getFirstVisiblePosition() == 0) {
                    isFirst = child.getTop() >= listView.getPaddingTop();
                }
            }
        }
        else if(view instanceof ReactViewGroup){
            isFirst=false;
            ReactViewGroup scrollView= (ReactViewGroup) view;
            if (scrollView.getChildCount()==0){
                isFirst=true;
            }
            else{
                View child=scrollView.getChildAt(0);
                isFirst=child.getTop()>=scrollView.getPaddingTop();
            }
            Log.e("info","isScroll-------"+isFirst);
        }
        Log.e("info",!ViewCompat.canScrollVertically(view, SCROLL_UP)+"------"+isFirst+"-------"+view.getClass());
        return !ViewCompat.canScrollVertically(view, SCROLL_UP) && isFirst;
    }

}
