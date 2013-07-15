package com.mapsaurus.paneslayout;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import com.mapsaurus.panelayout.R;
import com.mapsaurus.paneslayout.PanesSizer.PaneSizer;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.Scroller;

public class PanesLayout extends FrameLayout {

	/**
	 * Each pane exists inside of a scroll view.
	 */
	private ArrayList<SimpleScrollView> scrollers;

	/**
	 * Each pane contains a container for something else (fragment, view, etc)
	 */
	private ArrayList<PaneView> panes;

	/**
	 * Index (index) of the currently selected index
	 */
	private int currentIndex = 0;

	/**
	 * Currently visible indexes. Complete means the pane is completely visible.
	 */
	private int firstIndex;
	private int firstCompleteIndex;
	private int lastIndex;
	private int lastCompleteIndex;

	private int parentWidth;
	private int parentHeight;

	/**
	 * Set whenever panes are changed Unset whenever index is set
	 */
	private boolean panesChanged = true;

	/**
	 * This object sets the size of every pane base on it's type and focus
	 */
	private PaneSizer mPaneSizer;

	public void setPaneSizer(PaneSizer sizer) {
		this.mPaneSizer = sizer;
		this.requestLayout();
	}

	/**
	 * Whenever the visible panes change, this is fired.
	 */
	public interface OnIndexChangedListener {
		/**
		 * @param firstIndex
		 *            = the bottom-most visible pane
		 * @param lastIndex
		 *            = the top-most visible pane
		 * @param firstCompleteIndex
		 *            = the bottom-most completely visible pane
		 * @param lastCompleteIndex
		 *            = the top-most completely visible pane
		 */
		public void onIndexChanged(int firstIndex, int lastIndex,
				int firstCompleteIndex, int lastCompleteIndex);
	}

	private OnIndexChangedListener mIndexChangedListener;

	public void setOnIndexChangedListener(OnIndexChangedListener l) {
		this.mIndexChangedListener = l;
	}

	public PanesLayout(Context context) {
		this(context, null);
	}

	public PanesLayout(Context context, AttributeSet attrs) {
		super(context, attrs);

		scrollers = new ArrayList<SimpleScrollView>();
		panes = new ArrayList<PaneView>();
	}

	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		parentWidth = MeasureSpec.getSize(widthMeasureSpec)
				- this.getPaddingLeft() - this.getPaddingRight();
		parentHeight = MeasureSpec.getSize(heightMeasureSpec)
				- this.getPaddingTop() - this.getPaddingBottom();
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	/**
	 * Inside onLayout, we might need to update the index/scroll of the panes.
	 */
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);

		if (panesChanged || changed) {
			setIndex(currentIndex);
			panesChanged = false;
		}
	}

	private int getPaneX(SimpleScrollView scroll) {
		return parentWidth - scroll.getScrollX();
	}

	private PaneView getPaneFromScroll(double x) {
		for (int i = lastIndex; i >= 0; i--) {
			SimpleScrollView scroll = scrollers.get(i);
			PaneView pane = panes.get(i);
			if (pane == null || scroll == null)
				return null;

			if (x > getPaneX(scroll))
				return pane;
		}

		return null;
	}

	private double clampIndex(double index) {
		if (index < 0)
			index = 0;
		if (index > scrollers.size() - 1)
			index = scrollers.size() - 1;
		return index;
	}

	private void scrollHelper(SimpleScrollView s, double scrollX, boolean smooth) {
		scrollX = Math.min(scrollX, parentWidth);
		scrollX = Math.max(scrollX, 0);
		
		if (smooth)
			s.smoothScrollTo((int) scrollX, 0);
		else
			s.scrollTo((int) scrollX, 0);
	}

	/**
	 * Scroll all the panes based on some index. Returns which index is the
	 * top-most pane.
	 */
	private boolean partiallyVisible(int scroll, int width) {
		return (scroll - width < parentWidth + 5 && scroll >= 5);
	}

	private boolean completelyVisible(int scroll, int width) {
		return (scroll <= parentWidth + 5 && scroll - width >= -5);
	}

	private boolean scrollEverything(double index, boolean smooth) {
		if (parentWidth <= 0)
			return false;

		index = clampIndex(index);

		// get the top index
		int topIndex = (int) Math.ceil(index);
		if (topIndex < 0 || topIndex >= panes.size())
			return false;

		// get the minX & maxX of the top index
		PaneView topPane = panes.get(topIndex);
		SimpleScrollView topScroller = scrollers.get(topIndex);
		if (topPane == null)
			return false;

		int topWidth = topPane.containerWidth;
		double p = (topIndex - index);
		int topScroll = (int) (topWidth - topWidth * p);// + scrollOffset;

		int firstScroll = topScroll;
		for (int i = topIndex - 1; i >= 0; i--) {
			PaneView pane = panes.get(i);
			if (pane == null)
				continue;
			firstScroll += pane.containerWidth;
		}

		if (firstScroll < parentWidth)
			topScroll += parentWidth - firstScroll;

		// scroll the top pane!
		scrollHelper(topScroller, topScroll, smooth);

		int firstIndex = topIndex;
		int firstCompleteIndex = topIndex;
		int lastIndex = topIndex;
		int lastCompleteIndex = topIndex;

		int scroll = topScroll - topWidth;
		// move all panes after the top pane
		for (int i = topIndex + 1; i < panes.size(); i++) {
			SimpleScrollView scroller = scrollers.get(i);
			PaneView pane = panes.get(i);
			if (scroller == null || pane == null)
				continue;
			int width = pane.containerWidth;

			scrollHelper(scroller, scroll, smooth);

			if (partiallyVisible(scroll, width))
				lastIndex = i;
			if (completelyVisible(scroll, width))
				lastCompleteIndex = i;

			scroll -= pane.containerWidth;
		}

		scroll = topScroll;
		// move each pane before the top pane
		for (int i = topIndex - 1; i >= 0; i--) {
			SimpleScrollView scroller = scrollers.get(i);
			PaneView pane = panes.get(i);
			if (scroller == null || pane == null)
				continue;
			int width = pane.containerWidth;

			scroll += pane.containerWidth;
			scrollHelper(scroller, scroll, smooth);

			if (partiallyVisible(scroll, width))
				firstIndex = i;
			if (completelyVisible(scroll, width))
				firstCompleteIndex = i;
		}

		if (this.firstIndex != firstIndex
				|| this.firstCompleteIndex != firstCompleteIndex
				|| this.lastIndex != lastIndex
				|| this.lastCompleteIndex != lastCompleteIndex) {
			this.firstIndex = firstIndex;
			this.firstCompleteIndex = firstCompleteIndex;
			this.lastIndex = lastIndex;
			this.lastCompleteIndex = lastCompleteIndex;
			OnIndexChangedListener l = mIndexChangedListener;
			if (l != null)
				l.onIndexChanged(firstIndex, lastIndex, firstCompleteIndex,
						lastCompleteIndex);
		}

		return true;
	}

	public void setIndex(int index) {
		index = (int) clampIndex(index);
		if (scrollEverything(index, true))
			currentIndex = lastCompleteIndex;
		else
			currentIndex = index;
	}

	public int getCurrentIndex() {
		return currentIndex;
	}

	public int getNumPanes() {
		return panes.size();
	}

	public PaneView getPane(int i) {
		if (i >= panes.size())
			return null;
		if (i < 0)
			return null;
		return panes.get(i);
	}

	public PaneView addPane(int type, boolean focused) {
		panesChanged = true;

		int index = panes.size();

		PaneView pane = new PaneView(getContext(), type, index, focused);
		panes.add(pane);

		SimpleScrollView scroller = new SimpleScrollView(getContext());
		scrollers.add(scroller);

		scroller.addView(pane);
		addView(scroller, index);

		return pane;
	}

	public ArrayList<PaneView> removePanes(int removeI) {
		panesChanged = true;

		ArrayList<PaneView> deletedPanes = new ArrayList<PaneView>();
		for (int i = scrollers.size() - 1; i >= removeI; i--) {
			if (i < 0)
				break;

			scrollers.remove(i);
			deletedPanes.add(panes.remove(i));
			removeViewAt(i);
		}

		if (removeI <= currentIndex)
			setIndex(removeI - 1);
		return deletedPanes;
	}

	private double currentX;
	private double startX;

	private double currentY;
	private double startY;
	
	private boolean dragging;

	private void onTouchEventHelper(MotionEvent event) {
		currentX = event.getX();

		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			startX = currentX;
			dragging = true;
		}
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		onTouchEventHelper(event);

		double dx = Math.abs(currentX - startX);
		double dy = Math.abs(currentY - startY);

		if (dx > 4 * dy && dx > 10) {
			PaneView p = getPaneFromScroll(startX);

			// int bevelSize =
			// getResources().getDimensionPixelSize(R.dimen.bevel_size);

			if (p != null && p.focused == true
					&& (p.index >= firstCompleteIndex && p.index <= lastCompleteIndex)) {
				return false;
			}

			return true;
		}

		return false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!dragging)
			return true;

		onTouchEventHelper(event);

		double scroll = currentX - startX;
		double dIndex = -scroll / (parentWidth * 0.25);

		scrollEverything(currentIndex + dIndex, false);

		if ((event.getAction() == MotionEvent.ACTION_MOVE && Math.abs(dIndex) > 0.5)
				|| event.getAction() == MotionEvent.ACTION_UP) {
			setIndex((int) Math.round(currentIndex + dIndex));

			dragging = false;

			if (Math.abs(dIndex) > 0.25)
				return true;
		}

		return true;
	}

	public class PaneView extends LinearLayout {

		public int containerWidth;

		public int index;
		public int type;
		public boolean focused;

		private View startPadding;
		private View leftShadow;

		private ViewGroup container;

		private View rightShadow;
		private View endPadding;

		public int getInnerId() {
			return container.getId();
		}

		public ViewGroup getInner() {
			return container;
		}

		public PaneView(Context context, int type_, int index_, boolean focused_) {
			super(context);

			focused = focused_;
			index = index_;
			type = type_;

			setOrientation(LinearLayout.HORIZONTAL);
			setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.MATCH_PARENT));

			startPadding = new View(context);
			startPadding.setVisibility(View.INVISIBLE);

			leftShadow = new View(context);
			leftShadow.setBackgroundResource(R.drawable.shadow_left);

			addView(startPadding);
			addView(leftShadow);

			container = new FrameLayout(context);
			container.setId(Integer.MAX_VALUE - index);
			container.setClickable(true);
			addView(container);

			rightShadow = new View(context);
			rightShadow.setBackgroundResource(R.drawable.shadow_right);

			endPadding = new View(context);
			endPadding.setVisibility(View.INVISIBLE);

			addView(rightShadow);
			addView(endPadding);
		}

		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			int shadowWidth = getResources().getDimensionPixelSize(
					R.dimen.shadow_size);

			startPadding.measure(MeasureSpec.makeMeasureSpec(parentWidth
					- shadowWidth, MeasureSpec.EXACTLY), MeasureSpec
					.makeMeasureSpec(parentHeight, MeasureSpec.EXACTLY));

			leftShadow.measure(MeasureSpec.makeMeasureSpec(shadowWidth,
					MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(
					parentHeight, MeasureSpec.EXACTLY));

			PaneSizer paneSizer = mPaneSizer;
			if (paneSizer != null)
				containerWidth = paneSizer.getWidth(index, type, parentWidth,
						parentHeight);
			else
				containerWidth = (int) (0.7 * parentWidth);

			container.measure(MeasureSpec.makeMeasureSpec(containerWidth,
					MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(
					parentHeight, MeasureSpec.EXACTLY));

			rightShadow.measure(MeasureSpec.makeMeasureSpec(shadowWidth,
					MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(
					parentHeight, MeasureSpec.EXACTLY));

			endPadding.measure(MeasureSpec.makeMeasureSpec(parentWidth
					- containerWidth - shadowWidth, MeasureSpec.EXACTLY),
					MeasureSpec.makeMeasureSpec(parentHeight,
							MeasureSpec.EXACTLY));

			this.setMeasuredDimension(
					resolveSize(parentWidth * 2, widthMeasureSpec),
					resolveSize(parentHeight, heightMeasureSpec));
		}
	}

	public boolean onBackPressed() {
		int oldIndex = currentIndex;
		setIndex(currentIndex - 1);

		if (oldIndex != currentIndex)
			return true;
		return false;
	}

}