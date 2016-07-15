package com.firstproject.ptr;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;

import com.firstproject.utils.UIHelper;

/**
 * Created by lijie on 16/7/13.
 */

/**
 * Created by liuj on 2016/5/4.
 * 下拉刷新容器
 */
public class PtrFrameLayout extends FrameLayout {

    private static final String TAG = "PtrFrameLayout";

    private static final float FRICTION = 0.5f; //阻力
    private static final int DP_REFRESH_HEIGHT = 60; //刷新高度
    private static final int DP_MAX_PULL_HEIGHT = 160; //最大下拉距离

    private static final int REFRESH_DELAY = 250; //刷新回调延迟
    private static final int MOVE_ANIM_DURATION = 300; //移动动画的时间间隔
    private static final int AUTO_MOVE_ANIM_DELAY = 300; //自动触发移动动画的延迟
    private static final int COMPLETE_ANIM_DURATION = 300; //刷新完成的动画间隔


    private View mContentView;
    private View headerView;

    private PtrHeaderHandler ptrHeaderHandler;
    private OnRefreshListener refreshListener;
    private ValueAnimator currentAnim;

    private int refreshHeight;
    private int maxPullHeight;
    private int touchSlop;
    private int downX, downY;
    private int lastX, lastY;
    private int currentTop;
    private boolean isRefreshing;
    private boolean isMoving;

    public PtrFrameLayout(Context context) {
        super(context);
        init(context);
    }

    public PtrFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PtrFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private void init(Context context) {
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        refreshHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DP_REFRESH_HEIGHT, displayMetrics);
        maxPullHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DP_MAX_PULL_HEIGHT, displayMetrics);
    }

    /**
     * 正在刷新的列表位置高度
     *
     * @return
     */
    private int getRefreshHeight() {
        if (ptrHeaderHandler == null) {
            return refreshHeight;
        } else {
            return ptrHeaderHandler.getRefreshHeight();
        }
    }

    public boolean isRefreshing() {
        return isRefreshing;
    }

    public void updateLayout(){
        if (getChildCount() == 0) {
            return;
        }
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child instanceof PtrHeaderHandler) {
                headerView = child;
                ptrHeaderHandler = (PtrHeaderHandler) headerView;
            } else {
                mContentView = child;
            }
        }

        if (mContentView != null) {
            mContentView.bringToFront();
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        updateLayout();
    }


    /**
     * 设置头部
     *
     * @param headerView
     */
    public void setHeaderView(View headerView) {
        MarginLayoutParams marginLayoutParams = new MarginLayoutParams(-1, -2);
        addView(headerView, 0, marginLayoutParams);
        if (headerView instanceof PtrHeaderHandler) {
            ptrHeaderHandler = (PtrHeaderHandler) headerView;
        }
    }

    /**
     * 添加内容
     *
     * @param contentView
     */
    public void addContentView(View contentView) {
        this.mContentView = contentView;
        addView(contentView);
        if (mContentView != null) {
            mContentView.bringToFront();
        }

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        int x = (int) ev.getX();
        int y = (int) ev.getY();


        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            downX = x;
            downY = y;
            //取消当前动画
            if (currentAnim != null&&currentAnim.isStarted()) {
                currentAnim.cancel();
                currentAnim = null;
            }
        } else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            int deltaX = x - downX;
            int deltaY = y - downY;

            Log.d(TAG, "dispatchTouchEvent  action_move" + isMoving + " " + deltaY + " " + mContentView.getTop());

            if (!isMoving) {
                //列表内部已处于滚动状态，下拉时需要将控制权交给容器,这里模拟手势
                if (deltaY > 0 && mContentView.getTop() == 0 && canMove(deltaY, deltaX) && canIntercept(deltaY)) {
                    Log.d(TAG, "dispatchTouchEvent  canmove" + isMoving + " " + deltaY + " " + mContentView.getTop());
                    isMoving = true;
                    lastX = x;
                    lastY = y;
                    MotionEvent motionEvent = MotionEvent.obtain(ev);
                    motionEvent.setAction(MotionEvent.ACTION_CANCEL);//模拟手指抬起的事件，将时间传给mContentView
                    mContentView.onTouchEvent(motionEvent);
                    ev.setAction(MotionEvent.ACTION_DOWN);
                }
            } else {
                //向上滑动，列表的top为0，则将后继事件传递给列表
                if (deltaY < 0 && mContentView.getTop() == 0) {
                    isMoving = false;
                    MotionEvent motionEvent = MotionEvent.obtain(ev);
                    motionEvent.setAction(MotionEvent.ACTION_CANCEL);
                    dispatchTouchEvent(motionEvent);
                    ev.setAction(MotionEvent.ACTION_DOWN);
                }
            }

        } else if (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_CANCEL) {

            if (isMoving) {
                //列表处于随手指移动状态下
                if (mContentView.getTop() > getRefreshHeight()) {
                    //处理刷新，并且自动回到刷新位置
                    handleRefresh(false);
                } else {
                    //回到顶部
                    if (mContentView.getTop() > 0) {
                        animMoveContent(mContentView.getTop(), 0, 0, completeListener);
                    }
                }
            } else {
                //未移动时，需要判断是否有回至顶部的动画，如果没有则新启动一个
                if (currentAnim == null) {
                    if (mContentView.getTop() > 0) {
                        animMoveContent(mContentView.getTop(), 0, 0, completeListener);
                    }
                }
            }

            clearValues();
        }

        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        int x = (int) ev.getX();
        int y = (int) ev.getY();


        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            downX = x;
            downY = y;
        } else if (ev.getAction() == MotionEvent.ACTION_MOVE) {

            int deltaX = x - downX;
            int deltaY = y - downY;

            Log.d(TAG, "onInterceptTouchEvent  action_move" + isMoving + "  " + deltaY + " " + mContentView.getTop());
            if (!isMoving) {
                if (canMove(deltaY, deltaX) && canIntercept(deltaY)) {
                    isMoving = true;
                    lastX = x;
                    lastY = y;
                    return true;
                } else {
                    isMoving = false;
                }
            } else {
                return true;
            }

        } else if (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_CANCEL) {
            clearValues();
        }

        return super.onInterceptTouchEvent(ev);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            downX = x;
            downY = y;
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {

            int deltaX = x - downX;
            int deltaY = y - downY;

            if (isMoving) {
                int offset = y - lastY;
                moveContent(offset);
                Log.d(TAG, "onTouchEvent  action_move" + isMoving + "  " + deltaY + " " + mContentView.getTop());
                lastX = x;
                lastY = y;
            } else {
                if (canMove(deltaY, deltaX) && canIntercept(deltaY)) {
                    isMoving = true;
                    lastY = y;
                    lastX = x;
                    return true;
                }
            }

        } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {

            Log.d(TAG, mContentView.getTop() + "," + getRefreshHeight());

            clearValues();
        }


        return true;
    }

    /**
     * 清理一些参数
     */
    private void clearValues() {
        downX = 0;
        downY = 0;
        lastY = 0;
        lastX = 0;
        isMoving = false;
    }


    /**
     * 处理刷新
     *
     * @param auto 是否是自动刷新
     */
    private void handleRefresh(boolean auto) {

        InnerMoveAnimListener innerMoveAnimListener = null;

        if (!isRefreshing) {
            isRefreshing = true;
            if (ptrHeaderHandler != null) {
                ptrHeaderHandler.onRefreshing();
            }
            innerMoveAnimListener = autoRefreshListener;  //动画完成后调用的加载回调
        }
        animMoveContent(mContentView.getTop(), getRefreshHeight(), auto ? MOVE_ANIM_DURATION : 0, auto ? AUTO_MOVE_ANIM_DELAY : 0, innerMoveAnimListener);
    }

    /**
     * 判断移动手势是否有效
     *
     * @param deltaY
     * @param deltaX
     * @return
     */
    private boolean canMove(int deltaY, int deltaX) {
        return Math.abs(deltaY) > touchSlop && Math.abs(deltaY) > Math.abs(deltaX);
    }

    /**
     * 判断是否可以下拉刷新
     *
     * @param deltaY
     * @return
     */
    private boolean canIntercept(int deltaY) {
        int top = mContentView.getTop();
        Log.e("info",top+"-----top------"+deltaY);
        return top != 0 || deltaY > 0 && UIHelper.canScrollDown(mContentView);
    }


    /**
     * 动画更新列表位置
     *
     * @param from
     * @param to
     * @param delay
     * @param listener
     */
    private void animMoveContent(int from, final int to, int delay, final InnerMoveAnimListener listener) {
        animMoveContent(from, to, MOVE_ANIM_DURATION, delay, listener);
    }

    /**
     * 动画更新列表位置
     *
     * @param from     起始位置
     * @param to       结束位置
     * @param duration 动画间隔
     * @param delay    动画延迟
     * @param listener 动画回调
     */
    private void animMoveContent(final int from, final int to, int duration, int delay, final InnerMoveAnimListener listener) {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(from, to).setDuration(duration);
        valueAnimator.setStartDelay(delay);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int animValue = (int) animation.getAnimatedValue();
                currentTop = animValue;
                mContentView.offsetTopAndBottom(currentTop - mContentView.getTop());
                if (listener != null) {
                    listener.update(currentTop);
                }
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (listener != null) {
                    listener.finish();
                }
            }
        });
        currentAnim = valueAnimator;
        valueAnimator.start();
    }


    /**
     * 移动内容
     *
     * @param offset 位移
     */
    private void moveContent(int offset) {
        offset = (int) (FRICTION * offset);
        int top = mContentView.getTop() + offset;
        Log.d(TAG, "top" + top);
        if (top < 0) {
            offset = 0 - mContentView.getTop();
        } else if (top > maxPullHeight) {
            offset = maxPullHeight - mContentView.getTop();
        }
        Log.d(TAG, "offset" + offset);
        mContentView.offsetTopAndBottom(offset);
        currentTop = mContentView.getTop();
        //根据位移更新头部
        if (ptrHeaderHandler != null) {
            ptrHeaderHandler.onPositionUpdate(mContentView.getTop(), isRefreshing ? PtrState.REFRESHING : PtrState.INIT);
        }
    }


    /**
     * 完成刷新
     *
     * @param ptrState success or failure
     */
    public void completeRefresh(PtrState ptrState) {
        if (isRefreshing) {
            isRefreshing = false;
            if (ptrHeaderHandler != null) {
                ptrHeaderHandler.onCompleteRefreshing(ptrState);
            }
            //由于刷新完成后，要暂停显示刷新状态，此时如果继续滑动列表,动画的起始位置错误，延迟获取
            if (mContentView.getTop() > 0) {
                animMoveContent(mContentView.getTop(), 0, COMPLETE_ANIM_DURATION, completeListener);
            }

        }
    }

    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        refreshListener = onRefreshListener;
    }


    /**
     * 开始刷新
     * <p/>
     * 注：无需主动调用加载方法，刷新动画开始后会自动回调
     */
    public void startRefresh() {
        handleRefresh(true);
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (mContentView!=null){
            mContentView.layout(mContentView.getLeft(), currentTop, mContentView.getLeft() + mContentView.getMeasuredWidth(), currentTop + mContentView.getMeasuredHeight());
        }
    }

    /**
     * 完成移动动画的回调
     */
    private InnerMoveAnimListener completeListener = new InnerMoveAnimListener() {
        @Override
        public void update(int offset) {
            if (ptrHeaderHandler != null) {
                ptrHeaderHandler.onPositionUpdate(currentTop, isRefreshing() ? PtrState.REFRESHING : PtrState.INIT);
            }
        }

        @Override
        public void finish() {
            // do nothing ..
        }
    };

    /**
     * 自动下移动画的回调
     */
    private InnerMoveAnimListener autoRefreshListener = new InnerMoveAnimListener() {
        @Override
        public void update(int offset) {
            if (ptrHeaderHandler != null) {
                ptrHeaderHandler.onPositionUpdate(currentTop, isRefreshing() ? PtrState.REFRESHING : PtrState.INIT);
            }
        }

        @Override
        public void finish() {
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (refreshListener != null) {
                        refreshListener.onRefresh();
                    }
                }
            }, REFRESH_DELAY);
        }
    };

    /**
     * 内部移动动画回调
     */
    private interface InnerMoveAnimListener {

        void update(int offset);

        void finish();
    }


    /**
     * 刷新回调
     */
    public interface OnRefreshListener {

        void onRefresh();

    }

}

