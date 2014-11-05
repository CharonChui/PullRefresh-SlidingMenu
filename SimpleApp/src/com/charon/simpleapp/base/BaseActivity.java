package com.charon.simpleapp.base;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class BaseActivity extends FragmentActivity {
	protected SharedPreferences sp;
	private final String name = "SimpleApp";

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		sp = getSharedPreferences(name, Context.MODE_PRIVATE);
	}
}
