package com.firstproject.ptr;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.firstproject.R;

/**
 * Created by liuj on 2016/5/4.
 * 龙蛋刷新动画
 */
public class DragonEggAminView extends View {

    private static final String TAG = "DragEggAnim";

    public static enum State {
        Init,
        Moving,
        AnimStart
    }

    private State currentState;
    private int currentOffset;

    private Paint paint;
    private Bitmap topShell;
    private Bitmap fullEgg;
    private Bitmap dragonEgg;
    private Bitmap[] animArr;

    private int currentAnimIndex;

    public DragonEggAminView(Context context) {
        this(context, null);
    }

    public DragonEggAminView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragonEggAminView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }
    private static Bitmap small(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postScale(0.7f,0.7f); //长和宽放大缩小的比例
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
        return resizeBmp;
    }

    private void init(Context context) {
        paint = new Paint();
        topShell =small(BitmapFactory.decodeResource(getResources(), R.drawable.egg2));
        fullEgg = small(BitmapFactory.decodeResource(getResources(), R.drawable.egg0));
        dragonEgg = small(BitmapFactory.decodeResource(getResources(), R.drawable.egg3));
        animArr = new Bitmap[3];
        animArr[0] = dragonEgg;
        animArr[1] = small(BitmapFactory.decodeResource(getResources(), R.drawable.egg4));
        animArr[2] = small(BitmapFactory.decodeResource(getResources(), R.drawable.egg5));
        currentState = State.Init;
    }


    public void updateState(State state) {
        if (currentState != state) {
            currentState = state;
            currentAnimIndex = 0;
            invalidate();
        }
    }

    public void updateMovePos(int offset, PtrState state) {
        if (offset >0) {
            currentState = State.Moving;
            currentOffset = (int) (offset*1.5f);
        } else {
            currentOffset = 0;
            currentState = State.Init;
        }
        if (state == PtrState.REFRESHING) {
            currentState = State.AnimStart;
        } else {
            currentAnimIndex = 0;
            invalidate();
        }

    }


    public int getMaxOpenHeight() {
        return (int) ((getHeight() / 2 + topShell.getHeight() / 2)/1.5f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        switch (currentState) {
            case Init:
                canvas.drawBitmap(fullEgg, getWidth() / 2 - fullEgg.getWidth() / 2, getHeight() / 2 - fullEgg.getHeight() / 2, paint);
                break;
            case Moving:
                canvas.drawBitmap(dragonEgg, getWidth() / 2 - dragonEgg.getWidth() / 2, getHeight() / 2 - dragonEgg.getHeight() / 2, paint);
                Log.d(TAG, "currentOffset"+currentOffset + "");
                canvas.drawBitmap(topShell, getWidth() / 2 - topShell.getWidth() / 2, getHeight() / 2 - topShell.getHeight() / 2 - currentOffset, paint);
                break;
            case AnimStart:
                Bitmap animFrame = animArr[currentAnimIndex];
                canvas.drawBitmap(animFrame, getWidth() / 2 - animFrame.getWidth() / 2, getHeight() / 2 - animFrame.getHeight() / 2, paint);
                currentAnimIndex = (currentAnimIndex + 1) % animArr.length;
                postInvalidateDelayed(150);
                break;
        }
    }
}
