package com.mapsaurus.paneslayout;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.view.MenuItem;
import com.mapsaurus.panelayout.R;
import com.mapsaurus.paneslayout.PanesLayout.OnIndexChangedListener;
import com.mapsaurus.paneslayout.PanesLayout.PaneView;
import com.mapsaurus.paneslayout.PanesSizer.PaneSizer;

public class TabletDelegate extends ActivityDelegate implements PanesLayout.OnIndexChangedListener  {

	private PaneSizer mPaneSizer;

	public void setPaneSizer(PaneSizer sizer) {
		panesLayout.setPaneSizer(sizer);
		mPaneSizer = sizer;
	}

	/**
	 * This provides all the logic for displaying and moving panes.
	 */
	protected PanesLayout panesLayout;

	public TabletDelegate(PanesActivity a) {
		super(a);
	}

	/**
	 * Enable the action-bar home button if appropriate
	 */
	@Override
	public void onIndexChanged(int firstIndex, int lastIndex,
			int firstCompleteIndex, int lastCompleteIndex) {
		if (firstCompleteIndex == 0)
			getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		else getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	/**
	 * On action-bar home button selected, slide to the menu
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			panesLayout.setIndex(0);
			return true;
		}
		return false;
	}

	/**
	 * Handle back press
	 */
	@Override
	public boolean onBackPressed() {
		if (panesLayout.onBackPressed()) return true;
		return false;
	}

	/**
	 * Save the state of the panes
	 */
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		int[] panesType = new int[panesLayout.getNumPanes()];
		boolean[] panesFocused = new boolean[panesLayout.getNumPanes()];
		for (int i = 0; i < panesLayout.getNumPanes(); i ++) {
			PaneView p = panesLayout.getPane(i);
			panesType[i] = p.type;
			panesFocused[i] = p.focused;
		}

		savedInstanceState.putIntArray("PanesLayout_panesType", panesType);
		savedInstanceState.putBooleanArray("PanesLayout_panesFocused", panesFocused);
		savedInstanceState.putInt("PanesLayout_currentIndex", panesLayout.getCurrentIndex());
	}

	@Override
	public void onDestroy() {
		panesLayout.setOnIndexChangedListener(null);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		if (findViewById(R.id.content_frame) == null) {
			setContentView(R.layout.panes_layout);
		} else {
			View.inflate(getActivity(), R.layout.panes_layout,
					(ViewGroup) findViewById(R.id.content_frame));
		}

		panesLayout = (PanesLayout) findViewById(R.id.panes);
		panesLayout.setOnIndexChangedListener(this);

		if (savedInstanceState != null) {
			int[] panesType = savedInstanceState.getIntArray("PanesLayout_panesType");
			boolean[] panesFocused = savedInstanceState.getBooleanArray("PanesLayout_panesFocused");
			int currentIndex = savedInstanceState.getInt("PanesLayout_currentIndex");
			for (int i = 0; i < panesType.length; i ++) {
				panesLayout.addPane(panesType[i], panesFocused[i]);
			}
			panesLayout.setIndex(currentIndex);
		}

		if (savedInstanceState != null) {
			FragmentManager fm = getSupportFragmentManager();

			for (int index = 0; index < panesLayout.getNumPanes(); index ++) {
				int id = panesLayout.getPane(index).getInnerId();
				Fragment f = fm.findFragmentById(id);

				fragmentStack.add(f);
			}
		}
	}

	/* *********************************************************************
	 * Methods for dealing with adding/removing fragments.
	 * ********************************************************************* */

	/**
	 * Save the fragment stack manually. The reason to do this is because sometimes when
	 * we need to retrieve a fragment, that fragment has not yet been added.
	 */
	private ArrayList<Fragment> fragmentStack = new ArrayList<Fragment>();

	private void clearFragments(int index) {
		while (index < fragmentStack.size())
			fragmentStack.remove(index);

		ArrayList<PaneView> panes = panesLayout.removePanes(index);
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();

		for (PaneView pane : panes) {
			int id = pane.getInnerId();
			Fragment f = fm.findFragmentById(id);
			if (f != null)
				ft.remove(f);
		}

		ft.commit();
	}

	private void addFragment(Fragment f, int index) {
		if (f == null) return;
		
		clearFragments(index + 1);

		int type = 0;
		boolean focused = false;

		PaneSizer paneSizer = mPaneSizer;
		if (paneSizer != null) {
			type = paneSizer.getType(f);
			focused = paneSizer.getFocused(f);
		}

		PaneView p = panesLayout.getPane(index);
		if (p != null && p.type == type && p.focused == focused) {
		} else {
			clearFragments(index);
			p = panesLayout.addPane(type, focused);
			if (p.index != index)
				throw new IllegalStateException("Added pane has wrong index");
		}

		panesLayout.setIndex(index);

		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.replace(p.getInnerId(), f);
		ft.commit();

		fragmentStack.add(index, f);
	}

	@Override
	public void addFragment(Fragment oldFragment, Fragment newFragment) {
		if (newFragment != null) {
			int index = 1; // by default, add after menu

			int oldIndex = getIndex(oldFragment);
			if (oldIndex != -1) index = oldIndex + 1;

			addFragment(newFragment, index);
		} else {
			panesLayout.setIndex(1);
		}
	}

	@Override
	public void clearFragments() {
		clearFragments(0);
	}

	@Override
	public void setMenuFragment(Fragment menuFragment) {
		addFragment(menuFragment, 0);
	}

	private int getIndex(Fragment f) {
		for (int i = 0; i < fragmentStack.size(); i ++) {
			if (fragmentStack.get(i) == f)
				return i;
		}
		return -1;
	}

	private Fragment getFragment(int index) {
		if (index < fragmentStack.size() && index >= 0)
			return fragmentStack.get(index);

		// PaneView pane = panesLayout.getPane(index);
		// FragmentManager fm = getSupportFragmentManager();
		// Fragment f = fm.findFragmentById(pane.getInnerId());

		return null;
	}

	@Override
	public Fragment getMenuFragment() {
		return getFragment(0);
	}

	@Override
	public Fragment getTopFragment() {
		return getFragment(panesLayout.getNumPanes() - 1);
	}

	@Override
	public void showMenu() {
		panesLayout.setIndex(0);
	}

}
