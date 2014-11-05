/*
 * Copyright (C) 2013 Charon Chui <charon.chui@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.charon.slidingmenu.view;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Scroller;

public class SlidingMenu extends RelativeLayout {
    private View mLeftView;
    private View mRightView;
    private View mCenterView;

    private Scroller mScroller;
    private VelocityTracker mVelocityTracker;

    private Context mContext;

    private int mLastPostionX;
    // Use this to judge if is scrolling on the Y-axis, if is this condition we
    // can not intercept the event.
    private int mLastPostionY;

    /**
     * If the left view can be showing after scrolling to right.
     */
    private boolean mCanLeftViewShow = true;

    /**
     * If the right view can be showing after scrolling to left.
     */
    private boolean mCanRightViewShow = false;

    /**
     * The state before we click the view to show the left or right view.
     */
    private boolean mCanLeftViewShowBeforeToogle = true;
    private boolean mCanRightViewShowBeforeToogle = false;

    private int mLeftViewWidth;
    private int mRightViewWidth;

    private int mWindowWidth;

    /**
     * The min value for scrolling.
     */
    private int mTouchSlop;

    /**
     * If the velocity bigger than this, it is in flying state.
     */
    private int minVelocity;

    private static final int sDuration = 500;
    private static final String TAG = "SlidingMenu";

    /**
     * Record whether have clicked the toogle, so will recover the
     * setWhickSideCanSlide state when click two times.
     */
    private boolean mLeftViewToogleClicked;
    private boolean mRightViewToogleClicked;

    /**
     * Record we click the visible part of the middle view when the left view or
     * right view is showing, to make middle view show fully when we click the
     * middle view's visible part.
     */
    private boolean mClicked;

    public SlidingMenu(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public SlidingMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SlidingMenu(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        mScroller = new Scroller(context);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        minVelocity = ViewConfiguration.get(context)
                .getScaledMinimumFlingVelocity();
        mWindowWidth = getWindowWidth(context);
    }

    /**
     * Set the three view of the SlidingMenu and the width of the left view and
     * right view.
     * 
     * @param leftView
     * @param rightView
     * @param centerView
     * @param leftViewWidth Width of the left view.
     * @param rightViewWidth Width of the right view.
     */
    public void setView(View leftView, View rightView, View centerView,
            int leftViewWidth, int rightViewWidth) {
        RelativeLayout.LayoutParams leftParams = new LayoutParams(
                (int) convertDpToPixel(leftViewWidth, mContext),
                LayoutParams.MATCH_PARENT);
        leftParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        addView(leftView, leftParams);

        RelativeLayout.LayoutParams rightParams = new LayoutParams(
                (int) convertDpToPixel(rightViewWidth, mContext),
                LayoutParams.MATCH_PARENT);
        rightParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        addView(rightView, rightParams);

        RelativeLayout.LayoutParams centerParams = new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addView(centerView, centerParams);

        mLeftView = leftView;
        mRightView = rightView;
        mCenterView = centerView;
    }

    /**
     * Set which side can be showing.
     * 
     * @param isLeftCanShow true if the left view can be showing.
     * @param isRightCanShow
     */
    public void setWhichSideCanShow(boolean isLeftCanShow,
            boolean isRightCanShow) {
        mCanLeftViewShow = isLeftCanShow;
        mCanRightViewShow = isRightCanShow;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        mLeftViewWidth = mLeftView.getWidth();
        mRightViewWidth = mRightView.getWidth();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int x = (int) ev.getRawX();
        int y = (int) ev.getRawY();

        int action = ev.getAction();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastPostionX = x;
                mLastPostionY = y;

                if (mCanLeftViewShow) {
                    mLeftView.setVisibility(View.VISIBLE);
                    mRightView.setVisibility(View.GONE);
                } else if (mCanRightViewShow) {
                    mLeftView.setVisibility(View.GONE);
                    mRightView.setVisibility(View.VISIBLE);
                }

                // If the left view is showing, we click the visible part of the
                // middle view, the middle view should not response the click
                // event,
                // but make left view invisible.
                if (isLeftViewShowing()) {
                    if (x > mLeftViewWidth) {
                        Log.e(TAG, "on intercept touch event and x > mleft view width");
                        mClicked = true;
                        return true;
                    }
                }
                if (isRightViewShowing()) {
                    if (x < (mWindowWidth - mRightViewWidth)) {
                        Log.e(TAG, "on intercept touch event and the right view is showing");
                        mClicked = true;
                        return true;
                    }
                }
                Log.e(TAG, "on intercept touch event and return false");
                return false;
                // break;
            case MotionEvent.ACTION_MOVE:
                int distance = x - mLastPostionX;
                int yDistance = Math.abs(y - mLastPostionY);
                // If the Y-axis distance is bigger than X-axis,do not intercept
                // this and parse it to the child view.
                if (Math.abs(distance) <= yDistance) {
                    Log.e(TAG, "on intercept touch event and  x < y return false");
                    return false;
                    // break;
                }

                if (mCanLeftViewShow) {
                    // scroll to right or is in scrolling to right.
                    if (distance > mTouchSlop || mCenterView.getScrollX() < 0) {
                        mLastPostionX = x;
                        return true;
                    }
                }

                if (mCanRightViewShow) {
                    if (distance < -mTouchSlop || mCenterView.getScrollX() > 0) {
                        mLastPostionX = x;
                        return true;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                
                break;

            default:
                break;
        }

        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        
        Log.e("@@@", "on touch event");
        
        int x = (int) event.getRawX();
        int y = (int) event.getRawY();

        createVelocityTracker();
        mVelocityTracker.addMovement(event);
        mVelocityTracker.computeCurrentVelocity(1000);
        float xVelocity = mVelocityTracker.getXVelocity();

        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastPostionX = x;
                mLastPostionY = y;
                // If the view is scrolling when finger down, stop scrolling.
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                
                break;
            case MotionEvent.ACTION_MOVE:
                int distance = x - mLastPostionX;
                int targetPositon = mCenterView.getScrollX() - distance;
                mLastPostionX = x;
                // Scroll to right. The distance is negative and less than the
                // width
                // of left view.
                if (mCanLeftViewShow) {
                    if (targetPositon > 0) {
                        targetPositon = 0;
                    }

                    if (targetPositon < -mLeftViewWidth) {
                        targetPositon = -mLeftViewWidth;
                    }
                }

                if (mCanRightViewShow) {
                    if (targetPositon < 0) {
                        targetPositon = 0;
                    }

                    if (targetPositon > mRightViewWidth) {
                        targetPositon = mRightViewWidth;
                    }
                }

                // If distance is 0, it is static, On some pad will occur the
                // condition when the distance is 0 but come here.
                if (distance != 0) {
                    mClicked = false;
                    mCenterView.scrollTo(targetPositon, 0);
                }

                break;
            case MotionEvent.ACTION_UP:
                if (mClicked) {
                    mClicked = false;

                    // When we click the visible part of the middle view it may
                    // close the left view, so need reset the state here.
                    if (isLeftViewShowing()) {
                        resumeLeftViewClickState();
                    } else if (isRightViewShowing()) {
                        resumeRightViewClickState();
                    }

                    smoothScrollTo(-mCenterView.getScrollX());
                    break;
                }

                int dx = 0;
                if (mCanLeftViewShow) {
                    if (xVelocity > minVelocity) {
                        // To right.
                        dx = -mLeftViewWidth - mCenterView.getScrollX();
                    } else if (xVelocity < -minVelocity) {
                        // To left.
                        dx = -mCenterView.getScrollX();
                        // If we click the show left button then slide to left
                        // to
                        // close it, we should reset the state.
                        resumeLeftViewClickState();
                    } else if (mCenterView.getScrollX() <= -mLeftViewWidth / 2) {
                        // The velocity is a very small value, if the view has
                        // already scroll half width, we should scoll it to the
                        // right.
                        dx = -mLeftViewWidth - mCenterView.getScrollX();
                    } else {
                        // Scroll to the original position.
                        dx = -mCenterView.getScrollX();

                        resumeLeftViewClickState();
                    }

                } else if (mCanRightViewShow) {
                    if (xVelocity > minVelocity) {
                        // Scroll to the original position.
                        dx = -mCenterView.getScrollX();

                        resumeRightViewClickState();
                    } else if (xVelocity < -minVelocity) {
                        // Scroll to the left, show the right view.
                        dx = mRightViewWidth - mCenterView.getScrollX();
                    } else if (mCenterView.getScrollX() >= mRightViewWidth / 2) {
                        dx = mRightViewWidth - mCenterView.getScrollX();
                    } else {
                        // Scroll to the original position.
                        dx = -mCenterView.getScrollX();

                        resumeRightViewClickState();
                    }
                }

                smoothScrollTo(dx);
                releaseVelocityTracker();
                break;

            default:
                break;
        }

        return true;
    }

    /**
     * Scroll the middle view.Scroller can scroll more smoothly.
     * 
     * @param distance Distance will scroll.
     */
    private void smoothScrollTo(int distance) {
        mScroller.startScroll(mCenterView.getScrollX(), 0, distance, 0,
                sDuration);
        invalidate();
    }

    @Override
    // Called by a parent to request that a child update its values for mScrollX
    // and mScrollY if necessary.
    // This will typically be done if the child is animating a scroll using a
    // Scroller object.
    public void computeScroll() {
        // If do not override the computeScroll method, mScroller.startScroll
        // will have no effect.
        if (mScroller.computeScrollOffset()) {
            mCenterView.scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }
    }

    /**
     * If the left view is showing fully.
     * 
     * @return true if the left view is showing.
     */
    public boolean isLeftViewShowing() {
        return mCenterView.getScrollX() == -mLeftViewWidth;
    }

    /**
     * If the right view is showing fully.
     * 
     * @return true if the right view is showing.
     */
    public boolean isRightViewShowing() {
        return mCenterView.getScrollX() == mRightViewWidth;
    }

    /**
     * If the center view is showing fully.
     * 
     * @return true if the center view is showing.
     */
    public boolean isCenterViewFullShowing() {
        return mCenterView.getScrollX() == 0;
    }

    private void createVelocityTracker() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
    }

    private void releaseVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    /**
     * Control the visibility of the left view.
     */
    public void showLeftViewToogle() {
        // Avoid we click the show left view button, but the left view is
        // invisible now.
        if (isCenterViewFullShowing()) {
            mCanLeftViewShowBeforeToogle = mCanLeftViewShow;
            mCanRightViewShowBeforeToogle = mCanRightViewShow;
            setWhichSideCanShow(true, false);
            mLeftView.setVisibility(View.VISIBLE);
            mRightView.setVisibility(View.GONE);
            mLeftViewToogleClicked = true;
        } else if (isLeftViewShowing()) {
            resumeLeftViewClickState();
        }

        if (isLeftViewShowing()) {
            smoothScrollTo(mLeftViewWidth);
        } else {
            smoothScrollTo(-mLeftViewWidth);
        }
    }

    /**
     * Control the visibility of the right view.
     */
    public void showRightViewToogle() {
        if (isCenterViewFullShowing()) {
            mCanLeftViewShowBeforeToogle = mCanLeftViewShow;
            mCanRightViewShowBeforeToogle = mCanRightViewShow;

            setWhichSideCanShow(false, true);
            mLeftView.setVisibility(View.GONE);
            mRightView.setVisibility(View.VISIBLE);
            mRightViewToogleClicked = true;
        } else if (isRightViewShowing()) {
            resumeRightViewClickState();
        }

        if (isRightViewShowing()) {
            smoothScrollTo(-mRightViewWidth);
        } else {
            smoothScrollTo(mRightViewWidth);
        }
    }

    /**
     * Resume the state to before we click the show left view button.
     */
    private void resumeLeftViewClickState() {
        if (mLeftViewToogleClicked) {
            mLeftViewToogleClicked = false;
            setWhichSideCanShow(mCanLeftViewShowBeforeToogle,
                    mCanRightViewShowBeforeToogle);
        }
    }

    private void resumeRightViewClickState() {
        if (mRightViewToogleClicked) {
            setWhichSideCanShow(mCanLeftViewShowBeforeToogle,
                    mCanRightViewShowBeforeToogle);
            mRightViewToogleClicked = false;
        }
    }

    /**
     * This method converts dp unit to equivalent pixels, depending on device
     * density.
     * 
     * @param dp A value in dp (density independent pixels) unit. Which we need
     *            to convert into pixels
     * @param context Context to get resources and device specific display
     *            metrics
     * @return A float value to represent px equivalent to dp depending on
     *         device density
     */
    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

    /**
     * This method converts device specific pixels to density independent
     * pixels.
     * 
     * @param px A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display
     *            metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static float convertPixelsToDp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return dp;
    }

    private int getWindowWidth(Context context) {
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }
}
