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
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

public class ScrollingTabs extends HorizontalScrollView implements OnPageChangeListener {

    private LinearLayout mContainer;

    private ViewPager mViewPager;

    private TabAdapter mTabAdapter;

    private int mWindowWidth;
    /**
     * True if have segmentation view between two tab view.
     */
    private boolean isUseSeperator;

    private TabClickListener mTabClickListener;

    private PageSelectedListener mPageSelectedListener;

    /**
     * The width of the tab view, usually it may be 0.
     */
    private int tabWidth;

    /**
     * True if you want to make make all the tabs equals the width when the total width is less than the window width
     */
    private boolean mEqualWidth;

    public ScrollingTabs(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public ScrollingTabs(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ScrollingTabs(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        this.setHorizontalScrollBarEnabled(false);
        this.setHorizontalFadingEdgeEnabled(false);

        mContainer = new LinearLayout(context);
        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                        android.view.ViewGroup.LayoutParams.MATCH_PARENT);
        mContainer.setLayoutParams(params);
        mContainer.setOrientation(LinearLayout.HORIZONTAL);

        addView(mContainer);

        mWindowWidth = getWindowWidth(context);
    }

    private int getWindowWidth(Context context) {
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    public void setTabAdapter(TabAdapter adapter) {
        this.mTabAdapter = adapter;
        initTabView();
    }

    public void setViewPager(ViewPager pager) {
        this.mViewPager = pager;
        mViewPager.setOnPageChangeListener(this);
        initTabView();
    }

    /**
     * Add tab view.
     */
    private void initTabView() {
        int totalWidth = 0;

        if (mViewPager != null && mTabAdapter != null) {
            mContainer.removeAllViews();

            if (mEqualWidth) {
                for (int i = 0; i < mViewPager.getAdapter().getCount(); i++) {
                    View tab = mTabAdapter.getView(i);
                    measureView(tab);
                    totalWidth += tab.getWidth();

                    if (tab.getLayoutParams() != null) {
                        // int width = tab.getLayoutParams().width;
                        // int height = tab.getLayoutParams().height;
                    } else {
                        tab.setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 100f));
                    }

                    if (mTabAdapter.getSeparator() != null && i != mViewPager.getAdapter().getCount() - 1) {
                        isUseSeperator = true;
                        measureView(mTabAdapter.getSeparator());
                        totalWidth += mTabAdapter.getSeparator().getWidth();
                    }
                }

                if (totalWidth < mWindowWidth) {
                    int count = mViewPager.getAdapter().getCount();
                    if (isUseSeperator) {
                        tabWidth = (mWindowWidth - (count - 1) * mTabAdapter.getSeparator().getWidth()) / count;
                    } else {
                        tabWidth = mWindowWidth / count;
                    }
                }
            }

            for (int i = 0; i < mViewPager.getAdapter().getCount(); i++) {
                final View tab = mTabAdapter.getView(i);
                tab.setTag(i);

                if (tabWidth != 0) {
                    mContainer.addView(tab, new LinearLayout.LayoutParams(tabWidth,
                            android.widget.LinearLayout.LayoutParams.MATCH_PARENT));
                } else {
                    mContainer.addView(tab, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT,
                            100f));
                }

                // Segmentation view
                if (mTabAdapter.getSeparator() != null && i != mViewPager.getAdapter().getCount() - 1) {
                    isUseSeperator = true;
                    mContainer.addView(mTabAdapter.getSeparator());
                }

                // Set click listener on tab.
                tab.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        int index = (Integer) tab.getTag();
                        if (mTabClickListener != null) {
                            mTabClickListener.onClick(index);
                        } else {
                            if (mViewPager.getCurrentItem() == index) {
                                selectTab(index);
                            } else {
                                // If ViewPager change the page, the listener
                                // will call selectTab method
                                mViewPager.setCurrentItem(index, true);
                            }
                        }
                    }
                });

            }

            // Set the current selected tab when first coming.
            selectTab(mViewPager.getCurrentItem());
        }
    }

    /**
     * Select the tab, and move the tab to the middle position
     * 
     * @param position Position of the tab.
     */
    private void selectTab(int position) {
        if (!isUseSeperator) {
            for (int i = 0; i < mContainer.getChildCount(); i++) {
                View tab = mContainer.getChildAt(i);
                // Make the current tab selected, others unselected.
                tab.setSelected(i == position);

                if (mTabAdapter != null) {
                    if (i == position) {
                        mTabAdapter.onTabSelected(position, mContainer);
                    } else {
                        mTabAdapter.onTabUnSelected(i, mContainer);
                    }
                }

            }
        } else {
            // pos is the position of the tab.
            for (int i = 0, pos = 0; i < mContainer.getChildCount(); i += 2, pos++) {
                View tab = mContainer.getChildAt(i);
                tab.setSelected(pos == position);

                if (mTabAdapter != null) {
                    if (pos == position) {
                        mTabAdapter.onTabSelected(i, mContainer);
                    } else {
                        mTabAdapter.onTabUnSelected(i, mContainer);
                    }
                }
            }
        }
        View selectedView = null;
        if (!isUseSeperator) {
            selectedView = mContainer.getChildAt(position);
        } else {
            selectedView = mContainer.getChildAt(position * 2);
        }

        int tabWidth = selectedView.getMeasuredWidth();
        int tabLeft = selectedView.getLeft();

        // (tabLeft + tabWidth / 2) is the distance from current tab's middle to
        // the left of the screen
        int distance = (tabLeft + tabWidth / 2) - mWindowWidth / 2;

        smoothScrollTo(distance, this.getScrollY());
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        if (changed) {
            selectTab(mViewPager.getCurrentItem());
        }
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    @Override
    public void onPageSelected(int position) {
        selectTab(position);
        if (mPageSelectedListener != null) {
            mPageSelectedListener.onPageSelected(position);
        }
    }

    /**
     * Set the tab width be all equals when all the tab width is less than the window width<br>
     * <font color=red>Note：</font>This must be used before setTabAdapter and setViewPager
     * 
     * @param flag
     */
    public void setEqualWidth(boolean flag) {
        mEqualWidth = flag;
    }

    public void setTabClickListener(TabClickListener listener) {
        this.mTabClickListener = listener;
    }

    public void setPageSelectedListener(PageSelectedListener pageSelectedListener) {
        this.mPageSelectedListener = pageSelectedListener;
    }

    public static void measureView(View view) {
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        if (lp == null) {
            lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        int childMeasureWidth = ViewGroup.getChildMeasureSpec(0, 0, lp.width);
        int childMeasureHeight;
        if (lp.height > 0) {
            childMeasureHeight = MeasureSpec.makeMeasureSpec(lp.height, MeasureSpec.EXACTLY);
        } else {
            // Measure specification mode: The parent has not imposed any
            // constraint on the child. It can be whatever size it wants.
            childMeasureHeight = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        view.measure(childMeasureWidth, childMeasureHeight);
    }

    /**
     * On click Listener of the tab.
     */
    public interface TabClickListener {
        public void onClick(int position);
    }

    /**
     * Callback when ViewPager change Pager.To avoid use setOnPageChangeListener more times will override.
     */
    public interface PageSelectedListener {
        public void onPageSelected(int position);
    }

    /**
     * Interface to get the tab view.
     * 
     * @author Charon Chui
     */
    public interface TabAdapter {
        /**
         * Tab View
         * 
         * @param position Position of the tab view.
         * @return View View of the tab.
         */
        public View getView(int position);

        /**
         * Segmentation view between two tab.
         * 
         * @return View, If don't use you this may return null.
         */
        public View getSeparator();

        /**
         * Callback when the tab is selected.
         * 
         * @param position Selected tab postion
         * @param mContainer The parent of all the tab.
         */
        public void onTabSelected(int position, ViewGroup mContainer);

        /**
         * Callback when the tab is unselected.
         * 
         * @param position Unselected tab postion
         * @param mContainer The parent of all the tab.
         */
        public void onTabUnSelected(int position, ViewGroup mContainer);
    }
}
