package com.donutellko.technopolisshuttle;

import android.view.Gravity;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.CheckBox;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import com.donutellko.technopolisshuttle.DataLoader.STime;

import static com.donutellko.technopolisshuttle.DataLoader.getCurrentTime;
import static com.donutellko.technopolisshuttle.DataLoader.firstIsBefore;
import static com.donutellko.technopolisshuttle.MainActivity.timeTable;

public class FullScheduleView extends SView {
	private final int LAYOUT_RESOURCE = R.layout.full_layout;
	private CheckBox showPastCheckBox;
	private String[] weekdays;
	LinearLayout content;

	public FullScheduleView(Context context) {
		super(context);

		view = View.inflate(context, LAYOUT_RESOURCE, null);
		weekdays = context.getResources().getStringArray(R.array.weekdays_short);
		prepareView();
	}

	@Override
	public void prepareView() {
		showPastCheckBox = view.findViewById(R.id.view_past);
		showPastCheckBox.setChecked(Settings.singleton.showPast);
		showPastCheckBox.setOnCheckedChangeListener(mOnShowPastChangedListener);

		content = view.findViewById(R.id.content);

		updateView();
	}

	@Override
	public void updateView() {
		content.removeAllViews();
		content.addView(View.inflate(context, R.layout.full_2col_head, null)); //добавляем заголовок в таблицу так, чтобы он не пролистывался
		content.addView(makeTwoColumnsTable());
	}

	private View makeTwoColumnsTable() {
		ScrollView result = new ScrollView(context);
		TableLayout table = new TableLayout(context);
		result.addView(table);

		STime currentTime = getCurrentTime();

		List<TimeTable.ScheduleElement>
				from = new ArrayList<>(),
				to = new ArrayList<>();

		if (Settings.singleton.showPast) {
			int max = timeTable.from.length;
			if (timeTable.to.length > max) max = timeTable.to.length;
			for (int i = 0; i < max; i++) {
				table.addView(makeTwoColumnsRow(
						(i < timeTable.to.length ? timeTable.to[i] : null),
						(i < timeTable.from.length ? timeTable.from[i] : null), currentTime)); // суём инфу в таблицу
			}
		} else {
			for (TimeTable.ScheduleElement d : timeTable.from) //TODO: поменять на getTimeAfter
				if (d.time.hour > currentTime.hour || (d.time.hour == currentTime.hour && d.time.min > currentTime.min)) {
					from.add(d);
				}

			for (TimeTable.ScheduleElement d : timeTable.to)   //TODO: поменять на getTimeAfter
				if (d.time.hour > currentTime.hour || (d.time.hour == currentTime.hour && d.time.min > currentTime.min))
					to.add(d);

			for (int i = 0; i < Math.max(from.size(), to.size()); i++)
				table.addView(makeTwoColumnsRow(
						(i < to.size() ? to.get(i) : null),
						(i < from.size() ? from.get(i) : null), currentTime)); // суём инфу в таблицу
		}
		return result;
	}

	private View makeTwoColumnsRow(TimeTable.ScheduleElement colToTech, TimeTable.ScheduleElement colFromTech, STime currentTime) {
		View row = View.inflate(context, R.layout.full_2col_row, null);

		modifyRow(row, colToTech, true, currentTime);    // левая колонка
		modifyRow(row, colFromTech, false, currentTime); // правая колонка

		return row;
	}

	private void modifyRow(View row, TimeTable.ScheduleElement cell, boolean toTech, STime currentTime) {
		TextView text = row.findViewById(toTech ? R.id.to_tech : R.id.from_tech);
		if (cell == null) text.setText("");
		else {
			String commentsDays = makeDays(cell.mask);
			text.setTextSize(Settings.singleton.textSize);
			text.setText(cell.time.hour + ":" + (cell.time.min <= 9 ? "0" : "") + cell.time.min); //TODO: format
			setComments(commentsDays, row, toTech ? R.id.layout_to_tech : R.id.layout_from_tech);
			if (firstIsBefore(cell.time, currentTime) || !cell.worksAt(currentTime.weekday))
				text.setTextColor(Color.LTGRAY);
		}
	}

	private void setComments(String commentsDays, View row, int id) {
		if (!commentsDays.equals("")){
			TextView days = new TextView(context);
			days.setGravity(Gravity.CENTER);
			days.setTextColor(MainActivity.applicationContext.getResources().getColor(R.color.colorAccent));
			days.setText(commentsDays);
			days.setTextSize(9);
			LinearLayout linearLayout = row.findViewById(id);
			linearLayout.addView(days);
		}
	}


	private String makeDays(int mask) {
		if (mask == 31) return "";
		if (mask == 127) return context.getString(R.string.daily);

//		String[] weekdays = {"пн", "вт", "ср", "чт", "пт", "сб", "вс"};



		String does = context.getString(R.string.only) + " ", doesnt = context.getString(R.string.except) + " ";
		byte does_i = 0, doesnt_i = 0;

		for (int j = 0; j < 5; j++) {
			boolean tmp = ((1 << j) & mask) > 0;
			if (tmp) {
				does += (does_i > 0 ? ", " : "") + weekdays[j];
				does_i++;
			} else {
				doesnt += (doesnt_i > 0 ? ", " : "") + weekdays[j];
				doesnt_i++;
			}
		}
		boolean onSat = ((1 << 5) & mask) > 0, onSun = ((1 << 6) & mask) > 0;

		does_i += (onSat ? 1 : 0) + (onSun ? 1 : 0);
		doesnt_i += (!onSat ? 1 : 0) + (!onSun ? 1 : 0);

		does += (onSat ? ", " + weekdays[5] : "") + (onSun ? ", " + weekdays[6] : "");
		doesnt += (!onSat ? ", " + weekdays[5] : "") + (!onSun ? ", " + weekdays[6] : "");

		return(does_i < doesnt_i) ? does : doesnt;
	}




	private CheckBox.OnCheckedChangeListener mOnShowPastChangedListener
			= new CheckBox.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
			Log.i("listener", "showPast changed");
			Settings.singleton.showPast = compoundButton.isChecked();
			updateView();
		}
	};
}
