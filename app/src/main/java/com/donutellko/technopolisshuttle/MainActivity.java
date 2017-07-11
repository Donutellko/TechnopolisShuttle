package com.donutellko.technopolisshuttle;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.gms.maps.model.LatLng;

import java.util.Calendar;

import com.donutellko.technopolisshuttle.DataLoader.STime;


import com.donutellko.technopolisshuttle.DataLoader.SettingsObject;

public class MainActivity extends AppCompatActivity {

	private LatLng
			coordsTechnopolis = new LatLng(59.818026, 30.327783),
			coordsUnderground = new LatLng(59.854728, 30.320958);
	private final double DISTANCE_TO_SHOW_FROM = 2;
	private static int countToShowOnShort = 5; // defaults
	private State currentState = State.SHORT_VIEW; //default
	// CheckBox values
	private boolean showPast = true, showTo = true;

	Calendar curtime;
	LinearLayout contentBlock; // Область контента (всё кроме нав. панели)
	BottomNavigationView navigation;

	ShortScheduleView shortView;
	FullScheduleView fullView;
	MapView mapView;
	public static LayoutInflater layoutInflater;

	enum State {SHORT_VIEW, FULL_VIEW, MAP_VIEW, SETTINGS_VIEW}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i("onCreate", "Method called");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		layoutInflater = getLayoutInflater();

		curtime = Calendar.getInstance();

		contentBlock = (LinearLayout) findViewById(R.id.content);

		navigation = (BottomNavigationView) findViewById(R.id.navigation);
		navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

		SettingsObject settings = new SettingsObject();
		if (settings.loadPreferences(getApplicationContext())) {
			currentState = settings.currentState;
			countToShowOnShort = settings.countToShowOnShort;
			showPast = settings.showPast;
			Log.i("settings", currentState + " " + countToShowOnShort);
		} else {
			Log.i("settings", "Preferences не загружены");
		}

		//double toTechno = getDistanceBetween(TECHNOPOLIS, getLocation());
		//showFrom = toTechno >= 0 && toTechno < DISTANCE_TO_SHOW_FROM;

		DataLoader dataLoader = new DataLoader();
		TimeTable timeTable = dataLoader.getFullJsonInfo();
		Context context = getApplicationContext();

		timeTable = dataLoader.getFullJsonInfo();

		shortView = new ShortScheduleView(context, timeTable, countToShowOnShort, showTo);
		fullView = new FullScheduleView(context, timeTable, showPast);
		mapView = new MapView(context, getFragmentManager(), coordsTechnopolis, coordsUnderground);

		loadView(currentState);

		getUpdateTimer(1000).start(); // запускаем автообновление значений каждые (параметр) миллисекунд
	}

	private CountDownTimer getUpdateTimer(long interval) {
		return new CountDownTimer(Long.MAX_VALUE, interval) {
			@Override
			public void onTick(long millisUntilFinished) {
				switch (currentState) {
					case SHORT_VIEW:
						shortView.updateView();
						break;
					case FULL_VIEW:
						fullView.updateView();
						break;
					case MAP_VIEW:
						break;
				}
			}

			@Override
			public void onFinish() {
				this.start();
			}
		};
	}

	private void loadView(State currentState) {
		Log.i("loadView", "Method called");
		switch (currentState) {
			case SHORT_VIEW:
				navigation.setSelectedItemId(R.id.navigation_short);
				break;
			case FULL_VIEW:
				navigation.setSelectedItemId(R.id.navigation_full);
				break;
			case MAP_VIEW:
				navigation.setSelectedItemId(R.id.navigation_map);
				break;
		}
	}

	public static STime getCurrentTime() {
		Calendar curtime = Calendar.getInstance();
		return new STime(curtime.getTime());
	}

	public static int getWeekdayNumber() {
		Log.i("getWeekdayNumber", "Method called");
		Calendar curtime = Calendar.getInstance();
		return curtime.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY;
	}

	public static boolean firstIsBefore(DataLoader.STime d1, DataLoader.STime d2) {
		Log.i("firstIsBefore", "Method called");
		if (d1.hour < d2.hour)
			return true;
		if (d1.hour == d2.hour && d1.min < d2.min)
			return true;
		return false;
	}

	public void setContent(SView sView) {
		Log.i("setContent", "Method called");
		contentBlock.removeAllViews();
		contentBlock.addView(sView.getView());
	}

	@Override
	public void onStop() {
		Log.i("onStop", "Method called");
		new DataLoader.SettingsObject(countToShowOnShort, currentState, showPast).savePreferences(getApplicationContext());
		super.onStop();
	}

	private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
			= new BottomNavigationView.OnNavigationItemSelectedListener() {
		@Override
		public boolean onNavigationItemSelected(@NonNull MenuItem item) {
			Log.i("listener", "bootomnavigation changed");
			switch (item.getItemId()) {
				case R.id.navigation_short:
					setContent(shortView);
					return true;
				case R.id.navigation_full:
					setContent(fullView);
					return true;
				case R.id.navigation_map:
					setContent(mapView);
					return true;
			}
			return false;
		}
	};
}