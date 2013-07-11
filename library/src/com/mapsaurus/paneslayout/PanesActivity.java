package com.mapsaurus.paneslayout;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.mapsaurus.paneslayout.PanesSizer.PaneSizer;

public abstract class PanesActivity extends SherlockFragmentActivity implements FragmentLauncher{

	private ActivityDelegate mDelegate;

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);

		mDelegate.onSaveInstanceState(savedInstanceState);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		int screenSize = (getResources().getConfiguration().screenLayout
				& Configuration.SCREENLAYOUT_SIZE_MASK);

		if (screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE ||
				screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE) {
			mDelegate = new TabletDelegate(this);
		} else {
			mDelegate = new PhoneDelegate(this);
		}

		mDelegate.onCreate(savedInstanceState);
	}

	/* *********************************************************************
	 * Deal with over-riding activity methods
	 * ********************************************************************* */

	/**
	 * Deal with menu buttons
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDelegate.onOptionsItemSelected(item)) return true;
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDelegate.onPostCreate(savedInstanceState);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDelegate.onConfigurationChanged(newConfig);
	}

	/**
	 * Deal with back pressed
	 */
	@Override
	public void onBackPressed() {
		if (onBackPressedHelper()) return;
		super.onBackPressed();
	}

	protected boolean onBackPressedHelper() {
		if (mDelegate.onBackPressed()) return true;
		return false;
	}

	/* *********************************************************************
	 * Adding, removing, getting fragments
	 * 
	 * Note: fragments are added in a stack. Here's a sample use case:
	 * 
	 * setMenuFragment(A)
	 * stack: A
	 * 
	 * addFragment(A, B)
	 * stack: A, B
	 * 
	 * addFragment(B, C)
	 * stack: A, B, C
	 * 
	 * addFragment(B, D)
	 * stack: A, B, D
	 * 
	 * clearFragments()
	 * stack: A
	 * 
	 * ********************************************************************* */

	/**
	 * Add a new fragment after the previous fragment.
	 */
	@Override
	public void addFragment(Fragment prevFragment, Fragment newFragment) {
		mDelegate.addFragment(prevFragment, newFragment);
	}

	/**
	 * Add a fragment as a menu
	 */
	public void setMenuFragment(Fragment f) {
		mDelegate.setMenuFragment(f);
	}

	/**
	 * Clear all fragments from stack except the menu fragment
	 */
	public void clearFragments() {
		mDelegate.clearFragments();
	}

	/**
	 * Get menu framgent
	 */
	public Fragment getMenuFragment() {
		return mDelegate.getMenuFragment();
	}

	/**
	 * Get top framgent
	 */
	public Fragment getTopFragment() {
		return mDelegate.getTopFragment();
	}

	/**
	 * Show the menu
	 */
	public void showMenu() {
		mDelegate.showMenu();
	}

	/* *********************************************************************
	 * Setup tablet or phone delegates
	 * ********************************************************************* */

	/**
	 * Set the pane sizer
	 */
	public void setPaneSizer(PaneSizer sizer) {
		if (mDelegate instanceof TabletDelegate)
			((TabletDelegate) mDelegate).setPaneSizer(sizer);
	}
}
