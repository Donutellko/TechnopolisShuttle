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

	Context context;
	AppWidgetManager appWidgetManager;
	int[] appWidgetIds;

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		// There may be multiple widgets active, so update all of them
		//for (int appWidgetId : appWidgetIds) {
		//	updateAppWidget(context, appWidgetManager, appWidgetId);
		//}=

		this.context = context;
		this.appWidgetManager = appWidgetManager;
		this.appWidgetIds = appWidgetIds;

		widgetView = new WidgetView(context, context.getApplicationContext());
		RemoteViews rv = widgetView.getView();

		appWidgetManager.updateAppWidget(appWidgetIds[0], rv);
		Log.i("MyWidget", "onUpdate()");
	}

	public void onMyButtonClick() {
		this.onUpdate(context, appWidgetManager, appWidgetIds);
	}

	class WidgetView {
		//int
		//		TOGGLE_TO = R.id.to_tech,
		//		TOGGLE_FROM = R.id.from_tech;
		boolean showTo = true;
		final int[][] tvs = {
				{R.id.to1, R.id.fr1},
				{R.id.to2, R.id.fr2},
				{R.id.to3, R.id.fr3}
		};


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
			int count = 3;

			if (dataLoader == null) {
				dataLoader = new DataLoader(context);

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


			List<TimeTable.ScheduleElement> afterTo = timeTable.getTimeAfter(now, true, now.weekday);
			List<TimeTable.ScheduleElement> afterFr = timeTable.getTimeAfter(now, false, now.weekday);

			TimeTable.ScheduleElement[] afterArrayTo = new TimeTable.ScheduleElement[afterTo.size()];
			TimeTable.ScheduleElement[] afterArrayFr = new TimeTable.ScheduleElement[afterFr.size()];

			int m = 0;
			for (TimeTable.ScheduleElement element : afterTo) {
				afterArrayTo[m] = element;
				m++;
			}
			m = 0;
			for (TimeTable.ScheduleElement element : afterFr) {
				afterArrayFr[m] = element;
				m++;
			}

			Log.i("MyWidget", Settings.singleton.jsonCached);

			if (afterArrayTo.length + afterArrayFr.length == 0) {
				remoteViews.setTextViewText(R.id.to1, "-");
				remoteViews.setTextViewText(R.id.fr1, "-");
			} else {
				for (int i = 0; i < Math.min(afterArrayTo.length, count); i++)
					remoteViews.setTextViewText(tvs[i][0], afterArrayTo[i].time.toString());
				for (int i = 0; i < Math.min(afterArrayTo.length, count); i++)
					remoteViews.setTextViewText(tvs[i][1], afterArrayFr[i].time.toString());
			}
		}

		public RemoteViews getView() {
			Log.i("MyWidget", "getView()");
			return remoteViews;
		}

	}
	public void onUpdateButtonClick() {
		this.onUpdate(context, appWidgetManager, appWidgetIds);
	}
}

