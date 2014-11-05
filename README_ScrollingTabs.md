ScrollingTabs
===

Interactive paging indicator widget, compatible with the ViewPager from the Android Support Library.

Usage
===
  1. Include the ScrollingTabsView widget in your view. This should usually be placed
     adjacent to the `ViewPager` it represents.

  
        <LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
			android:layout_width="match_parent"
			android:layout_height="48dp"
			android:orientation="vertical" >

			<com.seal.scrollingtabs.view.ScrollingTabsView
				android:id="@+id/stv"
				android:layout_width="match_parent"
				android:layout_height="wrap_content" >
			</com.seal.scrollingtabs.view.ScrollingTabsView>

			<android.support.v4.view.ViewPager
				android:id="@+id/vp"
				android:layout_width="match_parent"
				android:layout_height="match_parent" >
			</android.support.v4.view.ViewPager>

		</LinearLayout>

  2. In your `onCreate` method (or `onCreateView` for a fragment), bind the
     widget to the `ViewPager`.

         // Set the pager with an adapter
         mViewPager = (ViewPager) findViewById(R.id.vp);
      
         mViewPager.setAdapter(new TestAdapter(getSupportFragmentManager()));

         // Bind the widget to the adapter
         mScrollingTabsView = (ScrollingTabsView) findViewById(R.id.stv);
		 mScrollingTabsView.setViewPager(mViewPager);
         mScrollingTabsView.setTabAdapter(new MyTabAdapter()); 



Developed By
============

 * Charon Chui - <charon.chui@gmail.com>


License
=======

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
