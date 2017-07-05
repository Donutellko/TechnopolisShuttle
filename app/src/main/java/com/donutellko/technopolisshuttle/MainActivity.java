package com.donutellko.technopolisshuttle;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private DataLoader dl = new DataLoader();

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

		loadFull();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

	private void loadFull() {
		Log.i("loading", "Started loading Full Schedule");
		TimeTable tt = dl.getFullDefaultInfo();
		TableLayout table = (TableLayout) findViewById(R.id.table);
		Log.i("loading", (tt == null) ? "!!! TimeTable is null !!!" : "TimeTable is ok.");
		Log.i("loading", (tt.lines_3 == null || tt.lines_3.size() == 0) ? "!!! Rows in TimeTable are null or empty !!!" : "Rows in TimeTable are ok.");

		table.addView(getLayoutInflater().inflate(R.layout.table_head_layout, null));
		for (TimeTable.Line l : tt.lines_3) table.addView(makeThreeColumnsRow(l));
		// for (int i = 0; i < tt.lines.size(); i++) table.addView(makeRow(tt.lines[i]));
	}

	public TableRow makeThreeColumnsRow (TimeTable.Line line) {
        TableRow tr = new TableRow(this);

        View row = getLayoutInflater().inflate(R.layout.row_layout_2, null);

        TextView text = (TextView) row.findViewById(R.id.time);
        ImageView imFrom = (ImageView) row.findViewById(R.id.from);
        ImageView imTo   = (ImageView) row.findViewById(R.id.to  );

        text.setText(line.time.getHours() + ":" + (line.time.getMinutes() <= 9 ? "0" : "") + line.time.getMinutes()); //TODO: format
        imFrom.setVisibility(line.from ? View.VISIBLE : View.INVISIBLE);
        imTo  .setVisibility(line.to   ? View.VISIBLE : View.INVISIBLE);

		if (line.isBefore(Calendar.getInstance().getTime()))
			text.setTextColor(Color.LTGRAY);

        tr.addView(row);
        return tr;
    }

    //public TableRow makeTwoColumnRow (TimeTable.)
}
