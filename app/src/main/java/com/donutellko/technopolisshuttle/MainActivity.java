package com.donutellko.technopolisshuttle;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private DataLoader dataLoader = new DataLoader();
    private TimeTable timeTable;
    LinearLayout contentView; // Область контента (всё кроме нижней панели)

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_short:
                    setShortScheduleView();
                    return true;
                case R.id.navigation_full:
                    setFullScheduleView();
                    return true;
                case R.id.navigation_map:
                    setMapView();
                    return true;
            }
            return false;
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        contentView = (LinearLayout) findViewById(R.id.content);
        setFullScheduleView();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        navigation.setSelectedItemId(R.id.navigation_full);
    }



    private void setShortScheduleView() {
        contentView.removeAllViews(); // очищаем от созданных ранее объектов
    }

    private void setFullScheduleView() {
        contentView.removeAllViews(); // очищаем от созданных ранее объектов

        View tableView = getLayoutInflater().inflate(R.layout.full_table, null); // Создаём view с таблицей
        TableLayout table = tableView.findViewById(R.id.table); // находим таблицу на созданном view

        contentView.addView(getLayoutInflater().inflate(R.layout.table_head_layout, null)); //добавляем заголовок в таблицу так, чтобы он не пролистывался
        if (timeTable == null)
            timeTable = dataLoader.getFullDefaultInfo(); // объект с данными о времени
        for (TimeTable.Line l : timeTable.lines) table.addView(makeThreeColumnsRow(l)); // суём инфу в таблицу

        contentView.addView(tableView); // добавляем созданный view в область контента

    }

    public void setMapView () {
        contentView.removeAllViews(); // очищаем от созданных ранее объектов
    }

    public TableRow makeThreeColumnsRow (TimeTable.Line line) {
        TableRow tr = new TableRow(this);

        View row = getLayoutInflater().inflate(R.layout.row_layout_2, null);

        TextView  text   = (TextView)  row.findViewById(R.id.time);
        ImageView imFrom = (ImageView) row.findViewById(R.id.from);
        ImageView imTo   = (ImageView) row.findViewById(R.id.to  );

        text  .setText(line.time.getHours() + ":" + (line.time.getMinutes() <= 9 ? "0" : "") + line.time.getMinutes()); //TODO: format
        imFrom.setVisibility(line.from ? View.VISIBLE : View.INVISIBLE);
        imTo  .setVisibility(line.to   ? View.VISIBLE : View.INVISIBLE);

        if (line.isBefore(Calendar.getInstance().getTime()))
            text.setTextColor(Color.LTGRAY);

        tr.addView(row);
        return tr;
    }
}
