package com.donutellko.technopolisshuttle;

import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.os.CountDownTimer;
import android.content.Context;
import android.view.MenuItem;
import android.os.Bundle;
import android.view.Menu;
import android.util.Log;

import java.util.Calendar;

import com.google.android.gms.maps.model.LatLng;

import com.donutellko.technopolisshuttle.DataLoader.SettingsSingleton;

public class MainActivity extends AppCompatActivity {

	private LatLng
			coordsTechnopolis = new LatLng(59.818026, 30.327783),
			coordsUnderground = new LatLng(59.854728, 30.320958);

	Calendar curtime;

	LinearLayout contentBlock; // Область контента (всё кроме нав. панели)
	BottomNavigationView navigation;

	ShortScheduleView shortView;
	FullScheduleView fullView;
	MapView mapView;
	SettingsView settingsView;

	public static LayoutInflater layoutInflater;
	public static SettingsSingleton settingsSingleton = SettingsSingleton.singleton;

	enum State { SHORT_VIEW, FULL_VIEW, MAP_VIEW, SETTINGS_VIEW }

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


		if (settingsSingleton.loadPreferences(getApplicationContext()))
			Log.i("Preferences", "loaded");
		else
			Log.i("Preferences", "not found");

		DataLoader dataLoader = new DataLoader();
		TimeTable timeTable = dataLoader.getFullJsonInfo();

		Context context = this;
		shortView = new ShortScheduleView(context, settingsSingleton, timeTable, settingsSingleton.showTo);
		fullView =  new FullScheduleView (context, timeTable, settingsSingleton.showPast);
		mapView =   new MapView(context, getFragmentManager(), coordsTechnopolis, coordsUnderground);

		settingsView = new SettingsView(context, settingsSingleton);

		changeView(settingsSingleton.currentState);

		getUpdateTimer(1000).start(); // запускаем автообновление значений каждые (параметр) миллисекунд
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_reload:
				Snackbar.make(contentBlock, "Not available yet", Snackbar.LENGTH_LONG)
							.setAction("Action", null).show();
				return true;
			case R.id.action_settings:
				setContent(settingsView);
				return true;
			case R.id.action_change:
				Snackbar.make(contentBlock, "Not available yet", Snackbar.LENGTH_LONG)
						.setAction("Action", null).show();
				return true;
			case R.id.action_help:
				Snackbar.make(contentBlock, "Not available yet", Snackbar.LENGTH_LONG)
						.setAction("Action", null).show();
				return true;
			case R.id.action_about:
				Snackbar.make(contentBlock, "Not available yet", Snackbar.LENGTH_LONG)
						.setAction("Action", null).show();
				return true;
			default:
				Log.e("Хьюстон!", "У нас проблемы!");
				return false;
		}
	}

	@Override
	public void onStop() {
		Log.i("onStop", "Method called");
		SettingsSingleton.singleton.savePreferences(getApplicationContext());
		super.onStop();
	}

	public void setContent(SView sView) {
		Log.i("setContent", "Method called");
		contentBlock.removeAllViews();
		contentBlock.addView(sView.getView());
	}

	private CountDownTimer getUpdateTimer(long interval) {
		return new CountDownTimer(Long.MAX_VALUE, interval) {
			@Override
			public void onTick(long millisUntilFinished) {
				switch (MainActivity.settingsSingleton.currentState) {
					case SHORT_VIEW:
						shortView.updateView();
						break;
					case FULL_VIEW:
//						fullView.updateView();
						break;
					case MAP_VIEW:
//						mapView.updateView();
						break;
					default:
						Log.e("Хьюстон!", "У нас проблемы!");
				}
			}

			@Override
			public void onFinish() {
				this.start();
			}
		};
	}

	private void changeView(State state) {
		Log.i("changeView", "Method called");
		switch (state) {
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

	private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
			= new BottomNavigationView.OnNavigationItemSelectedListener() {
		@Override
		public boolean onNavigationItemSelected(@NonNull MenuItem item) {
			Log.i("listener", "bootomnavigation changed");
			switch (item.getItemId()) {
				case R.id.navigation_short: loadView(State.SHORT_VIEW); return true;
				case R.id.navigation_full:  loadView(State.FULL_VIEW ); return true;
				case R.id.navigation_map:   loadView(State.MAP_VIEW  ); return true;
				default:
					Log.e("Хьюстон!", "У нас проблемы!");
			}
			return false;
		}
	};

	private void loadView(State state) {
		settingsSingleton.currentState = state;
		switch (state) {
			case SHORT_VIEW: setContent(shortView); break;
			case FULL_VIEW:  setContent(fullView ); break;
			case MAP_VIEW:   setContent(mapView  ); mapView.prepareView(); break;
			default:
				Log.e("Хьюстон!", "У нас проблемы!");
		}
	}
}
