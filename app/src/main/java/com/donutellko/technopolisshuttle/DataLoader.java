package com.donutellko.technopolisshuttle;

import android.content.SharedPreferences;
import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.donutellko.technopolisshuttle.TimeTable.ScheduleElement;
import com.donutellko.technopolisshuttle.DataLoader.STime;
import com.google.gson.Gson;

/**
 * Created by Donut on 04.07.2017.
 */

// Получает данные из памяти и базы данных
public class DataLoader {

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
			hour = (s.charAt(0) - '0') * 10 + (s.charAt(1) - '0');
			min = (s.charAt(3) - '0') * 10 + (s.charAt(4) - '0');
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

	public TimeTable getFullJsonInfo() {
		String s;
		s = getJsonOnline();
		if (s == null)
			s = getJsonCached();
		if (s == null)
			s = getDefaultJson();

		JsonObject jsonObject= new Gson().fromJson(s, JsonObject.class);

		ScheduleElement[]
				seFrom = jsonObject.toScheduleElementArray(jsonObject.fromOffice),
				seTo = jsonObject.toScheduleElementArray(jsonObject.toOffice);

		TimeTable t = new TimeTable(seFrom, seTo);
		return t;
	}

	public String getJsonOnline() {
		String s = null;
		// TODO
		return s;
	}

	private String getJsonCached() {
		String s = null;
		s = SettingsSingleton.singleton.jsonCached_s;
		return  s;
	}

	private static String getDefaultJson() {
		String s =
				"{\"fromOffice\"=[{\"time\":\"09:30\",\"mask\":31},{\"time\":\"10:10\",\"mask\":31},{\"time\":\"10:50\",\"mask\":31},{\"time\":\"11:30\",\"mask\":31},{\"time\":\"12:10\",\"mask\":31},{\"time\":\"12:50\",\"mask\":31},{\"time\":\"13:30\",\"mask\":31},{\"time\":\"14:10\",\"mask\":31},{\"time\":\"14:50\",\"mask\":31},{\"time\":\"15:10\",\"mask\":16},{\"time\":\"15:30\",\"mask\":31},{\"time\":\"15:50\",\"mask\":31},{\"time\":\"16:00\",\"mask\":16},{\"time\":\"16:30\",\"mask\":31},{\"time\":\"16:50\",\"mask\":31},{\"time\":\"17:00\",\"mask\":31},{\"time\":\"17:10\",\"mask\":31},{\"time\":\"17:30\",\"mask\":31},{\"time\":\"17:40\",\"mask\":31},{\"time\":\"17:50\",\"mask\":31},{\"time\":\"18:00\",\"mask\":31},{\"time\":\"18:10\",\"mask\":31},{\"time\":\"18:20\",\"mask\":31},{\"time\":\"18:30\",\"mask\":31},{\"time\":\"18:40\",\"mask\":31},{\"time\":\"18:50\",\"mask\":31},{\"time\":\"19:10\",\"mask\":31},{\"time\":\"19:20\",\"mask\":31},{\"time\":\"19:30\",\"mask\":31},{\"time\":\"19:40\",\"mask\":31},{\"time\":\"19:50\",\"mask\":31},{\"time\":\"20:10\",\"mask\":31},{\"time\":\"20:45\",\"mask\":31},{\"time\":\"21:20\",\"mask\":31}],"
				+"\"toOffice\"=[{\"time\":\"07:45\",\"mask\":31},{\"time\":\"08:00\",\"mask\":31},{\"time\":\"08:10\",\"mask\":31},{\"time\":\"08:20\",\"mask\":31},{\"time\":\"08:30\",\"mask\":31},{\"time\":\"08:35\",\"mask\":31},{\"time\":\"08:40\",\"mask\":31},{\"time\":\"08:50\",\"mask\":31},{\"time\":\"09:00\",\"mask\":31},{\"time\":\"09:10\",\"mask\":31},{\"time\":\"09:15\",\"mask\":31},{\"time\":\"09:20\",\"mask\":31},{\"time\":\"09:30\",\"mask\":31},{\"time\":\"09:40\",\"mask\":31},{\"time\":\"09:50\",\"mask\":31},{\"time\":\"09:55\",\"mask\":31},{\"time\":\"10:00\",\"mask\":31},{\"time\":\"10:10\",\"mask\":31},{\"time\":\"10:20\",\"mask\":31},{\"time\":\"10:30\",\"mask\":31},{\"time\":\"10:35\",\"mask\":31},{\"time\":\"10:40\",\"mask\":31},{\"time\":\"10:50\",\"mask\":31},{\"time\":\"11:00\",\"mask\":31},{\"time\":\"11:10\",\"mask\":31},{\"time\":\"11:20\",\"mask\":31},{\"time\":\"11:30\",\"mask\":31},{\"time\":\"11:50\",\"mask\":31},{\"time\":\"12:10\",\"mask\":31},{\"time\":\"12:30\",\"mask\":31},{\"time\":\"13:10\",\"mask\":31},{\"time\":\"13:50\",\"mask\":31},{\"time\":\"14:30\",\"mask\":31},{\"time\":\"15:10\",\"mask\":31},{\"time\":\"15:30\",\"mask\":31},{\"time\":\"16:10\",\"mask\":31},{\"time\":\"16:50\",\"mask\":31},{\"time\":\"17:20\",\"mask\":31}]}";
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

	public static class SettingsSingleton {
		public static SettingsSingleton singleton = new SettingsSingleton();
		public SettingsSingleton() { }

		private String
				countToShowOnShort_s = "countToShowOnShort_s",
				currentState_s =       "currentState",
				showPast_s =           "shopPast",
				distanceToShowFrom_s = "distanceToShowFrom",
				jsonCached =           "jsonCached";

		// fields with !!!!default!!! values
		public int                      countToShowOnShort = 5;
		public MainActivity.State       currentState = MainActivity.State.SHORT_VIEW;
		public boolean                  showPast = true;
		public boolean                  showTo = true; // не сохранять!
		public float                    distanceToShowFrom = 2;
		public String                   jsonCached_s = null;


		public boolean loadPreferences(Context context) {
			SharedPreferences sp = context.getSharedPreferences("Settings", Context.MODE_PRIVATE);

			currentState =       MainActivity.State.values()[
					             sp.getInt(currentState_s, currentState.ordinal())];
			countToShowOnShort = sp.getInt(countToShowOnShort_s, countToShowOnShort);
			showPast =           sp.getBoolean(showPast_s, showPast);
			distanceToShowFrom = sp.getFloat(distanceToShowFrom_s, distanceToShowFrom);
			jsonCached_s =       sp.getString(jsonCached, jsonCached_s);

			return true;
		}

		public void savePreferences(Context context) {
			SharedPreferences.Editor sp = context.getSharedPreferences("Settings", Context.MODE_PRIVATE).edit();

			sp.putInt(countToShowOnShort_s, countToShowOnShort);
			sp.putInt(currentState_s, currentState.ordinal());
			sp.putBoolean(showPast_s, showPast);
			sp.putString(jsonCached_s, jsonCached);

			sp.apply();
			Log.i("savePreferences()", "saved " + currentState.name() + ":" + currentState.ordinal());
		}
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