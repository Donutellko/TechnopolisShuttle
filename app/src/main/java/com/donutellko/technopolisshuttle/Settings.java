package com.donutellko.technopolisshuttle;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.donutellko.technopolisshuttle.MainActivity;

public class Settings {
	public static Settings singleton = new Settings();

	public Settings() {
	}

	// fields with !!!!default!!! values
	public int countToShowOnShort = 5;
	public MainActivity.State currentState = MainActivity.State.SHORT_VIEW;
	public boolean showPast = true;
	public boolean showTo = true; // не сохранять!
	public float distanceToShowFrom = 2;
	public String jsonCached = null;
	public boolean showToast = false;
	public boolean noSnackbar = false;
	public String serverIp = "http://192.168.0.100:8081";
	public int connection_timeout = 500;

	// названия, под которыми сохраняется
	private String
			countToShowOnShort_s = "countToShowOnShort_s",
			currentState_s = "currentState",
			showPast_s = "shopPast",
			distanceToShowFrom_s = "distanceToShowFrom",
			jsonCached_s = "jsonCached",
			showToast_s = "showToast",
			noSnackbar_s = "noSnackbar",
			serverIp_s = "serverIp",
			connection_timeout_s = "connection_timeout";




	public boolean loadPreferences(Context context) {
		SharedPreferences sp = context.getSharedPreferences("settings", Context.MODE_PRIVATE);

		currentState = MainActivity.State.values()[
				sp.getInt(currentState_s, currentState.ordinal())];
		countToShowOnShort = sp.getInt(countToShowOnShort_s, countToShowOnShort);
		showPast = sp.getBoolean(showPast_s, showPast);
		distanceToShowFrom = sp.getFloat(distanceToShowFrom_s, distanceToShowFrom);
		jsonCached = sp.getString(jsonCached_s, jsonCached);
		showToast = sp.getBoolean(showToast_s, showToast);
		noSnackbar = sp.getBoolean(noSnackbar_s, noSnackbar);
		serverIp = sp.getString(serverIp_s, serverIp);
		connection_timeout = sp.getInt(connection_timeout_s, connection_timeout);

		return true;
	}

	public void savePreferences(Context context) {
		SharedPreferences.Editor sp = context.getSharedPreferences("settings", Context.MODE_PRIVATE).edit();

		sp.putInt(countToShowOnShort_s, countToShowOnShort);
		sp.putInt(currentState_s, currentState.ordinal());
		sp.putBoolean(showPast_s, showPast);
		if (jsonCached != null)
			sp.putString(jsonCached_s, jsonCached);
		sp.putBoolean(showToast_s, showToast);
		sp.putBoolean(noSnackbar_s, noSnackbar);
		sp.putString(serverIp_s, serverIp);
		sp.putInt(connection_timeout_s, connection_timeout);

		sp.apply();
		Log.i("savePreferences()", "saved " + currentState.name() + ":" + currentState.ordinal());
	}

	public void reset() {
		singleton = new Settings();
		singleton.savePreferences(MainActivity.applicationContext);
	}
}