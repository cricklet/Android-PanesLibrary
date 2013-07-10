package com.mapsaurus.panesexample;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import com.mapsaurus.panesexample.R;
import com.mapsaurus.paneslayout.FragmentLauncher;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

public class ExampleFragment extends Fragment {

	private static int sExampleNum = 0;

	private TextView text1;
	private TextView text2;
	private Button addButton;
	private View parentView;

	private int text1Val = -1;
	private int text2Val = -1;

	/**
	 * Create a new ExampleFragment and add it!
	 */
	private void addExampleFragment() {
		// create a new fragment
		Fragment f = new ExampleFragment();
		//Fragment f = new ExampleListFragment();
		
		// get the activity and add the new fragment after this one!
		Activity a = getActivity();
		if (a != null && a instanceof FragmentLauncher)
			((FragmentLauncher) a).addFragment(ExampleFragment.this, f);
	}

	public ExampleFragment() {
		super();
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
				randomizeColor();
			}
		});

		addButton = (Button) parentView.findViewById(R.id.add);
		addButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				addExampleFragment();
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


	/* *********************************************************************
	 * Deal with coloring the fragment and showing the amount of time
	 * it's been alive.
	 * ********************************************************************* */

	/**
	 * Randomize the background of this fragment
	 */
	public void randomizeColor() {
		color = Color.rgb((int) (Math.random() * 255),
				(int) (Math.random() * 255), (int) (Math.random() * 255));
		parentView.setBackgroundColor(color);
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
