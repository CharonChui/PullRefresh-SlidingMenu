package com.charon.pulltorefresh.demo;

import java.util.Arrays;
import java.util.LinkedList;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.charon.pulltorefreshlistview.LoadMoreListView;
import com.charon.pulltorefreshlistview.LoadMoreListView.OnLoadMoreListener;

public class LoadMoreActivity extends Activity {
	private LoadMoreListView mLoadMoreListView;
	private String[] mNames = { "Fabian", "Carlos", "Alex", "Andrea", "Karla",
			"Freddy", "Lazaro", "Hector", "Carolina", "Edwin", "Jhon",
			"Edelmira", "Andres" };
	private LinkedList<String> mListItems;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loadmore);
		findView();
		initView();
	}

	private void findView() {
		mLoadMoreListView = (LoadMoreListView) findViewById(R.id.lmlv);
	}

	private void initView() {
		mListItems = new LinkedList<String>();
		mListItems.addAll(Arrays.asList(mNames));

		mLoadMoreListView.setAdapter(adapter);

		mLoadMoreListView.setOnLoadMoreListener(new OnLoadMoreListener() {

			@Override
			public void onLoadMore() {
				new LoadDataTask().execute();
			}
		});
	}

	private BaseAdapter adapter = new BaseAdapter() {

		@Override
		public int getCount() {
			return mListItems.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView view = new TextView(LoadMoreActivity.this);
			view.setTextSize(20);
			view.setHeight(100);
			view.setGravity(Gravity.CENTER_VERTICAL);
			view.setText(mListItems.get(position));
			return view;
		}

	};

	private class LoadDataTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {

			if (isCancelled()) {
				return null;
			}

			SystemClock.sleep(1000);

			mListItems.add("Load More...........");

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			adapter.notifyDataSetChanged();

			mLoadMoreListView.onLoadMoreComplete();

			super.onPostExecute(result);
		}

		@Override
		protected void onCancelled() {
			mLoadMoreListView.onLoadMoreComplete();
		}
	}
}
