package com.charon.pulltorefreshlistview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AbsListView;
import android.widget.GridView;

/**
 * Android load more GridView when scroll down.
 * 
 * @author CharonChui
 */
public class LoadMoreGridView extends GridView {
    private OnScrollListener mOnScrollListener;
    private OnLoadMoreListener mOnLoadMoreListener;

    /**
     * If is loading now.
     */
    private boolean mIsLoading;

    private int mCurrentScrollState;

    public LoadMoreGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public LoadMoreGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LoadMoreGridView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        /*
         * Must use super.setOnScrollListener() here to avoid override when call this view's setOnScrollListener method
         */
        super.setOnScrollListener(mSuperOnScrollListener);
    }

    @Override
    public void setOnScrollListener(OnScrollListener l) {
        mOnScrollListener = l;
    }

    /**
     * Set load more listener, usually you should get more data here.
     * 
     * @param listener OnLoadMoreListener
     * @see OnLoadMoreListener
     */
    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        mOnLoadMoreListener = listener;
    }

    /**
     * When complete load more data, you must use this method to hide the footer view, if not the footer view will be
     * shown all the time.
     */
    public void onLoadMoreComplete() {
        mIsLoading = false;
    }

    private OnScrollListener mSuperOnScrollListener = new OnScrollListener() {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            mCurrentScrollState = scrollState;
            // Avoid override when use setOnScrollListener
            if (mOnScrollListener != null) {
                mOnScrollListener.onScrollStateChanged(view, scrollState);
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (mOnScrollListener != null) {
                mOnScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
            }
            // The count of footer view will be add to visibleItemCount also are
            // added to totalItemCount
            if (visibleItemCount == totalItemCount) {
                // If all the item can not fill screen, we should make the
                // footer view invisible.
            } else if (!mIsLoading && (firstVisibleItem + visibleItemCount >= totalItemCount)
                    && mCurrentScrollState != SCROLL_STATE_IDLE) {
                mIsLoading = true;
                if (mOnLoadMoreListener != null) {
                    mOnLoadMoreListener.onLoadMore();
                }
            }
        }
    };

    /**
     * Interface for load more
     */
    public interface OnLoadMoreListener {
        /**
         * Load more data.
         */
        void onLoadMore();
    }

}
