package com.blacksmithdreamapps.presentgo.seekbar.budget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.ArrayRes;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.blacksmithdreamapps.presentgo.seekbar.BuilderParams;
import com.blacksmithdreamapps.presentgo.seekbar.IndicatorSeekBarType;
import com.blacksmithdreamapps.presentgo.seekbar.IndicatorType;
import com.blacksmithdreamapps.presentgo.seekbar.IndicatorUtils;
import com.blacksmithdreamapps.presentgo.seekbar.TickType;
import com.blacksmithdreamapps.presentgo.R;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;



/**
 * Developed and designed by Dream Apps Blacksmith
 * Code author is Boskin Kostya
 * On 027 27.02.2018.
 */

public class IndicatorSeekBar extends View implements ViewTreeObserver.OnGlobalLayoutListener, ViewTreeObserver.OnScrollChangedListener {
     static final int GAP_BETWEEN_SEEK_BAR_AND_BELOW_TEXT = 3;
     static final int CUSTOM_DRAWABLE_MAX_LIMITED_WIDTH = 30;
     static final String INSTANCE_STATE_KEY = "isb_instance_state";
     BuilderParams p;
     float mTickRadius;
     Indicator mIndicator;
     List<Float> mTextLocationList;
     Rect mCoverRect;
     int[] mLocation;
     ArrayList<String> mTextList;
     Context mContext;
     Paint mStockPaint;
     TextPaint mTextPaint;
     float mTouchX;
     float mTrackY;
     float mSeekLength;
     float mSeekStart;
     float mSeekEnd;
     Rect mRect;
     int mPaddingLeft;
     int mPaddingRight;
     int mMeasuredWidth;
     float mSeekBlockLength;
     int mPaddingTop;
     Bitmap mTickDraw;
     Bitmap mThumbDraw;
     boolean mDrawAgain;
     boolean mIsTouching;
     float mThumbRadius;
     float mThumbTouchRadius;
     OnSeekBarChangeListener mListener;
     float lastProgress;
     float mFaultTolerance = -1;
     int mTextHeight;
     float mThumbTouchHeight;
     float mCustomDrawableMaxHeight;
     float mScreenWidth = -1;
     Builder mBuilder;
     BuilderParams mRawParams;

    public IndicatorSeekBar(Context context) {
        this(context, null);
    }

    public IndicatorSeekBar(Builder builder) {
        super(builder.getContext(), null, 0);
        this.mContext = builder.getContext();
        this.mBuilder = builder;
        this.p = builder.p;
        this.mRawParams = new BuilderParams(mContext).copy(p);
        initData();
        Toast.makeText(builder.getContext(), "Indicator seek bar", Toast.LENGTH_LONG).show();
    }

    public IndicatorSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IndicatorSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initAttrs(mContext, attrs);
        this.mRawParams = new BuilderParams(mContext).copy(p);
        initData();
    }

     void initAttrs(Context context, AttributeSet attrs) {
        p = new BuilderParams(context);
        if (attrs == null) {
            return;
        }
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.IndicatorSeekBar);
        //seekBar
        p.setMSeekBarType(ta.getInt(R.styleable.IndicatorSeekBar_isb_seek_bar_type, p.getMSeekBarType()));
        p.setMMax(ta.getFloat(R.styleable.IndicatorSeekBar_isb_max, p.getMMax()));
        p.setMMin(ta.getFloat(R.styleable.IndicatorSeekBar_isb_min, p.getMMin()));
        p.setMProgress(ta.getFloat(R.styleable.IndicatorSeekBar_isb_progress, p.getMProgress()));
        p.setMClearPadding(ta.getBoolean(R.styleable.IndicatorSeekBar_isb_clear_default_padding, p.getMClearPadding()));
        p.setMForbidUserSeek(ta.getBoolean(R.styleable.IndicatorSeekBar_isb_forbid_user_seek, p.getMForbidUserSeek()));
        p.setMIsFloatProgress(ta.getBoolean(R.styleable.IndicatorSeekBar_isb_progress_value_float, p.getMIsFloatProgress()));
        p.setMTouchToSeek(ta.getBoolean(R.styleable.IndicatorSeekBar_isb_touch_to_seek, p.getMTouchToSeek()));
        //track
        p.setMBackgroundTrackSize(ta.getDimensionPixelSize(R.styleable.IndicatorSeekBar_isb_track_background_bar_size, p.getMBackgroundTrackSize()));
        p.setMProgressTrackSize(ta.getDimensionPixelSize(R.styleable.IndicatorSeekBar_isb_track_progress_bar_size, p.getMProgressTrackSize()));
        p.setMBackgroundTrackColor(ta.getColor(R.styleable.IndicatorSeekBar_isb_track_background_bar_color, p.getMBackgroundTrackColor()));
        p.setMProgressTrackColor(ta.getColor(R.styleable.IndicatorSeekBar_isb_track_progress_bar_color, p.getMProgressTrackColor()));
        p.setMTrackRoundedCorners(ta.getBoolean(R.styleable.IndicatorSeekBar_isb_track_rounded_corners, p.getMTrackRoundedCorners()));
        //thumb
        p.setMThumbColor(ta.getColor(R.styleable.IndicatorSeekBar_isb_thumb_color, p.getMThumbColor()));
        p.setMThumbSize(ta.getDimensionPixelSize(R.styleable.IndicatorSeekBar_isb_thumb_width, p.getMThumbSize()));
        p.setMThumbProgressStay(ta.getBoolean(R.styleable.IndicatorSeekBar_isb_thumb_progress_stay, p.getMThumbProgressStay()));
        p.setMThumbDrawable(ta.getDrawable(R.styleable.IndicatorSeekBar_isb_thumb_drawable));
        //indicator
        p.setMIndicatorType(ta.getInt(R.styleable.IndicatorSeekBar_isb_indicator_type, p.getMIndicatorType()));
        p.setMIndicatorColor(ta.getColor(R.styleable.IndicatorSeekBar_isb_indicator_color, p.getMIndicatorColor()));
        p.setMIndicatorTextColor(ta.getColor(R.styleable.IndicatorSeekBar_isb_indicator_text_color, p.getMIndicatorTextColor()));
        p.setMShowIndicator(ta.getBoolean(R.styleable.IndicatorSeekBar_isb_show_indicator, p.getMShowIndicator()));
        p.setMIndicatorStay(ta.getBoolean(R.styleable.IndicatorSeekBar_isb_indicator_stay, p.getMIndicatorStay()));
        p.setMIndicatorTextSize(ta.getDimensionPixelSize(R.styleable.IndicatorSeekBar_isb_indicator_text_size, p.getMIndicatorTextSize()));
        int indicatorCustomViewId = ta.getResourceId(R.styleable.IndicatorSeekBar_isb_indicator_custom_layout, 0);
        if (indicatorCustomViewId > 0) {
            p.setMIndicatorCustomView(View.inflate(mContext, indicatorCustomViewId, null));
        }
        int indicatorCustomTopContentLayoutId = ta.getResourceId(R.styleable.IndicatorSeekBar_isb_indicator_custom_top_content_layout, 0);
        if (indicatorCustomTopContentLayoutId > 0) {
            p.setMIndicatorCustomTopContentView(View.inflate(mContext, indicatorCustomTopContentLayoutId, null));
        }
        //tick
        p.setMTickDrawable(ta.getDrawable(R.styleable.IndicatorSeekBar_isb_tick_drawable));
        p.setMTickNum(ta.getInt(R.styleable.IndicatorSeekBar_isb_tick_num, p.getMTickNum()));
        p.setMTickColor(ta.getColor(R.styleable.IndicatorSeekBar_isb_tick_color, p.getMTickColor()));
        p.setMTickType(ta.getInt(R.styleable.IndicatorSeekBar_isb_tick_type, p.getMTickType()));
        p.setMTickHideBothEnds(ta.getBoolean(R.styleable.IndicatorSeekBar_isb_tick_both_end_hide, p.getMTickHideBothEnds()));
        p.setMTickOnThumbLeftHide(ta.getBoolean(R.styleable.IndicatorSeekBar_isb_tick_on_thumb_left_hide, p.getMTickOnThumbLeftHide()));
        p.setMTickSize(ta.getDimensionPixelSize(R.styleable.IndicatorSeekBar_isb_tick_size, p.getMTickSize()));
        //text
        p.setMTextArray(ta.getTextArray(R.styleable.IndicatorSeekBar_isb_text_array));
        p.setMLeftEndText(ta.getString(R.styleable.IndicatorSeekBar_isb_text_left_end));
        p.setMRightEndText(ta.getString(R.styleable.IndicatorSeekBar_isb_text_right_end));
        p.setMTextSize(ta.getDimensionPixelSize(R.styleable.IndicatorSeekBar_isb_text_size, p.getMTextSize()));
        p.setMTextColor(ta.getColor(R.styleable.IndicatorSeekBar_isb_text_color, p.getMTextColor()));
        int typefaceType = ta.getInt(R.styleable.IndicatorSeekBar_isb_text_typeface, 0);
        if (typefaceType == 1) {
            p.setMTextTypeface(Typeface.MONOSPACE);
        } else if (typefaceType == 2) {
            p.setMTextTypeface(Typeface.SANS_SERIF);
        } else if (typefaceType == 3) {
            p.setMTextTypeface(Typeface.SERIF);
        } else {
            p.setMTextTypeface(Typeface.DEFAULT);
        }

        ta.recycle();
    }

     void initData() {
        if (mTextLocationList == null) {
            mTextLocationList = new ArrayList<>();
        } else {
            mTextLocationList.clear();
        }
        if (mTextList == null) {
            mTextList = new ArrayList<>();
        } else {
            mTextList.clear();
        }
        if (p.getMMax() < p.getMMin()) {
            p.setMMax(p.getMMin());
        }
        if (p.getMProgress() < p.getMMin()) {
            p.setMProgress(p.getMMin());
        }
        if (p.getMProgress() > p.getMMax()) {
            p.setMProgress(p.getMMax());
        }
        if (p.getMBackgroundTrackSize() > p.getMProgressTrackSize()) {
            p.setMBackgroundTrackSize(p.getMProgressTrackSize());
        }
        if (p.getMTickNum() < 0) {
            p.setMTickNum(0);
        }
        if (p.getMTickNum() > 100) {
            p.setMTickNum(100);
        }
        if (p.getMLeftEndText() == null) {
            if (p.getMIsFloatProgress()) {
                p.setMLeftEndText(p.getMMin() + "");
            } else {
                p.setMLeftEndText(Math.round(p.getMMin()) + "");
            }
        }
        if (p.getMRightEndText() == null) {
            if (p.getMIsFloatProgress()) {
                p.setMRightEndText(p.getMMax() + "");
            } else {
                p.setMRightEndText(Math.round(p.getMMax()) + "");
            }
        }

        if (p.getMTickDrawable() != null) {
            p.setMTickType(TickType.Companion.getREC());//set a not none type
        }
        if (p.getMThumbDrawable() == null) {
            mThumbRadius = p.getMThumbSize() / 2.0f;
            mThumbTouchRadius = mThumbRadius * 1.2f;
            mThumbTouchHeight = mThumbTouchRadius * 2.0f;
        } else {
            int maxThumbWidth = IndicatorUtils.INSTANCE.dp2px(mContext, CUSTOM_DRAWABLE_MAX_LIMITED_WIDTH);
            if (p.getMThumbSize() > maxThumbWidth) {
                mThumbRadius = maxThumbWidth / 2.0f;
            } else {
                mThumbRadius = p.getMThumbSize() / 2.0f;
            }
            mThumbTouchRadius = mThumbRadius;
            mThumbTouchHeight = mThumbTouchRadius * 2.0f;
        }
        if (p.getMTickDrawable() == null) {
            mTickRadius = p.getMTickSize() / 2.0f;
        } else {
            int maxTickWidth = IndicatorUtils.INSTANCE.dp2px(mContext, CUSTOM_DRAWABLE_MAX_LIMITED_WIDTH);
            if (p.getMTickSize() > maxTickWidth) {
                mTickRadius = maxTickWidth / 2.0f;
            } else {
                mTickRadius = p.getMTickSize() / 2.0f;
            }
        }
        if (mThumbTouchRadius >= mTickRadius) {
            mCustomDrawableMaxHeight = mThumbTouchHeight;
        } else {
            mCustomDrawableMaxHeight = mTickRadius * 2.0f;
        }

        initStrokePaint();

        initDefaultPadding();
        if (noTick()) {
            if (p.getMMax() - p.getMMin() > 100) {
                p.setMTickNum(Math.round(p.getMMax() - p.getMMin()));
            } else {
                p.setMTickNum(100);
            }
            if (p.getMIsFloatProgress()) {
                p.setMTickNum(p.getMTickNum() * 10);
            }
        } else {
            p.setMTickNum(p.getMTickNum() < 2 ? 2 : (p.getMTickNum() - 1));
        }
        if (needDrawText()) {
            initTextPaint();
            mTextPaint.setTypeface(p.getMTextTypeface());
            mTextPaint.getTextBounds("jf1", 0, 3, mRect);
            mTextHeight = 0;
            mTextHeight += mRect.height() + IndicatorUtils.INSTANCE.dp2px(mContext, 2 * GAP_BETWEEN_SEEK_BAR_AND_BELOW_TEXT);
        }
        lastProgress = p.getMProgress();
    }

     void calculateProgressTouchX() {
        //progress
        float touchX = (p.getMProgress() - p.getMMin()) * mSeekLength / (p.getMMax() - p.getMMin()) + mPaddingLeft;
        calculateTouchX(touchX);
    }

    float getTouchX() {
        calculateProgressTouchX();
        return mTouchX;
    }

     boolean noTick() {
        return p.getMSeekBarType() == IndicatorSeekBarType.Companion.getCONTINUOUS() || p.getMSeekBarType() == IndicatorSeekBarType.Companion.getCONTINUOUS_TEXTS_ENDS();
    }

     void initEndTexts() {
        if (mTextList.size() == 0) {
            if (p.getMLeftEndText() != null) {
                mTextList.add(p.getMLeftEndText());
                mTextLocationList.add((float) mPaddingLeft);
            }
            if (p.getMRightEndText() != null) {
                mTextList.add(p.getMRightEndText());
                mTextLocationList.add((float) (mMeasuredWidth - mPaddingRight));
            }
        } else if (mTextList.size() == 1) {
            if (p.getMLeftEndText() != null) {
                mTextList.set(0, p.getMLeftEndText());
            }
            if (p.getMRightEndText() != null) {
                mTextList.add(p.getMRightEndText());
                mTextLocationList.add((float) (mMeasuredWidth - mPaddingRight));
            }
        } else {
            if (p.getMLeftEndText() != null) {
                mTextList.set(0, p.getMLeftEndText());
            }
            if (p.getMLeftEndText() != null) {
                mTextList.set(mTextList.size() - 1, p.getMRightEndText());
            }
        }
    }

     void initDefaultPadding() {
        if (!p.getMClearPadding()) {
            int normalPadding = IndicatorUtils.INSTANCE.dp2px(mContext, 16);
            if (getPaddingLeft() == 0) {
                setPadding(normalPadding, getPaddingTop(), getPaddingRight(), getPaddingBottom());
            }
            if (getPaddingRight() == 0) {
                setPadding(getPaddingLeft(), getPaddingTop(), normalPadding, getPaddingBottom());
            }
        }
    }

     void initStrokePaint() {
        if (mStockPaint == null) {
            mStockPaint = new Paint();
        }
        if (p.getMTrackRoundedCorners()) {
            mStockPaint.setStrokeCap(Paint.Cap.ROUND);
        }
        mStockPaint.setAntiAlias(true);
        if (p.getMBackgroundTrackSize() > p.getMProgressTrackSize()) {
            p.setMProgressTrackSize(p.getMBackgroundTrackSize());
        }
    }

     void initTextPaint() {
        if (mTextPaint == null) {
            mTextPaint = new TextPaint();
            mTextPaint.setAntiAlias(true);
            mTextPaint.setTextAlign(Paint.Align.CENTER);
            mTextPaint.setTextSize(p.getMTextSize());
            mTextPaint.setColor(p.getMTextColor());
        }
        if (mRect == null) {
            mRect = new Rect();
        }

    }

     boolean needDrawText() {
        return p.getMSeekBarType() == IndicatorSeekBarType.Companion.getCONTINUOUS_TEXTS_ENDS() || p.getMSeekBarType() == IndicatorSeekBarType.Companion.getDISCRETE_TICKS_TEXTS() || p.getMSeekBarType() == IndicatorSeekBarType.Companion.getDISCRETE_TICKS_TEXTS_ENDS() || p.getMThumbProgressStay();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height = Math.round(mCustomDrawableMaxHeight + .5f + getPaddingTop() + getPaddingBottom());
        setMeasuredDimension(resolveSize(IndicatorUtils.INSTANCE.dp2px(mContext, 170), widthMeasureSpec), height + mTextHeight);
        initSeekBarInfo();
        if (p.getMShowIndicator() && mIndicator == null) {
            mIndicator = new com.blacksmithdreamapps.presentgo.seekbar.budget.Indicator(mContext, this, p);
        }

    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //draw 2th track
        mStockPaint.setColor(p.getMProgressTrackColor());
        if (!mDrawAgain) {
            //progress
            float touchX = (p.getMProgress() - p.getMMin()) * mSeekLength / (p.getMMax() - p.getMMin()) + mPaddingLeft;
            calculateTouchX(touchX);
            mDrawAgain = true;
        }
        float thumbX = getThumbX();
        //draw progress track
        mStockPaint.setStrokeWidth(p.getMProgressTrackSize());
        canvas.drawLine(mSeekStart, mTrackY, thumbX, mTrackY, mStockPaint);
        //draw BG track
        mStockPaint.setStrokeWidth(p.getMBackgroundTrackSize());
        mStockPaint.setColor(p.getMBackgroundTrackColor());
        canvas.drawLine(thumbX + mThumbRadius, mTrackY, mSeekEnd, mTrackY, mStockPaint);
        //draw tick
        drawTicks(canvas, thumbX);
        //draw text below tick
        drawText(canvas);
        //drawThumbText
        drawThumbText(canvas, thumbX);
        //drawThumb
        drawThumb(canvas, thumbX);

        if (p.getMShowIndicator() && p.getMIndicatorStay() && !mIndicator.isShowing()) {
            if (!isCover()) {
                calculateProgressTouchX();
                mIndicator.show(mTouchX);
            }

        }
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (!p.getMShowIndicator()) {
            return;
        }
        if (View.GONE == visibility || View.INVISIBLE == visibility) {
            if (mIndicator != null) {
                mIndicator.forceHide();
            }
        }
    }

    boolean isCover() {
        if (mCoverRect == null) {
            mCoverRect = new Rect();
        }
        if (this.getGlobalVisibleRect(mCoverRect)) {
            if (mCoverRect.width() >= this.getMeasuredWidth() && mCoverRect.height() >= this.getMeasuredHeight()) {
                if (mScreenWidth < 0) {
                    initScreenWidth();
                }
                if (mScreenWidth > 0) {
                    int left = mCoverRect.left;
                    int top = mCoverRect.top;
                    if (mLocation == null) {
                        mLocation = new int[2];
                    }
                    this.getLocationInWindow(mLocation);
                    if (left == mLocation[0] && top == mLocation[1]) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

     void initScreenWidth() {
        DisplayMetrics metric = new DisplayMetrics();
        WindowManager systemService = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        if (systemService != null) {
            systemService.getDefaultDisplay().getMetrics(metric);
            mScreenWidth = metric.widthPixels;
        }
    }

     void drawThumb(Canvas canvas, float thumbX) {
        mStockPaint.setColor(p.getMThumbColor());
        if (p.getMThumbDrawable() != null) {
            if (mThumbDraw == null) {
                mThumbDraw = getBitmapDraw(p.getMThumbDrawable(), true);
            }
            canvas.drawBitmap(mThumbDraw, thumbX - mThumbDraw.getWidth() / 2.0f, mTrackY - mThumbDraw.getHeight() / 2.0f, mStockPaint);
        } else {
            canvas.drawCircle(thumbX + p.getMBackgroundTrackSize() / 2.0f, mTrackY, mIsTouching ? mThumbTouchRadius : mThumbRadius, mStockPaint);
        }
    }

     void drawThumbText(Canvas canvas, float thumbX) {
        if (p.getMSeekBarType() != IndicatorSeekBarType.Companion.getCONTINUOUS() && p.getMSeekBarType() != IndicatorSeekBarType.Companion.getDISCRETE_TICKS()) {
            return;
        }
        if (p.getMThumbProgressStay()) {
            canvas.drawText(getProgressString(p.getMProgress()), thumbX + p.getMBackgroundTrackSize() / 2.0f, mPaddingTop + mThumbTouchHeight + mRect.height() + IndicatorUtils.INSTANCE.dp2px(mContext, 2), mTextPaint);
        }
    }

     void initSeekBarInfo() {
        mMeasuredWidth = getMeasuredWidth();
        mPaddingLeft = getPaddingLeft();
        mPaddingRight = getPaddingRight();
        mPaddingTop = getPaddingTop();
        mSeekLength = mMeasuredWidth - mPaddingLeft - mPaddingRight;
        mSeekBlockLength = mSeekLength / p.getMTickNum();
        if (mThumbTouchRadius >= mTickRadius) {
            mTrackY = mPaddingTop + mThumbTouchRadius;
        } else {
            mTrackY = mPaddingTop + mTickRadius;
        }
        mSeekStart = p.getMTrackRoundedCorners() ? mPaddingLeft + p.getMBackgroundTrackSize() / 2.0f : mPaddingLeft;
        mSeekEnd = mMeasuredWidth - mPaddingRight - p.getMBackgroundTrackSize() / 2.0f;
        initLocationListData();
    }

     void drawTicks(Canvas canvas, float thumbX) {
        if (p.getMSeekBarType() == IndicatorSeekBarType.Companion.getCONTINUOUS() || p.getMSeekBarType() == IndicatorSeekBarType.Companion.getCONTINUOUS_TEXTS_ENDS() || p.getMTickType() == TickType.Companion.getNONE()) {
            return;
        }
        if (mTextLocationList.size() == 0) {
            return;
        }
        mStockPaint.setColor(p.getMTickColor());
        for (int i = 0; i < mTextLocationList.size(); i++) {
            float locationX = mTextLocationList.get(i);
            if (getThumbPosOnTick() == i) {
                continue;
            }
            if (p.getMTickOnThumbLeftHide()) {
                if (thumbX >= locationX) {
                    continue;
                }
            }
            if (p.getMTickHideBothEnds()) {
                if (i == 0 || i == mTextLocationList.size() - 1) {
                    continue;
                }
            }
            int rectWidth = IndicatorUtils.INSTANCE.dp2px(mContext, 1);
            if (p.getMTickDrawable() != null) {
                if (mTickDraw == null) {
                    mTickDraw = getBitmapDraw(p.getMTickDrawable(), false);
                }
                if (p.getMTickType() == TickType.Companion.getREC()) {
                    canvas.drawBitmap(mTickDraw, locationX - mTickDraw.getWidth() / 2.0f + rectWidth, mTrackY - mTickDraw.getHeight() / 2.0f, mStockPaint);
                } else {
                    canvas.drawBitmap(mTickDraw, locationX - mTickDraw.getWidth() / 2.0f, mTrackY - mTickDraw.getHeight() / 2.0f, mStockPaint);
                }
            } else {
                if (p.getMTickType() == TickType.Companion.getOVAL()) {
                    canvas.drawCircle(locationX, mTrackY, mTickRadius, mStockPaint);
                } else if (p.getMTickType() == TickType.Companion.getREC()) {
                    float rectTickHeightRange;
                    if (thumbX >= locationX) {
                        rectTickHeightRange = p.getMProgressTrackSize();
                    } else {
                        rectTickHeightRange = p.getMBackgroundTrackSize();
                    }
                    canvas.drawRect(locationX - rectWidth, mTrackY - rectTickHeightRange / 2.0f - .5f, locationX + rectWidth, mTrackY + rectTickHeightRange / 2.0f + .5f, mStockPaint);
                }
            }
        }

    }

     Bitmap getBitmapDraw(Drawable drawable, boolean isThumb) {
        if (drawable == null) {
            return null;
        }
        int width;
        int height;
        int maxRange = IndicatorUtils.INSTANCE.dp2px(mContext, CUSTOM_DRAWABLE_MAX_LIMITED_WIDTH);
        int intrinsicWidth = drawable.getIntrinsicWidth();
        if (intrinsicWidth > maxRange) {
            if (isThumb) {
                width = p.getMThumbSize();
            } else {
                width = p.getMTickSize();
            }
            height = getHeightByRatio(drawable, width);

            if (width > maxRange) {
                width = maxRange;
                height = getHeightByRatio(drawable, width);
            }
        } else {
            width = drawable.getIntrinsicWidth();
            height = drawable.getIntrinsicHeight();
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

     int getHeightByRatio(Drawable drawable, int width) {
        int intrinsicWidth = drawable.getIntrinsicWidth();
        int intrinsicHeight = drawable.getIntrinsicHeight();
        return Math.round(1.0f * width * intrinsicHeight / intrinsicWidth);
    }

     void drawText(Canvas canvas) {
        if (p.getMSeekBarType() == IndicatorSeekBarType.Companion.getCONTINUOUS() || p.getMSeekBarType() == IndicatorSeekBarType.Companion.getDISCRETE_TICKS()) {
            return;
        }
        if (mTextList.size() == 0) {
            return;
        }
        mStockPaint.setColor(p.getMTickColor());
        String allText = getAllText();
        mTextPaint.getTextBounds(allText, 0, allText.length(), mRect);
        int textHeight = Math.round(mRect.height() - mTextPaint.descent());
        int gap = IndicatorUtils.INSTANCE.dp2px(mContext, 3);
        for (int i = 0; i < mTextList.size(); i++) {
            String text = getStringText(i);
            mTextPaint.getTextBounds(text, 0, text.length(), mRect);
            if (i == 0) {
                canvas.drawText(text, mTextLocationList.get(i) + mRect.width() / 2.0f, mPaddingTop + mCustomDrawableMaxHeight + textHeight + gap, mTextPaint);
            } else if (i == mTextList.size() - 1) {
                canvas.drawText(text, mTextLocationList.get(i) - mRect.width() / 2.0f, mPaddingTop + mCustomDrawableMaxHeight + textHeight + gap, mTextPaint);
            } else {
                if (p.getMSeekBarType() == IndicatorSeekBarType.Companion.getCONTINUOUS_TEXTS_ENDS() || p.getMSeekBarType() == IndicatorSeekBarType.Companion.getDISCRETE_TICKS_TEXTS_ENDS()) {
                    continue;
                }
                canvas.drawText(text, mTextLocationList.get(i), mPaddingTop + mCustomDrawableMaxHeight + textHeight + gap, mTextPaint);
            }
        }
    }

    @NonNull
     String getStringText(int i) {
        String text;
        if (p.getMTextArray() != null) {
            if (i < p.getMTextArray().length) {
                text = p.getMTextArray()[i] + "";
            } else {
                text = " ";
            }
        } else {
            text = mTextList.get(i) + "";
        }
        return text;
    }

    @NonNull
     String getAllText() {
        StringBuilder sb = new StringBuilder();
        sb.append("j");
        if (p.getMTextArray() != null) {
            for (CharSequence text : p.getMTextArray()) {
                sb.append(text);
            }
        }
        return sb.toString();
    }

     void initLocationListData() {
        if (p.getMSeekBarType() == IndicatorSeekBarType.Companion.getCONTINUOUS()) {
            return;
        }
        if (p.getMSeekBarType() == IndicatorSeekBarType.Companion.getCONTINUOUS_TEXTS_ENDS()) {
            initEndTexts();
            return;
        }
        if (p.getMTickNum() > 1) {
            mTextLocationList.clear();
            mTextList.clear();
            for (int i = 0; i < p.getMTickNum() + 1; i++) {
                float tickX = mSeekBlockLength * i;
                mTextLocationList.add(tickX + mPaddingLeft);
                float tickProgress = p.getMMin() + (p.getMMax() - p.getMMin()) * tickX / mSeekLength;
                mTextList.add(getProgressString(tickProgress));
            }
            initEndTexts();
            initDefaultTextArray(mTextList);
        }
    }

     void initDefaultTextArray(ArrayList<String> mTextList) {
        if (p.getMTextArray() != null) {
            return;
        }
        CharSequence[] charSequence = new CharSequence[mTextList.size()];
        for (int i = 0; i < mTextList.size(); i++) {
            charSequence[i] = mTextList.get(i);
        }
        p.setMTextArray(charSequence);
    }

     float getThumbX() {
        float mThumbXCache = mTouchX - p.getMBackgroundTrackSize() / 2.0f;
        float mThumbX;
        if (mThumbXCache <= mSeekStart) {
            if (mThumbXCache <= mPaddingLeft) {
                mThumbX = getPaddingLeft() - p.getMBackgroundTrackSize() / 2.0f;
            } else {
                mThumbX = mThumbXCache + p.getMBackgroundTrackSize() / 2.0f;
            }

        } else if (mThumbXCache >= mMeasuredWidth - mPaddingRight - p.getMBackgroundTrackSize() / 2.0f) {
            mThumbX = mMeasuredWidth - mPaddingRight - p.getMBackgroundTrackSize() / 2.0f;
        } else {
            mThumbX = mThumbXCache;
        }
        return mThumbX;
    }

    public int getThumbPosOnTick() {
        if (p.getMSeekBarType() > 1) {
            return Math.round((mTouchX - mPaddingLeft) / mSeekBlockLength);
        } else {
            return -1;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                performClick();
                float mX = event.getX();
                float mY = event.getY();
                if (isTouchSeekBar(mX, mY) && !p.getMForbidUserSeek() && isEnabled()) {
                    if (p.getMTouchToSeek() || isTouchThumb(mX)) {
                        if (mListener != null) {
                            mListener.onStartTrackingTouch(this, getThumbPosOnTick());
                        }
                        refreshSeekBar(event, true);
                        return true;
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                refreshSeekBar(event, false);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mListener != null) {
                    mListener.onStopTrackingTouch(this);
                }
                mIsTouching = false;
                invalidate();
                if (p.getMShowIndicator()) {
                    mIndicator.hide();
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

     void setListener(boolean formUserTouch) {
        if (mListener != null) {
            mListener.onProgressChanged(this, getProgress(), getProgressFloat(), formUserTouch);
            if (p.getMSeekBarType() > 1) {
                int thumbPosOnTick = getThumbPosOnTick();
                if (p.getMTextArray() != null && thumbPosOnTick < (p.getMTextArray().length)) {
                    mListener.onSectionChanged(this, thumbPosOnTick, String.valueOf(p.getMTextArray()[thumbPosOnTick]), formUserTouch);
                } else {
                    mListener.onSectionChanged(this, thumbPosOnTick, "", formUserTouch);
                }
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                getParent().requestDisallowInterceptTouchEvent(false);
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE_STATE_KEY, super.onSaveInstanceState());
        bundle.putFloat("isb_progress", p.getMProgress());
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            p.setMProgress(bundle.getFloat("isb_progress"));
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATE_KEY));
            return;
        }
        super.onRestoreInstanceState(state);
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (enabled == isEnabled()) {
            return;
        }
        super.setEnabled(enabled);
        if (isEnabled()) {
            setAlpha(1.0f);
        } else {
            setAlpha(0.3f);
        }
        if (p.getMIndicatorStay() && getIndicator() != null) {
            getIndicator().forceHide();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        post(new Runnable() {
            @Override
            public void run() {
                requestLayout();
            }
        });
        checkIndicatorLoc();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mIndicator != null) {
            mIndicator.forceHide();
        }
        if (p.getMIndicatorStay() && p.getMShowIndicator()) {
            if (Build.VERSION.SDK_INT < 16) {
                this.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            } else {
                this.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
            this.getViewTreeObserver().removeOnScrollChangedListener(this);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (p.getMIndicatorStay() && p.getMShowIndicator()) {
            this.getViewTreeObserver().addOnGlobalLayoutListener(this);
            this.getViewTreeObserver().addOnScrollChangedListener(this);
        }
    }

    @Override
    public void onGlobalLayout() {
        checkIndicatorLoc();
    }

    @Override
    public void onScrollChanged() {
        checkIndicatorLoc();
    }

     void checkIndicatorLoc() {
        if (mIndicator == null || !p.getMShowIndicator()) {
            return;
        }
        if (p.getMIndicatorStay()) {
            if (mIndicator.isShowing()) {
                mIndicator.update();
            } else {
                mIndicator.show();
            }
        } else {
            mIndicator.forceHide();
        }
    }

     void refreshSeekBar(MotionEvent event, boolean isDownTouch) {
        calculateTouchX(adjustTouchX(event));
        calculateProgress();
        mIsTouching = true;
        if (isDownTouch) {
            if (lastProgress != p.getMProgress()) {
                setListener(true);
            }
            invalidate();
            if (p.getMShowIndicator()) {
                if (mIndicator.isShowing()) {
                    mIndicator.update(mTouchX);
                } else {
                    mIndicator.show(mTouchX);
                }
            }
        } else {
            if (lastProgress != p.getMProgress()) {
                setListener(true);
                invalidate();
                if (p.getMShowIndicator()) {
                    mIndicator.update(mTouchX);
                }
            }
        }
    }

     float adjustTouchX(MotionEvent event) {
        float mTouchXCache;
        if (event.getX() < mPaddingLeft) {
            mTouchXCache = mPaddingLeft;
        } else if (event.getX() > mMeasuredWidth - mPaddingRight) {
            mTouchXCache = mMeasuredWidth - mPaddingRight;
        } else {
            mTouchXCache = event.getX();
        }
        return mTouchXCache;
    }

     void calculateProgress() {
        lastProgress = p.getMProgress();
        p.setMProgress(p.getMMin() + (p.getMMax() - p.getMMin()) * (mTouchX - mPaddingLeft) / mSeekLength);
    }

     void calculateTouchX(float touchX) {
        int touchBlockSize = Math.round((touchX - mPaddingLeft) / mSeekBlockLength);
        mTouchX = mSeekBlockLength * touchBlockSize + mPaddingLeft;
    }

     boolean isTouchSeekBar(float mX, float mY) {
        if (mFaultTolerance == -1) {
            mFaultTolerance = IndicatorUtils.INSTANCE.dp2px(mContext, 5);
        }
        boolean inWidthRange = mX >= (mPaddingLeft - 2 * mFaultTolerance) && mX <= (mMeasuredWidth - mPaddingRight + 2 * mFaultTolerance);
        boolean inHeightRange = mY >= mTrackY - mThumbTouchRadius - mFaultTolerance && mY <= mTrackY + mThumbTouchRadius + mFaultTolerance;
        return inWidthRange && inHeightRange;
    }

     float getProgressFloat(int newScale) {
        BigDecimal bigDecimal = BigDecimal.valueOf(p.getMProgress());
        return bigDecimal.setScale(newScale, BigDecimal.ROUND_HALF_UP).floatValue();
    }

     float getProgressFloat(int newScale, float progress) {
        BigDecimal bigDecimal = BigDecimal.valueOf(progress);
        return bigDecimal.setScale(newScale, BigDecimal.ROUND_HALF_UP).floatValue();
    }

     int getProgress(float progress) {
        return Math.round(progress);
    }

     String getProgressString(float progress) {
        String progressString;
        if (p.getMIsFloatProgress()) {
            progressString = String.valueOf(getProgressFloat(1, progress));
        } else {
            progressString = String.valueOf(getProgress(progress));
        }
        return progressString;
    }

    String getProgressString() {
        if (p.getMSeekBarType() == IndicatorSeekBarType.Companion.getDISCRETE_TICKS_TEXTS()) {
            int thumbPosOnTick = getThumbPosOnTick();
            if (thumbPosOnTick >= p.getMTextArray().length) {
                return "";
            } else {
                return String.valueOf(p.getMTextArray()[thumbPosOnTick]);
            }
        } else {
            return getProgressString(p.getMProgress());
        }
    }

    /**
     * Get the seek bar's current level of progress in int type.
     *
     * @return progress in int type.
     */
    public int getProgress() {
        return Math.round(p.getMProgress());
    }

    /**
     * set the max value for SeekBar
     *
     * @param max the max value , if is less than min, will set to min.
     */
    public synchronized void setMax(float max) {
        if (max < mRawParams.getMMin()) {
            max = mRawParams.getMMin();
        }
        this.mRawParams.setMMax(max);
        this.p.copy(mRawParams);
        initData();
        requestLayout();
        initLocationListData();
        if (p.getMIndicatorStay() && mIndicator != null && mIndicator.isShowing()) {
            mIndicator.update();
        }
    }

    /**
     * set the min value for SeekBar
     *
     * @param min the min value , if is larger than max, will set to max.
     */
    public synchronized void setMin(float min) {
        if (min > mRawParams.getMMax()) {
            min = mRawParams.getMMax();
        }
        this.mRawParams.setMMin(min);
        this.p.copy(mRawParams);
        initData();
        requestLayout();
        initLocationListData();
        if (p.getMIndicatorStay() && mIndicator != null && mIndicator.isShowing()) {
            mIndicator.update();
        }
    }

    /**
     * Sets the current progress to the specified value.
     *
     * @param progress a new progress value , if the new progress is less than min , it will set to min ,if over max ,will be max.
     */
    public synchronized void setProgress(float progress) {
        if (progress < p.getMMin()) {
            p.setMProgress(p.getMMin());
        } else if (progress > p.getMMax()) {
            p.setMProgress(p.getMMax());
        } else {
            p.setMProgress(progress);
        }
        setListener(false);
        float touchX = (p.getMProgress() - p.getMMin()) * mSeekLength / (p.getMMax() - p.getMMin()) + mPaddingLeft;
        calculateTouchX(touchX);
        initSeekBarInfo();
        postInvalidate();
        if (p.getMIndicatorStay() && mIndicator != null && mIndicator.isShowing()) {
            mIndicator.update();
        }
    }

    /**
     * call this to avoid user to seek;
     *
     * @param forbidding
     */
    public void forbidUserToSeek(boolean forbidding) {
        p.setMForbidUserSeek(forbidding);
    }

    /**
     * get the SeekBar builder.
     *
     * @return
     */
    public synchronized Builder getBuilder() {
        if (mBuilder == null) {
            mBuilder = new Builder(mContext);
        }
        mRawParams.setMProgress(p.getMProgress());
        return mBuilder.setParams(mRawParams).setSeekBar(this);
    }

    /**
     * Get the seek bar's current level of progress in float type.
     *
     * @return current progress in float type.
     */
    public synchronized float getProgressFloat() {
        return getProgressFloat(1);
    }

    /**
     * @return the upper limit of this seek bar's range.
     */
    public float getMax() {
        return p.getMMax();
    }

    /**
     * * @return the lower limit of this seek bar's range.
     *
     * @return the seek bar min value
     */
    public float getMin() {
        return p.getMMin();
    }

    /**
     * get current indicator
     *
     * @return the indicator
     */
    public Indicator getIndicator() {
        return mIndicator;
    }

    /**
     * Set a new indicator view you wanted when touch to show.
     *
     * @param customIndicatorView the view is the indicator you touch to show;
     */
    public synchronized void setCustomIndicator(@NonNull View customIndicatorView) {
        mIndicator.setCustomIndicator(customIndicatorView);
    }

    /**
     * Set a new indicator view you wanted when touch to show.
     *
     * @param customIndicatorViewId the layout ID for indicator you touch to show;
     */
    public synchronized void setCustomIndicator(@LayoutRes int customIndicatorViewId) {
        mIndicator.setCustomIndicator(View.inflate(mContext, customIndicatorViewId, null));
    }

    /**
     * Set a new indicator view you wanted when touch to show. ,which can show seeking progress when touch to seek.
     *
     * @param customIndicatorView the view is the indicator you touch to show;
     * @param progressTextViewId  the progress id in the indicator root view , this id view must be a textView to show the progress
     */
    public synchronized void setCustomIndicator(@NonNull View customIndicatorView, @IdRes int progressTextViewId) {
        View tv = customIndicatorView.findViewById(progressTextViewId);
        if (tv == null) {
            throw new IllegalArgumentException(" can not find the textView in topContentView by progressTextViewId. ");
        }
        if (!(tv instanceof TextView)) {
            throw new ClassCastException(" the view identified by progressTextViewId can not be cast to TextView. ");
        }
        mIndicator.setProgressTextView((TextView) tv);
        mIndicator.setCustomIndicator(customIndicatorView);
    }

    /**
     * set the seek bar build params . not null.
     *
     * @param p a new BuilderParams
     */
     void apply(BuilderParams p) {
        if (p == null) {
            throw new NullPointerException(" BuilderParams can not be a null value. ");
        }
        this.mRawParams.copy(p);
        this.p.copy(p);
        initData();
        initSeekBarInfo();
        setProgress(p.getMProgress());
        requestLayout();
        if (mIndicator != null) {
            if (mIndicator.isShowing()) {
                mIndicator.forceHide();
            }
            mIndicator.initIndicator();
            if (p.getMIndicatorStay()) {
                if (mIndicator.isShowing()) {
                    mIndicator.update();
                } else {
                    mIndicator.show();
                }
            }
        }
    }


    /**
     * get all the texts below ticks in a array;
     *
     * @return the array of texts below tick
     */
    public CharSequence[] getTextArray() {
        return p.getMTextArray();
    }

    /**
     * set the texts below ticks
     *
     * @param textArray the array of texts below tick
     */
    public void setTextArray(@NonNull CharSequence[] textArray) {
        this.p.setMTextArray(textArray);
        invalidate();
    }

    /**
     * set the texts below ticks
     *
     * @param textArray the array of texts below tick
     */
    public void setTextArray(@ArrayRes int textArray) {
        this.p.setMTextArray(mContext.getResources().getStringArray(textArray));
        invalidate();
    }

    /**
     * set the listener when touch to seek.not null.
     *
     * @param listener OnSeekBarChangeListener
     */
    public void setOnSeekChangeListener(@NonNull OnSeekBarChangeListener listener) {
        this.mListener = listener;
    }

    public boolean isTouchThumb(float mX) {
        float rawTouchX = getTouchX();
        return rawTouchX - p.getMThumbSize() / 2f <= mX && mX <= rawTouchX + p.getMThumbSize() / 2f;
    }

    public interface OnSeekBarChangeListener {

        /**
         * Notification that the progress level has changed. Clients can use the fromUser parameter
         * to distinguish user-initiated changes from those that occurred programmatically.
         *
         * @param seekBar       The SeekBar whose progress has changed
         * @param progress      The current progress level. This will be in the range 0..max where max
         *                      was set by default. (The default value for max is 100.)
         * @param progressFloat The current progress level. This will be in the range 0.0f.max where max
         *                      was set by default. (The default value for max is 100.0f.)
         * @param fromUserTouch True if the progress change was initiated by the user.
         */
        void onProgressChanged(IndicatorSeekBar seekBar, int progress, float progressFloat, boolean fromUserTouch);

        /**
         * this callback only effect on discrete serious seek bar type ; when the thumb location change on tick ,will call this.
         *
         * @param seekBar        The SeekBar whose progress has changed
         * @param thumbPosOnTick The thumb's position on the seek bar's tick,if seek bar type is DISCRETE serious,
         *                       min thumbPosition is 0 , max is ticks length - 1 ; if seek bar type is CONTINUOUS serious,
         *                       no ticks on seek bar, so the thumbPosition will be always -1.
         * @param textBelowTick  this text  show below tick ,if tick below text is empty . text is "";
         * @param fromUserTouch  True if the seeking change was initiated by the user.
         */
        void onSectionChanged(IndicatorSeekBar seekBar, int thumbPosOnTick, String textBelowTick, boolean fromUserTouch);

        /**
         * Notification that the user has started a touch gesture. Clients may want to use this
         * to disable advancing the seekbar.
         *
         * @param seekBar        The SeekBar in which the touch gesture began
         * @param thumbPosOnTick The thumb's position on the seek bar's tick,if seek bar type is DISCRETE serious,
         *                       min thumbPosition is 0 , max is ticks length - 1 ; if seek bar type is CONTINUOUS serious,
         *                       no ticks on seek bar, so the thumbPosition will be always -1.
         */
        void onStartTrackingTouch(IndicatorSeekBar seekBar, int thumbPosOnTick);

        /**
         * Notification that the user has finished a touch gesture. Clients may want to use this
         * to re-enable advancing the seekbar.
         *
         * @param seekBar The SeekBar in which the touch gesture began
         */
        void onStopTrackingTouch(IndicatorSeekBar seekBar);
    }

    public static class Builder {
        BuilderParams p;
        IndicatorSeekBar indicatorSeekBar;

        public Builder(Context context) {
            this.p = new BuilderParams(context);
        }

        /**
         * call this to new an IndicatorSeekBar
         *
         * @return IndicatorSeekBar
         */
        public IndicatorSeekBar build() {
            return new IndicatorSeekBar(this);
        }

        /**
         * call this to refresh an indicatorSeekBar
         *
         * @return
         */
        public IndicatorSeekBar apply() {
            indicatorSeekBar.apply(p);
            return indicatorSeekBar;
        }

        /**
         * Sets the current progress to the specified value.
         *
         * @param progress a new progress value , if the new progress is less than min , it will set to min ,if over max ,will be max.
         * @return Builder
         */
        public Builder setProgress(float progress) {
            p.setMProgress(progress);
            return this;
        }

        /**
         * Set the  upper limit of this seek bar's range.
         *
         * @param max range
         * @return Builder
         */
        public Builder setMax(float max) {
            p.setMMax(max);
            return this;
        }

        /**
         * Set the  lower limit of this seek bar's range.
         *
         * @param min min progress
         * @return Builder
         */
        public Builder setMin(float min) {
            p.setMMin(min);
            return this;
        }

        public Context getContext() {
            return p.getMContext();
        }

        /**
         * seek bar has a default padding left and right(16 dp) , call this method to set both to zero.
         *
         * @param clearPadding true to clear the default padding, false to keep.
         * @return Builder
         */
        public Builder clearPadding(boolean clearPadding) {
            p.setMClearPadding(clearPadding);
            return this;
        }

        /**
         * call this method to show the indicator when touching.
         *
         * @param showIndicator true to show the indicator when touch.
         * @return Builder
         */
        public Builder showIndicator(boolean showIndicator) {
            p.setMShowIndicator(showIndicator);
            return this;
        }

        /**
         * set the seek bar's background track's Stroke Width
         *
         * @param backgroundTrackSize The dp size.
         * @return Builder
         */
        public Builder setBackgroundTrackSize(int backgroundTrackSize) {
            p.setMBackgroundTrackSize(IndicatorUtils.INSTANCE.dp2px(p.getMContext(), backgroundTrackSize));
            return this;
        }

        /**
         * set the size for text which below seek bar's tick .
         *
         * @param textSize The scaled pixel size.
         * @return Builder
         */
        public Builder setTextSize(int textSize) {
            p.setMTextSize(IndicatorUtils.INSTANCE.sp2px(p.getMContext(), textSize));
            return this;
        }

        /**
         * set the text's textTypeface .
         *
         * @param textTypeface The text  textTypeface.
         * @return Builder
         */
        public Builder setTextTypeface(Typeface textTypeface) {
            p.setMTextTypeface(textTypeface);
            return this;
        }


        /**
         * set the seek bar's background track's color.
         *
         * @param backgroundTrackColor colorInt
         * @return Builder
         */
        public Builder setBackgroundTrackColor(@ColorInt int backgroundTrackColor) {
            p.setMBackgroundTrackColor(backgroundTrackColor);
            return this;
        }

        /**
         * set the seek bar's progress track's color.
         *
         * @param progressTrackColor colorInt
         * @return Builder
         */
        public Builder setProgressTrackColor(@ColorInt int progressTrackColor) {
            p.setMProgressTrackColor(progressTrackColor);
            return this;
        }

        /**
         * set the seek bar's thumb's color.
         *
         * @param thumbColor colorInt
         * @return Builder
         */
        public Builder setThumbColor(@ColorInt int thumbColor) {
            p.setMThumbColor(thumbColor);
            return this;
        }

        /**
         * call this method to change the block size of seek bar, when seek bar type is continuous series will be not worked.
         *
         * @param tickNum the tick count show on seek bar. if you want seek bar block size is N , this tickNum should be N+1.
         * @return Builder
         */
        public Builder setTickNum(int tickNum) {
            p.setMTickNum(tickNum);
            return this;
        }

        /**
         * set the seek bar's tick width , if tick type is rec, call this method will be not worked(tick type is rec,has a regular value 2dp).
         *
         * @param tickSize the dp size.
         * @return Builder
         */
        public Builder setTickSize(int tickSize) {
            p.setMTickSize(IndicatorUtils.INSTANCE.dp2px(p.getMContext(), tickSize));
            return this;
        }

        /**
         * call this method to show different tick
         *
         * @param tickType TickType.REC;
         *                 TickType.OVAL;
         *                 TickType.NONE;
         * @return Builder
         */
        public Builder setTickType(int tickType) {
            p.setMTickType(tickType);
            return this;
        }

        /**
         * set the seek bar's tick's color.
         *
         * @param tickColor colorInt
         * @return Builder
         */
        public Builder setTickColor(@ColorInt int tickColor) {
            p.setMTickColor(tickColor);
            return this;
        }

        /**
         * call this method to show different series seek bar.
         *
         * @param seekBarType IndicatorSeekBarType.CONTINUOUS;
         *                    IndicatorSeekBarType.CONTINUOUS_TEXTS_ENDS;
         *                    IndicatorSeekBarType.DISCRETE;
         *                    IndicatorSeekBarType.DISCRETE_TICKS;
         *                    IndicatorSeekBarType.DISCRETE_TICKS_TEXTS;
         *                    IndicatorSeekBarType.DISCRETE_TICKS_TEXTS_ENDS;
         * @return Builder
         */
        public Builder setSeekBarType(int seekBarType) {
            p.setMSeekBarType(seekBarType);
            return this;
        }

        /**
         * call this method to hide the ticks which show in the both ends sides of seek bar.
         *
         * @param tickHideBothEnds true for hide.
         * @return Builder
         */
        public Builder hideBothEndsTicks(boolean tickHideBothEnds) {
            p.setMTickHideBothEnds(tickHideBothEnds);
            return this;
        }

        /**
         * call this method to hide the ticks on seekBar thumb left;
         *
         * @param tickOnThumbLeftHide true for hide.
         * @return Builder
         */
        public Builder hideTickOnThumbLeft(boolean tickOnThumbLeftHide) {
            p.setMTickOnThumbLeftHide(tickOnThumbLeftHide);
            return this;
        }

        /**
         * call this method to show the seek bar's ends to square corners.default rounded corners.
         *
         * @param trackCornersIsRounded false to show square corners.
         * @return Builder
         */
        public Builder isRoundedTrackCorner(boolean trackCornersIsRounded) {
            p.setMTrackRoundedCorners(trackCornersIsRounded);
            return this;
        }

        /**
         * set the seek bar's progress track's Stroke Width
         *
         * @param progressTrackSize The dp size.
         * @return Builder
         */
        public Builder setProgressTrackSize(int progressTrackSize) {
            p.setMProgressTrackSize(IndicatorUtils.INSTANCE.dp2px(p.getMContext(), progressTrackSize));
            return this;
        }

        /**
         * call this method to replace the seek bar's tick's below text.
         *
         * @param textArray the length should same as tickNum.
         * @return Builder
         */
        public Builder setTextArray(CharSequence[] textArray) {
            p.setMTextArray(textArray);
            return this;
        }

        /**
         * call this method to replace the seek bar's tick's below text.
         *
         * @param textArray the length should same as tickNum.
         * @return Builder
         */
        public Builder setTextArray(@ArrayRes int textArray) {
            p.setMTextArray(p.getMContext().getResources().getStringArray(textArray));
            return this;
        }


        /**
         * set the seek bar's thumb's Width.
         *
         * @param thumbWidth The dp size.
         * @return Builder
         */
        public Builder setThumbWidth(int thumbWidth) {
            p.setMThumbSize(IndicatorUtils.INSTANCE.dp2px(p.getMContext(), thumbWidth));
            return this;
        }

        /**
         * set the seek bar's indicator's color. have no influence on custom indicator.
         *
         * @param indicatorColor colorInt
         * @return Builder
         */
        public Builder setIndicatorColor(@ColorInt int indicatorColor) {
            p.setMIndicatorColor(indicatorColor);
            return this;
        }

        /**
         * set the seek bar's indicator's custom indicator layout identify. only effect on custom indicator type.
         *
         * @param mIndicatorCustomLayout the custom indicator layout identify
         * @return Builder
         */
        public Builder setIndicatorCustomLayout(@LayoutRes int mIndicatorCustomLayout) {
            p.setMIndicatorType(IndicatorType.Companion.getCUSTOM());
            p.setMIndicatorCustomView(View.inflate(p.getMContext(), mIndicatorCustomLayout, null));
            return this;
        }

        /**
         * set the seek bar's indicator's custom indicator layout identify. only effect on custom indicator type.
         *
         * @param indicatorCustomView the custom view for indicator identify
         * @return Builder
         */
        public Builder setIndicatorCustomView(@NonNull View indicatorCustomView) {
            p.setMIndicatorType(IndicatorType.Companion.getCUSTOM());
            p.setMIndicatorCustomView(indicatorCustomView);
            return this;
        }

        /**
         * set the seek bar's indicator's custom top content view layout identify. no effect on custom indicator type.
         *
         * @param mIndicatorCustomTopContentLayout the custom indicator top content layout identify
         * @return Builder
         */
        public Builder setIndicatorCustomTopContentLayout(@LayoutRes int mIndicatorCustomTopContentLayout) {
            p.setMIndicatorCustomTopContentView(View.inflate(p.getMContext(), mIndicatorCustomTopContentLayout, null));
            return this;
        }

        /**
         * set the seek bar's indicator's custom top content view layout identify. no effect on custom indicator type.
         *
         * @param topContentView the custom view for indicator top content.
         * @return Builder
         */
        public Builder setIndicatorCustomTopContentView(@NonNull View topContentView) {
            p.setMIndicatorCustomTopContentView(topContentView);
            return this;
        }

        /**
         * call this method to show different shape of indicator.
         *
         * @param indicatorType IndicatorType.SQUARE_CORNERS;
         *                      IndicatorType.ROUNDED_CORNERS;
         *                      IndicatorType.CUSTOM;
         * @return Builder
         */
        public Builder setIndicatorType(int indicatorType) {
            p.setMIndicatorType(indicatorType);
            return this;
        }

        /**
         * call this method to show the text below thumb ,the text will slide with the thumb. have no influence on discrete series seek bar type.
         *
         * @param thumbProgressStay true to show thumb text.
         * @return Builder
         */
        public Builder thumbProgressStay(boolean thumbProgressStay) {
            p.setMThumbProgressStay(thumbProgressStay);
            return this;
        }

        /**
         * make the progress in float type. default in int type.
         *
         * @param floatProgress true for float progress
         * @return Builder
         */
        public Builder isFloatProgress(boolean floatProgress) {
            p.setMIsFloatProgress(floatProgress);
            return this;
        }

        /**
         * replace the left ends text of seek bar.
         * leftEndText the text below SeekBar's left end below
         *
         * @param leftEndText the text of SeekBar's left end below.
         * @return Builder
         */
        public Builder setLeftEndText(String leftEndText) {
            p.setMLeftEndText(leftEndText);
            return this;
        }

        /**
         * replace the right ends text of seek bar.
         *
         * @param rightEndText the text below SeekBar's right end below
         * @return Builder
         */
        public Builder setRightEndText(String rightEndText) {
            p.setMRightEndText(rightEndText);
            return this;
        }

        /**
         * call this method to custom the tick showing drawable.
         *
         * @param tickDrawableId the drawableId for tick drawable.
         * @return Builder
         */
        public Builder setTickDrawableId(@DrawableRes int tickDrawableId) {
            p.setMTickDrawable(p.getMContext().getResources().getDrawable(tickDrawableId));
            return this;
        }

        /**
         * call this method to custom the tick showing drawable.
         *
         * @param tickDrawable the drawable show as tick.
         * @return Builder
         */
        public Builder setTickDrawable(@NonNull Drawable tickDrawable) {
            p.setMTickDrawable(tickDrawable);
            return this;
        }

        /**
         * call this method to custom the thumb showing drawable.
         *
         * @param thumbDrawableId the drawableId for thumb drawable.
         * @return Builder
         */
        public Builder setThumbDrawable(@DrawableRes int thumbDrawableId) {
            p.setMThumbDrawable(p.getMContext().getResources().getDrawable(thumbDrawableId));
            return this;
        }

        /**
         * call this method to custom the thumb showing drawable.
         *
         * @param thumbDrawable the drawable show as Thumb.
         * @return Builder
         */
        public Builder setThumbDrawable(Drawable thumbDrawable) {
            p.setMThumbDrawable(thumbDrawable);
            return this;
        }

        /**
         * set the color for text below seek bar's tick.
         *
         * @param textColor ColorInt
         * @return Builder
         */
        public Builder setTextColor(@ColorInt int textColor) {
            p.setMTextColor(textColor);
            return this;
        }

        /**
         * set the color for indicator text . have no influence on custom tickDrawable.
         *
         * @param indicatorTextColor ColorInt
         * @return Builder
         */
        public Builder setIndicatorTextColor(@ColorInt int indicatorTextColor) {
            p.setMIndicatorTextColor(indicatorTextColor);
            return this;
        }

        /**
         * change the size for indicator text.have no influence on custom indicator.
         *
         * @param indicatorTextSize The scaled pixel size.
         * @return Builder
         */
        public Builder setIndicatorTextSize(int indicatorTextSize) {
            p.setMIndicatorTextSize(IndicatorUtils.INSTANCE.sp2px(p.getMContext(), indicatorTextSize));
            return this;
        }

        /**
         * call this method to set indicator to show all the time.
         *
         * @param stay true to stay.
         * @return
         */
        public Builder setIndicatorStay(boolean stay) {
            p.setMIndicatorStay(stay);
            return this;
        }

        /**
         * prevent user from touching to seek
         *
         * @param forbidding true can not seek.
         * @return
         */
        public Builder forbidUserToSeek(boolean forbidding) {
            p.setMForbidUserSeek(forbidding);
            return this;
        }


        /**
         * user change the thumb's location by touching thumb/touching track
         *
         * @param touchToSeek true for seeking by touch track, false for seeking by thumb.default true;
         * @return
         */
        public Builder touchToSeek(boolean touchToSeek) {
            p.setMTouchToSeek(touchToSeek);
            return this;
        }

        Builder setParams(BuilderParams p) {
            this.p = p;
            return this;
        }

        Builder setSeekBar(IndicatorSeekBar indicatorSeekBar) {
            this.indicatorSeekBar = indicatorSeekBar;
            return this;
        }

    }

}