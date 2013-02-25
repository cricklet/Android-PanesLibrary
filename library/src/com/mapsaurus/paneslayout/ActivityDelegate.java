package com.mapsaurus.paneslayout;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;

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
	public abstract void onSaveInstanceState(Bundle savedInstanceState);
	
	/**
	 * onCreate
	 */
	public abstract void onCreate(Bundle savedInstanceState);

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
	 * Update a fragment
	 */
	public abstract void updateFragment(Fragment f);

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

	protected PanesActivity getActivity() {
		return mActivity;
	}
	
	protected void setContentView(int layoutResId) {
		mActivity.setContentView(layoutResId);
	}

	protected ActionBar getSupportActionBar() {
		return mActivity.getSupportActionBar();
	}

	protected Resources getResources() {
		return mActivity.getResources();
	}

	protected FragmentManager getSupportFragmentManager() {
		return mActivity.getSupportFragmentManager();
	}

	protected View findViewById(int id) {
		return mActivity.findViewById(id);
	}
	
}
