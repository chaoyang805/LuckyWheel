package me.chaoyang805.luckywheel;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by chaoyang805 on 2015/7/20.
 */
public class LuckyWheel extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    private SurfaceHolder mHolder;
    private Canvas mCanvas;
    private Thread t;
    private boolean isRunning = false;

    private int[] mImgs = new int[]{R.drawable.danfan, R.drawable.iphone, R.drawable.ipad, R.drawable.f015, R.drawable.meizi, R.drawable.f040};
    private String[] mStrs = new String[]{"单反相机", "IPHONE", "IPAD", "谢谢惠顾", "纸巾一包", "谢谢惠顾"};
    private int[] mColors = new int[]{0xFFFFC300, 0xFFF17E01, 0xFFFFC300, 0xFFF17E01, 0xFFFFC300, 0xFFF17E01};
    private Bitmap[] mImgsBitmaps;
    private Bitmap mBgBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bg2);
    private RectF mRange;
    private int mRadius;
    private int mItemCount = 6;
    private Paint mTextPaint, mArcPaint;


    private double mSpeed = 0;
    private volatile float mStartAngle = 0;
    private boolean isShouldEnd;
    private int mCenter;
    private int mPadding;
    private float mTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 20f, getResources().getDisplayMetrics());

    public LuckyWheel(Context context) {
        this(context, null);
    }

    public LuckyWheel(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LuckyWheel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mHolder = getHolder();
        mHolder.addCallback(this);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setKeepScreenOn(true);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        mTextPaint = new Paint();
        mTextPaint.setColor(0xffffffff);
        mTextPaint.setTextSize(mTextSize);

        mArcPaint = new Paint();
        mArcPaint.setAntiAlias(true);
        mArcPaint.setDither(true);
        mRange = new RectF(mPadding, mPadding, mPadding + mRadius, mPadding + mRadius);
        mImgsBitmaps = new Bitmap[mItemCount];
        for (int i = 0; i < mItemCount; i++) {
            mImgsBitmaps[i] = BitmapFactory.decodeResource(getResources(), mImgs[i]);
        }


        isRunning = true;
        t = new Thread(this);
        t.start();
    }

    private void draw() {

        try {
            mCanvas = mHolder.lockCanvas();
            if (mCanvas != null) {
                //draw something
                //drawBackground
                drawBackground();
                float tmpAngle = mStartAngle;
                float sweepAngle = 360 / mItemCount;

                for (int i = 0; i < mItemCount; i++) {
                    mArcPaint.setColor(mColors[i]);
                    mCanvas.drawArc(mRange, tmpAngle, sweepAngle, true, mArcPaint);
                    drawTextOnPath(tmpAngle, sweepAngle, mStrs[i]);
                    drawIcon(tmpAngle, mImgsBitmaps[i]);
                    tmpAngle += sweepAngle;
                }
                mStartAngle += mSpeed;
                if (isShouldEnd) {
                    mSpeed -= 1;
                }
                if (mSpeed <= 0) {
                    mSpeed = 0;
                    isShouldEnd = false;
                }
            }
        } catch (Exception e) {
        } finally {
            mHolder.unlockCanvasAndPost(mCanvas);
        }
    }

    private void drawIcon(float tmpAngle, Bitmap bitmap) {
        int imgWidth = mRadius / 8;
        double angle = ((tmpAngle + 360 / mItemCount / 2) * Math.PI / 180);
        int x = (int) (mCenter + mRadius / 2 / 2 * Math.cos(angle));
        int y = (int) (mCenter + mRadius / 2 / 2 * Math.sin(angle));
        Rect rect = new Rect(x - imgWidth / 2, y - imgWidth / 2, x + imgWidth / 2, y + imgWidth / 2);

        mCanvas.drawBitmap(bitmap, null, rect, null);
    }

    private void drawTextOnPath(float tmpAngle, float sweepAngle, String mStr) {
        Path path = new Path();
        path.addArc(mRange, tmpAngle, sweepAngle);
        float textLength = mTextPaint.measureText(mStr);
        float hOffset = (float) ((mRadius * Math.PI / mItemCount - textLength) / 2);
        float vOffset = mRadius / 2 / 6;
        mCanvas.drawTextOnPath(mStr, path, hOffset, vOffset, mTextPaint);
    }

    private void drawBackground() {
        mCanvas.drawColor(0xffffffff);
        mCanvas.drawBitmap(mBgBitmap, null, new RectF(mPadding / 2, mPadding / 2,
                getMeasuredWidth() - mPadding / 2, getMeasuredHeight() - mPadding / 2), null);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = Math.min(getMeasuredWidth(), getMeasuredHeight());
        mPadding = getPaddingLeft();
        mRadius = width - mPadding * 2;
        mCenter = width / 2;
        setMeasuredDimension(width, width);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isRunning = false;
    }

    @Override
    public void run() {
        while (isRunning) {
            long start = System.currentTimeMillis();
            draw();
            long end = System.currentTimeMillis();
            if (end - start < 50) {
                try {
                    Thread.sleep(50 - (end - start));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void start(int speed, int index) {

        mSpeed = speed;
        if (index >= 0 && index <= 5) {
            float angle = 360 / mItemCount;
            //计算每一项的中奖范围
            /**
             * 1->150~210
             * 0->210~270
             */
            float from = 270 - (index + 1) * angle;
            float end = from + angle;

            //设置停下来需要旋转的距离
            float targetFrom = 4 * 360 + from;
            float targetEnd = 4 * 360 + end;

            //v1 = (-1 + Math.sqrt(1 + 8 * targetFrom)) / 2
            float v1 = (float) ((-1 + Math.sqrt(1 + 8 * targetFrom)) / 2);
            float v2 = (float) ((-1 + Math.sqrt(1 + 8 * targetEnd)) / 2);

            mSpeed = v1 + Math.random() * (v2 - v1);
        }
        isShouldEnd = false;
    }

    public void stop() {
        mStartAngle = 0;
        isShouldEnd = true;
    }

    public boolean isStarted() {
        return mSpeed != 0;
    }
}
