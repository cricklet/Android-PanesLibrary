package com.mapsaurus.paneslayout;

import java.lang.ref.WeakReference;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;
import com.mapsaurus.panelayout.R;
import com.slidingmenu.lib.SlidingMenu;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.util.Log;

public class PhoneDelegate extends ActivityDelegate implements 
SlidingMenu.OnOpenListener, SlidingMenu.OnCloseListener, OnBackStackChangedListener {

	private SlidingMenu menu;

	public PhoneDelegate(PanesActivity a) {
		super(a);
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		savedInstanceState.putBoolean("PhoneLayout_menuOpen", menu.isMenuShowing());
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.content_frame);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// initialize sliding menu
		menu = new SlidingMenu(getActivity());
		menu.setMode(SlidingMenu.LEFT);
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);

		int shadowWidth = getResources().getDimensionPixelSize(R.dimen.shadow_size);
		int menuOffset = getResources().getDimensionPixelSize(R.dimen.menu_offset);

		menu.setShadowWidth(shadowWidth);
		menu.setShadowDrawable(R.drawable.shadow_left);
		menu.setBehindOffset(menuOffset);

		menu.setOnCloseListener(this);
		menu.setOnOpenListener(this);

		menu.setMenu(R.layout.menu_frame);

		FragmentManager fm = getSupportFragmentManager();
		fm.addOnBackStackChangedListener(this);

		if (savedInstanceState != null) {
			updateFragment(getTopFragment());
			updateFragment(getMenuFragment());
		}

		menu.attachToActivity(getActivity(), SlidingMenu.SLIDING_CONTENT);
		if (savedInstanceState == null) {
			menu.showMenu();
		} else {
			if (savedInstanceState.getBoolean("PhoneLayout_menuOpen"))
				menu.showMenu();
			else menu.showContent();
		}
	}

	/* *********************************************************************
	 * Interactions with menu/back/etc
	 * ********************************************************************* */

	@Override
	public void onClose() {
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public void onOpen() {
		getSupportActionBar().setDisplayHomeAsUpEnabled(false);
	}

	@Override
	public boolean onBackPressed() {
		FragmentManager fm = getSupportFragmentManager();
		
		if (menu.isMenuShowing() == false) {
			if (fm.getBackStackEntryCount() > 0) {
				return false;
			} else {
				menu.showMenu();
				return true;
			}
		} else {
			return false;
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			if (!menu.isMenuShowing())
				menu.showMenu();
			return true;
		}
		return false;
	}

	@Override
	public void onBackStackChanged() {
		updateFragment(getTopFragment());
		updateFragment(getMenuFragment());
	}

	/* *********************************************************************
	 * Adding, removing, getting fragments
	 * ********************************************************************* */
	
	private WeakReference<Fragment> wMenuFragment;
	// This is necessary because sometimes addFragment gets called before the menu fragment
	// transaction has been committed-- and addFragment adds fragments differently depending
	// on if they're supposed to be added after the menu fragment
	
	@Override
	public void addFragment(Fragment prevFragment, Fragment newFragment) {
		boolean addToBackStack = false;
		if (prevFragment == wMenuFragment.get() || prevFragment == null) {
			clearFragments();
		} else {
			addToBackStack = true;
		}

		if (menu.isMenuShowing()) menu.showContent();

		if (newFragment != null) {
			FragmentManager fm = getSupportFragmentManager();
			FragmentTransaction ft = fm.beginTransaction();
			ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out,
					android.R.anim.fade_in, android.R.anim.fade_out);
			ft.replace(R.id.content_frame, newFragment);
			if (addToBackStack) ft.addToBackStack(newFragment.toString());
			ft.commit();
			
			updateFragment(newFragment);
		}
	}

	@Override
	public void clearFragments() {
		FragmentManager fm = getSupportFragmentManager();
		for(int i = 0; i < fm.getBackStackEntryCount(); i ++)    
			fm.popBackStack();
	}
	
	@Override
	public void setMenuFragment(Fragment f) {
		wMenuFragment = new WeakReference<Fragment>(f);
		
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.replace(R.id.menu_frame, f);
		ft.commit();

		updateFragment(f);
	}

	@Override
	public Fragment getMenuFragment() {
		return wMenuFragment.get();
	}

	@Override
	public Fragment getTopFragment() {
		FragmentManager fm = getSupportFragmentManager();
		return fm.findFragmentById(R.id.content_frame);
	}

	@Override
	public void showMenu() {
		menu.showMenu();
	}

}
