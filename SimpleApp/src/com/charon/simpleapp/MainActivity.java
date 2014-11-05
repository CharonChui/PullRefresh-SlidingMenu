package com.charon.simpleapp;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.charon.framework.util.LogUtil;
import com.charon.framework.util.ShortCutUtils;
import com.charon.simpleapp.base.BaseActivity;
import com.charon.simpleapp.fragment.CenterFragment;
import com.charon.simpleapp.fragment.CenterFragment.OnViewPagerChangeListener;
import com.charon.simpleapp.fragment.LeftFragment;
import com.charon.simpleapp.fragment.RightFragment;
import com.charon.slidingmenu.view.SlidingMenu;

/**
 * 首页，包含了一个SlidingMenu
 */
public class MainActivity extends BaseActivity {
	private static final String TAG = "MainActivity";
	private SlidingMenu mSlidingMenu;
	private LeftFragment mLeftFragment;
	private RightFragment mRightFragment;
	private CenterFragment mCenterFragment;
	private final String ISFIRSTUSE = "isFirstUse";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		findView();
		initView();
		addShortCut();
	}

	private void findView() {
		mSlidingMenu = (SlidingMenu) findViewById(R.id.sm);
	}

	private void initView() {
		View leftView = View.inflate(this, R.layout.frame_left, null);
		View rightView = View.inflate(this, R.layout.frame_right, null);
		View centerView = View.inflate(this, R.layout.frame_center, null);
		LogUtil.e(TAG, "slidingMenu:::" + (mSlidingMenu == null));
		LogUtil.e(TAG, "leftView:::" + (leftView == null));
		LogUtil.e(TAG, "rightView:::" + (rightView == null));
		// 设置SlidingMenu左、中、右三个子View
		mSlidingMenu.setView(leftView, rightView, centerView, 200, 250);

		// 将SlidingMenu中左、中、右三个View替换成相应的Fragment
		FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();
		mLeftFragment = new LeftFragment();
		transaction.replace(R.id.fl_left, mLeftFragment);
		mRightFragment = new RightFragment();
		transaction.replace(R.id.fl_right, mRightFragment);
		mCenterFragment = new CenterFragment();
		transaction.replace(R.id.fl_center, mCenterFragment);
		transaction.commit();

		// 给中间视图的Fragment设置ViewPager发生切换时的监听
		mCenterFragment
				.setOnViewPagerChangeListener(new OnViewPagerChangeListener() {

					@Override
					public void onPageChage(int position) {
						if (mCenterFragment.isFirst()) {
							mSlidingMenu.setWhichSideCanShow(true, false);
						} else if (mCenterFragment.isLast()) {
							mSlidingMenu.setWhichSideCanShow(false, true);
						} else {
							mSlidingMenu.setWhichSideCanShow(false, false);
						}
					}
				});
	}

	private void addShortCut() {
		boolean isFirstUse = sp.getBoolean(ISFIRSTUSE, true);
		if (isFirstUse) {
			ShortCutUtils.addShortcut(this, R.drawable.ic_launcher);
			sp.edit().putBoolean(ISFIRSTUSE, false).commit();
		}
	}

	// 暴露出显示左边的控制开关供Fragment使用
	public void showLeftViewToogle() {
		mSlidingMenu.showLeftViewToogle();
	}

	public void showRightViewToogle() {
		mSlidingMenu.showRightViewToogle();
	}

}
