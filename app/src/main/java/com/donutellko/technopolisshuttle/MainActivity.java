package com.donutellko.technopolisshuttle;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Calendar;
import com.donutellko.technopolisshuttle.DataLoader.STime;

import java.util.List;
import java.util.Timer;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

	//CONSTANTS
	private static final int COUNT_TO_SHOW_ON_SHORT = 5;
	private State currentState = State.SHORT_VIEW;

	enum State {SHORT_VIEW, FULL_VIEW, MAP_VIEW, SETTINGS_VIEW}

	// CheckBox values
	private boolean showPast = true;

	private DataLoader dataLoader = new DataLoader();
	private TimeTable timeTable;
	private LayoutInflater layoutInflater;
	Calendar curtime;

	// Подставляемые View
	private View shortView, fullView, mapView;
	LinearLayout contentView; // Область контента (всё кроме нав. панели)
	ToggleButton toggleButtonToTechnopolis, toggleButtonToUnderground;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		layoutInflater = getLayoutInflater();

		contentView = (LinearLayout) findViewById(R.id.content);
		makeShortScheduleView();

		BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
		navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
		navigation.setSelectedItemId(R.id.navigation_short);

		curtime = Calendar.getInstance();

		CountDownTimer timer = new CountDownTimer(Long.MAX_VALUE, 1000) {
			@Override
			public void onTick(long millisUntilFinished) {
				switch (currentState) {
					case SHORT_VIEW: updateShortViewTable(); break;
					case FULL_VIEW: makeFullScheduleView(); break;
				}
			}
			@Override
			public void onFinish() {
				this.start();
			}
		};
		timer.start();
	}

	private void makeShortScheduleView() {
		currentState = State.SHORT_VIEW;

		contentView.removeAllViews(); // очищаем от добавленных ранее отображений
		if (shortView == null) {
			Log.i("shortView", "inflating");
			shortView = layoutInflater.inflate(R.layout.short_layout, null);
		}
		contentView.addView(shortView);

		if (timeTable == null) {
			Log.i("timeTable", "loading");
			timeTable = dataLoader.getFullJsonInfo(); // объект с расписанием
		}

		if (toggleButtonToTechnopolis == null) {
			toggleButtonToTechnopolis = shortView.findViewById(R.id.toggle_to);
			toggleButtonToTechnopolis.setOnClickListener(toggleToTechnopolis);
		}

		if (toggleButtonToUnderground == null) {
			toggleButtonToUnderground = shortView.findViewById(R.id.toggle_from);
			toggleButtonToUnderground.setOnClickListener(toggleToUnderground);
		}

		Spinner weekdays = shortView.findViewById(R.id.spinner_weekdays);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.weekdays, android.R.layout.simple_spinner_item); // Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // Apply the adapter to the spinner
		weekdays.setAdapter(adapter);
		weekdays.setSelection(getWeekdayNumber());

		updateShortViewTable();
	}

	public void updateShortViewTable () {
		ToggleButton from_tumbler = (ToggleButton) shortView.findViewById(R.id.toggle_from);
		STime now = getCurrentTime();
		Log.i("getCurrentTime()", now.toString());

		TableLayout table = shortView.findViewById(R.id.table);

		table.removeAllViews();
		table.addView(layoutInflater.inflate(R.layout.short_head, null));

		List<TimeTable.ScheduleElement> after = timeTable.getTimeAfter(now, from_tumbler.isChecked());

		for (int i = 0; i < Math.min(after.size(), COUNT_TO_SHOW_ON_SHORT); i++)
			table.addView(getTimeLeftRow(after.get(i), now));

		if (after.size() == 0) {
			TableRow empty_row = new TableRow(getApplicationContext());
			TextView empty_text = new TextView(getApplicationContext());
			empty_text.setText("Cегодня автобусов в этом направлении больше нет.");
			empty_text.setTextColor(Color.DKGRAY);
			empty_text.setPadding(5, 5, 5, 5);
			empty_row.addView(empty_text);

			table.removeAllViews();
			table.addView(empty_row);
		}
	}

	public View getTimeLeftRow(TimeTable.ScheduleElement t, STime now) {
		STime left = now.getDifference(t.time);

		String timeLeft = "";
		if (left.isZero())
			timeLeft = "прямо сейчас";
		else {
			if (left.hour != 0) timeLeft += " " + left.hour + " час";
			if (left.min != 0) timeLeft += " " + left.min + " мин";
		}

		View row = layoutInflater.inflate(R.layout.short_row, null);
		((TextView) row.findViewById(R.id.time)).setText(t.time.hour + ":" + (t.time.min <= 9 ? "0" : "") + t.time.min);
		((TextView) row.findViewById(R.id.timeleft)).setText(timeLeft);

		return row;
	}


	private void makeFullScheduleView() {
		currentState = State.FULL_VIEW;
		if (fullView == null) {
			fullView = layoutInflater.inflate(R.layout.full_layout, null); // Создаём view с таблицей
			CheckBox checkBox = fullView.findViewById(R.id.view_past);
			checkBox.setOnCheckedChangeListener(mOnShowPastChangedListener);
			checkBox.setChecked(showPast);
		}

		contentView.removeAllViews(); // очищаем от созданных ранее объектов
		contentView.addView(fullView); // добавляем созданный view в область контента

		if (timeTable == null)
			timeTable = dataLoader.getFullJsonInfo(); // объект с данными о времени


		LinearLayout content = fullView.findViewById(R.id.content);
		content.removeAllViews();
		content.addView(layoutInflater.inflate(R.layout.full_2col_head, null)); //добавляем заголовок в таблицу так, чтобы он не пролистывался
		content.addView(makeTwoColumnsTable(timeTable));
	}


	public TableLayout makeTwoColumnsTable(TimeTable timeTable) {
		STime now = getCurrentTime();
		TableLayout table = new TableLayout(this);  // находим таблицу на созданном view

		List<TimeTable.ScheduleElement>
				from = new ArrayList<>(),
				to   = new ArrayList<>();

		if (showPast) {
			int max = timeTable.from.length;
			if (timeTable.to.length > max) max = timeTable.to.length;
			for (int i = 0; i < max; i++) {
				table.addView(makeTwoColumnsRow(
						(i < timeTable.from.length ? timeTable.from[i] : null),
						(i < timeTable.to  .length ? timeTable.to  [i] : null))); // суём инфу в таблицу
			}
		} else {
			for (TimeTable.ScheduleElement d : timeTable.from)
				if (d.time.hour > now.hour || (d.time.hour == now.hour && d.time.min > now.min))
					from.add(d);

			for (TimeTable.ScheduleElement d : timeTable.to)
				if (d.time.hour > now.hour || (d.time.hour == now.hour && d.time.min > now.min))
					to.add(d);

			for (int i = 0; i < Math.max(from.size(), to.size()); i++)
				table.addView(makeTwoColumnsRow(
						(i < from.size() ? from.get(i) : null),
						(i < to.size() ? to.get(i) : null))); // суём инфу в таблицу
		}
		return table;
	}

	private STime getCurrentTime() {
		curtime = Calendar.getInstance();
		return new STime(curtime.getTime());
	}

	private int getWeekdayNumber() {
		if (curtime == null) curtime = Calendar.getInstance();
		return curtime.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY;
	}

	public View makeTwoColumnsRow(TimeTable.ScheduleElement t1, TimeTable.ScheduleElement t2) {
		View row = layoutInflater.inflate(R.layout.full_2col_row, null);
		STime now = getCurrentTime();

		TextView tFrom = (TextView) row.findViewById(R.id.t_from);
		TextView tTo = (TextView) row.findViewById(R.id.t_to);

		if (t1 == null) tFrom.setText("");
		else {
			tFrom.setText(t1.time.hour + ":" + (t1.time.min <= 9 ? "0" : "") + t1.time.min); //TODO: format
			if (firstIsBefore(t1.time, now))
				tFrom.setTextColor(Color.LTGRAY);
		}

		if (t2 == null) tTo.setText("");
		else {
			tTo.setText(t2.time.hour + ":" + (t2.time.min <= 9 ? "0" : "") + t2.time.min); //TODO: format
			if (firstIsBefore(t2.time, now))
				tTo.setTextColor(Color.LTGRAY);
		}

		return row;
	}

	public boolean firstIsBefore(DataLoader.STime d1, DataLoader.STime d2) {
		if (d1.hour < d2.hour)
			return true;
		if (d1.hour == d2.hour && d1.min < d2.min)
			return true;
		return false;
	}

	public void makeMapView() {
		currentState = State.MAP_VIEW;
		contentView.removeAllViews(); // очищаем от созданных ранее объектов
		if (mapView == null) mapView = layoutInflater.inflate(R.layout.map_layout, null);

		MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);

		contentView.addView(mapView);
	}

	public void setContent(View view) {
		contentView.removeAllViews();
		contentView.addView(view);
	}


	private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
			= new BottomNavigationView.OnNavigationItemSelectedListener() {
		@Override
		public boolean onNavigationItemSelected(@NonNull MenuItem item) {
			Log.i("listener", "bootomnavigation changed");
			switch (item.getItemId()) {
				case R.id.navigation_short:
					makeShortScheduleView();
					return true;
				case R.id.navigation_full:
					makeFullScheduleView();
					return true;
				case R.id.navigation_map:
					makeMapView();
					return true;
			}
			return false;
		}
	};

	private ToggleButton.OnClickListener toggleToTechnopolis
			= new ToggleButton.OnClickListener() {
		@Override
		public void onClick(View view) {
			Log.i("listener", "toggle To clicked");
			toggleButtonToUnderground.setChecked(!toggleButtonToTechnopolis.isChecked());
			updateShortViewTable();
		}
	};

	private ToggleButton.OnClickListener toggleToUnderground
			= new ToggleButton.OnClickListener() {
		@Override
		public void onClick(View view) {
			Log.i("listener", "toggle From clicked");
			toggleButtonToTechnopolis.setChecked(!toggleButtonToUnderground.isChecked());
			updateShortViewTable();
		}
	};

	private CheckBox.OnCheckedChangeListener mOnShowPastChangedListener
			= new CheckBox.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
			Log.i("listener", "showPast changed");
			showPast = compoundButton.isChecked();
			makeFullScheduleView();
		}
	};

	@Override
	public void onMapReady(GoogleMap map) {
		LatLng technopolis = new LatLng(59.818026, 30.327783);
		LatLng underground = new LatLng(59.854728, 30.320958);

		LatLngBounds all = new LatLngBounds(
				new LatLng(59.8, 30.32), new LatLng(59.87, 30.33));

		map.moveCamera(CameraUpdateFactory.newLatLngBounds(all, 0));

		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			// TODO: Consider calling
			//    ActivityCompat#requestPermissions
			// here to request the missing permissions, and then overriding
			//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
			//                                          int[] grantResults)
			// to handle the case where the user grants the permission. See the documentation
			// for ActivityCompat#requestPermissions for more details.
			return;
		}

		map.setMyLocationEnabled(true);
//        map.moveCamera(CameraUpdateFactory.newLatLngZoom(underground, 13));
//        map.moveCamera(CameraUpdateFactory.newLatLngZoom(technopolis, 13));

		map.addMarker(new MarkerOptions()
				.title(     "м. Московская")
				.snippet("Московский проспект, 189")
				.position(underground));


		map.addMarker(new MarkerOptions()
				.title("TECHNOPOLIS")
				.snippet("Пулковское шоссе, 40к4")
				.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
				.position(technopolis));
	}
}