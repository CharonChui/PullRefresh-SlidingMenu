package com.charon.scrollingtabsdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity implements OnClickListener {
	private Button mScrollingTabsButton;
	private Button mTriangleScrollingTabsButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		findView();
		initView();
	}

	private void findView() {
		mScrollingTabsButton = (Button) findViewById(R.id.bt_scrollingtabs_main);
		mTriangleScrollingTabsButton = (Button) findViewById(R.id.bt_trianglescrollingtabs_main);
	}

	private void initView() {
		mScrollingTabsButton.setOnClickListener(this);
		mTriangleScrollingTabsButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.bt_scrollingtabs_main:
			startActivity(new Intent(MainActivity.this,
					ScroolingTabsActivity.class));
			break;
		case R.id.bt_trianglescrollingtabs_main:
			startActivity(new Intent(MainActivity.this,
					TriangleScroolingTabsActivity.class));
			break;
		default:
			break;
		}
	}

}
