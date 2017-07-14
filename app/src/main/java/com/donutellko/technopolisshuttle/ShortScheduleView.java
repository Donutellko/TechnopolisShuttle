package com.donutellko.technopolisshuttle;

import android.support.design.widget.TabLayout;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.ToggleButton;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.content.Context;
import android.widget.Spinner;
import android.graphics.Color;
import android.view.View;

import java.util.List;

import static com.donutellko.technopolisshuttle.DataLoader.getWeekdayNumber;
import static com.donutellko.technopolisshuttle.DataLoader.getCurrentTime;

public class ShortScheduleView extends SView {
	private final int
			LAYOUT_RESOURCE = R.layout.short_layout,
			TOGGLE_TO = R.id.toggle_to,
			TOGGLE_FROM = R.id.toggle_from;
	private TimeTable timeTable;
	private int weekdaySelected;
	private TableLayout table;
	private DataLoader.SettingsSingleton settingsSingleton;

	ToggleButton toggleTo, toggleFrom;

	public ShortScheduleView(Context context, DataLoader.SettingsSingleton settingsSingleton, TimeTable timeTable) {
		super(context);

		view = View.inflate(context, LAYOUT_RESOURCE, null);

		this.timeTable = timeTable;
		this.settingsSingleton  = settingsSingleton;

		prepareView();
	}

	public void setTimeTable(TimeTable timeTable) {
		this.timeTable = timeTable;
	}

	@Override
	public void prepareView() {
		int weekday = getWeekdayNumber();

		LinearLayout container = (LinearLayout) view.findViewById(R.id.container);
		container.addView(View.inflate(context, R.layout.short_head, null));
		ScrollView scrollView = new ScrollView(context);
		table = new TableLayout(context);
		scrollView.addView(table);
		container.addView(scrollView);

		toggleTo = view.findViewById(TOGGLE_TO);
		toggleTo.setOnClickListener(toggleToListener);
		toggleTo.setChecked(settingsSingleton.showTo);

		toggleFrom = view.findViewById(TOGGLE_FROM);
		toggleFrom.setOnClickListener(toggleFromListener);
		toggleFrom.setChecked(!settingsSingleton.showTo);

		Spinner weekdaysSpinner = view.findViewById(R.id.spinner_weekdays);
		ArrayAdapter<CharSequence> adapter =
				ArrayAdapter.createFromResource(context, R.array.weekdays, android.R.layout.simple_spinner_item); // Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // Apply the adapter to the spinner
		weekdaysSpinner.setOnItemSelectedListener(mWeekdaysSpinnerListener);
		weekdaysSpinner.setAdapter(adapter);
		weekdaysSpinner.setSelection(weekday);

//		double dist = MainActivity.locationListener.getDistanceToTechnopolis();
//		settingsSingleton.showTo = dist > settingsSingleton.distanceToShowFrom;
//		Log.i("lsnd", dist + " > " + settingsSingleton.distanceToShowFrom + " = " + settingsSingleton.showTo);

		updateView();
	}

	@Override
	public void updateView() {
		DataLoader.STime now = getCurrentTime();

		settingsSingleton.showTo = toggleTo.isChecked();
		table.removeAllViews();

		List<TimeTable.ScheduleElement> after = timeTable.getTimeAfter(now, settingsSingleton.showTo, weekdaySelected);

		for (int i = 0; i < Math.min(after.size(), settingsSingleton.countToShowOnShort); i++)
			table.addView(getTimeLeftRow(after.get(i), now));

		TableRow ending = new TableRow(context);
		TextView ending_text = new TextView(context);

//		ending_text.setTextColor(Color.DKGRAY);
		ending.setPadding(15, 15, 15, 15);
		ending_text.setPadding(15, 15, 15, 15);

		ending.addView(ending_text);
		if (after.size() == 0) {
			ending_text.setText("Cегодня автобусов в этом направлении больше нет.");
			table.removeAllViews();
		} else if (after.size() >= settingsSingleton.countToShowOnShort - 1)
			ending_text.setText("Показаны " + settingsSingleton.countToShowOnShort + " ближайших рейсов.");
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
