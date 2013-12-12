package com.charon.pulltorefresh.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity implements OnClickListener {
	private Button bt_pull_to_refresh;
	private Button bt_load_more;
	private Button bt_pull_and_loadmore;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		findView();
	}

	private void findView() {
		bt_pull_to_refresh = (Button) findViewById(R.id.bt_pull_to_refresh);
		bt_load_more = (Button) findViewById(R.id.bt_load_more);
		bt_pull_and_loadmore = (Button) findViewById(R.id.bt_pull_and_loadmore);
		bt_pull_to_refresh.setOnClickListener(this);
		bt_load_more.setOnClickListener(this);
		bt_pull_and_loadmore.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_pull_to_refresh:
			startActivity(new Intent(this, PullToRefreshActivity.class));
			break;
		case R.id.bt_load_more:
			startActivity(new Intent(this, LoadMoreActivity.class));
			break;
		case R.id.bt_pull_and_loadmore:
			startActivity(new Intent(this, PullAndLoadMoreActivity.class));
			break;

		default:
			break;
		}
	}
}
