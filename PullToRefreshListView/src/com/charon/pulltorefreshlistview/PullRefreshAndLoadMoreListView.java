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

package com.charon.pulltorefreshlistview;

import com.charon.pulltorefreshlistview.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;

/**
 * Android load more ListView when scroll down.
 * 
 * @author Charon Chui
 */
public class PullRefreshAndLoadMoreListView extends PullToRefreshListView {
    protected static final String TAG = "LoadMoreListView";
    private View mFooterView;
    private OnScrollListener mOnScrollListener;
    private OnLoadMoreListener mOnLoadMoreListener;

    /**
     * If is loading now.
     */
    private boolean mIsLoading;

    private int mCurrentScrollState;

    public PullRefreshAndLoadMoreListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public PullRefreshAndLoadMoreListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PullRefreshAndLoadMoreListView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        mFooterView = View.inflate(context, R.layout.load_more_footer, null);
        addFooterView(mFooterView);
        hideFooterView();
        /*
         * Must use super.setOnScrollListener() here to avoid override when call this view's setOnScrollListener method
         */
        super.setOnScrollListener(superOnScrollListener);
    }

    /**
     * Hide the load more view(footer view)
     */
    private void hideFooterView() {
        mFooterView.setVisibility(View.GONE);
    }

    /**
     * Show load more view
     */
    private void showFooterView() {
        mFooterView.setVisibility(View.VISIBLE);
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
        hideFooterView();
    }

    private OnScrollListener superOnScrollListener = new OnScrollListener() {

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
                hideFooterView();
            } else if (!mIsLoading && (firstVisibleItem + visibleItemCount >= totalItemCount)
                    && mCurrentScrollState != SCROLL_STATE_IDLE) {
                showFooterView();
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
