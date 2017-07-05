package com.donutellko.technopolisshuttle;

import android.util.Log;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Donut on 04.07.2017.
 */

// Получает данные из памяти и базы данных
public class DataLoader {

	@SuppressWarnings("deprecation")
	Time[] defaultTimeFrom = new Time[]{
			new Time(7, 45, 0),

			new Time(8, 00, 0),
			new Time(8, 10, 0),
			new Time(8, 20, 0),
			new Time(8, 30, 0),
			new Time(8, 35, 0),
			new Time(8, 40, 0),
			new Time(8, 50, 0),

			new Time(9, 00, 0),
			new Time(9, 10, 0),
			new Time(9, 15, 0),
			new Time(9, 20, 0),
			new Time(9, 30, 0),
			new Time(9, 40, 0),
			new Time(9, 50, 0),
			new Time(9, 55, 0),

			new Time(10, 00, 0),
			new Time(10, 10, 0),
			new Time(10, 20, 0),
			new Time(10, 30, 0),
			new Time(10, 35, 0),
			new Time(10, 40, 0),
			new Time(10, 50, 0),

			new Time(11, 00, 0),
			new Time(11, 10, 0),
			new Time(11, 20, 0),
			new Time(11, 30, 0),
			new Time(11, 50, 0),

			new Time(12, 10, 0),
			new Time(12, 30, 0),

			new Time(13, 10, 0),
			new Time(13, 50, 0),

			new Time(14, 30, 0),

			new Time(15, 10, 0),
			new Time(15, 30, 0),

			new Time(16, 10, 0),
			new Time(16, 50, 0),

			new Time(17, 20, 0),
	};

	@SuppressWarnings("deprecation")
	Time[] defaultTimeTo = new Time[]{
			new Time(9, 30, 0),

			new Time(10, 10, 0),
			new Time(10, 50, 0),

			new Time(11, 30, 0),

			new Time(12, 10, 0),
			new Time(12, 50, 0),

			new Time(13, 30, 0),

			new Time(14, 10, 0),
			new Time(14, 50, 0),

			new Time(15, 10, 0),
			new Time(15, 30, 0),
			new Time(15, 50, 0),

			new Time(16, 00, 0),
			new Time(16, 30, 0),
			new Time(16, 50, 0),

			new Time(17, 00, 0),
			new Time(17, 10, 0),
			new Time(17, 30, 0),
			new Time(17, 40, 0),
			new Time(17, 50, 0),

			new Time(18, 00, 0),
			new Time(18, 10, 0),
			new Time(18, 20, 0),
			new Time(18, 30, 0),
			new Time(18, 40, 0),
			new Time(18, 50, 0),

			new Time(19, 10, 0),
			new Time(19, 20, 0),
			new Time(19, 30, 0),
			new Time(19, 40, 0),
			new Time(19, 50, 0),

			new Time(20, 10, 0),
			new Time(20, 45, 0),

			new Time(21, 20, 0),

	};


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
}

class TimeTable {
	public List<Line> lines_3;
	public Time[] from, to;

	public TimeTable(Time[] from, Time[] to) {
		lines_3 = new ArrayList<Line>();
		this.from = from;
		this.to = to;

		Log.i("lol", "создаём таблицу");
		int i = 0, j = 0;
		while (i < from.length) {
			if (j < to.length) {
				switch (from[i].compareTo(to[j])) {
					case -1:
						lines_3.add(new Line(from[i], false, true));
						i++;
						break;
					case 0:
						lines_3.add(new Line(from[i], true, true));
						i++;
						j++;
						break;
					case 1:
						lines_3.add(new Line(to[j], true, false));
						j++;
						break;
				}
			} else {
				lines_3.add(new Line(from[i], true, false));
				i++;
			}
		}

		while (j < to.length) {
			lines_3.add(new Line(to[j], false, true));
			j++;
		}

		Log.i("lol", this.toString());
	}

	public TimeTable(Line[] lines) {
		this.lines_3= new ArrayList<Line>(lines.length);
		for (Line l : lines)
			this.lines_3.add(l);
	}

	@Override
	public String toString() {
		String s = "";
		for (Line l : lines_3)
			s += "\n" + l;
		return s;
	}

	class Line {
		public Time time;
		public boolean from, to;

		public Line(Time time, boolean from, boolean to) {
			this.time = time;
			this.from = from;
			this.to = to;
		}

		//public boolean isBefore(Time current) {
		//	return current.compareTo(time) < 0;
		//}

		public boolean isBefore(Date current) {
			int curH = current.getHours(), schedH = time.getHours(),
			curM = current.getMinutes(), schedM = time.getMinutes();

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