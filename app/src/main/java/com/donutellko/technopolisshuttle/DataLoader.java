package com.donutellko.technopolisshuttle;

import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.donutellko.technopolisshuttle.DataLoader.STime;

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
		return new TimeTable(defaultTimeFrom, defaultTimeTo);
	}

	public TimeTable getFullSavedInfo() {
		//TODO
		return getFullDefaultInfo();
	}

	public TimeTable getFullDBInfo() {
		//TODO
		return getFullDefaultInfo();
	}

	STime[] defaultTimeFrom = new STime[] {
			new STime(7, 45),

			new STime(8, 00),
			new STime(8, 10),
			new STime(8, 20),
			new STime(8, 30),
			new STime(8, 35),
			new STime(8, 40),
			new STime(8, 50),

			new STime(9, 00),
			new STime(9, 10),
			new STime(9, 15),
			new STime(9, 20),
			new STime(9, 30),
			new STime(9, 40),
			new STime(9, 50),
			new STime(9, 55),

			new STime(10, 00),
			new STime(10, 10),
			new STime(10, 20),
			new STime(10, 30),
			new STime(10, 35),
			new STime(10, 40),
			new STime(10, 50),

			new STime(11, 00),
			new STime(11, 10),
			new STime(11, 20),
			new STime(11, 30),
			new STime(11, 50),

			new STime(12, 10),
			new STime(12, 30),

			new STime(13, 10),
			new STime(13, 50),

			new STime(14, 30),

			new STime(15, 10),
			new STime(15, 30),

			new STime(16, 10),
			new STime(16, 50),

			new STime(17, 20),
	};

	STime[] defaultTimeTo = new STime[]{
			new STime(9, 30),

			new STime(10, 10),
			new STime(10, 50),

			new STime(11, 30),

			new STime(12, 10),
			new STime(12, 50),

			new STime(13, 30),

			new STime(14, 10),
			new STime(14, 50),

			new STime(15, 10),
			new STime(15, 30),
			new STime(15, 50),

			new STime(16, 00),
			new STime(16, 30),
			new STime(16, 50),

			new STime(17, 00),
			new STime(17, 10),
			new STime(17, 30),
			new STime(17, 40),
			new STime(17, 50),

			new STime(18, 00),
			new STime(18, 10),
			new STime(18, 20),
			new STime(18, 30),
			new STime(18, 40),
			new STime(18, 50),

			new STime(19, 10),
			new STime(19, 20),
			new STime(19, 30),
			new STime(19, 40),
			new STime(19, 50),

			new STime(20, 10),
			new STime(20, 45),

			new STime(21, 20),

	};
}

class TimeTable {
	public List<Line> lines;
	public STime[] from, to;

	public TimeTable(STime[] from, STime[] to) {
		lines = new ArrayList<Line>();
		this.from = from;
		this.to = to;

		Log.i("lol", "создаём таблицу");
		int i = 0, j = 0;
		while (i < from.length) {
			if (j < to.length) {
				switch (from[i].compareTo(to[j])) {
					case -1:
						lines.add(new Line(from[i], true, false));
						i++;
						break;
					case 0:
						lines.add(new Line(from[i], true, true));
						i++;
						j++;
						break;
					case 1:
						lines.add(new Line(to[j], true, false));
						j++;
						break;
				}
			} else {
				lines.add(new Line(from[i], true, false));
				i++;
			}
		}

		while (j < to.length) {
			lines.add(new Line(to[j], false, true));
			j++;
		}

		Log.i("lol", this.toString());
	}

	public List<STime> getTimeAfter (STime now, boolean To) {
		Log.i("lolaofaosdjfsdfoajsf", to.length + " " + from.length + " " + To);
		STime[] cur = To ? to : from;
		List<STime> result = new ArrayList<>();

		for (STime t : cur)
			if (now.isBefore(t))
				result.add(t);

		return result;
	}

	public TimeTable(Line[] lines) {
		this.lines = new ArrayList<Line>(lines.length);
		for (Line l : lines)
			this.lines.add(l);
	}

	@Override
	public String toString() {
		String s = "";
		for (Line l : lines)
			s += "\n" + l;
		return s;
	}

	class Line {
		public STime time;
		public boolean from, to;

		public Line(STime time, boolean from, boolean to) {
			this.time = time;
			this.from = from;
			this.to = to;
		}

		public boolean isBefore(STime current) {
			int curH = current.hour, schedH = time.hour,
					curM = current.min, schedM = time.min;

			if (curH > schedH) return true;
			else if (curH < schedH) return false;
			return curM > schedM;
		}

		@Override
		public String toString() {
			return time + " " + (from ? "+" : "-") + " " + (to ? "+" : "-");
		}
	}

}