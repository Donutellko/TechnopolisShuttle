package com.donutellko.technopolisshuttle;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int COUNT_TO_SHOW_ON_SHORT = 5;
    private DataLoader dataLoader = new DataLoader();
    private TimeTable timeTable;
    private LayoutInflater layoutInflater;
    private boolean toTechnopolis = true;
    LinearLayout contentView; // Область контента (всё кроме нижней панели)
    private View shortView, fullView, mapView;
    Calendar curtime = Calendar.getInstance();

    int width, height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Display display = getWindowManager().getDefaultDisplay();
        width = display.getWidth();  // deprecated
        height = display.getHeight();  // deprecated

        layoutInflater = getLayoutInflater();

        contentView = (LinearLayout) findViewById(R.id.content);
        makeShortScheduleView();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_short);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
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

    private RadioGroup.OnCheckedChangeListener mOnRadioGroupChangedListener
            = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
            Log.i("radiobutton", "нажата радиокнопка");
            toTechnopolis = ((RadioButton) findViewById(R.id.rb_to)).isActivated();
            makeShortScheduleView();
        }
    };

    private void makeShortScheduleView() {
        contentView.removeAllViews(); // очищаем от добавленных ранее отображений
        Date now = curtime.getTime(); // определяем текущее время

        if (shortView == null) {
            shortView = layoutInflater.inflate(R.layout.short_layout, null);
            RadioGroup radioGroup = (RadioGroup) shortView.findViewById(R.id.radio_group);
            radioGroup.setOnCheckedChangeListener(mOnRadioGroupChangedListener);
            toTechnopolis = ((RadioButton) shortView.findViewById(R.id.rb_to)).isActivated();
        }

        TableLayout table = shortView.findViewById(R.id.table);
        table.removeAllViews();
        table.addView(layoutInflater.inflate(R.layout.short_head, null));

        if (timeTable == null) timeTable = dataLoader.getFullDefaultInfo(); // объект с расписанием

        if (curtime.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) { // особое расписание по пятницам
            CheckBox friday_view = contentView.findViewById(R.id.friday); // TODO: универсализировать для любых наборов дней недели
            friday_view.setChecked(true);
        }

        List<Date> closest = new ArrayList<>(3);
        for (TimeTable.Line line : timeTable.lines) {
            if ((toTechnopolis == line.to || toTechnopolis != line.from) && !line.isBefore(now)) closest.add(line.time);
            if (closest.size() >= COUNT_TO_SHOW_ON_SHORT) break;
        }

        if (closest.size() == 0) {
            TableRow empty_row = new TableRow(getApplicationContext());
            TextView empty_text = new TextView(getApplicationContext());
            empty_text.setText("На сегодня автобусов в этом направлении больше нет.");
            empty_row.addView(empty_text);
            table.addView(empty_row);
        } else
            for (Date t : closest) {
                table.addView(makeTimeLeftRow(t, now));
            }

        contentView.addView(shortView);
    }

    private void makeFullScheduleView() {
        contentView.removeAllViews(); // очищаем от созданных ранее объектов
        fullView = layoutInflater.inflate(R.layout.full_layout, null); // Создаём view с таблицей
        contentView.addView(fullView); // добавляем созданный view в область контента

        if (timeTable == null) timeTable = dataLoader.getFullDefaultInfo(); // объект с данными о времени

        ((LinearLayout) fullView.findViewById(R.id.content)).addView(
                //makeThreeColumnsTable(timeTable)
                makeTwoColumnsTable(timeTable)
                );
    }

    public void makeMapView() {
        contentView.removeAllViews(); // очищаем от созданных ранее объектов


    }

    public TableLayout makeThreeColumnsTable(TimeTable timeTable) {
        TableLayout table = new TableLayout(this); //fullView.findViewById(R.id.table); // находим таблицу на созданном view
        contentView.addView(layoutInflater.inflate(R.layout.full_2col_head, null)); //добавляем заголовок в таблицу так, чтобы он не пролистывался

        for (TimeTable.Line l : timeTable.lines)
            table.addView(makeThreeColumnsRow(l)); // суём инфу в таблицу
        return table;
    }

    public TableLayout makeTwoColumnsTable(TimeTable timeTable) {
        TableLayout table = new TableLayout(this); //fullView.findViewById(R.id.table); // находим таблицу на созданном view
        contentView.addView(layoutInflater.inflate(R.layout.full_2col_head, null)); //добавляем заголовок в таблицу так, чтобы он не пролистывался

        table.addView(layoutInflater.inflate(R.layout.full_3col_head, null));

        int max = timeTable.from.length;
        if (timeTable.to.length > max) max = timeTable.to.length;
        for (int i = 0; i < max; i++) {
            table.addView(makeTwoColumnsRow(
                    (i < timeTable.from.length ? timeTable.from[i] : null),
                    (i < timeTable.to  .length ? timeTable.to  [i] : null))); // суём инфу в таблицу
        }
        return table;
    }

    public View makeTwoColumnsRow(Date t1, Date t2) {
        View row = layoutInflater.inflate(R.layout.full_2col_row, null);
        Date now = Calendar.getInstance().getTime();

        TextView tFrom = (TextView) row.findViewById(R.id.t_from);
        TextView tTo   = (TextView) row.findViewById(R.id.t_to  );

        if (t1 == null) tFrom.setText("");
        else {
            tFrom.setText(t1.getHours() + ":" + (t1.getMinutes() <= 9 ? "0" : "") + t1.getMinutes()); //TODO: format
            if (firstIsBefore(t1, now))
                tFrom.setTextColor(Color.LTGRAY);
        }

        if (t2 == null) tTo.setText("");
        else {
            tTo.setText(t2.getHours() + ":" + (t2.getMinutes() <= 9 ? "0" : "") + t2.getMinutes()); //TODO: format
            if (firstIsBefore(t2, now))
                tTo  .setTextColor(Color.LTGRAY);
        }

        return row;
    }

    public boolean firstIsBefore(Date d1, Date d2) {
        if (d1.getHours() < d2.getHours())
            return true;
        if (d1.getHours() == d2.getHours() && d1.getMinutes() < d2.getMinutes())
            return true;
        return false;
    }

    public View makeThreeColumnsRow(TimeTable.Line line) {
        View row = layoutInflater.inflate(R.layout.full_3col_row, null);
        Calendar calendar = Calendar.getInstance();

        TextView text = (TextView) row.findViewById(R.id.time);
        ImageView imFrom = (ImageView) row.findViewById(R.id.from);
        ImageView imTo = (ImageView) row.findViewById(R.id.to);

        text.setText(line.time.getHours() + ":" + (line.time.getMinutes() <= 9 ? "0" : "") + line.time.getMinutes()); //TODO: format
        imFrom.setVisibility(line.from ? View.VISIBLE : View.INVISIBLE);
        imTo.setVisibility(line.to ? View.VISIBLE : View.INVISIBLE);

        if (line.isBefore(calendar.getTime()))
            text.setTextColor(Color.LTGRAY);

        return row;
    }

    public View makeTimeLeftRow(Date t, Date today) {
        int leftH = t.getHours() - today.getHours();
        int leftM = t.getMinutes() - today.getMinutes();

        String timeLeft;
        if (leftH == 0 && leftM == 0)
            timeLeft = "(прямо сейчас)";
        else {
            if (leftM < 0) {
                leftH--;
                leftM += 60;
            }
            timeLeft = ""; //"(осталось";
            if (leftH != 0) timeLeft += " " + leftH + " час";
            if (leftM != 0) timeLeft += " " + leftM + " мин";
            //timeLeft += ")";
        }

        View row = layoutInflater.inflate(R.layout.short_row, null);
        ((TextView) row.findViewById(R.id.time)).setText(t.getHours() + ":" + (t.getMinutes() <= 9 ? "0" : "") + t.getMinutes());
        ((TextView) row.findViewById(R.id.timeleft)).setText(timeLeft );

        return row;
    }
}