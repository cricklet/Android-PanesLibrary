package com.mapsaurus.paneslayout;

import java.util.List;

import android.content.Context;
import android.graphics.Rect;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.Scroller;

public class SimpleScrollView extends FrameLayout {

	private Scroller mScroller;

	/**
	 * True when the layout has changed but the traversal has not come through yet.
	 * Ideally the view hierarchy would keep track of this for us.
	 */
	private boolean mIsLayoutDirty = true;

	public SimpleScrollView(Context context) {
		super(context);
		mScroller = new Scroller(context);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		if (widthMode == MeasureSpec.UNSPECIFIED) {
			return;
		}

		if (getChildCount() > 0) {
			final View child = getChildAt(0);
			int width = getMeasuredWidth();
			if (child.getMeasuredWidth() < width) {
				final FrameLayout.LayoutParams lp = (LayoutParams) child.getLayoutParams();

				int childHeightMeasureSpec = getChildMeasureSpec(heightMeasureSpec,
						getPaddingTop() + getPaddingBottom(), lp.height);
				width -= getPaddingLeft();
				width -= getPaddingRight();
				int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);

				child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
			}
		}
	}

	@Override
	protected void measureChild(View child, int parentWidthMeasureSpec, int parentHeightMeasureSpec) {
		ViewGroup.LayoutParams lp = child.getLayoutParams();

		int childWidthMeasureSpec;
		int childHeightMeasureSpec;

		childHeightMeasureSpec = getChildMeasureSpec(parentHeightMeasureSpec,
				getPaddingTop() + getPaddingBottom(), lp.height);

		childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);

		child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
	}

	@Override
	protected void measureChildWithMargins(View child, int parentWidthMeasureSpec, int widthUsed,
			int parentHeightMeasureSpec, int heightUsed) {
		final MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

		final int childHeightMeasureSpec = getChildMeasureSpec(parentHeightMeasureSpec,
				getPaddingTop() + getPaddingBottom() + lp.topMargin + lp.bottomMargin
				+ heightUsed, lp.height);
		final int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(
				lp.leftMargin + lp.rightMargin, MeasureSpec.UNSPECIFIED);

		child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
	}

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			int oldX = getScrollX();
			int oldY = getScrollY();
			int x = mScroller.getCurrX();
			int y = mScroller.getCurrY();
			if (getChildCount() > 0) {
				View child = getChildAt(0);
				x = clamp(x, getWidth() - getPaddingRight() - getPaddingLeft(), child.getWidth());
				y = clamp(y, getHeight() - getPaddingBottom() - getPaddingTop(), child.getHeight());
			}

			scrollTo(x, y);

			if (oldX != getScrollX() || oldY != getScrollY()) {
				onScrollChanged(getScrollX(), getScrollY(), oldX, oldY);
			}

			// Keep on drawing until the animation has finished.
			postInvalidate();
		}
	}

	private int clamp(int n, int my, int child) {
		if (my >= child || n < 0) {
			return 0;
		}
		if ((my + n) > child) {
			return child - my;
		}
		return n;
	}

	@Override
	public void requestLayout() {
		mIsLayoutDirty = true;
		super.requestLayout();
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		mIsLayoutDirty = false;

		// Calling this with the present values causes it to re-clam them
		scrollTo(getScrollX(), getScrollY());
	}
	
	public boolean isLayoutDirty() {
		return mIsLayoutDirty;
	}

	/* ************************************************************************
	 * Edited by Kenrick. Simple smooth scroll!
	 */

	/**
	 * Like {@link View#scrollBy}, but scroll smoothly instead of immediately.
	 *
	 * @param dx the number of pixels to scroll by on the X axis
	 * @param dy the number of pixels to scroll by on the Y axis
	 */
	public final void smoothScrollBy(int dx, int dy) {
		mScroller.startScroll(getScrollX(), getScrollY(), dx, dy);
		invalidate();
		mIsLayoutDirty = true;
	}

	/**
	 * Like {@link #scrollTo}, but scroll smoothly instead of immediately.
	 *
	 * @param x the position where to scroll on the X axis
	 * @param y the position where to scroll on the Y axis
	 */
	public final void smoothScrollTo(int x, int y) {
		smoothScrollBy(x - getScrollX(), y - getScrollY());
	}

}
