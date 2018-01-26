package com.maotou.recordviewdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by lyzhang3 on 2017/11/24.
 */
public class RecordWaveBaseView extends SurfaceView {

  private Paint mPaint;  //绘制线条的paint
  private Paint mGrainPaint;//波纹paint
  private Paint mScalePaint;
  private Paint mDotPaint;

  private Paint mViewPaint;

  private Canvas mCanvas = new Canvas();
  private Canvas mBackCanVans = new Canvas();

  private Bitmap mBitmap, mBackgroundBitmap;

  private int width;

  private int height;

  protected int mMin;

  protected int mScaleMargin; //刻度间距
  protected int mScaleHeight; //刻度线的高度
  protected int mScaleMaxHeight; //整刻度线高度

  protected int mRectWidth; //总宽度
  protected int mRectHeight; //高度

  protected int mMiddleLineHeight;//中线高度

  private int r;
  private int markR;

  private Context context;

  final protected Object mLock = new Object();

  //    private ArrayList<Short> dataList = new ArrayList<>();

  private ArrayList<HashMap<String, Integer>> dataList = new ArrayList<HashMap<String, Integer>>();
  private ArrayList<Integer> dotList = new ArrayList<>();

  private int startMargin, endMargin, bottomMargin;
  private int topMargin;

  private boolean mIsDraw = true;

  private DrawThread mInnerThread;

  private int maxSize, grainSize;

  private int cursorSize;

  private final String KEY_VOLUME = "volume";
  private final String KEY_TIME = "time";

  //    private int l;

  private int mLastX;
  private int mLastY;

  float fontScale;
  private PorterDuffXfermode cxfermode;
  private PorterDuffXfermode sxfermode;

  public RecordWaveBaseView(Context context) {
    super(context);
    this.context = context;
    init(context);
  }

/*    public void setInt(int l){
        this.l = l ;
        requestLayout();

    }*/

  public RecordWaveBaseView(Context context, AttributeSet attrs) {
    super(context, attrs);
    this.context = context;
    init(context);
  }

  public RecordWaveBaseView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    this.context = context;
    init(context);
  }

  private void init(Context context) {

    mViewPaint = new Paint();

    mGrainPaint = new Paint();
    mGrainPaint.setAntiAlias(true);          //抗锯齿
    mGrainPaint.setColor(Color.parseColor("#E2FFFFFF"));//画笔颜色
    mGrainPaint.setStyle(Paint.Style.FILL);  //画笔风格
    mGrainPaint.setTextSize(dip2px(context, 10));             //绘制文字大小，单位px
    mGrainPaint.setStrokeWidth(dip2px(context, 0.5f));

    mScalePaint = new Paint();
    mScalePaint.setAntiAlias(true);          //抗锯齿
    mScalePaint.setColor(Color.parseColor("#78FFFFFF"));//画笔颜色
    mScalePaint.setStyle(Paint.Style.FILL);  //画笔风格
    mScalePaint.setTextSize(36);             //绘制文字大小，单位px
    mScalePaint.setStrokeWidth(dip2px(context, 0.5f));

    mDotPaint = new Paint();
    mDotPaint.setAntiAlias(true);          //抗锯齿
    mDotPaint.setColor(Color.parseColor("#ffe443"));//画笔颜色
    mDotPaint.setStyle(Paint.Style.FILL);  //画笔风格
    mDotPaint.setTextSize(dip2px(context, 16));             //绘制文字大小，单位px
    mDotPaint.setStrokeWidth(dip2px(context, 0.5f));

    mPaint = new Paint();
    mPaint.setAntiAlias(true);          //抗锯齿
    mPaint.setColor(Color.parseColor("#78FFFFFF"));//画笔颜色
    mPaint.setStyle(Paint.Style.FILL);  //画笔风格
    mPaint.setTextSize(36);             //绘制文字大小，单位px
    mPaint.setStrokeWidth(dip2px(context, 0.5f));

    cxfermode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
    sxfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER);

    mMin = dip2px(context, 0.25f);
    mScaleHeight = dip2px(context, 3);
    mScaleMargin = dip2px(context, 1);

    mRectHeight = dip2px(context, 55.5f);
    mScaleMaxHeight = mScaleHeight * 3;

    startMargin = dip2px(context, 5);//开始的边距

    endMargin = dip2px(context, 5);//结束的边距

    bottomMargin = dip2px(context, 5);

    r = dip2px(context, 2.5f);

    topMargin = r;//上方边距

    markR = dip2px(context, 2);

    width = getScreenWidth() - dip2px(context, 30);

    maxSize = (width - startMargin - endMargin) / mScaleMargin;

    cursorSize = maxSize / 2;

    grainSize = cursorSize;

    mMiddleLineHeight = mRectHeight - dip2px(context, 20);

    fontScale = context.getResources().getDisplayMetrics().density / 2;
  }

  /**
   * 得到屏幕宽度
   */
  private int getScreenWidth() {
    WindowManager windowManager =
        (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
    DisplayMetrics displayMetrics = new DisplayMetrics();
    windowManager.getDefaultDisplay().getMetrics(displayMetrics);
    return displayMetrics.widthPixels;
  }

  private int mWidthSpecSize;
  private int mHeightSpecSize;

  @Override
  protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
    super.onVisibilityChanged(changedView, visibility);
    if (visibility == VISIBLE && mBackgroundBitmap == null) {
      ViewTreeObserver vto = getViewTreeObserver();
      vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
        @Override
        public boolean onPreDraw() {
          if (width > 0 && mRectHeight > 0) {
            mWidthSpecSize = width;
            mHeightSpecSize = mRectHeight;
            mBackgroundBitmap =
                Bitmap.createBitmap(mWidthSpecSize, mHeightSpecSize, Bitmap.Config.ARGB_8888);
            mBitmap = Bitmap.createBitmap(mWidthSpecSize, mHeightSpecSize, Bitmap.Config.ARGB_8888);
            mBackCanVans.setBitmap(mBackgroundBitmap);
            mCanvas.setBitmap(mBitmap);
            ViewTreeObserver vto = getViewTreeObserver();
            vto.removeOnPreDrawListener(this);
          }
          return true;
        }
      });
    }
  }

  /**
   * dip转为PX
   */
  private int dip2px(Context context, float dipValue) {
    float fontScale = context.getResources().getDisplayMetrics().density;
    return (int) (dipValue * fontScale + 0.5f);
  }

  @Override
  protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    mIsDraw = false;
    if (mBitmap != null && !mBitmap.isRecycled()) {
      mBitmap.recycle();
    }
    if (mBackgroundBitmap != null && !mBackgroundBitmap.isRecycled()) {
      mBackgroundBitmap.recycle();
    }
  }

  int f = 1;

  private class DrawThread extends Thread {
    @Override
    public void run() {

      while (mIsDraw) {
        long time = System.currentTimeMillis();
        ArrayList<HashMap<String, Integer>> drawList = new ArrayList<HashMap<String, Integer>>();
        synchronized (dataList) {
          if (dataList.size() != 0) {
            try {
              drawList = (ArrayList<HashMap<String, Integer>>) deepCopy(dataList);// 保存  接收数据
            } catch (Exception e) {
              e.printStackTrace();
              continue;
            }
          }
        }
        if (mBackgroundBitmap == null) {
          Log.e("**********", " mBackgroundBitmap == null");
          continue;
        }

        if (mBackCanVans != null) {
          mScalePaint.setXfermode(cxfermode);
          mBackCanVans.drawPaint(mScalePaint);
          mScalePaint.setXfermode(sxfermode);
          int memory = 0;
          if (drawList.size() != 0) {
            memory = drawList.get(drawList.size() - 1).get(KEY_TIME);
          }

          SimpleDateFormat sdf1 = new SimpleDateFormat("mm:ss");
          try {
            //画横线
            mBackCanVans.drawLine(startMargin, topMargin, width - endMargin, topMargin,
                mScalePaint);

            for (int i = 0; i < drawList.size(); i++) {

              //画刻度
              if (drawList.get(i).get(KEY_TIME) % 120 == 0) {
                mBackCanVans.drawLine(startMargin + i * mScaleMargin, topMargin,
                    startMargin + i * mScaleMargin, topMargin + mScaleMaxHeight,
                    mScalePaint);
                if (drawList.get(i).get(KEY_TIME) != 0) {
                  mBackCanVans.drawText(
                      sdf1.format(
                          new Date(drawList.get(i).get(KEY_TIME) * 500)),
                      startMargin + i * mScaleMargin + dip2px(context, 5),
                      topMargin + mScaleMaxHeight + dip2px(context, 3),
                      mGrainPaint);
                }
              } else if (drawList.get(i).get(KEY_TIME) % 20 == 0) {
                mBackCanVans.drawLine(startMargin + i * mScaleMargin, topMargin,
                    startMargin + i * mScaleMargin, topMargin + mScaleHeight,
                    mScalePaint);
              }
              int height = 0;

              //if (f % 10 != 0) {
                height = drawList.get(i).get(KEY_VOLUME);
              //} else {
              //  height = 0;
              //}
              if (height == 0){
                Log.d("********","height==0");
              }
              //画下波纹
              mBackCanVans.drawLine(startMargin + i * mScaleMargin,
                  mMiddleLineHeight + height +mMin,
                  startMargin + i * mScaleMargin, mMiddleLineHeight + mMin,
                  mGrainPaint);
              //画上波纹
              mBackCanVans.drawLine(startMargin + i * mScaleMargin,
                  mMiddleLineHeight - height - mMin,
                  startMargin + i * mScaleMargin, mMiddleLineHeight - mMin,
                  mGrainPaint);

              //画标记
              if (dotList.contains(i)) {
                mBackCanVans.drawCircle(startMargin + i * mScaleMargin,
                    topMargin, markR, mDotPaint);
              }
            }

            for (int i = drawList.size(); i < maxSize; i++) {
              memory++;
              //画刻度
              if (memory % 120 == 0) {

                mBackCanVans.drawLine(startMargin + i * mScaleMargin, topMargin,
                    startMargin + i * mScaleMargin, topMargin + mScaleMaxHeight, mScalePaint);
                if (memory != 0) {
                  mBackCanVans.drawText(sdf1.format(new Date(memory * 500)),
                      startMargin + i * mScaleMargin + dip2px(context, 5),
                      topMargin + mScaleMaxHeight + dip2px(context, 3),
                      mGrainPaint);
                }
              } else if (memory % 20 == 0) {
                mBackCanVans.drawLine(startMargin + i * mScaleMargin, topMargin,
                    startMargin + i * mScaleMargin, topMargin + mScaleHeight, mScalePaint);
              }
            }

            //画游标
            int x;
            if (drawList.size() < cursorSize) {
              x = drawList.size() * mScaleMargin + startMargin;
            } else {
              x = cursorSize * mScaleMargin + startMargin;
            }
            mBackCanVans.drawCircle(x, topMargin, r, mDotPaint);
            mBackCanVans.drawLine(x, topMargin, x, mRectHeight - endMargin, mDotPaint);
          } catch (Exception e) {
            e.printStackTrace();
            continue;
          }

          //                    Log.e("*********", System.currentTimeMillis() - time + "");

          synchronized (mLock) {
            //mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            mViewPaint.setXfermode(cxfermode);
            mCanvas.drawPaint(mViewPaint);
            mViewPaint.setXfermode(sxfermode);
            mCanvas.drawBitmap(mBackgroundBitmap, 0, 0, mViewPaint);
          }
          //休眠暂停资源
          try {
            Thread.sleep(250);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
          //                    Log.e("******stop=",""+System.currentTimeMillis());
          //                    Message msg = new Message();
          //                    msg.what = 0;
          //                    handler.sendMessage(msg);

          f++;
        }
      }
    }
  }

  public List deepCopy(List src) throws IOException, ClassNotFoundException {
    ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
    ObjectOutputStream out = new ObjectOutputStream(byteOut);
    out.writeObject(src);

    ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
    ObjectInputStream in = new ObjectInputStream(byteIn);
    List dest = (List) in.readObject();
    return dest;
  }

  /**
   * 开始绘制
   */
  public void startView() {
    if (mInnerThread != null && mInnerThread.isAlive()) {
      mIsDraw = false;
      while (mInnerThread.isAlive()) ;
      mScalePaint.setXfermode(cxfermode);
      mBackCanVans.drawPaint(mScalePaint);
      mScalePaint.setXfermode(sxfermode);
      mViewPaint.setXfermode(cxfermode);
      mCanvas.drawPaint(mViewPaint);
      mViewPaint.setXfermode(sxfermode);
    }
    mIsDraw = true;
    mInnerThread = new DrawThread();
    mInnerThread.start();
  }

  /**
   * 停止绘制
   */
  public void stopView() {
    mIsDraw = false;
    dataList.clear();
    if (mInnerThread != null) {
      while (mInnerThread.isAlive()) ;
    }
    mScalePaint.setXfermode(cxfermode);
    mBackCanVans.drawPaint(mScalePaint);
    mScalePaint.setXfermode(sxfermode);
    mViewPaint.setXfermode(cxfermode);
    mCanvas.drawPaint(mViewPaint);
    mViewPaint.setXfermode(sxfermode);
  }

  //    @Override
  //    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
  //        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
  //        WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
  //        width = mRecDataList.size() * mScaleMargin + wm.getDefaultDisplay().getWidth() - dip2px(context,80);//左边预留10dp,右边预留屏幕的宽度减去10dp
  //        height = mRectHeight;
  //        //设置宽度和高度
  //        setMeasuredDimension(width, height);
  //    }
  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
  }

  //重写该方法，在这里绘图
  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    if (mIsDraw && mBitmap != null) {
      synchronized (mLock) {
        canvas.drawBitmap(mBitmap, 0, 0, mViewPaint);
      }
    }
  }

  Handler handler = new Handler() {
    @Override
    public void handleMessage(Message msg) {
      RecordWaveBaseView.this.invalidate();
    }
  };

  public void setDataList(ArrayList<Integer> volumes, ArrayList<Integer> dotList) {
    //        dataList.addAll(mRecDataList);
    sendData(volumes);
    //        this.dotList.addAll(dotList);
    //
    Message msg = new Message();
    msg.what = 0;
    handler.sendMessage(msg);
  }

  private volatile int time = 0;

  private void sendData(ArrayList<Integer> volumes) {
    if (dataList != null) {
      time++;
      int max = 0;
      for (int i = 0; i < volumes.size(); i++) {
        if (volumes.get(i) > max) {
          max = volumes.get(i);
        }
      }
      HashMap<String, Integer> timeMap = new HashMap<String, Integer>();
      timeMap.put(KEY_TIME, time);
      timeMap.put(KEY_VOLUME, (int) (max * fontScale));
      dataList.add(timeMap);
      if (dataList.size() > grainSize && dataList.size() != 0) {
        dataList.remove(0);
      }

      //            }
    }
  }

}
