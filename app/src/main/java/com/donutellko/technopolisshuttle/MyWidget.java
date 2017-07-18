package com.donutellko.technopolisshuttle;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RemoteViews;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.ToggleButton;

import java.util.List;

import static com.donutellko.technopolisshuttle.DataLoader.getCurrentTime;

/**
 * Implementation of App Widget functionality.
 */
public class MyWidget extends AppWidgetProvider {

	private WidgetView widgetView;

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		// There may be multiple widgets active, so update all of them
		//for (int appWidgetId : appWidgetIds) {
		//	updateAppWidget(context, appWidgetManager, appWidgetId);
		//}=
		widgetView = new WidgetView(context, context.getApplicationContext());
		RemoteViews rv = widgetView.getView();

		appWidgetManager.updateAppWidget(appWidgetIds[0], rv);
		Log.i("MyWidget", "onUpdate()");
	}


	class WidgetView {
		//int
		//		TOGGLE_TO = R.id.to_tech,
		//		TOGGLE_FROM = R.id.from_tech;
		boolean showTo = true;


		Context context, appcontext;
		RemoteViews remoteViews;

		Button toggleTo, toggleFrom;
		private DataLoader dataLoader;
		private TimeTable timeTable;

		public WidgetView(Context context, Context appcontext) {
			this.context = context;
			this.appcontext = appcontext;
			prepareView();
		}

		public void prepareView() {
			Log.i("MyWidget", "prepareView()");

			if (dataLoader == null) {
				dataLoader = new DataLoader();

				if (Settings.singleton.jsonCached == null) {
					Settings.singleton = new Settings();
					Settings.singleton.loadPreferences(appcontext);
					if (Settings.singleton.jsonCached == null)
						Settings.singleton.jsonCached = DataLoader.getJsonDefault();
				}

				timeTable = DataLoader.getJsonTimeTable(dataLoader.getJsonOffline());
			}

			remoteViews = new RemoteViews(context.getPackageName(), R.layout.my_widget); //R.id.widget_content);

			DataLoader.STime now = getCurrentTime();


			List<TimeTable.ScheduleElement> after =
					timeTable.getTimeAfter(now, showTo, now.weekday);

			TimeTable.ScheduleElement[] afterarray = new TimeTable.ScheduleElement[after.size()];
			int i = 0;
			for (TimeTable.ScheduleElement element : after) {
				afterarray[i] = element;
				i++;
			}

			Log.i("MyWidget", Settings.singleton.jsonCached);
			Log.i("MyWidget", "afterarray.length = " + afterarray.length);

			if (afterarray.length == 0)
				remoteViews.setTextViewText(R.id.time1, "пусто");
			if (afterarray.length > 0) {
				remoteViews.setTextViewText(R.id.time1, afterarray[0].time.toString());
				remoteViews.setTextViewText(R.id.timeleft1, now.getDifference(afterarray[0].time).toTextString());
			}
			if (afterarray.length > 1) {
				remoteViews.setTextViewText(R.id.time2, afterarray[1].time.toString());
				remoteViews.setTextViewText(R.id.timeleft2, now.getDifference(afterarray[1].time).toTextString());
			}
			if (afterarray.length > 2) {
				remoteViews.setTextViewText(R.id.time3, afterarray[2].time.toString());
				remoteViews.setTextViewText(R.id.timeleft3, now.getDifference(afterarray[2].time).toTextString());
			}

			/*toggleTo = view.findViewById(TOGGLE_TO);
			toggleTo.setOnClickListener(toggleToListener);
			toggleTo.setChecked(showTo);

			toggleFrom = view.findViewById(TOGGLE_FROM);
			toggleFrom.setOnClickListener(toggleFromListener);
			toggleFrom.setChecked(!showTo);*/
		}

		public RemoteViews getView() {
			Log.i("MyWidget", "getView()");
			return remoteViews;
		}

	}
}

