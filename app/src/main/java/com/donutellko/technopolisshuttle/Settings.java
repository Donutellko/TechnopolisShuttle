package com.donutellko.technopolisshuttle;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.donutellko.technopolisshuttle.MainActivity;

public class Settings {
	public static Settings singleton = new Settings();
	private SharedPreferences sp;

	public Settings() {
	}

	// fields with !!!!default!!! values
	public int countToShowOnShort = 5;
	public MainActivity.State currentState = MainActivity.State.SHORT_VIEW;
	public boolean showPast = true;
	public boolean showTo = true; // не сохранять!
	//public float distanceToShowFrom = 2;
	public boolean showToast = false;
	public boolean noSnackbar = false;
	public String serverIpContainerAddress = "http://freetexthost.com/ppk46tkyqd"; //"http://192.168.0.100:8081"; // "http://188.134.12.107:8081";
	public String jsonCached = null;
	public String jsonLastSync = "2017.07.14 14:54";
	public String serverIp = null;
	public int connection_timeout = 500;
	public float textSize = 22;

	// названия, под которыми сохраняется
	private String
			countToShowOnShort_s = "countToShowOnShort_s",
			currentState_s = "currentState",
			showPast_s = "showPast",
			//distanceToShowFrom_s = "distanceToShowFrom",
			jsonCached_s = "jsonCached",
			showToast_s = "showToast",
			noSnackbar_s = "noSnackbar",
			serverIpContainerAddress_s = "serverIpContainerAddress",
			serverIp_s = "serverIp",
			connection_timeout_s = "connection_timeout",
			jsonLastSync_s = "jsonLastSync",
			textSize_s = "textSize";


	public boolean loadPreferences(Context context) {
		sp = context.getSharedPreferences("settings", Context.MODE_PRIVATE);

		currentState = MainActivity.State.values()[
				sp.getInt(currentState_s, currentState.ordinal())];
		countToShowOnShort = sp.getInt(countToShowOnShort_s, countToShowOnShort);
		showPast = sp.getBoolean(showPast_s, showPast);
		//distanceToShowFrom = sp.getFloat(distanceToShowFrom_s, distanceToShowFrom);
		jsonCached = sp.getString(jsonCached_s, jsonCached);
		showToast = sp.getBoolean(showToast_s, showToast);
		noSnackbar = sp.getBoolean(noSnackbar_s, noSnackbar);
		serverIpContainerAddress = sp.getString(serverIpContainerAddress_s, serverIpContainerAddress);
		jsonLastSync = sp.getString(jsonLastSync_s, jsonLastSync);
		serverIp = sp.getString(serverIp_s, serverIp);
		connection_timeout = sp.getInt(connection_timeout_s, connection_timeout);
		textSize = sp.getFloat(textSize_s, textSize);

		Log.i("loadPreferences()", "loaded " + currentState.name() + ":" + currentState.ordinal());
		return true;
	}

	public void savePreferences(Context context) {
		SharedPreferences.Editor sp = context.getSharedPreferences("settings", Context.MODE_PRIVATE).edit();

		sp.putInt(countToShowOnShort_s, countToShowOnShort);
		if (currentState.ordinal() < 3)
			sp.putInt(currentState_s, currentState.ordinal());
		sp.putBoolean(showPast_s, showPast);
		if (jsonCached != null)
			sp.putString(jsonCached_s, jsonCached);
		sp.putBoolean(showToast_s, showToast);
		sp.putBoolean(noSnackbar_s, noSnackbar);
		sp.putString(serverIpContainerAddress_s, serverIpContainerAddress);
		sp.putString(jsonLastSync_s, jsonLastSync);
		sp.putInt(connection_timeout_s, connection_timeout);
		sp.putFloat(textSize_s, textSize);
		if (serverIp != null)
			sp.putString(serverIp_s, serverIp);

		sp.apply();
		Log.i("savePreferences()", "saved " + currentState.name() + ":" + currentState.ordinal());
	}

	public void reset() {

		sp.edit().clear().commit();
		singleton = new Settings();
		Log.i("reset", "настройки сброшены... должны быть");
		//singleton.savePreferences(MainActivity.applicationContext);

		Log.i("reset", "восстановлены значения " + singleton.connection_timeout + " и " + singleton.serverIpContainerAddress);
	}
}