package com.charon.pulltorefresh.demo;

import java.util.ArrayList;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.charon.pulltorefreshlistview.PullToRefreshListView;
import com.charon.pulltorefreshlistview.PullToRefreshListView.OnRefreshListener;

public class PullToRefreshActivity extends Activity {
	private PullToRefreshListView mPullToRefreshListView;
	private MyAdapter adapter;
	private ArrayList<String> data;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pulltorefresh);
		findView();
		initView();
	}

	private void findView() {
		mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.ptlv);
	}

	private void initView() {

		data = new ArrayList<String>();
		data.add("a");
		data.add("b");
		data.add("c");

		adapter = new MyAdapter();

		mPullToRefreshListView.setAdapter(adapter);

		mPullToRefreshListView.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				new AsyncTask<Void, Void, Void>() {

					@Override
					protected Void doInBackground(Void... params) {
						SystemClock.sleep(1000);
						data.add("add after refresh");
						return null;
					}

					@Override
					protected void onPostExecute(Void result) {
						super.onPostExecute(result);
						adapter.notifyDataSetChanged();
						mPullToRefreshListView.onRefreshComplete();
					}
				}.execute();
			}
		});

		mPullToRefreshListView
				.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// If you call addHeaderView, the position will contains
						// the count of header view.
						// int realPosition = position
						// - mPullToRefreshListView.getHeaderViewsCount();
						// Toast.makeText(PullToRefreshActivity.this,
						// data.get(realPosition), Toast.LENGTH_SHORT).show();

						Toast.makeText(
								PullToRefreshActivity.this,
								(String) mPullToRefreshListView.getAdapter()
										.getItem(position), Toast.LENGTH_SHORT)
								.show();

					}

				});

	}

	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return data.size();
		}

		@Override
		public Object getItem(int position) {
			return data.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView textView = new TextView(getApplicationContext());
			textView.setTextSize(20);
			textView.setText(data.get(position));

			return textView;
		}
	}
}
