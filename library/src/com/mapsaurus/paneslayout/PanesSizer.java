package com.mapsaurus.paneslayout;

public interface PanesSizer {

	/**
	 * Used to specify the sizes of each pane.
	 */
	public interface PaneSizer {
		/**
		 * @param index = the index of this pane
		 * @param type = the type of this pane (the possible types are arbitrary)
		 * @param parentWidth = the width of the panes layout
		 * @param parentHeight = the height of the panes layout
		 */
		public int getWidth(int index, int type, int parentWidth, int parentHeight);
		
		/**
		 * @param o = fragment/view
		 * @return the type associated with this object
		 */
		public int getType(Object o);
		
		/**
		 * @param o = fragment/view
		 * @return whether this fragment/view should be focused
		 */
		public boolean getFocused(Object o);
	}
	
}
