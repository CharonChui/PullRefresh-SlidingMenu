PullToRefreshListView
=====================

A customizable ListView implementation that has 'Pull to Refresh' functionality. This ListView can be used as a replacement of the normal Android android.widget.ListView class.

Usage
---

Check out the [example project](https://github.com/CharonChui/PullToRefreshListView) 
in this repository for an implementation example. 

1. Use PullToRefreshListView in your layout
```xml
<com.charon.pulltorefreshlistview.PullToRefreshListView
	android:id="@+id/ptlv"
	android:layout_width="match_parent"
	android:layout_height="match_parent" />
```

2. Setup in your Activity
```java
mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.ptlv);
mPullToRefreshListView.setAdapter(adapter);
mPullToRefreshListView.setOnRefreshListener(new OnRefreshListener() {

	@Override
	public void onRefresh() {
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				//TODO Refresh data.
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				adapter.notifyDataSetChanged();
				//Do not forget use this method after refreshed data.
				mPullToRefreshListView.onRefreshComplete();
			
	}
});
```

Author
===

- 邮箱 ：charon.chui@gmail.com
- Good Luck! 

License
===

```                            
Copyright (C) 2013 Charon Chui <charon.chui@gmail.com>

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```