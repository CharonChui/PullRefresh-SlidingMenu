package com.charon.simpleapp.fragment;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.charon.scrollingtabs.view.ScrollingTabs;
import com.charon.scrollingtabs.view.ScrollingTabs.PageSelectedListener;
import com.charon.scrollingtabs.view.ScrollingTabs.TabAdapter;
import com.charon.simpleapp.MainActivity;
import com.charon.simpleapp.R;

public class CenterFragment extends Fragment implements OnClickListener {
	private ViewPager mViewPager;
	private ImageView mLeftToogle;
	private ImageView mRightToogle;
	private ScrollingTabs mScrollingTabsView;

	private OnViewPagerChangeListener listener;

	private MyPagerAdapter adapter;
	private MyTabAdapter mTabAdapter;

	private List<String> tabs;

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
		mScrollingTabsView = (ScrollingTabs) view.findViewById(R.id.stv);
	}

	private void initView(View view) {
		mLeftToogle.setOnClickListener(this);
		mRightToogle.setOnClickListener(this);

		pagerItemList.add(PagerFragment.getInstance(1));
		pagerItemList.add(PagerFragment.getInstance(2));
		pagerItemList.add(PagerFragment.getInstance(3));
		pagerItemList.add(PagerFragment.getInstance(4));
		pagerItemList.add(PagerFragment.getInstance(5));
		pagerItemList.add(PagerFragment.getInstance(6));

		adapter = new MyPagerAdapter(getFragmentManager());

		mViewPager.setAdapter(adapter);

		// 这样会有问题，在ScrollingTabsView中也会对ViewPager设置onPageChangeListener会导致覆盖
		// mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
		//
		// @Override
		// public void onPageSelected(int position) {
		// listener.onPageChage(position);
		// }
		//
		// @Override
		// public void onPageScrolled(int arg0, float arg1, int arg2) {
		//
		// }
		//
		// @Override
		// public void onPageScrollStateChanged(int arg0) {
		//
		// }
		// });

		// 对ScrollingTabsView进行初始化设置
		tabs = new ArrayList<String>();
		tabs.add("新闻");
		tabs.add("趣事");
		tabs.add("微博");
		tabs.add("秘密");
		tabs.add("军事");
		tabs.add("交友");

		mTabAdapter = new MyTabAdapter();
		mScrollingTabsView.setTabAdapter(mTabAdapter);
		mScrollingTabsView.setViewPager(mViewPager);

		// 解决上面对同一个ViewPager设置多次setonPageChangeListener导致覆盖的问题。我们通过这个回调方法来得到ScrollingTabsView中
		// 设置的ViewPageChangeListener中的onPageSelected方法,这样就解决了覆盖的问题
		mScrollingTabsView.setPageSelectedListener(new PageSelectedListener() {

			@Override
			public void onPageSelected(int position) {
				listener.onPageChage(position);
			}
		});

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
			return pagerItemList.size();
		}

	}

	private class MyTabAdapter implements TabAdapter {

		@Override
		public View getView(int position) {
			View view = View.inflate(CenterFragment.this.getActivity(),
					R.layout.tab_center, null);
			TextView tv = (TextView) view.findViewById(R.id.tv_tab_center);
			tv.setText(tabs.get(position));
			return view;
		}

		@Override
		public View getSeparator() {
			return null;
		}

        @Override
        public void onTabSelected(int position, ViewGroup mContainer) {
            
        }

        @Override
        public void onTabUnSelected(int position, ViewGroup mContainer) {
            
        }
	}

	/**
	 * 当ViewPager中发生页面改变后的回调
	 */
	public interface OnViewPagerChangeListener {
		void onPageChage(int position);
	}
}
