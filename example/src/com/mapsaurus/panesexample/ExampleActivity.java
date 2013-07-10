package com.mapsaurus.panesexample;

import com.mapsaurus.paneslayout.PanesActivity;
import com.mapsaurus.paneslayout.PanesSizer.PaneSizer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TimingLogger;

public class ExampleActivity extends PanesActivity {

	/**
	 * The PaneSizer allows you to programatically size each pane.
	 * 
	 * Type is an arbitrary integer that represents the type of view or fragment 
	 * contained within a pane.
	 * 
	 * In this example, there are only two types, 'default' and 'unknown.'
	 * Panes associated with ExampleFragments are given type 'default.'
	 * 
	 * Certain panes can also be focused. This means that swipe gestures will
	 * not be detected on those panes.
	 */
	private class ExamplePaneSizer implements PaneSizer {
		private static final int DEFAULT_PANE_TYPE = 0;
		private static final int UNKNOWN_PANE_TYPE = -1;

		@Override
		public int getWidth(int index, int type, int parentWidth, int parentHeight) {
			if (parentWidth > parentHeight) {
				if (type == DEFAULT_PANE_TYPE && index == 0)
					return (int) (0.25 * parentWidth);
				else if (type == DEFAULT_PANE_TYPE)
					return (int) (0.375 * parentWidth);
				else throw new IllegalStateException("Pane has unknown type");
			} else {
				if (type == DEFAULT_PANE_TYPE && index == 0)
					return (int) (0.4 * parentWidth);
				else if (type == DEFAULT_PANE_TYPE)
					return (int) (0.6 * parentWidth);
				if (type == DEFAULT_PANE_TYPE)
					return (int) (0.75 * parentWidth);
				else throw new IllegalStateException("Pane has unknown type");
			}
		}

		@Override
		public int getType(Object o) {
			if (o instanceof ExampleFragment)
				return DEFAULT_PANE_TYPE;
			else return UNKNOWN_PANE_TYPE;
		}

		@Override
		public boolean getFocused(Object o) {
			return false;
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setPaneSizer(new ExamplePaneSizer());

		// Lets setup a menu and a first pane!
		if (savedInstanceState == null) {
			Fragment menu = new ExampleFragment();
			Fragment first = new ExampleFragment();
			setMenuFragment(menu);
			addFragment(menu, first);
			
			// if you wanted to add more fragments after these ones, you can do:
			 Fragment second = new ExampleFragment();
			 Fragment third = new ExampleFragment();
			 addFragment(first, second);
			 addFragment(second, third);
		}
	}
	
}