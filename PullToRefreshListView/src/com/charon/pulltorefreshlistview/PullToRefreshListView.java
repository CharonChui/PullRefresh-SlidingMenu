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

import java.util.Date;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.charon.pulltorefreshlistview.R;

/**
 * Android pull to refresh ListView.
 */
public class PullToRefreshListView extends ListView {

	/**
	 * Avoid pull all the screen height will make header view too large, so make
	 * a scale ratio.
	 */
	private static final int RATIO = 3;

	private View mHeader;
	private ImageView iv_arrow;
	private ProgressBar pb_refresh;
	private TextView tv_title;
	private TextView tv_time;

	/**
	 * Height of the HeaderView
	 */
	private int mHeaderHeight;

	/**
	 * The Y axis position when finger onTouch down.
	 */
	private int downPositionY;

	/**
	 * The Y axis position when finger onTouch moving
	 */
	private int currentPositionY;

	/**
	 * The pull distance.
	 */
	private int pullDistance;

	/**
	 * Current State
	 */
	private State mState;

	/**
	 * Animation for the arrow when is moving clockwise.
	 */
	private Animation animation;

	/**
	 * Animation for the arrow when is moving anticlockwise.
	 */
	private Animation reverseAnimation;

	/**
	 * If can pull now
	 */
	private boolean isCanPullToRefresh;

	private OnRefreshListener mOnRefreshListener;

	/**
	 * If you pull down but not release, your finger move up, this time we
	 * should play a reverse Animation.
	 */
	private boolean isBack;

	private OnScrollListener mOnScrollListener;

	public PullToRefreshListView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	public PullToRefreshListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public PullToRefreshListView(Context context) {
		super(context);
		initView(context);
	}

	private void initView(Context context) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		mHeader = inflater.inflate(R.layout.pull_to_refresh_header, null);
		iv_arrow = (ImageView) mHeader.findViewById(R.id.iv_arrow);
		pb_refresh = (ProgressBar) mHeader.findViewById(R.id.pb_refresh);
		tv_title = (TextView) mHeader.findViewById(R.id.tv_title);
		tv_time = (TextView) mHeader.findViewById(R.id.tv_time);

		measureHeaderView(mHeader);

		mHeaderHeight = mHeader.getMeasuredHeight();
		// To make header view above the window, so use -mHeaderHeight.
		mHeader.setPadding(0, -mHeaderHeight, 0, 0);

		mHeader.invalidate();

		addHeaderView(mHeader);

		mState = State.ORIGNAL;

		super.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// Avoid override when use setOnScrollListener
				if (mOnScrollListener != null) {
					mOnScrollListener.onScrollStateChanged(view, scrollState);
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {

				if (mOnScrollListener != null) {
					mOnScrollListener.onScroll(view, firstVisibleItem,
							visibleItemCount, totalItemCount);
				}

				if (firstVisibleItem == 0) {
					isCanPullToRefresh = true;
				} else {
					isCanPullToRefresh = false;
				}
			}
		});

		animation = new RotateAnimation(-180, 0, Animation.RELATIVE_TO_SELF,
				0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		animation.setDuration(250);
		animation.setFillAfter(true);
		animation.setInterpolator(new LinearInterpolator());

		reverseAnimation = new RotateAnimation(0, -180,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		reverseAnimation.setDuration(200);
		reverseAnimation.setFillAfter(true);
		reverseAnimation.setInterpolator(new LinearInterpolator());
	}

	/**
	 * Measure the height of the view will be when showing.
	 * 
	 * @param view
	 *            View to measure.
	 */
	private void measureHeaderView(View view) {
		ViewGroup.LayoutParams lp = view.getLayoutParams();
		if (lp == null) {
			lp = new ViewGroup.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}

		int childMeasureWidth = ViewGroup.getChildMeasureSpec(0, 0, lp.width);
		int childMeasureHeight;
		if (lp.height > 0) {
			childMeasureHeight = MeasureSpec.makeMeasureSpec(lp.height,
					MeasureSpec.EXACTLY);
		} else {
			// Measure specification mode: The parent has not imposed any
			// constraint on the child. It can be whatever size it wants.
			childMeasureHeight = MeasureSpec.makeMeasureSpec(0,
					MeasureSpec.UNSPECIFIED);
		}
		view.measure(childMeasureWidth, childMeasureHeight);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		int y = (int) ev.getRawY();
		int action = ev.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			downPositionY = y;
			break;
		case MotionEvent.ACTION_MOVE:
			if (!isCanPullToRefresh) {
				break;
			}
			currentPositionY = y;

			pullDistance = (currentPositionY - downPositionY) / RATIO;

			if (mState == State.REFRESHING) {
				break;
			} else if (mState == State.ORIGNAL && pullDistance > 0) {
				mState = State.PULL_TO_REFRESH;
				changeState();
			} else if (mState == State.PULL_TO_REFRESH
					&& pullDistance > mHeaderHeight) {
				mState = State.RELEASE_TO_REFRESH;
				changeState();
			} else if (mState == State.RELEASE_TO_REFRESH) {
				if (pullDistance < 0) {
					// invisible
					mState = State.ORIGNAL;
					changeState();
				} else if (pullDistance < mHeaderHeight) {
					mState = State.PULL_TO_REFRESH;
					isBack = true;
					changeState();
				}

			}

			if (mState != State.REFRESHING) {
				mHeader.setPadding(0, (int) (pullDistance - mHeaderHeight), 0,
						0);
			}

			break;
		case MotionEvent.ACTION_UP:
			if (mState == State.REFRESHING) {
				break;
			} else if (mState == State.PULL_TO_REFRESH) {
				mState = State.ORIGNAL;
			} else if (mState == State.RELEASE_TO_REFRESH) {
				mState = State.REFRESHING;
			} else {
				break;
			}
			changeState();
			break;

		default:
			break;
		}

		return super.onTouchEvent(ev);
	}

	/**
	 * Change the state of header view when ListView in different state.
	 */
	private void changeState() {
		if (mState == State.ORIGNAL) {
			iv_arrow.setVisibility(View.VISIBLE);
			pb_refresh.setVisibility(View.GONE);
			tv_time.setVisibility(View.VISIBLE);
			tv_title.setVisibility(View.VISIBLE);
			iv_arrow.clearAnimation();
			mHeader.setPadding(0, -mHeaderHeight, 0, 0);
		} else if (mState == State.PULL_TO_REFRESH) {
			iv_arrow.setVisibility(View.VISIBLE);
			pb_refresh.setVisibility(View.GONE);
			tv_title.setVisibility(View.VISIBLE);
			tv_time.setVisibility(View.VISIBLE);

			iv_arrow.clearAnimation();
			tv_title.setText(getResources().getString(R.string.pull_refresh));

			if (isBack) {
				// Come from release to refresh to pull to refresh
				iv_arrow.startAnimation(animation);
				isBack = false;
			}
		} else if (mState == State.RELEASE_TO_REFRESH) {
			iv_arrow.setVisibility(View.VISIBLE);
			pb_refresh.setVisibility(View.GONE);
			tv_title.setVisibility(View.VISIBLE);
			tv_time.setVisibility(View.VISIBLE);

			iv_arrow.clearAnimation();
			tv_time.setText(getResources().getString(
					R.string.release_to_refresh));

			iv_arrow.startAnimation(reverseAnimation);
		} else if (mState == State.REFRESHING) {
			iv_arrow.setVisibility(View.GONE);
			pb_refresh.setVisibility(View.VISIBLE);
			tv_title.setVisibility(View.VISIBLE);
			tv_time.setVisibility(View.VISIBLE);

			iv_arrow.clearAnimation();
			tv_title.setText(getResources().getString(R.string.refreshing));

			mHeader.setPadding(0, 0, 0, 0);

			if (mOnRefreshListener != null) {
				mOnRefreshListener.onRefresh();
			}
		}

	}

	/**
	 * When complete refresh data, you must use this method to hide the header
	 * view, if not the header view will be shown all the time.
	 */
	@SuppressWarnings("deprecation")
	public void onRefreshComplete() {
		mState = State.ORIGNAL;
		changeState();
		tv_time.setText(getResources().getString(R.string.update_time)
				+ new Date().toLocaleString());
	}

	@SuppressWarnings("deprecation")
	@Override
	public void setAdapter(ListAdapter adapter) {
		tv_time.setText(getResources().getString(R.string.update_time)
				+ new Date().toLocaleString());
		super.setAdapter(adapter);
	}

	@Override
	public void setOnScrollListener(OnScrollListener l) {
		this.mOnScrollListener = l;
	}

	/**
	 * Set refresh data listener.
	 * 
	 * @param listener
	 *            OnRefreshListener
	 */
	public void setOnRefreshListener(OnRefreshListener listener) {
		this.mOnRefreshListener = listener;
	}

	/**
	 * All the state of ListView
	 */
	public enum State {
		ORIGNAL, PULL_TO_REFRESH, REFRESHING, RELEASE_TO_REFRESH;
	}

	/**
	 * Interface when refresh data.
	 */
	public interface OnRefreshListener {
		abstract void onRefresh();
	}

}
