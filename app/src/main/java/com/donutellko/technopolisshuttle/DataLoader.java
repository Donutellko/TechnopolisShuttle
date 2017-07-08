package com.donutellko.technopolisshuttle;

import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.donutellko.technopolisshuttle.DataLoader.STime;
import com.donutellko.technopolisshuttle.TimeTable.ScheduleElement;

/**
 * Created by Donut on 04.07.2017.
 */

// Получает данные из памяти и базы данных
public class DataLoader {

	public static class STime {
		int hour, min;

		public STime (int hour, int min) {
			this.hour = hour;
			this.min = min;
		}

		@Deprecated
		public STime (Date d) {
			hour = d.getHours();
			min = d.getMinutes();
		}

		@Deprecated
		public STime (java.sql.Time t) {
			hour = t.getHours();
			min = t.getMinutes();
		}

		public boolean isBefore(STime t) {
			if (hour < t.hour) return true;
			if (hour == t.hour && min < t.min) return true;
			return false;
		}

		public STime getDifference (STime t) {
			int dh = t.hour - hour;
			int dm = t.min - min;
			if (dm < 0 && dh > 0) {
				dh--;
				dm += 60;
			}
			return new STime(dh, dm);
		}

		public ArrayList<STime> getListAfter (STime[] arr) {
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

		public String toString() {
			return hour + ":" + min;
		}

		public boolean isZero() {
			return hour == 0 && min == 0;
		}
	}

	public TimeTable getFullDefaultInfo() {
		ScheduleElement[] defaultTimeFrom = new ScheduleElement[] {
				new ScheduleElement(7, 45, 31),

				new ScheduleElement(8, 00, 31),
				new ScheduleElement(8, 10, 31),
				new ScheduleElement(8, 20, 31),
				new ScheduleElement(8, 30, 31),
				new ScheduleElement(8, 35, 31),
				new ScheduleElement(8, 40, 31),
				new ScheduleElement(8, 50, 31),

				new ScheduleElement(9, 00, 31),
				new ScheduleElement(9, 10, 31),
				new ScheduleElement(9, 15, 31),
				new ScheduleElement(9, 20, 31),
				new ScheduleElement(9, 30, 31),
				new ScheduleElement(9, 40, 31),
				new ScheduleElement(9, 50, 31),
				new ScheduleElement(9, 55, 31),

				new ScheduleElement(10, 00, 31),
				new ScheduleElement(10, 10, 31),
				new ScheduleElement(10, 20, 31),
				new ScheduleElement(10, 30, 31),
				new ScheduleElement(10, 35, 31),
				new ScheduleElement(10, 40, 31),
				new ScheduleElement(10, 50, 31),

				new ScheduleElement(11, 00, 31),
				new ScheduleElement(11, 10, 31),
				new ScheduleElement(11, 20, 31),
				new ScheduleElement(11, 30, 31),
				new ScheduleElement(11, 50, 31),

				new ScheduleElement(12, 10, 31),
				new ScheduleElement(12, 30, 31),

				new ScheduleElement(13, 10, 31),
				new ScheduleElement(13, 50, 31),

				new ScheduleElement(14, 30, 31),

				new ScheduleElement(15, 10, 31),
				new ScheduleElement(15, 30, 31),

				new ScheduleElement(16, 10, 31),
				new ScheduleElement(16, 50, 31),

				new ScheduleElement(17, 20, 31),
		};

		ScheduleElement[] defaultTimeTo = new ScheduleElement[]{
				new ScheduleElement(9, 30, 31),

				new ScheduleElement(10, 10, 31),
				new ScheduleElement(10, 50, 31),

				new ScheduleElement(11, 30, 31),

				new ScheduleElement(12, 10, 31),
				new ScheduleElement(12, 50, 31),

				new ScheduleElement(13, 30, 31),

				new ScheduleElement(14, 10, 31),
				new ScheduleElement(14, 50, 31),

				new ScheduleElement(15, 10, 31),
				new ScheduleElement(15, 30, 31),
				new ScheduleElement(15, 50, 31),

				new ScheduleElement(16, 00, 31),
				new ScheduleElement(16, 30, 31),
				new ScheduleElement(16, 50, 31),

				new ScheduleElement(17, 00, 31),
				new ScheduleElement(17, 10, 31),
				new ScheduleElement(17, 30, 31),
				new ScheduleElement(17, 40, 31),
				new ScheduleElement(17, 50, 31),

				new ScheduleElement(18, 00, 31),
				new ScheduleElement(18, 10, 31),
				new ScheduleElement(18, 20, 31),
				new ScheduleElement(18, 30, 31),
				new ScheduleElement(18, 40, 31),
				new ScheduleElement(18, 50, 31),

				new ScheduleElement(19, 10, 31),
				new ScheduleElement(19, 20, 31),
				new ScheduleElement(19, 30, 31),
				new ScheduleElement(19, 40, 31),
				new ScheduleElement(19, 50, 31),

				new ScheduleElement(20, 10, 31),
				new ScheduleElement(20, 45, 31),

				new ScheduleElement(21, 20, 31),

		};
		return new TimeTable(defaultTimeFrom, defaultTimeTo);
	}

	public TimeTable getFullSavedInfo() {
		//TODO
		return getFullDefaultInfo();
	}

	public TimeTable getFullJsonInfo() {
		String json = getJson();
		ScheduleElement[][] fromAndTo = null;
		//= new Gson().fromJson()

		TimeTable t = new TimeTable(fromAndTo[0], fromAndTo[1]);
		return t;
	}

	public String getJson() {
		// TODO
		return getDefaultJson();
	}

	private String getDefaultJson() {
		String s = "[{\"time\":\"09:30\",\"mask\":31},{\"time\":\"10:10\",\"mask\":31},{\"time\":\"10:50\",\"mask\":31},{\"time\":\"11:30\",\"mask\":31},{\"time\":\"12:10\",\"mask\":31},{\"time\":\"12:50\",\"mask\":31},{\"time\":\"13:30\",\"mask\":31},{\"time\":\"14:10\",\"mask\":31},{\"time\":\"14:50\",\"mask\":31},{\"time\":\"15:10\",\"mask\":15},{\"time\":\"15:30\",\"mask\":31},{\"time\":\"15:50\",\"mask\":31},{\"time\":\"16:00\",\"mask\":15},{\"time\":\"16:30\",\"mask\":31},{\"time\":\"16:50\",\"mask\":31},{\"time\":\"17:00\",\"mask\":31},{\"time\":\"17:10\",\"mask\":31},{\"time\":\"17:30\",\"mask\":31},{\"time\":\"17:40\",\"mask\":31},{\"time\":\"17:50\",\"mask\":31},{\"time\":\"18:00\",\"mask\":31},{\"time\":\"18:10\",\"mask\":31},{\"time\":\"18:20\",\"mask\":31},{\"time\":\"18:30\",\"mask\":31},{\"time\":\"18:40\",\"mask\":31},{\"time\":\"18:50\",\"mask\":31},{\"time\":\"19:10\",\"mask\":31},{\"time\":\"19:20\",\"mask\":31},{\"time\":\"19:30\",\"mask\":31},{\"time\":\"19:40\",\"mask\":31},{\"time\":\"19:50\",\"mask\":31},{\"time\":\"20:10\",\"mask\":31},{\"time\":\"20:45\",\"mask\":31},{\"time\":\"21:20\",\"mask\":31}]						[{\"time\":\"07:45\",\"mask\":31},{\"time\":\"08:00\",\"mask\":31},{\"time\":\"08:10\",\"mask\":31},{\"time\":\"08:20\",\"mask\":31},{\"time\":\"08:30\",\"mask\":31},{\"time\":\"08:35\",\"mask\":31},{\"time\":\"08:40\",\"mask\":31},{\"time\":\"08:50\",\"mask\":31},{\"time\":\"09:00\",\"mask\":31},{\"time\":\"09:10\",\"mask\":31},{\"time\":\"09:15\",\"mask\":31},{\"time\":\"09:20\",\"mask\":31},{\"time\":\"09:30\",\"mask\":31},{\"time\":\"09:40\",\"mask\":31},{\"time\":\"09:50\",\"mask\":31},{\"time\":\"09:55\",\"mask\":31},{\"time\":\"10:00\",\"mask\":31},{\"time\":\"10:10\",\"mask\":31},{\"time\":\"10:20\",\"mask\":31},{\"time\":\"10:30\",\"mask\":31},{\"time\":\"10:35\",\"mask\":31},{\"time\":\"10:40\",\"mask\":31},{\"time\":\"10:50\",\"mask\":31},{\"time\":\"11:00\",\"mask\":31},{\"time\":\"11:10\",\"mask\":31},{\"time\":\"11:20\",\"mask\":31},{\"time\":\"11:30\",\"mask\":31},{\"time\":\"11:50\",\"mask\":31},{\"time\":\"12:10\",\"mask\":31},{\"time\":\"12:30\",\"mask\":31},{\"time\":\"13:10\",\"mask\":31},{\"time\":\"13:50\",\"mask\":31},{\"time\":\"14:30\",\"mask\":31},{\"time\":\"15:10\",\"mask\":31},{\"time\":\"15:30\",\"mask\":31},{\"time\":\"16:10\",\"mask\":31},{\"time\":\"16:50\",\"mask\":31},{\"time\":\"17:20\",\"mask\":31}]";
		return s;
	}


}

class TimeTable {
	public ScheduleElement[] from, to;

	public TimeTable(ScheduleElement[] from, ScheduleElement[] to) {
		this.from = from;
		this.to = to;
	}

	public TimeTable(List<ScheduleElement> from, List<ScheduleElement> to) {
		this.from = (ScheduleElement[]) from.toArray();
		this.to = (ScheduleElement[]) to.toArray();
	}

	public List<ScheduleElement> getTimeAfter (STime now, boolean To) {
		List<ScheduleElement> result = new ArrayList<>();
		for (ScheduleElement t : (To ? to : from))
			if (now.isBefore(t.time))
				result.add(t);

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