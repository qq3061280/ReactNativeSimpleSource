package com.firstproject.ptr;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firstproject.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by lijie on 16/7/13.
 */
public class PluPtrHeaderView extends LinearLayout implements PtrHeaderHandler {

    private static final String UPDATE_TIME_KEY = "update_time";
    private static final int DP_ANM_SIZE = 60;

    private DateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm");
    private DateFormat timeFormat = new SimpleDateFormat("HH:mm");

    private SharedPreferences sharedPreferences;
    private DragonEggAminView dragonEggAminView;
    private TextView refreshStateTv;
    private TextView updateTimeTv;
    private String key;

    private int currentOffset;


    public PluPtrHeaderView(Context context) {
        super(context);
        init(context);
    }

    public PluPtrHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PluPtrHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {

        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER);

        dragonEggAminView = new DragonEggAminView(getContext());
        int animViewSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DP_ANM_SIZE, getResources().getDisplayMetrics());
        dragonEggAminView.setLayoutParams(new MarginLayoutParams(animViewSize, animViewSize));
        addView(dragonEggAminView);

        View textLayout = LayoutInflater.from(context).inflate(R.layout.head_text_layout, null);
        refreshStateTv = (TextView) textLayout.findViewById(R.id.tv_state);
        updateTimeTv = (TextView) textLayout.findViewById(R.id.tv_time);
        addView(textLayout);

        setVisibility(View.INVISIBLE);

    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        String lastUpdateTime = getFormatUpdateTime(getLastUpdateTime());
        updateTimeTv(lastUpdateTime);
    }

    @Override
    public void onPositionUpdate(int offset, PtrState state) {

        currentOffset = offset;

        //正在刷新不改变龙蛋动画
        if (state == PtrState.REFRESHING) {
            return;
        }

        if (offset > 0) {
            dragonEggAminView.updateMovePos(offset - getHeight() * 2 / 3, state);
            if (getVisibility() != View.VISIBLE)
                setVisibility(View.VISIBLE);
        } else {
            dragonEggAminView.updateMovePos(0, state);
            if (getVisibility() != View.INVISIBLE) {
                setVisibility(View.INVISIBLE);
            }
        }
        Log.d("ptr", offset + "," + getHeight());
        if (state == PtrState.INIT) {
            if (offset < getRefreshHeight()) {
                refreshStateTv.setText(R.string.ptr_pull_to_refresh);
            } else {
                refreshStateTv.setText(R.string.ptr_release_to_refresh);
            }
        }

    }

    @Override
    public void onRefreshing() {
        Log.d("headerView", "onrefreshing");
        if (getVisibility() != View.VISIBLE) {
            setVisibility(View.VISIBLE);
        }
        dragonEggAminView.updateState(DragonEggAminView.State.AnimStart);
        refreshStateTv.setText(R.string.ptr_refreshing);
    }

    @Override
    public void onCompleteRefreshing(PtrState state) {
        if (currentOffset <= 0) {
            if (getVisibility() != View.INVISIBLE) {
                setVisibility(View.INVISIBLE);
            }
        }
        if (state == PtrState.REFRESH_SUCCESS) {
            refreshStateTv.setText(R.string.ptr_refresh_complete);
            handleUpdateSuccess();
        } else {
            refreshStateTv.setText(R.string.ptr_refresh_failure);
        }
        dragonEggAminView.updateMovePos(dragonEggAminView.getMaxOpenHeight(), PtrState.INIT);
    }

    @Override
    public int getRefreshHeight() {
        return getHeight() * 2 / 3 + dragonEggAminView.getMaxOpenHeight();
    }


    /**
     * 更新刷新时间
     */
    public void updateRefreshTime() {
        handleUpdateSuccess();
    }

    private SharedPreferences getSharedPreference() {
        if (sharedPreferences == null && !TextUtils.isEmpty(key)) {
            sharedPreferences = getContext().getSharedPreferences(key, 0);
        }
        return sharedPreferences;
    }


    /**
     * 设置保存更新时间唯一键值
     *
     * @param key
     */
    public void setSaveUpdateTimeKey(String key) {
        this.key = key;
        sharedPreferences = getSharedPreference();
    }

    /**
     * 保存更新时间
     */
    private void saveUpdateTime() {
        long time = System.currentTimeMillis();
        SharedPreferences sharedPreferences = getSharedPreference();
        if (sharedPreferences != null) {
            sharedPreferences.edit().putLong(UPDATE_TIME_KEY, time).apply();
        }
    }

    /**
     * 获取上一次更新时间
     *
     * @return
     */
    private long getLastUpdateTime() {
        SharedPreferences sharedPreferences = getSharedPreference();
        if (sharedPreferences != null) {
            return sharedPreferences.getLong(UPDATE_TIME_KEY, 0);
        }
        return 0;
    }

    /**
     * 获取格式化更新时间文本
     *
     * @param time
     * @return
     */
    private String getFormatUpdateTime(long time) {
        if (time == 0) {
            return "";
        }
        long sysDate = Calendar.getInstance().getTimeInMillis() / (1000 * 3600 * 24);
        long updateDate = time / (1000 * 3600 * 24);
        if (sysDate == updateDate) {
            return String.format(getResources().getString(R.string.ptr_today_time), timeFormat.format(new Date(time)));
        }

        return dateFormat.format(new Date(time));
    }

    private void handleUpdateSuccess() {
        String updateTime = getFormatUpdateTime(System.currentTimeMillis());
        updateTimeTv(updateTime);
        saveUpdateTime();
    }

    private void updateTimeTv(String formatTime) {
        if (!TextUtils.isEmpty(formatTime)) {
            String txt = getResources().getString(R.string.ptr_update_time) + " " + formatTime;
            updateTimeTv.setText(txt);
        } else {
            updateTimeTv.setText(getResources().getString(R.string.ptr_update_time));
        }
    }
}
