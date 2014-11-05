PullRefresh&SlidingMenu
===

This has three parts, include PullToRefreshListView、 SlidingMenu、 ScrollingTabs.

                 
PullToRefreshListView
===

A customizable ListView implementation that has 'Pull to Refresh' functionality. This ListView can be used as a replacement of the normal Android android.widget.ListView class.

![image](https://raw.githubusercontent.com/CharonChui/AndroidNote/master/Pic/pullrefresh.gif)    

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

SlidingMenu
===

SlidingMenu with left and right menu,This is an original component demo, does not rely on any third-party lib library.

![image](https://raw.githubusercontent.com/CharonChui/PullRefresh&SlidingMenu/master/Pics/SlidingMenu.gif)  

Usage
===

1. Include the SlidingMenu widget in your view.        

```xml
<com.charon.slidingmenu.view.SlidingMenu xmlns:android="http://schemas.android.com/apk/res/android"
	android:id="@+id/sm"
	android:layout_width="match_parent"
	android:layout_height="match_parent" >
</com.charon.slidingmenu.view.SlidingMenu>
```	             

2. In your `onCreate` method (or `onCreateView` for a fragment), bind the widget to the `SlidingMenu`.

```java
mSlidingMenu = (SlidingMenu) findViewById(R.id.sm);

//prepare three view for the slidingmenu
View leftView = View.inflate(this, R.layout.frame_left, null);
View rightView = View.inflate(this, R.layout.frame_right, null);
View centerView = View.inflate(this, R.layout.frame_center, null);

//add the three view to SlidingMenu
mSlidingMenu.setView(leftView, rightView, centerView, 200, 250);

//replace the three view by three fragment
FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
mLeftFragment = new LeftFragment();
transaction.replace(R.id.fl_left, mLeftFragment);
mRightFragment = new RightFragment();
transaction.replace(R.id.fl_right, mRightFragment);
mCenterFragment = new CenterFragment();
transaction.replace(R.id.fl_center, mCenterFragment);
transaction.commit();

//set which side can show of SlidingMenu use the interface made by 
mCenterFragment.setOnViewPagerChangeListener(new OnViewPagerChangeListener() {

	@Override
	public void onPageChage(int position) {
		if (mCenterFragment.isFirst()) {
			mSlidingMenu.setWhichSideCanShow(true, false);
		} else if (mCenterFragment.isLast()) {
			mSlidingMenu.setWhichSideCanShow(false, true);
		} else {
			mSlidingMenu.setWhichSideCanShow(false, false);
		}
	}
});

//make two method for CenterFragment to use
public void showLeftViewToogle() {
	mSlidingMenu.showLeftViewToogle();
}

public void showRightViewToogle() {
	mSlidingMenu.showRightViewToogle();
}
```
		
		
ScrollingTabs
===

Interactive paging indicator widget, compatible with the ViewPager from the Android Support Library.

![image](https://raw.githubusercontent.com/CharonChui/PullRefresh&SlidingMenu/master/Pics/ScrollingTabs.gif)  

Usage
===

1. Include the ScrollingTabsView widget in your view. This should usually be placed adjacent to the `ViewPager` it represents.

```java
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
	android:layout_width="match_parent"
	android:layout_height="48dp"
	android:orientation="vertical" >

	<com.charon.scrollingtabs.view.ScrollingTabsView
		android:id="@+id/stv"
		android:layout_width="match_parent"
		android:layout_height="wrap_content" >
	</com.charon.scrollingtabs.view.ScrollingTabsView>

	<android.support.v4.view.ViewPager
		android:id="@+id/vp"
		android:layout_width="match_parent"
		android:layout_height="match_parent" >
	</android.support.v4.view.ViewPager>

</LinearLayout>
```

2. In your `onCreate` method (or `onCreateView` for a fragment), bind the widget to the `ViewPager`.

```java
 // Set the pager with an adapter
 mViewPager = (ViewPager) findViewById(R.id.vp);

 mViewPager.setAdapter(new TestAdapter(getSupportFragmentManager()));

 // Bind the widget to the adapter
 mScrollingTabsView = (ScrollingTabsView) findViewById(R.id.stv);
 mScrollingTabsView.setViewPager(mViewPager);
 mScrollingTabsView.setTabAdapter(new MyTabAdapter()); 
```

SimpleApp
===

SimpleApp using this three projects and [AndroidDevelopFramework](https://github.com/CharonChui/AndroidDevelopFramework)

![image](https://raw.githubusercontent.com/CharonChui/PullRefresh&SlidingMenu/master/Pics/SimpleApp.gif)  


Developed By
===

 * Charon Chui - <charon.chui@gmail.com>


License
===

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
