package com.charon.pulltorefreshlistview;

import java.util.Arrays;
import java.util.LinkedList;

import com.charon.pulltorefreshlistview.view.PullRefreshAndLoadMoreListView;
import com.charon.pulltorefreshlistview.view.PullRefreshAndLoadMoreListView.OnLoadMoreListener;
import com.charon.pulltorefreshlistview.view.PullRefreshAndLoadMoreListView.OnRefreshListener;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class PullAndLoadMoreActivity extends Activity {
	private PullRefreshAndLoadMoreListView mPullRefreshAndLoadMoreListView;
	private MyAdapter adapter;

	private String[] mNames = { "Fabian", "Carlos", "Alex", "Andrea", "Karla",
			"Freddy", "Lazaro", "Hector", "Carolina", "Edwin", "Jhon",
			"Edelmira", "Andres" };
	private LinkedList<String> mListItems;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pull_and_loadmore);
		findView();
		initView();
	}

	private void findView() {
		mPullRefreshAndLoadMoreListView = (PullRefreshAndLoadMoreListView) findViewById(R.id.palmlv);
	}

	private void initView() {
		mListItems = new LinkedList<String>();
		mListItems.addAll(Arrays.asList(mNames));

		adapter = new MyAdapter();

		mPullRefreshAndLoadMoreListView.setAdapter(adapter);

		mPullRefreshAndLoadMoreListView
				.setOnRefreshListener(new OnRefreshListener() {

					@Override
					public void onRefresh() {
						new AsyncTask<Void, Void, Void>() {

							@Override
							protected Void doInBackground(Void... params) {
								SystemClock.sleep(2000);
								mListItems.add("add after refresh");
								return null;
							}

							@Override
							protected void onPostExecute(Void result) {
								super.onPostExecute(result);
								adapter.notifyDataSetChanged();
								mPullRefreshAndLoadMoreListView
										.onRefreshComplete();
							}
						}.execute();
					}
				});

		mPullRefreshAndLoadMoreListView.setAdapter(adapter);

		mPullRefreshAndLoadMoreListView
				.setOnLoadMoreListener(new OnLoadMoreListener() {

					@Override
					public void onLoadMore() {
						new LoadDataTask().execute();
					}
				});

	}

	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mListItems.size();
		}

		@Override
		public Object getItem(int position) {
			return mListItems.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView textView = new TextView(getApplicationContext());
			textView.setText(mListItems.get(position));

			return textView;
		}
	}

	private class LoadDataTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {

			if (isCancelled()) {
				return null;
			}

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}

			for (int i = 0; i < mNames.length; i++)
				mListItems.add(mNames[i]);

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			mListItems.add("Added after load more");

			adapter.notifyDataSetChanged();

			mPullRefreshAndLoadMoreListView.onLoadMoreComplete();

			super.onPostExecute(result);
		}

		@Override
		protected void onCancelled() {
			mPullRefreshAndLoadMoreListView.onLoadMoreComplete();
		}
	}
}
