package com.mapsaurus.paneslayout;

import java.util.ArrayList;

import com.mapsaurus.panelayout.R;
import com.mapsaurus.paneslayout.ExampleFragment.OnExampleClickListener;
import com.mapsaurus.paneslayout.PanesLayout.OnIndexChangedListener;
import com.mapsaurus.paneslayout.PanesLayout.PaneView;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;

public class ExampleActivity extends Activity implements
OnExampleClickListener, OnIndexChangedListener {

	public static final String TAG = "PanesBaseActivity";

	public static final int DEFAULT_PANE_TYPE = 0;

	protected PanesLayout panesLayout;

	private class ExamplePaneSizer implements PanesLayout.PaneSizer {
		@Override
		public int getWidth(int level, int type, int parentWidth, int parentHeight) {
			if (parentWidth > parentHeight) {
				return (int) (parentWidth * 0.4);
			} else {
				return (int) (parentWidth * 0.8);
			}
		}
	}

	@Override
	public void onIndexChanged(int firstIndex, int lastIndex,
			int firstCompleteIndex, int lastCompleteIndex) {
		if (firstCompleteIndex == 0)
			getActionBar().setDisplayHomeAsUpEnabled(false);
		else getActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);

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
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.example_activity);

		panesLayout = (PanesLayout) findViewById(R.id.panes);
		panesLayout.setPaneSizer(new ExamplePaneSizer());
		panesLayout.setOnIndexChangedListener(this);
		
		// super important code if you want fragments to be retained correctly on orientation change
		// this re-adds all the old panes
		if (savedInstanceState != null) {
			int[] panesType = savedInstanceState.getIntArray("PanesLayout_panesType");
			boolean[] panesFocused = savedInstanceState.getBooleanArray("PanesLayout_panesFocused");
			int currentIndex = savedInstanceState.getInt("PanesLayout_currentIndex");
			for (int i = 0; i < panesType.length; i ++)
				addPane(panesType[i], panesFocused[i]);

			panesLayout.setIndex(currentIndex);

			FragmentManager fm = getFragmentManager();
			// if any fragments were repopulated during an orientation change
			// make sure the fragments are correctly configured (not pointing to old activity)
			for (int level = 0; level < panesLayout.getNumPanes(); level ++) {
				int id = panesLayout.getPane(level).getInnerId();
				Fragment f = fm.findFragmentById(id);

				if (f == null) continue;
				if (f instanceof ExampleFragment)
					((ExampleFragment) f).setOnExampleClickListener(this);
			}
		}

		findViewById(R.id.back).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				panesLayout.setIndex(panesLayout.getCurrentIndex() - 1);
			}
		});

		findViewById(R.id.subtract).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				clearPanes(panesLayout.getCurrentIndex());
			}
		});
		
		findViewById(R.id.add).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ExampleFragment f = new ExampleFragment();
				f.setOnExampleClickListener(ExampleActivity.this);
				addFragment(DEFAULT_PANE_TYPE, false, f);
			}
		});

		findViewById(R.id.forward).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				panesLayout.setIndex(panesLayout.getCurrentIndex() + 1);
			}
		});
	}

	private PaneView addPane(int type, boolean focused) {
		int level = panesLayout.getNumPanes();
		PaneView p = panesLayout.addPane(type, focused);
		panesLayout.setIndex(level);
		return p;
	}

	private void clearPanes(int level) {
		ArrayList<PaneView> panes = panesLayout.removePanes(level);
		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();

		for (PaneView pane : panes) {
			int id = pane.getInnerId();
			Fragment f = fm.findFragmentById(id);
			if (f != null)
				ft.remove(f);
		}

		ft.commit();
	}

	private void addFragment(int type, boolean focused, Fragment f) {
		PaneView p = addPane(type, focused);
		int id = p.getInnerId();

		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.replace(id, f);
		ft.commit();
	}

	@Override
	public void onClick(ExampleFragment f) {
		f.randomizeColor();
	}

	@Override
	public void onBackPressed() {
		if (panesLayout.onBackPressed()) return;
		super.onBackPressed();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			panesLayout.setIndex(0);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
