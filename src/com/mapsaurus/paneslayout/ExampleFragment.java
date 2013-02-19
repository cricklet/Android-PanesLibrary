package com.mapsaurus.paneslayout;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import com.mapsaurus.panelayout.R;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

public class ExampleFragment extends Fragment {

	private static int sExampleNum = 0;

	private TextView text1;
	private TextView text2;
	private View parentView;

	private int text1Val = -1;
	private int text2Val = -1;

	/**
	 * Listener that fires when the user clicks on this fragment.
	 * 
	 * The purpose of this is just to demonstrate that retained fragments need to have
	 * their listener's reset so they don't point to old activities.
	 */
	public interface OnExampleClickListener {
		public void onClick(ExampleFragment f);
	}

	private WeakReference<OnExampleClickListener> wListener = new WeakReference<OnExampleClickListener>(null);

	public void setOnExampleClickListener(OnExampleClickListener l) {
		wListener = new WeakReference<OnExampleClickListener>(l);
	}

	/**
	 * Current color of this fragment
	 */
	private int color = -1;

	/**
	 * These variables control the threads
	 */
	private boolean paused = true, destroyed = false;

	private Thread thread = new Thread() {
		private double fps = 1;

		public void run() {
			while (!destroyed) {
				if (paused) {synchronized (this) {
					try {
						wait();
					} catch (InterruptedException e) {
					}
				}}

				long startTime = System.currentTimeMillis();

				text2Val ++;
				text2.post(new Runnable() {public void run() {
					text2.setText("" + text2Val);
				}});

				long endTime = System.currentTimeMillis();
				long dTime = endTime - startTime;
				try {
					Thread.sleep((long) Math.max(1000 / fps - dTime, 0));
				} catch (InterruptedException e) {
				}
			}
		}
	};

	public ExampleFragment() {
		super();
	}

	/**
	 * Randomize the background of this fragment
	 */
	public void randomizeColor() {
		color = Color.rgb((int) (Math.random() * 255),
				(int) (Math.random() * 255), (int) (Math.random() * 255));
		parentView.setBackgroundColor(color);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		setRetainInstance(true);

		parentView = inflater.inflate(R.layout.example_fragment, container, false);
		if (color != -1) {
			parentView.setBackgroundColor(color);
		} else {
			randomizeColor();
		}

		parentView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				OnExampleClickListener l = wListener.get();
				if (l != null) l.onClick(ExampleFragment.this);
			}
		});

		if (text1Val == -1)
			text1Val = sExampleNum ++;

		text1 = (TextView) parentView.findViewById(R.id.text1);
		text1.setText("" + text1Val);

		text2 = (TextView) parentView.findViewById(R.id.text2);
		text2.setText("...");

		return parentView;
	}

	@Override
	public void onStart() {
		super.onStart();
		if (!thread.isAlive())
			thread.start();
	}

	@Override
	public void onResume() {
		super.onResume();
		paused = false;
		synchronized (thread) {
			thread.notify();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		paused = true;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		destroyed = true;
	}

}
