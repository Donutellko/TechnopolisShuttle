package com.donutellko.technopolisshuttle;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.design.widget.Snackbar;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.Window;
import android.widget.LinearLayout;
import android.os.CountDownTimer;
import android.content.Context;
import android.view.MenuItem;
import android.os.Bundle;
import android.view.Menu;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;

import com.google.android.gms.maps.model.LatLng;

import static android.widget.Toast.LENGTH_SHORT;

public class MainActivity extends AppCompatActivity {

	public static Context applicationContext;
	public static Window getWindow;
	private static boolean needtoUpdateTimeTable = false;

	private LatLng
			coordsTechnopolis = new LatLng(59.818026, 30.327783),
			coordsUnderground = new LatLng(59.854728, 30.320958);

	Calendar curtime;
	static TimeTable timeTable;
	DataLoader dataLoader;

	static LinearLayout contentBlock; // Область контента (всё кроме нав. панели)
	static BottomNavigationView navigation;

	static ShortScheduleView shortView;
	static FullScheduleView fullView;
	static MapView mapView;
	static SettingsView settingsView;

	public static LayoutInflater layoutInflater;
	public static Settings settings = Settings.singleton;

//	LocationManager locationManager;
//	static SLocationListener locationListener;

	enum State {SHORT_VIEW, FULL_VIEW, MAP_VIEW, SETTINGS_VIEW, HELP_VIEW, ABOUT_VIEW}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		applicationContext = getApplicationContext();
		getWindow = getWindow();

		if (settings.loadPreferences(applicationContext))
			Log.i("Preferences", "loaded");
		else
			Log.i("Preferences", "not found");

		layoutInflater = getLayoutInflater();
		curtime = Calendar.getInstance();
		contentBlock = (LinearLayout) findViewById(R.id.content);

		navigation = (BottomNavigationView) findViewById(R.id.navigation);
		navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

		dataLoader = new DataLoader();
		timeTable = dataLoader.updateJsonInfo();

		shortView = new ShortScheduleView(this);
		fullView =  new FullScheduleView (this);
		mapView =   new MapView(this, getFragmentManager(), coordsTechnopolis, coordsUnderground);

		settingsView = new SettingsView(this);

		changeView(settings.currentState);

		getUpdateTimer(1000).start(); // запускаем автообновление значений каждые (параметр) миллисекунд

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}

	public static void viewNotifier(String s) {
		if (settings.noSnackbar)
			return;
		if (settings.showToast)
			viewToast(s);
		else
			viewSnackbar(s);
	}

	public static void viewSnackbar(String s) {
		Snackbar.make(contentBlock, s, Snackbar.LENGTH_SHORT)
				.setAction("Action", null).show();
	}

	public static void viewToast(String s) {
		Toast toast = Toast.makeText(applicationContext,
				s, LENGTH_SHORT);
		toast.show();
	}

	public static void updateTimeTable(TimeTable timeTable1) {
		timeTable = timeTable1;
		needtoUpdateTimeTable = true;
	}

	private void checkUpdatedTimeTable() {
		if (needtoUpdateTimeTable) {
			needtoUpdateTimeTable = false;
			if (settings.currentState == MainActivity.State.FULL_VIEW)
				fullView.updateView();
			else if (settings.currentState == State.SHORT_VIEW)
				shortView.updateView();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_reload:
//				timeTable = dataLoader.updateJsonInfo();
				dataLoader.updateJsonOnline();
				return true;
			case R.id.action_settings:
				settings.currentState = State.SETTINGS_VIEW;
				setContent(settingsView);
				return true;
			case R.id.action_change:
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://docs.google.com/spreadsheets/d/1yajaDHYL4pWad_cYUAab1C2ZypiYTDg2Vqxe3zmWDiI"));
				startActivity(browserIntent);
//				viewNotifier("Not available yet");
				return true;
			case R.id.action_help:
				settings.currentState = State.HELP_VIEW;
				viewNotifier("Not available yet");
				return true;
			case R.id.action_about:
				settings.currentState = State.ABOUT_VIEW;
				viewNotifier("Not available yet");
				return true;
			default:
				Log.e("Хьюстон!", "У нас проблемы!");
				return false;
		}
	}

	@Override
	public void onStop() {
		Log.i("onStop", "Method called");
		settings.singleton.savePreferences(getApplicationContext());
		super.onStop();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i("onDestroy", "Method called");
	}


	// При нажатии "назад" возвращение из меню в основную часть, при двойном выход
	private long backPressedTime = 0;
	@Override
	public void onBackPressed() {
		if (settings.currentState == State.FULL_VIEW || settings.currentState == State.SHORT_VIEW || settings.currentState == State.MAP_VIEW) {
			long time = Calendar.getInstance().getTimeInMillis();
			if (time - backPressedTime < LENGTH_SHORT)
				super.onBackPressed();
			else {
				viewSnackbar("Нажмите повторно для выхода");
				backPressedTime = time;
			}
		} else {
			navigation.setSelectedItemId(navigation.getSelectedItemId()); // заново вызываем текущий стейт (шорт, фул или мап)
		}
	}

	public static void setContent(SView sView) {
		Log.i("setContent", "Method called");
		contentBlock.removeAllViews();
		contentBlock.addView(sView.getView());
	}

	private CountDownTimer getUpdateTimer(long interval) {
		return new CountDownTimer(Long.MAX_VALUE, interval) {
			@Override
			public void onTick(long millisUntilFinished) {
				checkUpdatedTimeTable();
				switch (MainActivity.settings.currentState) {
					case SHORT_VIEW:
						shortView.updateView();
						break;
					case FULL_VIEW:
//						fullView.updateView();
						break;
					case MAP_VIEW:
//						mapView.updateView();
						break;
					case SETTINGS_VIEW:
//						settingsView.updateView();
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

	public static void changeView(State state) {
		Log.i("changeView", "Method called");
		switch (state) {
			case SHORT_VIEW: navigation.setSelectedItemId(R.id.navigation_short); break;
			case FULL_VIEW:  navigation.setSelectedItemId(R.id.navigation_full ); break;
			case MAP_VIEW:   navigation.setSelectedItemId(R.id.navigation_map  ); break;
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
		settings.currentState = state;
		switch (state) {
			case SHORT_VIEW: setContent(shortView); break;
			case FULL_VIEW:  setContent(fullView ); break;
			case MAP_VIEW:   setContent(mapView  ); mapView.prepareView(); break;
			default:
				Log.e("Хьюстон!", "У нас проблемы!");
		}
	}
}
