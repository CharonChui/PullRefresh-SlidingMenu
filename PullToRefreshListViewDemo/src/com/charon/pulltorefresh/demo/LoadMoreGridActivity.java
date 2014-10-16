
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

import com.charon.pulltorefreshlistview.LoadMoreGridView;
import com.charon.pulltorefreshlistview.LoadMoreGridView.OnLoadMoreListener;

public class LoadMoreGridActivity extends Activity {
    private LoadMoreGridView mGridView;
    private View mAutoLoadView;
    private String[] mNames = {
            "Fabian", "Carlos", "Alex", "Andrea", "Karla",
            "Freddy", "Lazaro", "Hector", "Carolina", "Edwin", "Jhon",
            "Edelmira", "Andres", "Fabian", "Carlos", "Alex", "Andrea", "Karla",
            "Freddy", "Lazaro", "Hector", "Carolina", "Edwin", "Jhon",
            "Edelmira", "Andres"
    };
    private LinkedList<String> mListItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loadmoregrid);
        findView();
        initView();
    }

    private void findView() {
        mGridView = (LoadMoreGridView) findViewById(R.id.lmgv);
        mAutoLoadView = findViewById(R.id.auto_load_view);
    }

    private void initView() {
        mListItems = new LinkedList<String>();
        mListItems.addAll(Arrays.asList(mNames));

        mGridView.setAdapter(adapter);

        mGridView.setOnLoadMoreListener(new OnLoadMoreListener() {

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
            TextView view = new TextView(LoadMoreGridActivity.this);
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
            mListItems.add("Load More...........");
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (mAutoLoadView != null) {
                mAutoLoadView.setVisibility(View.GONE);
            }
            adapter.notifyDataSetChanged();
            mGridView.onLoadMoreComplete();
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            if (mAutoLoadView != null) {
                mAutoLoadView.setVisibility(View.VISIBLE);
            }
            super.onPreExecute();
        }

        @Override
        protected void onCancelled() {
            mGridView.onLoadMoreComplete();
        }
    }
}
