package com.wangln.shoplist;

import android.content.Context;
import android.graphics.Point;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.Scroller;

/**
 * Created by Administrator on 2017/9/14 0014.
 */

public class BestRefreshView extends ViewGroup{
    ViewGroup headView;
    ViewGroup footerView;
    ViewGroup headBack;
    ViewGroup footerBack;
    View mTarget;
    private boolean isRefreshing = false;
    int pullDirection;
    int footerHeight;
    int headerHeight;
    float lastY;
    int mTop;
    float sumY = 0;
    boolean end = false;
    OnRefreshListner lisetner;
    public static final int RESET = 0;
    public static final int PULL_DOWN = 1;
    public static final int PULL_UP = 2;
    public static final int REFRESH = 3;
    public static final int PULL_TO_REFRESH = 4;
    public static final int RELEASE_TO_REFRESH = 5;
    private static final float FRICTION = 2.0f;
    int mMode = 1;
    Scroller mScroller;
    public BestRefreshView(Context context, AttributeSet attrs) {
        super(context, attrs);
        AccelerateInterpolator accl = new AccelerateInterpolator();
        mScroller = new Scroller(getContext(),accl);
    }
    public void setHeaderLayout(View view) {
        this.headView = (ViewGroup) view;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        addView(headView,params);
    }
    private int getRefreshHeight(){
        Point point = new Point();
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getSize(point);
        return point.y * 2 / 5;
    }
    public void setFooterLayout(View view) {
        this.footerView = (ViewGroup) view;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        addView(footerView,params);
    }
    public void setHeadBackgroud(View view){
        headBack = (ViewGroup) view;
        removeView(headView);
        headBack.addView(headView);
        addView(headBack);
    }
    public void setFooterBackground(View view){
        footerBack = (ViewGroup) view;
        removeView(footerView);
        footerBack.addView(footerView);
        addView(footerBack);
    }
    public void setOnRefreshLisetner(OnRefreshListner lisetner){
        this.lisetner = lisetner;
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//         int size = getChildCount();
//        Log.d("wang","onMeasure size " + size);
//        for (int i = 0; i < size; ++i) {
//            View child = getChildAt(i);
//            measureChild(child, widthMeasureSpec, heightMeasureSpec);
//        }
        measureChildren(widthMeasureSpec,heightMeasureSpec);
        Log.d("wang","onMeasure");
    }

    private void pullEvent(MotionEvent event){
        float currY = event.getY();
        float changeY = (currY - lastY) / FRICTION;
        judgePullEvent();
        scrollBy(0, (int) -changeY);
        lastY = currY;
        sumY += changeY;
        if(lisetner != null) {
            switch (judgePullEvent()) {
                case PULL_DOWN:
                    lisetner.onPull(sumY/headerHeight, PULL_DOWN);
                    break;
                case PULL_UP:
                    lisetner.onPull(sumY/footerHeight,PULL_UP);
                    break;
            }

        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.d("wang","onInterceptTouchEvent " + pullDirection);
        if(canChildScrollDown() || canChildScrollUp()) {
            Log.d("wang","start action " + ev.getAction());
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    lastY = ev.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    Log.d("wang","move");
                    float currY = ev.getY();
                    float changeY = currY - lastY;
                    if(changeY > 0) {
                        Log.d("wang","change pull down");
                        if(!canChildScrollUp()) {
                            Log.d("wang","can not scroll up");
                            return true;
                        }
                    } else {
                        Log.d("wang","change pull up");
                        if(!canChildScrollDown()) {
                            Log.d("wang","can not scroll down");
                            return true;
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    break;
            }
        }
        return false;
    }

    public boolean canChildScrollUp() {
        if (android.os.Build.VERSION.SDK_INT < 14) {
            if (mTarget instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) mTarget;
                return absListView.getChildCount() > 0
                        && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
                        .getTop() < absListView.getPaddingTop());
            } else {
                return ViewCompat.canScrollVertically(mTarget, -1) || mTarget.getScrollY() > 0;
            }
        } else {
            return ViewCompat.canScrollVertically(mTarget, -1);
        }
    }
    public boolean canChildScrollDown() {
        if (android.os.Build.VERSION.SDK_INT < 14) {
            if (mTarget instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) mTarget;
                final int lastVisiblePosition = absListView.getLastVisiblePosition();
                final int childIndex = lastVisiblePosition - absListView.getFirstVisiblePosition();
                final View lastVisibleChild = absListView.getChildAt(childIndex);
                return absListView.getChildCount() > 0
                        && (absListView.getLastVisiblePosition() >= (absListView.getCount() - 1)
                        );
            } else {
                return ViewCompat.canScrollVertically(mTarget, 1) || mTarget.getScrollY() < 0;
            }
        } else {
            return ViewCompat.canScrollVertically(mTarget, 1);
        }
    }
    private int judgePullEvent(){
        int changeY = getScrollY();
        if(changeY > 0) {
            mMode = PULL_UP;
        } else {
            mMode = PULL_DOWN;
        }
        pullDirection = mMode;
        return mMode;
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                pullEvent(event);
                break;
            case MotionEvent.ACTION_UP:
                releaseView();
                break;
        }
        return true;
    }
    public void endRefresh(){
        end = true;
        if(end) {
            isRefreshing = false;
            mMode = REFRESH;
            smoothScrollTo(mTop);
        }
    }
    public void releaseView(){
        Log.d("wang","mMode " + mMode + " sumY " + sumY + " headerheight " + headerHeight);
        switch (mMode) {
           case PULL_DOWN:
                if(sumY <= headerHeight) {
//                    scrollTo(0,mTop);
                    smoothScrollTo(mTop);
                    mMode = REFRESH;
                } else {
                    smoothScrollTo(headerHeight);
                    mMode = RELEASE_TO_REFRESH;
                }
                break;
            case PULL_UP:
                if(Math.abs(sumY) <= footerHeight ) {
//                    scrollTo(0,mTop);
                    smoothScrollTo(mTop);
                    mMode = REFRESH;
                } else {
                    smoothScrollTo(-footerHeight);
                    mMode = RELEASE_TO_REFRESH;
//                    scrollTo(0,footerHeight);
                }
                break;
        }

    }

    private void smoothScrollTo(int y){
        Log.d("wang","sumY " + sumY + " y " + y);
        mScroller.startScroll(0, (int) (mTop - sumY),0, (int) (sumY - y));
        sumY = y;
        invalidate();
    }
    private void setState(float value){
        switch (mMode) {
            case PULL_TO_REFRESH:
              lisetner.onPullToRefresh(value,pullDirection);
                break;
            case REFRESH:
                lisetner.onRelease(value,pullDirection);
                break;
        }
    }
    @Override
    public void computeScroll() {
        super.computeScroll();
        if(mScroller.computeScrollOffset()) {
            Log.d("wang","y " + mScroller.getCurrY());
            scrollTo(0,mScroller.getCurrY());
            float aa = mScroller.getFinalY() - mScroller.getStartY();
            Log.d("wang","aa " + aa);
            float value = Math.abs((mScroller.getCurrY() - mScroller.getStartY()) * 1.0f / (mScroller.getFinalY() - mScroller.getStartY()));
            setState(value);
            invalidate();
        } else {
            if(mMode == RELEASE_TO_REFRESH && !isRefreshing) {
                isRefreshing = true;
                if(lisetner != null) {
                    lisetner.onRefresh(pullDirection);
                }
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mTop = t;
        if(headView != null) {
            headerHeight = headView.getMeasuredHeight();
        }
        if(footerView != null) {
            footerHeight = footerView.getMeasuredHeight();
        }
        if(headBack == null && headView != null) {
            headView.layout(l,t - headView.getMeasuredHeight(),r,t);
        } else if(headBack != null) {
            headBack.layout(l,t - headBack.getMeasuredHeight(),r,t);
        }
        mTarget = getChildAt(0);
        mTarget.layout(l,t,r,b);

        if(footerBack == null && footerView != null) {
            footerView.layout(l,b,r,b+footerView.getMeasuredHeight());
        } else if(footerBack != null){
            footerBack.layout(l,b,r,b+footerBack.getMeasuredHeight());
        }
    }
}
