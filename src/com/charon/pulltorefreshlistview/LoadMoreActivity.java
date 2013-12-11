package com.charon.pulltorefreshlistview;

import java.util.Arrays;
import java.util.LinkedList;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.charon.pulltorefreshlistview.view.LoadMoreListView;
import com.charon.pulltorefreshlistview.view.LoadMoreListView.OnLoadMoreListener;

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
			TextView view = (TextView) View.inflate(LoadMoreActivity.this,
					android.R.layout.simple_list_item_1, null);
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

			mLoadMoreListView.onLoadMoreComplete();

			super.onPostExecute(result);
		}

		@Override
		protected void onCancelled() {
			mLoadMoreListView.onLoadMoreComplete();
		}
	}
}
