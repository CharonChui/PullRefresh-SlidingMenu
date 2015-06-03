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

package com.charon.scrollingtabs.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class TriangleScrollingTabs extends ScrollingTabs {
	public static final int mTriangleWidth = 50;
	public static final int mTriangleHeight = 25;
	public static final int STROKE_COLOR = Color.parseColor("#ff0000");
	public static final int FILL_COLOR = Color.parseColor("#00ff00");

	private Paint mStrokePaint;
	private Paint mFillPaint;
	private Path mStrokePath;
	private Path mFillPath;

	private int mTriangleStartPosition;
	private int mTriangleCurrentPosition;

	public TriangleScrollingTabs(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		initPaint();
	}

	public TriangleScrollingTabs(Context context, AttributeSet attrs) {
		super(context, attrs);
		initPaint();
	}

	public TriangleScrollingTabs(Context context) {
		super(context);
		initPaint();
	}

	protected void initPaint() {
		mStrokePaint = new Paint();
		mStrokePaint.setAntiAlias(true);
		mStrokePaint.setColor(STROKE_COLOR);
		mStrokePaint.setStyle(Style.STROKE);
		mStrokePaint.setStrokeWidth(5);
		mStrokePaint.setPathEffect(new CornerPathEffect(3));

		mFillPaint = new Paint();
		mFillPaint.setAntiAlias(true);
		mFillPaint.setColor(FILL_COLOR);
		mFillPaint.setStyle(Style.FILL);
		mFillPaint.setPathEffect(new CornerPathEffect(3));

		mStrokePath = new Path();
		mStrokePath.moveTo(0, 0);

		mStrokePath.lineTo(mTriangleWidth / 2, -mTriangleHeight);
		mStrokePath.lineTo(mTriangleWidth, 0);
		mStrokePath.close();

		mFillPath = new Path();
		mFillPath.moveTo(0, 5);

		mFillPath.lineTo(mTriangleWidth / 2, -mTriangleHeight + 5);
		mFillPath.lineTo(mTriangleWidth - 5, 0);
		mFillPath.close();
	}

	/**
	 * Select the tab, and move the tab to the middle position
	 * 
	 * @param position
	 *            Position of the tab.
	 */
	protected void selectTab(int position) {
		super.selectTab(position);

		View selectedView = getSelectedView(position);
		if (selectedView != null) {
			int width = selectedView.getMeasuredWidth();
			if (width != 0) {
				tabWidth = width;
			}
			if (tabWidth !=0 && mTriangleStartPosition == 0) {
				mTriangleStartPosition = (int) ( tabWidth/2 - mTriangleWidth/2);
			}
			Log.e("@@@", "selectTab : " + mTriangleStartPosition);
		}
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		drawTriangle(arg0, arg1);
	}

	private void drawTriangle(int position, float offset) {
		int distance = (int) (tabWidth * (offset + position));
		 
		if (distance != 0) {
			mTriangleCurrentPosition = distance;
		}
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (mTriangleStartPosition != 0) {
			canvas.save();
			canvas.translate(mTriangleStartPosition + mTriangleCurrentPosition, getHeight());
			
			Log.e("@@@", "onDraw : " + mTriangleStartPosition + "currentPosition : " + mTriangleCurrentPosition);
			canvas.drawPath(mStrokePath, mStrokePaint);
			canvas.drawPath(mFillPath, mFillPaint);
			canvas.restore();
		}
	}

}
