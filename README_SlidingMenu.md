SlidingMenu
===

SlidingMenu with left and right menu,This is an original component demo, does not rely on any third-party lib library.

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
  2. In your `onCreate` method (or `onCreateView` for a fragment), bind the
     widget to the `SlidingMenu`.

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
