package com.example.nguyennghia.passcodeview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import java.util.Arrays;


public class PassCode extends View {

    private static final String TAG = PassCode.class.getSimpleName();
    private int mCurrentRow;
    private int mCurrentColumn;
    private float mWidthKeypadView;
    private float mHeightKeypadView;
    private int mLastTouchX;
    private int mLastTouchY;

    private Paint mPaintNormal;
    private Paint mPaintPress;
    private Paint mPaintKeyPad;
    private Paint mPaintCodebar;
    private Paint mPaintHeaderText;

    private Rect[] mCoordinateKeypad = new Rect[12];
    private boolean[] mPressState = new boolean[12];
    private Rect mRectActived;

    private String[] mKeyPadNumbers = new String[12];

    private Rect[] mCoordinateCode = new Rect[4];
    private boolean[] mCodebarState = new boolean[4];

    private int mPressIndex = -1;
    private int mCurrentIndex = -1;
    private boolean mIsLeave;

    private float mWidth;
    private float mHalfHeight;
    private float mQuaterHeight;

    private int mMarginLeft;


    private String mHeaderText;
    private int mHeaderTextColor;
    private int mHeaderTextSize;
    private int mKeyPadTextColor;
    private int mKeypadTextSize;
    private int mPressColor;
    private int mNormalColor;
    private int mCodebarColor;
    private int mCodebarHeight;

    private Bitmap mDeleteBitmap;
    private StringBuilder mTextBuilder;
    private int mDistance;

    public interface OnActionCompleted {
        void onCompleted(String value);
    }

    private OnActionCompleted mListener;

    public PassCode(Context context) {
        this(context, null);
    }


    public PassCode(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PassCode(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        this.setClickable(true);

        //setup attributes
        TypedArray a = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.PassCode, 0, 0);

        try {
            mHeaderText = a.getString(R.styleable.PassCode_headerText);
            mHeaderTextSize = a.getDimensionPixelSize(R.styleable.PassCode_headerTextSize, 50);
            mHeaderTextColor = a.getColor(R.styleable.PassCode_headerTextColor, Color.BLACK);

            mKeyPadTextColor = a.getColor(R.styleable.PassCode_keyPadTextColor, Color.BLACK);
            mKeypadTextSize = a.getDimensionPixelSize(R.styleable.PassCode_keyPadTextSize, 100);

            mPressColor = a.getColor(R.styleable.PassCode_pressColor, Color.parseColor("#ffdce1e7"));
            mNormalColor = a.getColor(R.styleable.PassCode_normalColor, Color.WHITE);
            mCodebarColor = a.getColor(R.styleable.PassCode_codeBarColor, Color.parseColor("#0174af"));
            mCodebarHeight = a.getDimensionPixelOffset(R.styleable.PassCode_codeBarHeight, 20);

        } finally {
            a.recycle();
        }


        //setup paint
        mPaintNormal = new Paint();
        mPaintNormal.setAntiAlias(true);
        mPaintNormal.setColor(mNormalColor);

        mPaintPress = new Paint();
        mPaintPress.setAntiAlias(true);
        mPaintPress.setColor(mPressColor);

        mPaintKeyPad = new Paint();
        mPaintKeyPad.setAntiAlias(true);
        mPaintKeyPad.setColor(mKeyPadTextColor);
        mPaintKeyPad.setTextSize(mKeypadTextSize);
        mPaintKeyPad.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

        mPaintCodebar = new Paint();
        mPaintCodebar.setAntiAlias(true);
        mPaintCodebar.setColor(mCodebarColor);


        mPaintHeaderText = new Paint();
        mPaintHeaderText.setAntiAlias(true);
        mPaintHeaderText.setColor(mHeaderTextColor);
        mPaintHeaderText.setTextSize(mHeaderTextSize);


        //init coordinate
        int count = mCoordinateKeypad.length;
        for (int i = 0; i < count; i++) {
            if (i < 4) {
                mCoordinateCode[i] = new Rect();
            }
            mCoordinateKeypad[i] = new Rect();
        }

        //init text number keypad
        for (int i = 0; i < 9; i++) {
            mKeyPadNumbers[i] = String.valueOf(i + 1);

        }

        mKeyPadNumbers[10] = "0";
        mKeyPadNumbers[11] = "C";

        mTextBuilder = new StringBuilder();

        mDeleteBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.num_back_n);
    }

    public void setActionCompleted(OnActionCompleted listener) {
        mListener = listener;
    }


    public void setStyle(@StyleRes int resId) {
        TypedArray a = getContext().getTheme().obtainStyledAttributes(resId, R.styleable.PassCode);
        try {
            mHeaderText = a.getString(R.styleable.PassCode_headerText);
            mHeaderTextSize = a.getDimensionPixelSize(R.styleable.PassCode_headerTextSize, 50);
            mHeaderTextColor = a.getColor(R.styleable.PassCode_headerTextColor, Color.BLACK);
            if (mPaintHeaderText != null) {
                mPaintHeaderText.setTextSize(mHeaderTextSize);
                mPaintHeaderText.setColor(mHeaderTextColor);
            }

            mKeyPadTextColor = a.getColor(R.styleable.PassCode_keyPadTextColor, Color.BLACK);
            mKeypadTextSize = a.getDimensionPixelSize(R.styleable.PassCode_keyPadTextSize, 100);
            if (mPaintKeyPad != null) {
                mPaintKeyPad.setColor(mKeyPadTextColor);
                mPaintKeyPad.setTextSize(mKeypadTextSize);
            }

            mPressColor = a.getColor(R.styleable.PassCode_pressColor, Color.parseColor("#ffdce1e7"));
            if (mPaintPress != null)
                mPaintPress.setColor(mPressColor);
            mNormalColor = a.getColor(R.styleable.PassCode_normalColor, Color.WHITE);
            if (mPaintNormal != null)
                mPaintNormal.setColor(mNormalColor);

            mCodebarColor = a.getColor(R.styleable.PassCode_codeBarColor, Color.parseColor("#3498db"));
            if (mPaintCodebar != null)
                mPaintCodebar.setColor(mCodebarColor);

            mCodebarHeight = a.getDimensionPixelSize(R.styleable.PassCode_codeBarHeight, 20);

        } finally {
            a.recycle();
        }




    }

    public void setHeaderTextColor(int color) {
        mHeaderTextColor = color;
        mPaintHeaderText.setColor(color);
        invalidate();
    }

    public void setHeaderTextSize(int size) {
        mHeaderTextSize = size;
        mPaintHeaderText.setTextSize(size);
        invalidate();
    }

    public void setKeypadTextColor(int color) {
        mKeyPadTextColor = color;
        mPaintKeyPad.setColor(color);
        invalidate();
    }

    public void setKeypadTextSize(int size) {
        mKeypadTextSize = size;
        mPaintKeyPad.setTextSize(size);
        invalidate();
    }

    public void setNormalColor(int color) {
        mNormalColor = color;
        mPaintNormal.setColor(color);
        invalidate();
    }

    public void setPressColor(int color) {
        mPressColor = color;
        mPaintPress.setColor(color);
        invalidate();
    }


    public void setCodebarColor(int color) {
        mCodebarColor = color;
        mPaintCodebar.setColor(color);
        invalidate();
    }

    public void setHeaderText(String headerText) {
        Log.i(TAG, "setHeaderText");
        mHeaderText = headerText;
        invalidate();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        Log.i(TAG, "onMeausre");
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        mMarginLeft = widthSize / 6;
        mDistance = mMarginLeft / 3;
        mWidth = widthSize;
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        mHalfHeight = heightSize / 2.0f;
        mQuaterHeight = heightSize / 4.0f;

        float width = (widthSize - 2 * mMarginLeft - 3 * mDistance) / 4.0f;

        int start = mMarginLeft;
        for (int i = 0; i < 4; i++) {
            int left = Math.round(i * width + i * mDistance) + start;
            int top = (int) mQuaterHeight + (int)(mQuaterHeight / 5.0f);
            int right = Math.round(left + width);
            int bottom = top + mCodebarHeight;

            mCoordinateCode[i].set(left, top, right, bottom);
        }

        mWidthKeypadView = (float) widthSize / 3;
        mHeightKeypadView = mHalfHeight / 4;

        for (int i = 0; i < mCoordinateKeypad.length; i++) {
            if (mCurrentColumn > 2) {
                mCurrentRow++;
                mCurrentColumn = 0;
            }

            int left = Math.round(mCurrentColumn * mWidthKeypadView);
            int top = Math.round(mCurrentRow * mHeightKeypadView + mHalfHeight);
            int right = Math.round(left + mWidthKeypadView);
            int bottom = Math.round(top + mHeightKeypadView);
            mCoordinateKeypad[i].set(left, top, right, bottom);
            mCurrentColumn++;
        }
        mCurrentColumn = mCurrentRow = 0;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mHeaderText != null) {
            float widthMeasureHeaderText = mPaintHeaderText.measureText(mHeaderText);
            canvas.drawText(mHeaderText, (getMeasuredWidth() - widthMeasureHeaderText) / 2.0f, mQuaterHeight - (int)(mQuaterHeight / 5.0f), mPaintHeaderText);
        }

        for (int i = 0; i < 4; i++) {
            if (!mCodebarState[i])
                canvas.drawRect(mCoordinateCode[i], mPaintCodebar);
            else {
                Rect rect = mCoordinateCode[i];
                float x = rect.width() / 2.0f + rect.left;
                float y = rect.top + rect.height() / 2.0f;
                float radius = (0.7f * rect.width()) / 2.0f;
                canvas.drawCircle(x, y, radius, mPaintCodebar);
            }
        }

        mPaintNormal.setColor(Color.parseColor("#e8e8e8"));
        canvas.drawLine(0, mHalfHeight - 1, mWidth, mHalfHeight, mPaintNormal);
        mPaintNormal.setColor(mNormalColor);

        for (int i = 0; i < 12; i++) {
            Rect rect = mCoordinateKeypad[i];
            if (i != 9) {
                if (i == 11) {
                    float left = rect.left + (rect.width() - mDeleteBitmap.getWidth()) / 2.0f;
                    float top = rect.top + (rect.height() - mDeleteBitmap.getHeight()) / 2.0f;
                    if (!mPressState[i]) {
                        canvas.drawRect(rect, mPaintNormal);
                        canvas.drawBitmap(mDeleteBitmap, left, top, mPaintNormal);
                    } else {
                        canvas.drawRect(rect, mPaintPress);
                        canvas.drawBitmap(mDeleteBitmap, left, top, mPaintNormal);
                    }
                } else {

                    Rect rectBoundText = new Rect();
                    mPaintKeyPad.getTextBounds(mKeyPadNumbers[i], 0, mKeyPadNumbers[i].length(), rectBoundText);
                    float widthMeasuredText = mPaintKeyPad.measureText(mKeyPadNumbers[i]);
                    float x = rect.left + (rect.width() - widthMeasuredText) / 2.0f;
                    float y = rect.top + (rect.height() + rectBoundText.height()) / 2.0f;
                    if (!mPressState[i]) {
                        canvas.drawRect(rect, mPaintNormal);
                        canvas.drawText(mKeyPadNumbers[i], x, y, mPaintKeyPad);
                    } else {
                        canvas.drawRect(rect, mPaintPress);
                        canvas.drawText(mKeyPadNumbers[i], x, y, mPaintKeyPad);
                    }
                }
            } else {
                canvas.drawRect(rect, mPaintNormal);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastTouchX = (int) event.getX();
                mLastTouchY = (int) event.getY();
                int lenght = mCoordinateKeypad.length;
                for (int i = 0; i < lenght; i++) {
                    if (i != 9) {
                        if (mCoordinateKeypad[i].contains(mLastTouchX, mLastTouchY)) {
                            mPressState[i] = true;
                            mPressIndex = i;
                            mRectActived = mCoordinateKeypad[i];
                            invalidate();
                        }
                    }
                }
                break;

            case MotionEvent.ACTION_MOVE:
                int x = (int) event.getX();
                int y = (int) event.getY();
                if (mPressIndex > -1 && !mRectActived.contains(x, y)) {
                    mPressState[mPressIndex] = false;
                    invalidate();
                    mIsLeave = true;
                }

                break;
            case MotionEvent.ACTION_UP:
                if (mPressIndex >= 0 && mPressIndex != 9) {
                    if (!mIsLeave) {
                        handlePressKeypad(mKeyPadNumbers[mPressIndex]);
                    }
                    mPressState[mPressIndex] = false;
                    mPressIndex = -1;
                    invalidate();
                }
                mIsLeave = false;
                break;
        }
        return true;
    }

    private void handlePressKeypad(String value) {
        if (value.equals("C")) {
            if (mCurrentIndex >= 0) {
                mCodebarState[mCurrentIndex] = false;
                mTextBuilder.deleteCharAt(mCurrentIndex);
            }
            mCurrentIndex--;
            mCurrentIndex = mCurrentIndex < 0 ? -1 : mCurrentIndex;

        } else {
            mCurrentIndex++;
            if (mCurrentIndex <= 3) {
                mCodebarState[mCurrentIndex] = true;
                mTextBuilder.append(value);
            }
            mCurrentIndex = mCurrentIndex > 3 ? 3 : mCurrentIndex;
        }

        if (mCurrentIndex == 3 && mListener != null) {
            mListener.onCompleted(mTextBuilder.toString());
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    reset();
                }
            }, 100);
        }
    }

    public void reset() {
        Arrays.fill(mCodebarState, false);
        mCurrentIndex = -1;
        if (mTextBuilder != null) {
            mTextBuilder.delete(0, mTextBuilder.length());
        }

        invalidate();
    }
}
