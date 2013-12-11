package com.charon.pulltorefreshlistview;

import java.util.ArrayList;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.charon.pulltorefreshlistview.view.PullToRefreshListView;
import com.charon.pulltorefreshlistview.view.PullToRefreshListView.OnRefreshListener;

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
						SystemClock.sleep(2000);
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
			textView.setText(data.get(position));

			return textView;
		}
	}
}
