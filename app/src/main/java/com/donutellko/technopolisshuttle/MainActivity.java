package com.donutellko.technopolisshuttle;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.design.widget.Snackbar;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
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
	public static boolean settingsUpdated = false;


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

	static ColorStateList defaultColors;

	enum State {SHORT_VIEW, FULL_VIEW, MAP_VIEW, SETTINGS_VIEW, HELP_VIEW, ACTION_WEB, ABOUT_VIEW}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);


		ActionBar actionBar = getSupportActionBar();
		actionBar.setTitle("");
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setIcon(R.mipmap.ic_tech);
		applicationContext = getApplicationContext();
		getWindow = getWindow();

		if (Settings.singleton.loadPreferences(applicationContext))
			Log.i("Preferences", "loaded");
		else
			Log.i("Preferences", "not found");

		layoutInflater = getLayoutInflater();
		curtime = Calendar.getInstance();
		contentBlock = (LinearLayout) findViewById(R.id.content);

		navigation = (BottomNavigationView) findViewById(R.id.navigation);
		defaultColors = navigation.getItemTextColor();
		navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

		dataLoader = new DataLoader(this);
		timeTable = dataLoader.updateJsonInfo();

		shortView = new ShortScheduleView(this);
		fullView = new FullScheduleView(this);
		mapView = new MapView(this, getFragmentManager(), timeTable.coordsTo, timeTable.coordsFr);

		settingsView = new SettingsView(this);

		getUpdateTimer(500).start(); // запускаем автообновление значений каждые (параметр) миллисекунд

		changeView(Settings.singleton.currentState);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}

	public static void viewNotifier(String s) {
		if (Settings.singleton.noSnackbar)
			return;
		if (Settings.singleton.showToast)
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
			if (Settings.singleton.currentState == MainActivity.State.FULL_VIEW)
				fullView.updateView();
			else if (Settings.singleton.currentState == State.SHORT_VIEW)
				shortView.updateView();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		final WebView webView;
		Intent browserIntent;
		switch (item.getItemId()) {
			case R.id.action_reload:
//				timeTable = dataLoader.updateJsonInfo();
				dataLoader.updateJsonOnline();
				return true;
			case R.id.action_settings:
				Settings.singleton.currentState = State.SETTINGS_VIEW;
				setContent(settingsView);
				navigation.setVisibility(View.INVISIBLE);
				return true;
			case R.id.action_web:
				navigation.setVisibility(View.VISIBLE);
				navigation.setItemTextColor(ColorStateList.valueOf(Color.GRAY));
				navigation.setItemIconTintList(ColorStateList.valueOf(Color.GRAY));

				Settings.singleton.currentState = State.ACTION_WEB;
				browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse((Settings.singleton.serverIp != null ? Settings.singleton.serverIp : "http://188.134.12.107:8081") + "/index.html"));
				startActivity(browserIntent);
				return true;
			case R.id.action_change:
				navigation.setVisibility(View.VISIBLE);
				navigation.setItemTextColor(ColorStateList.valueOf(Color.GRAY));
				navigation.setItemIconTintList(ColorStateList.valueOf(Color.GRAY));

				browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://docs.google.com/spreadsheets/d/1yajaDHYL4pWad_cYUAab1C2ZypiYTDg2Vqxe3zmWDiI"));
				startActivity(browserIntent);
//				viewNotifier("Not available yet");
				return true;
			case R.id.action_help:
				viewNotifier(getString(R.string.loading));
				navigation.setVisibility(View.VISIBLE);
				navigation.setItemTextColor(ColorStateList.valueOf(Color.GRAY));
				navigation.setItemIconTintList(ColorStateList.valueOf(Color.GRAY));

				Settings.singleton.currentState = State.HELP_VIEW;
				webView = new WebView(this);
				webView.getSettings().setSupportZoom(true);
				webView.getSettings().setBuiltInZoomControls(true);
				webView.setWebViewClient(new WebViewClient());
				webView.setWebChromeClient(new WebChromeClient());
				new Runnable() {
					public void run() {
						webView.getSettings().setDisplayZoomControls(false);
					}
				}.run();
				webView.loadUrl("https://github.com/Donutellko/TechnopolisShuttle/wiki");
				setContent(webView);
				return true;
			case R.id.action_about:
				Settings.singleton.currentState = State.ABOUT_VIEW;
				webView = new WebView(this);
				webView.getSettings().setSupportZoom(true);
				webView.getSettings().setBuiltInZoomControls(true);
				webView.setWebViewClient(new WebViewClient());
				webView.setWebChromeClient(new WebChromeClient());
				new Runnable() {
					public void run() {
						webView.getSettings().setDisplayZoomControls(false);
					}
				}.run();
				webView.loadUrl("//http://www.technopolis.fi/russia/arenduemye-ofisnye-ploshhadi/sankt-peterburg/");
				setContent(webView);
				return true;
			default:
				Log.e("Хьюстон!", "У нас проблемы!");
				return false;
		}
	}

	//@Override
	//public void onPause() {
	//	settings.savePreferences(getApplicationContext());
	//	super.onPause();
	//}

	@Override
	public void onPause() {
		Settings.singleton.savePreferences(getApplicationContext());
		super.onPause();
	}


	// При нажатии "назад" возвращение из меню в основную часть, при двойном выход
	private long backPressedTime = 0;

	@Override
	public void onBackPressed() {
		navigation.setVisibility(View.VISIBLE);
		if (
				Settings.singleton.currentState == State.FULL_VIEW
						|| Settings.singleton.currentState == State.SHORT_VIEW
						|| Settings.singleton.currentState == State.MAP_VIEW) {
			long time = Calendar.getInstance().getTimeInMillis();
			if (time - backPressedTime < 1000)
				super.onBackPressed();
			else {
				viewSnackbar(getString(R.string.click_again));
				backPressedTime = time;
			}
		} else {
			navigation.setSelectedItemId(navigation.getSelectedItemId()); // заново вызываем текущий стейт (шорт, фул или мап)
		}
	}

	public static void setContent(SView sView) {
		contentBlock.removeAllViews();
		contentBlock.addView(sView.getView());
	}

	public static void setContent(View view) {
		contentBlock.removeAllViews();
		contentBlock.addView(view);
	}

	private CountDownTimer getUpdateTimer(long interval) {
		return new CountDownTimer(Long.MAX_VALUE, interval) {
			@Override
			public void onTick(long millisUntilFinished) {
				checkUpdatedTimeTable();
				switch (Settings.singleton.currentState) {
					case SHORT_VIEW:
						navigation.setItemTextColor(defaultColors);
						navigation.setItemIconTintList(defaultColors);
						shortView.updateView();
						break;
					case FULL_VIEW:
						navigation.setItemTextColor(defaultColors);
						navigation.setItemIconTintList(defaultColors);
						if (settingsUpdated) {
							settingsUpdated = false;
							fullView.updateView();
						}
						break;
					case MAP_VIEW:
						navigation.setItemTextColor(defaultColors);
						navigation.setItemIconTintList(defaultColors);
//						mapView.updateView();
						break;
					case SETTINGS_VIEW:
//						settingsView.updateView();
//						navigation.setVisibility(View.INVISIBLE);
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
			switch (item.getItemId()) {
				case R.id.navigation_short:
					loadView(State.SHORT_VIEW);
					return true;
				case R.id.navigation_full:
					loadView(State.FULL_VIEW);
					return true;
				case R.id.navigation_map:
					loadView(State.MAP_VIEW);
					return true;
			}
			return false;
		}
	};

	private void loadView(State state) {
		Settings.singleton.currentState = state;
		switch (state) {
			case SHORT_VIEW:
				setContent(shortView);
				break;
			case FULL_VIEW:
				setContent(fullView);
				break;
			case MAP_VIEW:
				setContent(mapView);
				mapView.prepareView();
				break;
			default:
				Log.e("Хьюстон!", "У нас проблемы!");
		}
	}
	public String[] getWeekdays(){
		return getResources().getStringArray(R.array.weekdays_short);
	}

}
