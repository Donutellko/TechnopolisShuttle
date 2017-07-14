package com.donutellko.technopolisshuttle;

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

public class FullScheduleView extends SView {
	private final int LAYOUT_RESOURCE = R.layout.full_layout;
	private TimeTable timeTable;
	private CheckBox showPastCheckBox;
	private boolean showPastState;
	LinearLayout content;

	public FullScheduleView(Context context, boolean showPastState) {
		super(context);

		view = View.inflate(context, LAYOUT_RESOURCE, null);
		this.timeTable = MainActivity.timeTable;
		this.showPastState = showPastState;

		prepareView();
	}

	@Override
	public void prepareView() {
		showPastCheckBox = view.findViewById(R.id.view_past);
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

	public void setTimeTable(TimeTable timeTable) {
		this.timeTable = timeTable;
	}

	public void setShowPastCheckBoxState(boolean b) {
		showPastCheckBox.setChecked(showPastState);
	}

	private View makeTwoColumnsTable() {
		ScrollView result = new ScrollView(context);
		TableLayout table = new TableLayout(context);
		result.addView(table);

		STime currentTime = getCurrentTime();

		List<TimeTable.ScheduleElement>
				from = new ArrayList<>(),
				to = new ArrayList<>();

		if (showPastState) {
			int max = timeTable.from.length;
			if (timeTable.to.length > max) max = timeTable.to.length;
			for (int i = 0; i < max; i++) {
				table.addView(makeTwoColumnsRow(
						(i < timeTable.from.length ? timeTable.from[i] : null),
						(i < timeTable.to.length ? timeTable.to[i] : null), currentTime)); // суём инфу в таблицу
			}
		} else {
			for (TimeTable.ScheduleElement d : timeTable.from)
				if (d.time.hour > currentTime.hour || (d.time.hour == currentTime.hour && d.time.min > currentTime.min))
					from.add(d);

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

	private View makeTwoColumnsRow(TimeTable.ScheduleElement t1, TimeTable.ScheduleElement t2, STime currentTime) {
		View row = View.inflate(context, R.layout.full_2col_row, null);

		TextView tFrom = row.findViewById(R.id.from_tech);
		TextView tTo = row.findViewById(R.id.to_tech);

		if (t1 == null) tFrom.setText("");
		else {
			tFrom.setText(t1.time.hour + ":" + (t1.time.min <= 9 ? "0" : "") + t1.time.min); //TODO: format
			if (firstIsBefore(t1.time, currentTime))
				tFrom.setTextColor(Color.LTGRAY);
		}

		if (t2 == null) tTo.setText("");
		else {
			tTo.setText(t2.time.hour + ":" + (t2.time.min <= 9 ? "0" : "") + t2.time.min); //TODO: format
			if (firstIsBefore(t2.time, currentTime))
				tTo.setTextColor(Color.LTGRAY);
		}

		return row;
	}

	private CheckBox.OnCheckedChangeListener mOnShowPastChangedListener
			= new CheckBox.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
			Log.i("listener", "showPast changed");
			showPastState = compoundButton.isChecked();
			updateView();
		}
	};
}
