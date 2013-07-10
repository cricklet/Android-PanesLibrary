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
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

public class ExampleListFragment extends ListFragment {

	private static int sExampleNum = 0;
	String[] countries = new String[] {
			"India",
			"Pakistan",
			"Sri Lanka",
			"China",
			"Bangladesh",
			"Nepal",
			"Afghanistan",
			"North Korea",
			"South Korea",
			"Japan"
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		/** Creating an array adapter to store the list of countries **/
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(inflater.getContext(), android.R.layout.simple_list_item_1,countries);

		/** Setting the list adapter for the ListFragment */
		setListAdapter(adapter);

		return super.onCreateView(inflater, container, savedInstanceState);
	}
}
