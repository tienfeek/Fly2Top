/**
 * 
 */
package com.tien.fly2top;

import java.util.ArrayList;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.BaseAdapter;
import android.widget.ListView;

/**
 * TODO
 * 
 * @author wangtianfei01
 * 
 */
public class DynamicListView extends ListView {
    
    private static final int ANIMATION_TIME = 200;
    private final int SMOOTH_SCROLL_AMOUNT_AT_EDGE = 15;
    
    private BitmapDrawable mSelectedHoverCell;
    private BitmapDrawable mAboveHoverCell;
    
    private Rect mSelectedCellCurrentRect;
    private Rect mSelectedCellOriginRect;
    private Rect mAboveCellCurrentRect;
    private Rect mAboveCellOriginRect;
    
    private long mMobileItemId;
    private long mAboveItemId;
    
    private View selectedView;
    private View aboveView;
    
    private int mSmoothScrollAmountAtEdge = 0;
    private int selectedPosition;
    private ArrayList datas;
    
    public DynamicListView(Context context) {
        super(context);
        init(context);
    }
    
    public DynamicListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }
    
    public DynamicListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }
    
    private void init(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        mSmoothScrollAmountAtEdge = (int) (SMOOTH_SCROLL_AMOUNT_AT_EDGE / metrics.density);
    }
    
    public void setDatas(ArrayList datas) {
        this.datas = datas;
    }
    
    public void fly(int position) {
        selectedPosition = position;
        int itemNum = position - getFirstVisiblePosition();
        selectedView = getChildAt(itemNum);
        
        mSelectedHoverCell = getAndAddSelectedView(selectedView);
        
        if (itemNum > 0) {
            aboveView = getChildAt(itemNum - 1);
            mAboveHoverCell = getAndAddAboveView(aboveView);
        }
        
        mMobileItemId = getAdapter().getItemId(position);
        
        start();
    }
    
    private BitmapDrawable getAndAddSelectedView(View v) {
        
        int w = v.getWidth();
        int h = v.getHeight();
        int top = v.getTop();
        int left = v.getLeft();
        
        Bitmap b = getBitmapForView(v);
        
        BitmapDrawable drawable = new BitmapDrawable(getResources(), b);
        mSelectedCellOriginRect = new Rect(left, top, left + w, top + h);
        mSelectedCellCurrentRect = new Rect(mSelectedCellOriginRect);
        
        drawable.setBounds(mSelectedCellCurrentRect);
        
        return drawable;
    }
    
    private BitmapDrawable getAndAddAboveView(View v) {
        
        int w = v.getWidth();
        int h = v.getHeight();
        int top = v.getTop();
        int left = v.getLeft();
        
        Bitmap b = getBitmapForView(v);
        
        BitmapDrawable drawable = new BitmapDrawable(getResources(), b);
        mAboveCellOriginRect = new Rect(left, top, left + w, top + h);
        mAboveCellCurrentRect = new Rect(mAboveCellOriginRect);
        
        drawable.setBounds(mAboveCellCurrentRect);
        
        return drawable;
    }
    
    private Bitmap getBitmapForView(View v) {
        Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        
        v.draw(canvas);
        
        return bitmap;
    }
    
    private void start() {
        int deltaY = mSelectedCellOriginRect.top - mAboveCellOriginRect.top;
        ValueAnimator animator = ValueAnimator.ofInt(0, deltaY);
        
        Log.i("wanges", "mSelectedCellCurrentRect:" + mSelectedCellCurrentRect);
        Log.i("wanges", "deltaY:" + deltaY);
        
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(ANIMATION_TIME);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (Integer) animation.getAnimatedValue();
                mSelectedCellCurrentRect.offsetTo(mSelectedCellOriginRect.left, mSelectedCellOriginRect.top - value);
                mAboveCellCurrentRect.offsetTo(mAboveCellOriginRect.left, mAboveCellOriginRect.top + value);
                
                mSelectedHoverCell.setBounds(mSelectedCellCurrentRect);
                mAboveHoverCell.setBounds(mAboveCellCurrentRect);
                
                invalidate();
            }
        });
        animator.addListener(new AnimatorListener() {
            
            @Override
            public void onAnimationStart(Animator animation) {
                if (selectedView != null) {
                    selectedView.setVisibility(View.GONE);
                }
                if (aboveView != null) {
                    aboveView.setVisibility(View.GONE);
                }
                
                setEnabled(false);
            }
            
            @Override
            public void onAnimationRepeat(Animator animation) {
                
            }
            
            @Override
            public void onAnimationEnd(Animator animation) {
                swapElements(datas, selectedPosition, selectedPosition - 1);
                
                ((BaseAdapter) getAdapter()).notifyDataSetChanged();
                if (selectedView != null) {
                    selectedView.setVisibility(View.VISIBLE);
                }
                if (aboveView != null) {
                    aboveView.setVisibility(View.VISIBLE);
                }
                
                mSelectedHoverCell = null;
                mAboveHoverCell = null;
                
                Log.i("wanges", "selectedPosition:"+selectedPosition+" getFirstVisiblePosition():"+getFirstVisiblePosition());
                if(selectedPosition - 1 == getFirstVisiblePosition()){
                    smoothScrollBy( - getHeight() + mSelectedCellOriginRect.height() , 1000);
                    
                    if (selectedPosition - 1 > 0) {
                        postDelayed(new Runnable() {
                            
                            @Override
                            public void run() {
                                fly(selectedPosition - 1);
                            }
                        }, 1100);
                    }
                }else{
                    if (selectedPosition - 1 > 0) {
                        postDelayed(new Runnable() {
                            
                            @Override
                            public void run() {
                                fly(selectedPosition - 1);
                            }
                        }, 100);
                    }
                }
                
                if(getFirstVisiblePosition() == 0){
                    setEnabled(true);
                }
            }
            
            @Override
            public void onAnimationCancel(Animator animation) {
                
            }
        });
        animator.start();
    }
    
    private void swapElements(ArrayList arrayList, int indexOne, int indexTwo) {
        Object temp = arrayList.get(indexOne);
        arrayList.set(indexOne, arrayList.get(indexTwo));
        arrayList.set(indexTwo, temp);
    }
    
    public boolean handleMobileCellScroll(Rect r) {
        int offset = computeVerticalScrollOffset();
        int height = getHeight();
        int extent = computeVerticalScrollExtent();
        int range = computeVerticalScrollRange();
        int hoverViewTop = r.top;
        int hoverHeight = r.height();
        
        smoothScrollBy(-mSmoothScrollAmountAtEdge, 0);
        if (hoverViewTop <= hoverHeight && offset > 0) {
            return true;
        }
        
//        if (hoverViewTop + hoverHeight >= height && (offset + extent) < range) {
//            smoothScrollBy(mSmoothScrollAmountAtEdge, 0);
//            return true;
//        }
        
        return false;
    }
    
    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        
        if (mSelectedHoverCell != null) {
            mSelectedHoverCell.draw(canvas);
        }
        if (mAboveHoverCell != null) {
            mAboveHoverCell.draw(canvas);
        }
        canvas.drawText("welcome to ", 200, 200, new Paint());
    }
    
}
