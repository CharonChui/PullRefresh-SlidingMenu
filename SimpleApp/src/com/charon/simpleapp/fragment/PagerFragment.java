package com.charon.simpleapp.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.charon.pulltorefreshlistview.PullToRefreshListView;
import com.charon.pulltorefreshlistview.PullToRefreshListView.OnRefreshListener;
import com.charon.simpleapp.R;

/**
 * ViewPager中每个页面对应的Fragment
 */
public class PagerFragment extends Fragment {
	private int index;
	private PullToRefreshListView mPullToRefreshListView;

	private Handler handler = new Handler();

	public PagerFragment() {
		super();
	}

	public static PagerFragment getInstance(int index) {
		PagerFragment fragment = new PagerFragment();
		fragment.setIndex(index);
		return fragment;
	}

	private void setIndex(int index) {
		this.index = index;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.lv_pager_center, null);
		findView(view);
		initView(view);
		return view;
	}

	private void findView(View view) {
		mPullToRefreshListView = (PullToRefreshListView) view
				.findViewById(R.id.ptrlv_pager);

	}

	private void initView(View view) {
		mPullToRefreshListView.setAdapter(new BaseAdapter() {

			private ViewHolder holder;

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				if (convertView == null) {
					convertView = View.inflate(
							PagerFragment.this.getActivity(),
							R.layout.item_lv_center, null);
					TextView tv = (TextView) convertView
							.findViewById(R.id.tv_item_lv);
					holder = new ViewHolder();
					holder.tv_item_lv = tv;
					convertView.setTag(holder);
				} else {
					holder = (ViewHolder) convertView.getTag();
				}

				holder.tv_item_lv.setText(index + ":::" + position);
				return convertView;
			}

			@Override
			public long getItemId(int position) {
				return 0;
			}

			@Override
			public Object getItem(int position) {
				return null;
			}

			@Override
			public int getCount() {
				return 50;
			}
		});
		mPullToRefreshListView.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				new Thread() {
					@Override
					public void run() {
						super.run();
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						handler.post(new Runnable() {

							@Override
							public void run() {
								mPullToRefreshListView.onRefreshComplete();
							}
						});
					}
				}.start();
			}
		});

	}

	static class ViewHolder {
		private TextView tv_item_lv;
	}
}
