package com.charon.slidingmenudemo.fragment;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.charon.slidingmenudemo.MainActivity;
import com.charon.slidingmenudemo.R;

public class CenterFragment extends Fragment implements OnClickListener {
	private ViewPager mViewPager;
	private ImageView mLeftToogle;
	private ImageView mRightToogle;

	private OnViewPagerChangeListener listener;

	private MyPagerAdapter adapter;

	private ArrayList<Fragment> pagerItemList = new ArrayList<Fragment>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_center, null);
		findView(view);
		initView(view);
		return view;
	}

	private void findView(View view) {
		mViewPager = (ViewPager) view.findViewById(R.id.vp);
		mLeftToogle = (ImageView) view.findViewById(R.id.iv_center_left);
		mRightToogle = (ImageView) view.findViewById(R.id.iv_center_right);
	}

	private void initView(View view) {
		mLeftToogle.setOnClickListener(this);
		mRightToogle.setOnClickListener(this);

		pagerItemList.add(PagerFragment.getInstance(1));
		pagerItemList.add(PagerFragment.getInstance(2));
		pagerItemList.add(PagerFragment.getInstance(3));
		pagerItemList.add(PagerFragment.getInstance(4));

		adapter = new MyPagerAdapter(getFragmentManager());

		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				if (listener != null) {
					listener.onPageChage(position);
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});

		mViewPager.setAdapter(adapter);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.iv_center_left:
			((MainActivity) this.getActivity()).showLeftViewToogle();
			break;
		case R.id.iv_center_right:
			((MainActivity) this.getActivity()).showRightViewToogle();
			break;
		default:
			break;
		}
	}

	public void setOnViewPagerChangeListener(OnViewPagerChangeListener listener) {
		this.listener = listener;
	}

	public boolean isFirst() {
		return mViewPager.getCurrentItem() == 0;
	}

	public boolean isLast() {
		return mViewPager.getCurrentItem() == pagerItemList.size() - 1;
	}

	private class MyPagerAdapter extends FragmentStatePagerAdapter {

		public MyPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int arg0) {
			return pagerItemList.get(arg0);
		}

		@Override
		public int getCount() {
			return 4;
		}

	}

	/**
	 * Interface when the page have changed.
	 */
	public interface OnViewPagerChangeListener {
		void onPageChage(int position);
	}

}
