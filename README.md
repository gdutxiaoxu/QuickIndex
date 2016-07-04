# 常用的自定义控件四（QuickBarView）

标签（空格分隔）： 自定义View 通讯录字母快速索引

---

**在Android日常开发中，我们经常在联系人界面看到一些字母导航栏，点击字母的时候，会根据汉字的首拼音来查找是否存在相应的item，这种效果很常见，几乎所有涉及到通讯的都会用到，包括qq，微信，微博等，今天我为大家带来的就是这种自定义控件**


### 废话不多说 ，大家先来看一下时间的效果

*  效果图一

![](http://7xvjnq.com1.z0.glb.clouddn.com/16-7-1/67266410.jpg)


### 大家先来看一下源码

```java
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
```

### 代码 解析
* 代码其实不长，加上一些注释总共才180多行，总体来说，思路分分为以下几个步骤

1. 在构造方法里面初始化画笔，同时为了使用方便，我们封装了自定义属性
```java
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
```

2. 接着我们在onSizeChange 方法里面拿到我们时间的宽度和高度，有人可能会问了为什么不在onMeasure里面获取了，其实在onMeasure方法里面获取是可以的，只不过我们还需要调用一下measure方法而已，在onSizeChnage方法里面，我们直接调用
```java
 cellWidth = getMeasuredWidth();
```
即可获取到我们需要的宽度和高度。用起来比较方便，不过更多的是为了让大家知道View有这一个方法存在以及怎么使用它
顺便我们来看一下google官方对onSizeChange方法的解释

> This is called during layout when the size of this view has changed. If you were just added to the view hierarchy, you're called with the old values of 0.

从官方的解释我们可以知道这个方法是在onLayout方法中当大小改变的时候会调用这个方法，因此我们直接调用getMeasuredWidth();是可以获取得到宽度的，因为onMeasure 是先于onLayout方法调用的。
3. 接着我们重写onDraw方法，在onDraw方法我们所做的工作就是绘制 我们需要的26个字母
```java
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
```
4. 讲到这里，我们的工作已经完成一大半了，接着就是处理我们是按下或者一个字母了,我们重写onTouchEvent方法，并且return true;是为了保证 Action_down动作按下以后,Action_move以后的动作能够顺利接受到，这涉及到View的事件分发机制，有空的话我会尝试总结一下，这里就不说了
```java
// 获取当前触摸到的字母索引
index = (int) (event.getY() / cellHeight);
```
同时我们记录下我们当前是触摸或者按下哪一个字母
```java
mLastTouchIndex = index;
```
5. 知道了我们当前是触摸或者按下哪一个字母了，那我们要怎样将这些信息暴露出去了，不难想象就是采用接口回调的方法,为此我们提供了这样一个接口
```java
 /**
 * 暴露一个字母的监听
 */
public interface OnLetterUpdateListener {

    void onLetterUpdate(String letter);
}
```
并且提供了设置监听器的方法，这样我们就成功将我们的按下字母的信息提供给外界了
```java
public void setListener(OnLetterUpdateListener listener) {
        this.listener = listener;
}
```
详细代码如下
```
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
```
**到此 QuickBarView的源码分析位置下面我们来学习一下是怎样结合ListView使用的**

-------
**下面我贴出核心代码，想仔细了解的请见demo**
```java
mQuickIndexBar.setListener(new OnLetterUpdateListener() {
    @Override
    public void onLetterUpdate(String letter) {
        //          UIUtils.showToast(getApplicationContext(), letter);

        showLetter(letter);
        // 根据字母定位ListView, 找到集合中第一个以letter为拼音首字母的对象,得到索引
        for (int i = 0; i < persons.size(); i++) {
            Person person = persons.get(i);
            String l = person.getPinyin().charAt(0) + "";
            if (TextUtils.equals(letter, l)) {
                // 匹配成功
                mListView.setSelection(i);
                break;
            }
        }
    }
});


**思路解析 如下**
1. 在我们的List数据里面查找是否有有相应的首字母是触摸的字母，有的话返回相应的index，
2. 然后再调用ListView的setSelection(i)方法选中哪一个Item
```java
mListView.setSelection(i);
```
至于有些item有显示字母，有一些没有显示字母，其实就是判断上一个item的首字母是不是跟当前的首字母是不是一样的，不一样的话，显示当前item的字母，不过要注意一点，**就是position等于0的时候，我们需要做特殊处理**，代码如下
```java
String str = null;
String currentLetter = p.getPinyin().charAt(0) + "";
// 根据上一个首字母,决定当前是否显示字母
if(position == 0){
   str = currentLetter;
}else {
   // 上一个人的拼音的首字母
   String preLetter = persons.get(position - 1).getPinyin().charAt(0) + "";
   if(!TextUtils.equals(preLetter, currentLetter)){
      str = currentLetter;
   }
}

// 根据str是否为空,决定是否显示索引栏
mViewHolder.mIndex.setVisibility(str == null ? View.GONE : View.VISIBLE);
```

## 到此我们的分析为止
[**转载请注明原博客地址：**]()
[**源码下载地址：**](https://github.com/gdutxiaoxu/QuickIndex.git)https://github.com/gdutxiaoxu/QuickIndex.git

