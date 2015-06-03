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
import android.view.View;

public class TriangleScrollingTabs extends ScrollingTabs {
	public static final int TRIANGLE_WIDHT = 50;
	public static final int TRIANGLE_HEIGHT = 25;
	public static final int STROKE_WIDHT = 5;
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
		mStrokePaint = getPaint(STROKE_COLOR, Style.STROKE);
		mFillPaint = getPaint(FILL_COLOR, Style.FILL);

		mStrokePath = getPath(0, 0, TRIANGLE_WIDHT / 2, -TRIANGLE_HEIGHT,
				TRIANGLE_WIDHT, 0);
		mFillPath = getPath(0, STROKE_WIDHT, TRIANGLE_WIDHT / 2,
				-TRIANGLE_HEIGHT + STROKE_WIDHT, TRIANGLE_WIDHT - STROKE_WIDHT,
				0);
	}

	public Paint getPaint(int color, Style style) {
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(color);
		paint.setStyle(style);
		paint.setStrokeWidth(STROKE_WIDHT);
		paint.setPathEffect(new CornerPathEffect(3));
		return paint;
	}

	public Path getPath(int leftX, int leftY, int topX, int topY, int rightX,
			int rightY) {
		Path path = new Path();
		path.moveTo(leftX, leftY);
		path.lineTo(topX, topY);
		path.lineTo(rightX, rightY);
		path.close();
		return path;
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
			if (tabWidth != 0 && mTriangleStartPosition == 0) {
				mTriangleStartPosition = (int) (tabWidth / 2 - TRIANGLE_WIDHT / 2);
			}
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
			canvas.translate(mTriangleStartPosition + mTriangleCurrentPosition,
					getHeight());
			canvas.drawPath(mStrokePath, mStrokePaint);
			canvas.drawPath(mFillPath, mFillPaint);
			canvas.restore();
		}
	}

}
