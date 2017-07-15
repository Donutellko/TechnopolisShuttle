package com.donutellko.technopolisshuttle;

import android.content.SharedPreferences;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.donutellko.technopolisshuttle.TimeTable.ScheduleElement;
import com.donutellko.technopolisshuttle.DataLoader.STime;
import com.google.gson.Gson;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Donut on 04.07.2017.
 */

// Получает данные из памяти, базы данных и сервера
public class DataLoader {
	public static DataLoader singleton = new DataLoader();

	public static class STime {
		int hour, min;

		public STime(int hour, int min) {
			this.hour = hour;
			this.min = min;
		}

		@Deprecated
		public STime(Date d) {
			hour = d.getHours();
			min = d.getMinutes();
		}

		@Deprecated
		public STime(java.sql.Time t) {
			hour = t.getHours();
			min = t.getMinutes();
		}

		public STime(String s) { // исключительно вида "09:15:
			if (s.length() == 5) {
				hour = (s.charAt(0) - '0') * 10 + (s.charAt(1) - '0');
				min = (s.charAt(3) - '0') * 10 + (s.charAt(4) - '0');
			} else if (s.length() == 4) {
				hour = (s.charAt(0) - '0');
				min = (s.charAt(2) - '0') * 10 + (s.charAt(3) - '0');
			} else Log.e("WRONG!", "Неправильный формат времени: " + s);
		}

		public boolean isBefore(STime t) {
			if (hour < t.hour) return true;
			if (hour == t.hour && min < t.min) return true;
			return false;
		}

		public STime getDifference(STime t) {
			int dh = t.hour - hour;
			int dm = t.min - min;
			if (dm < 0 && dh > 0) {
				dh--;
				dm += 60;
			}
			return new STime(dh, dm);
		}

		public ArrayList<STime> getListAfter(STime[] arr) {
			ArrayList<STime> list = new ArrayList<STime>();
			for (STime t : arr)
				if (this.isBefore(t)) list.add(t);
			return list;
		}

		public int compareTo(STime t) {
			if (hour < t.hour)
				return -1;
			if (hour == t.hour && min == t.min)
				return 0;
			return 1;
		}

		@Override
		public String toString() {
			return hour + ":" + min;
		}

		public boolean isZero() {
			return hour == 0 && min == 0;
		}
	}

	public TimeTable updateJsonInfo() {
		updateJsonOnline();
		return getJsonTimeTable(getJsonOffline());
	}

	public static TimeTable getJsonTimeTable(String s) {
		JsonObject jsonObject = new Gson().fromJson(s, JsonObject.class);

		ScheduleElement[]
				seFrom = jsonObject.toScheduleElementArray(jsonObject.fromOffice),
				seTo = jsonObject.toScheduleElementArray(jsonObject.toOffice);

		TimeTable t = new TimeTable(seFrom, seTo);
		return t;
	}

	public String getJsonOffline() {
		String s;
		s = getJsonCached();
		if (s == null)
			s = getJsonDefault();

		Log.i("Got offline JSON: ", s);

		return s;
	}

	public void updateJsonOnline() {
//		AsyncTask<String, Void, String> lol =
//		new JsonGetter().execute(Settings.singleton.serverIpContainerAddress + "/schedule");
		new JsonGetter().execute();
	}

	//@Deprecated
	//public void getJsonOnline() {
	//	Log.i("getJsonOnline()", "trying to load online");
	//	AsyncTask<String, Void, Void> lol = new JsonGetter().execute(Settings.singleton.serverIpContainerAddress + "/schedule");
	//}

	private String getJsonCached() {
		Log.i("getJsonCaches()", "trying to load from cache");
		String s = null;
		s = Settings.singleton.jsonCached;
		return s;
	}

	private static String getJsonDefault() {
		Log.i("getJsonDefault()", "getting default json");
		String s =
				"{\"fromOffice\"=[{\"time\":\"09:30\",\"mask\":31},{\"time\":\"10:10\",\"mask\":31},{\"time\":\"10:50\",\"mask\":31},{\"time\":\"11:30\",\"mask\":31},{\"time\":\"12:10\",\"mask\":31},{\"time\":\"12:50\",\"mask\":31},{\"time\":\"13:30\",\"mask\":31},{\"time\":\"14:10\",\"mask\":31},{\"time\":\"14:50\",\"mask\":31},{\"time\":\"15:10\",\"mask\":16},{\"time\":\"15:30\",\"mask\":31},{\"time\":\"15:50\",\"mask\":31},{\"time\":\"16:00\",\"mask\":16},{\"time\":\"16:30\",\"mask\":31},{\"time\":\"16:50\",\"mask\":31},{\"time\":\"17:00\",\"mask\":31},{\"time\":\"17:10\",\"mask\":31},{\"time\":\"17:30\",\"mask\":31},{\"time\":\"17:40\",\"mask\":31},{\"time\":\"17:50\",\"mask\":31},{\"time\":\"18:00\",\"mask\":31},{\"time\":\"18:10\",\"mask\":31},{\"time\":\"18:20\",\"mask\":31},{\"time\":\"18:30\",\"mask\":31},{\"time\":\"18:40\",\"mask\":31},{\"time\":\"18:50\",\"mask\":31},{\"time\":\"19:10\",\"mask\":31},{\"time\":\"19:20\",\"mask\":31},{\"time\":\"19:30\",\"mask\":31},{\"time\":\"19:40\",\"mask\":31},{\"time\":\"19:50\",\"mask\":31},{\"time\":\"20:10\",\"mask\":31},{\"time\":\"20:45\",\"mask\":31},{\"time\":\"21:20\",\"mask\":31}],"
						+ "\"toOffice\"=[{\"time\":\"07:45\",\"mask\":31},{\"time\":\"08:00\",\"mask\":31},{\"time\":\"08:10\",\"mask\":31},{\"time\":\"08:20\",\"mask\":31},{\"time\":\"08:30\",\"mask\":31},{\"time\":\"08:35\",\"mask\":31},{\"time\":\"08:40\",\"mask\":31},{\"time\":\"08:50\",\"mask\":31},{\"time\":\"09:00\",\"mask\":31},{\"time\":\"09:10\",\"mask\":31},{\"time\":\"09:15\",\"mask\":31},{\"time\":\"09:20\",\"mask\":31},{\"time\":\"09:30\",\"mask\":31},{\"time\":\"09:40\",\"mask\":31},{\"time\":\"09:50\",\"mask\":31},{\"time\":\"09:55\",\"mask\":31},{\"time\":\"10:00\",\"mask\":31},{\"time\":\"10:10\",\"mask\":31},{\"time\":\"10:20\",\"mask\":31},{\"time\":\"10:30\",\"mask\":31},{\"time\":\"10:35\",\"mask\":31},{\"time\":\"10:40\",\"mask\":31},{\"time\":\"10:50\",\"mask\":31},{\"time\":\"11:00\",\"mask\":31},{\"time\":\"11:10\",\"mask\":31},{\"time\":\"11:20\",\"mask\":31},{\"time\":\"11:30\",\"mask\":31},{\"time\":\"11:50\",\"mask\":31},{\"time\":\"12:10\",\"mask\":31},{\"time\":\"12:30\",\"mask\":31},{\"time\":\"13:10\",\"mask\":31},{\"time\":\"13:50\",\"mask\":31},{\"time\":\"14:30\",\"mask\":31},{\"time\":\"15:10\",\"mask\":31},{\"time\":\"15:30\",\"mask\":31},{\"time\":\"16:10\",\"mask\":31},{\"time\":\"16:50\",\"mask\":31},{\"time\":\"17:20\",\"mask\":31}]}";
		return s;
	}

	private class JsonObject {
		DataObject[] fromOffice, toOffice;

		public ScheduleElement[] toScheduleElementArray(DataObject[] dataObjects) {
			ScheduleElement[] scheduleElements = new ScheduleElement[dataObjects.length];
			for (int i = 0; i < dataObjects.length; i++)
				scheduleElements[i] = dataObjects[i].toScheduleElement();
			return scheduleElements;
		}

		class DataObject {
			String time;
			int mask;

			public ScheduleElement toScheduleElement() {
				return new ScheduleElement(new STime(time), mask);
			}
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
		if (d1.hour < d2.hour)
			return true;
		if (d1.hour == d2.hour && d1.min < d2.min)
			return true;
		return false;
	}


}

class TimeTable {
	public ScheduleElement[] from, to;

	public TimeTable(ScheduleElement[] from, ScheduleElement[] to) {
		this.from = from;
		this.to = to;
	}

	public List<ScheduleElement> getTimeAfter(STime now, boolean To, int weekday) {
		List<ScheduleElement> result = new ArrayList<>();

		for (ScheduleElement t : (To ? to : from))
			if (now.isBefore(t.time) && t.worksAt(weekday)) {
				result.add(t);
			}

		return result;
	}

	static class ScheduleElement {
		public STime time;
		public int mask;

		public ScheduleElement(STime time, int mask) {
			this.time = time;
			this.mask = mask;
		}

		public ScheduleElement(int hour, int min, int mask) {
			this.time = new STime(hour, min);
			this.mask = mask;
		}

		public boolean worksAt(int weekday) { // номер дня недели, начиная с нуля
			int m = 1 << weekday;
			return ((mask & m) != 0);
		}
	}

}


class JsonGetter extends AsyncTask<Void, Void, Void> {
	@Override
	protected Void doInBackground(Void... voids) {
		try {
			String s = (getDataFromUrl(getServerIp() + "/schedule"));
			if (s.equals("") || s.equals("{\"fromOffice\":[],\"toOffice\":[]}"))
				throw new Exception("Пришёл такой се ответ!!!");

			Settings.singleton.jsonCached = s;
			Settings.singleton.savePreferences(MainActivity.applicationContext);
			Date date = Calendar.getInstance().getTime();
			Settings.singleton.jsonLastSync =
					1900 + date.getYear() + "." + date.getMonth() + "." + date.getDay() + " " +
					date.getHours() + ":" + date.getMinutes();
			MainActivity.viewNotifier("Расписание синхронизировано!");
			MainActivity.updateTimeTable(DataLoader.singleton.getJsonTimeTable(s));
		} catch (Exception e) {
			MainActivity.viewNotifier("Не синхронизировано с " + Settings.singleton.jsonLastSync);
			e.printStackTrace();
		}
		return null;
	}

	public static String getDataFromUrl(String url_s) throws Exception {
		String result = null;

		BufferedReader reader = null;
		URLConnection uc = null;

		try {
			URL url = new URL(url_s);
			uc = url.openConnection();
			uc.setConnectTimeout(Settings.singleton.connection_timeout);
			uc.connect();
			reader = new BufferedReader(new InputStreamReader(uc.getInputStream()));
			StringBuffer buffer = new StringBuffer();
			int read;
			char[] chars = new char[1024];
			while ((read = reader.read(chars)) != -1)
				buffer.append(chars, 0, read);

			result = buffer.toString();
		} finally {
			if (reader != null)
				reader.close();
		}

		Log.i("GOT ONLINE DATA!!!", result);
		return result;
	}

	public static String getServerIp() {
		String url = Settings.singleton.serverIpContainerAddress;
		try {
			String page = getDataFromUrl(url);
			int begin = page.indexOf("URL=");
			int end = page.indexOf("=URL");
			String result = page.substring(begin + 4, end);
			Log.e("FOUND URL", result);
			Settings.singleton.serverIp = result;
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
