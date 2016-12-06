package com.xujun.quickindex.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.xujun.quickindex.R;
import com.xujun.quickindex.util.UIUtils;

/**
 * 博客地址：http://blog.csdn.net/gdutxiaoxu
 * 快速索引，根据字母的索引查找相应的联系人
 *
 * @author xujun
 * @time 2015/11/1 21:40.
 */
public class QuickIndexBar extends View {

    private static final String[] LETTERS = new String[]{
            "A", "B", "C", "D", "E", "F",
            "G", "H", "I", "J", "K", "L",
            "M", "N", "O", "P", "Q", "R",
            "S", "T", "U", "V", "W", "X",
            "Y", "Z"};

    private static final String TAG = "xujun";
    private Paint mPaint;
    //字母的宽度
    private int cellWidth;
    //字母的高度
    private float cellHeight;
    //记录上一次触摸的Index
    private int mLastTouchIndex = -1;
    //字母被选中显示的颜色
    private int mSelectColor = Color.GRAY;
    //字母正常显示的颜色
    private int mNormalColor = Color.WHITE;
    private Context mContext;

    /**
     * 暴露一个字母的监听
     */
    public interface OnLetterUpdateListener {

        void onLetterUpdate(String letter);
    }

    private OnLetterUpdateListener listener;

    public OnLetterUpdateListener getListener() {
        return listener;
    }

    /**
     * 设置字母更新监听
     *
     * @param listener
     */
    public void setListener(OnLetterUpdateListener listener) {
        this.listener = listener;
    }

    public QuickIndexBar(Context context) {
        this(context, null);
    }

    public QuickIndexBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public QuickIndexBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        //初始化自定义属性
        obtainAttrs(attrs);

        // 初始化画笔
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        float textSize = UIUtils.dip2px(15, mContext);
        mPaint.setTextSize(textSize);
        mPaint.setTypeface(Typeface.DEFAULT_BOLD);
    }

    private void obtainAttrs(AttributeSet attrs) {
        TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.QuickIndexBar);
        int selectColor = typedArray.getColor(R.styleable.QuickIndexBar_select_color, -1);
        if (selectColor != -1) {
            mSelectColor = selectColor;
        }
        int normalColor = typedArray.getColor(R.styleable.QuickIndexBar_normal_color, -1);
        if (normalColor != -1) {
            mNormalColor = normalColor;
        }

        typedArray.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        for (int i = 0; i < LETTERS.length; i++) {
            String text = LETTERS[i];
            // 计算坐标
            int x = (int) (cellWidth / 2.0f - mPaint.measureText(text) / 2.0f);
            // 获取文本的高度
            Rect bounds = new Rect();// 矩形
            mPaint.getTextBounds(text, 0, text.length(), bounds);
            int textHeight = bounds.height();
            int y = (int) (cellHeight / 2.0f + textHeight / 2.0f + i * cellHeight);

            // 根据按下的字母, 设置画笔颜色

            mPaint.setColor(mLastTouchIndex == i ? mSelectColor : mNormalColor);

            // 绘制文本A-Z
            canvas.drawText(text, x, y, mPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int index = -1;
        switch (MotionEventCompat.getActionMasked(event)) {
            case MotionEvent.ACTION_DOWN:
                // 获取当前触摸到的字母索引
                index = (int) (event.getY() / cellHeight);
                if (index >= 0 && index < LETTERS.length) {
                    // 判断是否跟上一次触摸到的一样，不一样才进行回调
                    if (index != mLastTouchIndex) {
                        if (listener != null) {
                            //
                            listener.onLetterUpdate(LETTERS[index]);
                        }
                        Log.d(TAG, "onTouchEvent: " + LETTERS[index]);
                        //记录上一次触摸的Index为当前的index；
                        mLastTouchIndex = index;
                    }
                }

                break;
            case MotionEvent.ACTION_MOVE:
                index = (int) (event.getY() / cellHeight);
                if (index >= 0 && index < LETTERS.length) {
                    // 判断是否跟上一次触摸到的一样
                    if (index != mLastTouchIndex) {

                        if (listener != null) {
                            listener.onLetterUpdate(LETTERS[index]);
                        }
                        Log.d(TAG, "onTouchEvent: " + LETTERS[index]);

                        mLastTouchIndex = index;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                // 手指抬起的时候重置
                mLastTouchIndex = -1;
                break;

            default:
                break;
        }
        //调用这个方法会重新调用draw方法，重新绘制
        invalidate();
        return true;
    }

    /**
     * 当大小 改变的时候会回调这个方法，
     * 这里我们就不主动调用measure（）方法了
     *
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // 获取单元格的宽和高
        cellWidth = getMeasuredWidth();
        int mHeight = getMeasuredHeight();
        cellHeight = mHeight * 1.0f / LETTERS.length;

    }


}
