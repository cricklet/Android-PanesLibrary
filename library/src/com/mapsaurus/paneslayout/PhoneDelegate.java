package com.mapsaurus.paneslayout;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;
import com.mapsaurus.panelayout.R;

import android.app.SearchManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.ActionProvider;
import android.view.SubMenu;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem.OnActionExpandListener;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.FrameLayout;
import android.widget.Toast;

public class PhoneDelegate extends ActivityDelegate implements OnBackStackChangedListener {

	private ActionBarDrawerToggle drawerToggle;
	private DrawerLayout drawer;

	public PhoneDelegate(PanesActivity a) {
		super(a);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		if (findViewById(R.id.content_frame) == null)
			setContentView(R.layout.phone_layout);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		
		drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawer.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		
		drawerToggle = new ActionBarDrawerToggle(
				getActivity(), drawer, R.drawable.ic_drawer,
				R.string.drawer_open, R.string.drawer_close) {
			public void onDrawerClosed(View view) {
				supportInvalidateOptionsMenu();
				// creates call to onPrepareOptionsMenu()
			}

			public void onDrawerOpened(View drawerView) {
				supportInvalidateOptionsMenu();
				// creates call to onPrepareOptionsMenu()
			}
		};
		drawer.setDrawerListener(drawerToggle);

		FragmentManager fm = getSupportFragmentManager();
		fm.addOnBackStackChangedListener(this);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		// Sync the toggle state after onRestoreInstanceState has occurred.
		drawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// Pass any configuration change to the drawer toggles
		drawerToggle.onConfigurationChanged(newConfig);
	}

	/* *********************************************************************
	 * Interactions with menu/back/etc
	 * ********************************************************************* */

	@Override
	public boolean onBackPressed() {
		return false;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			// drawer enabled
			if (drawerToggle.isDrawerIndicatorEnabled()) {
				// The action bar home/up action should open or close the drawer.
				// ActionBarDrawerToggle will take care of this.
				if (drawerToggle.onOptionsItemSelected(new MenuItemWrapper(item))) 
					return true;
			} else {
				clearFragments();
				return true;
			}
		}

		return false;
	}
	
	@Override
	public void onBackStackChanged() {
		FragmentManager fm = getSupportFragmentManager();
		int count = fm.getBackStackEntryCount();
		if (count > 0)
			drawerToggle.setDrawerIndicatorEnabled(false);
		else
			drawerToggle.setDrawerIndicatorEnabled(true);
	}

	/* *********************************************************************
	 * Adding, removing, getting fragments
	 * ********************************************************************* */

	/**
	 * Save the menu fragment. The reason to do this is because sometimes when
	 * we need to retrieve a fragment, that fragment has not yet been added.
	 */
	private WeakReference<Fragment> wMenuFragment = new WeakReference<Fragment>(null);

	@Override
	public void addFragment(Fragment prevFragment, Fragment newFragment) {
		boolean addToBackStack = false;
		if (prevFragment == getMenuFragment() || prevFragment == null) {
			clearFragments();
		} else {
			addToBackStack = true;
		}

		drawer.closeDrawer(GravityCompat.START);

		if (newFragment != null) {
			FragmentManager fm = getSupportFragmentManager();
			FragmentTransaction ft = fm.beginTransaction();
			ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out,
					android.R.anim.fade_in, android.R.anim.fade_out);
			ft.replace(R.id.content_frame, newFragment);
			if (addToBackStack) ft.addToBackStack(newFragment.toString());
			ft.commit();
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
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.replace(R.id.menu_frame, f);
		ft.commit();

		wMenuFragment = new WeakReference<Fragment>(f);
	}

	@Override
	public Fragment getMenuFragment() {
		Fragment f = wMenuFragment.get();
		if (f == null) {
			f = getSupportFragmentManager().findFragmentById(R.id.menu_frame);
			wMenuFragment = new WeakReference<Fragment>(f);
		}
		return f;
	}

	@Override
	public Fragment getTopFragment() {
		return getSupportFragmentManager().findFragmentById(R.id.content_frame);
	}

	@Override
	public void showMenu() {
		drawer.openDrawer(GravityCompat.START);
	}

}
