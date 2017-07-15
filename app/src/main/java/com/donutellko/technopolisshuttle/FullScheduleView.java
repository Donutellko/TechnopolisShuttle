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

import static android.view.View.TEXT_ALIGNMENT_CENTER;
import static android.view.View.TEXT_ALIGNMENT_GRAVITY;
import static com.donutellko.technopolisshuttle.DataLoader.getCurrentTime;
import static com.donutellko.technopolisshuttle.DataLoader.firstIsBefore;
import static com.donutellko.technopolisshuttle.MainActivity.settings;
import static com.donutellko.technopolisshuttle.MainActivity.timeTable;

public class FullScheduleView extends SView {
	private final int LAYOUT_RESOURCE = R.layout.full_layout;
	private CheckBox showPastCheckBox;
	LinearLayout content;

	public FullScheduleView(Context context) {
		super(context);

		view = View.inflate(context, LAYOUT_RESOURCE, null);

		prepareView();
	}

	@Override
	public void prepareView() {
		showPastCheckBox = view.findViewById(R.id.view_past);
		showPastCheckBox.setChecked(settings.showPast);
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

		if (settings.showPast) {
			int max = timeTable.from.length;
			if (timeTable.to.length > max) max = timeTable.to.length;
			for (int i = 0; i < max; i++) {
				table.addView(makeTwoColumnsRow(
						(i < timeTable.from.length ? timeTable.from[i] : null),
						(i < timeTable.to.length ? timeTable.to[i] : null), currentTime)); // суём инфу в таблицу
			}
		} else {
			for (TimeTable.ScheduleElement d : timeTable.from)
				if (d.time.hour > currentTime.hour || (d.time.hour == currentTime.hour && d.time.min > currentTime.min)) {
					from.add(d);

				}

			for (TimeTable.ScheduleElement d : timeTable.to)
				if (d.time.hour > currentTime.hour || (d.time.hour == currentTime.hour && d.time.min > currentTime.min))
					to.add(d);

			for (int i = 0; i < Math.max(from.size(), to.size()); i++)
				table.addView(makeTwoColumnsRow(
						(i < from.size() ? from.get(i) : null),
						(i < to.size() ? to.get(i) : null), currentTime)); // суём инфу в таблицу
		}
		return result;
	}

	private String makeDays(int mask) {
		if (mask == 31) return "";
		if (mask == 127) return "ежедневно";
		String[] weekdays = {"пн", "вт", "ср", "чт", "пт", "сб", "вс"};
		String does = "только ", doesnt = "кроме ", result;
		byte does_i = 0, doesnt_i = 0;
		for (int j = 0; j < 5; j++) {
			if (((1 << j) & mask) > 0) {
				does += (does_i > 0 ? ", " : "") + weekdays[j];
				does_i++;
			} else {
				doesnt += (doesnt_i > 0 ? ", " : "") + weekdays[j];
				doesnt_i++;
			}
		}
		boolean onSat = ((1 << 5) & mask) > 0, onSun = ((1 << 6) & mask) > 0;

		if (onSat) does_i++;
		else doesnt_i++;

		if (onSun) does_i++;
		else doesnt_i++;

		if (onSat) {
			does += ", " + weekdays[5];
			doesnt += ", " + weekdays[5];
		}
		if (onSat) {
			does += ", " + weekdays[6];
			doesnt += ", " + weekdays[6];
		}

		if (does_i < doesnt_i) {
			result = does;
		} else {
			result = doesnt;
		}
		return result;
	}

	private View makeTwoColumnsRow(TimeTable.ScheduleElement colToTech, TimeTable.ScheduleElement colFromTech, STime currentTime) {
		View row = View.inflate(context, R.layout.full_2col_row, null);

		TextView tFrom = row.findViewById(R.id.from_tech);
		TextView tTo = row.findViewById(R.id.to_tech);

		// ЛЕВАЯ КОЛОНКА
		if (colToTech == null) tFrom.setText("");
		else {
			String commentsDays = makeDays(colToTech.mask);
			tFrom.setText(colToTech.time.hour + ":" + (colToTech.time.min <= 9 ? "0" : "") + colToTech.time.min); //TODO: format
			if (commentsDays != "") {
				//tFrom.setPadding(tFrom.getPaddingLeft(), tFrom.getPaddingTop(), tFrom.getPaddingRight(), 15);
				//tTo.setPadding(tFrom.getPaddingLeft(), tFrom.getPaddingTop(), tFrom.getPaddingRight(), 15); // не ошибка копипасты
				TextView days = new TextView(context);
				days.setGravity(Gravity.CENTER);
				days.setTextColor(MainActivity.applicationContext.getResources().getColor(R.color.colorAccent));
				days.setText(commentsDays);
				days.setTextSize(9);
				LinearLayout linearLayout = row.findViewById(R.id.layout_from_tech);
				linearLayout.addView(days);
			}
			if (firstIsBefore(colToTech.time, currentTime))
				tFrom.setTextColor(Color.LTGRAY);
		}

		//ПРАВАЯ КОЛОНКА
		if (colFromTech == null) tTo.setText("");
		else {
			String commentsDays = makeDays(colFromTech.mask);
			tTo.setText(colFromTech.time.hour + ":" + (colFromTech.time.min <= 9 ? "0" : "") + colFromTech.time.min); //TODO: format
			if (commentsDays != "") {
				TextView days = new TextView(context);
				days.setGravity(Gravity.CENTER);
				days.setTextColor(MainActivity.applicationContext.getResources().getColor(R.color.colorAccent));
				days.setText(commentsDays);
				days.setTextSize(9);
				LinearLayout linearLayout = row.findViewById(R.id.layout_to_tech);
				linearLayout.addView(days);
			}
			if (firstIsBefore(colFromTech.time, currentTime))
				tTo.setTextColor(Color.LTGRAY);
		}

		return row;
	}

	private CheckBox.OnCheckedChangeListener mOnShowPastChangedListener
			= new CheckBox.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
			Log.i("listener", "showPast changed");
			settings.showPast = compoundButton.isChecked();
			updateView();
		}
	};
}
