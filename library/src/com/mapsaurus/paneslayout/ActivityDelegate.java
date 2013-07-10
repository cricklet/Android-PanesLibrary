package com.mapsaurus.paneslayout;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;

public abstract class ActivityDelegate {

	private PanesActivity mActivity;

	public ActivityDelegate(PanesActivity a) {
		mActivity = a;
	}

	/**
	 * Save the state of the panes 
	 */
	public void onSaveInstanceState(Bundle savedInstanceState) {}

	/**
	 * onCreate
	 */
	public abstract void onCreate(Bundle savedInstanceState);

	/**
	 * onDestroy
	 */
	public void onDestroy() {}

	protected void onPostCreate(Bundle savedInstanceState) {}
	protected void onConfigurationChanged(Configuration newConfig) {}

	/* *********************************************************************
	 * Adding, removing, getting fragments
	 * ********************************************************************* */

	/**
	 * Add a fragment as a menu
	 */
	public abstract void addFragment(Fragment prevFragment, Fragment newFragment);

	/**
	 * Add a fragment as a menu
	 */
	public abstract void setMenuFragment(Fragment f);

	/**
	 * Clear all fragments from stack
	 */
	public abstract void clearFragments();

	/**
	 * Get menu framgent
	 */
	public abstract Fragment getMenuFragment();

	/**
	 * Get top framgent
	 */
	public abstract Fragment getTopFragment();

	/**
	 * Show the menu
	 */
	public abstract void showMenu();

	/* *********************************************************************
	 * Deal with over-riding activity methods
	 * ********************************************************************* */

	/**
	 * Deal with back pressed
	 */
	public abstract boolean onBackPressed();

	/**
	 * Deal with menu buttons
	 */
	public abstract boolean onOptionsItemSelected(MenuItem item);

	/* *********************************************************************
	 * Wrapper functions to make coding a delegate less annoying
	 * ********************************************************************* */

	protected final void supportInvalidateOptionsMenu() {
		mActivity.supportInvalidateOptionsMenu();
	}

	protected final PanesActivity getActivity() {
		return mActivity;
	}

	protected final void setContentView(int layoutResId) {
		mActivity.setContentView(layoutResId);
	}

	protected final ActionBar getSupportActionBar() {
		return mActivity.getSupportActionBar();
	}

	protected final Resources getResources() {
		return mActivity.getResources();
	}

	protected final FragmentManager getSupportFragmentManager() {
		return mActivity.getSupportFragmentManager();
	}

	protected final View findViewById(int id) {
		return mActivity.findViewById(id);
	}

}
