package com.donutellko.technopolisshuttle;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.List;

import static com.donutellko.technopolisshuttle.MainActivity.getCurrentTime;
import static com.donutellko.technopolisshuttle.MainActivity.getWeekdayNumber;

public class ShortScheduleView extends SView {
	private final int LAYOUT_RESOURCE = R.layout.short_layout;
	private TimeTable timeTable;
	private int countToShow;
	private boolean showToTechno;
	private int weekdaySelected;
	private TableLayout table;

	ToggleButton toggleTo, toggleFrom;

	public ShortScheduleView(Context context, TimeTable timeTable, int countToShow, boolean showToTechno) {
		super(context);

		view = View.inflate(context, LAYOUT_RESOURCE, null);

		this.timeTable = timeTable;
		this.countToShow = countToShow;
		this.showToTechno = showToTechno;

		prepareView();
	}

	public void setTimeTable(TimeTable timeTable) {
		this.timeTable = timeTable;
	}

	@Override
	public void prepareView() {
		int weekday = getWeekdayNumber();

		table = view.findViewById(R.id.table);

		toggleTo = view.findViewById(R.id.toggle_to);
		toggleTo.setOnClickListener(toggleToListener);
		toggleTo.setChecked(showToTechno);

		toggleFrom = view.findViewById(R.id.toggle_from);
		toggleFrom.setOnClickListener(toggleFromListener);
		toggleFrom.setChecked(!showToTechno);

		Spinner weekdaysSpinner = view.findViewById(R.id.spinner_weekdays);
		ArrayAdapter<CharSequence> adapter =
				ArrayAdapter.createFromResource(context, R.array.weekdays, android.R.layout.simple_spinner_item); // Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // Apply the adapter to the spinner
		weekdaysSpinner.setOnItemSelectedListener(mWeekdaysSpinnerListener);
		weekdaysSpinner.setAdapter(adapter);
		weekdaysSpinner.setSelection(weekday);

		updateView();
	}

	@Override
	public void updateView() {
		DataLoader.STime now = getCurrentTime();
		showToTechno = toggleTo.isChecked();
		table.removeAllViews();

		table.addView(View.inflate(context, R.layout.short_head, null));
		List<TimeTable.ScheduleElement> after = timeTable.getTimeAfter(now, !showToTechno, weekdaySelected);

		for (int i = 0; i < Math.min(after.size(), countToShow); i++)
			table.addView(getTimeLeftRow(after.get(i), now));

		TableRow ending = new TableRow(context);
		TextView ending_text = new TextView(context);

		ending_text.setTextColor(Color.DKGRAY);
		ending.setPadding(15, 15, 15, 15);
		ending_text.setPadding(15, 15, 15, 15);

		ending.addView(ending_text);
		if (after.size() == 0) {
			ending_text.setText("Cегодня автобусов в этом направлении больше нет.");
			table.removeAllViews();
		} else if (after.size() >= countToShow - 1)
			ending_text.setText("Показаны " + countToShow + " ближайших рейсов.");
		else {
			ending_text.setText("Больше нет рейсов на сегодня.");
		}
		table.addView(ending);
	}

	public View getTimeLeftRow(TimeTable.ScheduleElement t, DataLoader.STime now) {
		DataLoader.STime left = now.getDifference(t.time);

		String timeLeft = "";
		if (left.isZero())
			timeLeft = "прямо сейчас";
		else {
			if (left.hour != 0) timeLeft += " " + left.hour + " час";
			if (left.min != 0) timeLeft += " " + left.min + " мин";
		}

		View row = View.inflate(context, R.layout.short_row, null);
		((TextView) row.findViewById(R.id.time)).setText(t.time.hour + ":" + (t.time.min <= 9 ? "0" : "") + t.time.min);
		((TextView) row.findViewById(R.id.timeleft)).setText(timeLeft);

		return row;
	}

	private ToggleButton.OnClickListener toggleToListener = new ToggleButton.OnClickListener() {
		@Override
		public void onClick(View v) {
			toggleTo.setChecked(true);
			
			if (toggleFrom.isChecked()) {
				toggleFrom.setChecked(false);
			}
			updateView();
		}
	};

	private ToggleButton.OnClickListener toggleFromListener = new ToggleButton.OnClickListener() {
		@Override
		public void onClick(View v) {
			toggleFrom.setChecked(true);

			if (toggleTo.isChecked()) {
				toggleTo.setChecked(false);
			}
			updateView();
		}
	};

	private AdapterView.OnItemSelectedListener mWeekdaysSpinnerListener = new AdapterView.OnItemSelectedListener() {
		@Override
		public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
			weekdaySelected = i;
		}

		@Override
		public void onNothingSelected(AdapterView<?> adapterView) {
			weekdaySelected = getWeekdayNumber();
		}
	};
}
