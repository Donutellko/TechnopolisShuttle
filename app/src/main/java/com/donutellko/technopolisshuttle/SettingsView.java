package com.donutellko.technopolisshuttle;

import android.content.Context;
import android.widget.EditText;
import android.widget.TextView;
import android.view.KeyEvent;
import android.view.View;
import android.util.Log;

/**
 * Created by donat on 7/13/17.
 */

public class SettingsView extends SView {
	int LAYOUT_RESOURCE = R.layout.settings_layout;
	DataLoader.SettingsSingleton settingsSingleton;
	EditText countOnShort, technoRadius;

	public SettingsView(Context context, DataLoader.SettingsSingleton settingsSingleton) {
		super(context);
		this.settingsSingleton = settingsSingleton;
		prepareView();
	}

	@Override
	public void prepareView() {
		view = View.inflate(context, LAYOUT_RESOURCE, null);
		countOnShort = view.findViewById(R.id.count_to_show_on_short);
		countOnShort.setText(settingsSingleton.countToShowOnShort + "");
		countOnShort.setOnKeyListener(countOnShortListener);

		technoRadius = view.findViewById(R.id.techno_radius);
		technoRadius.setText(settingsSingleton.distanceToShowFrom + "");
		technoRadius.setOnKeyListener(technoRadiusListener);
	}

	@Override
	public void updateView() {
		throw new UnsupportedOperationException("updateView не должно вызываться у SettingsView");
	}

	private EditText.OnKeyListener countOnShortListener
			= new EditText.OnKeyListener() {
		@Override
		public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
			if(keyEvent.getAction() == KeyEvent.ACTION_DOWN &&
					(keyCode == KeyEvent.KEYCODE_ENTER)) {
				settingsSingleton.countToShowOnShort = Integer.parseInt(countOnShort.getText() + "");
				Log.i("editor", "lol " + settingsSingleton.countToShowOnShort);
				return true;
			}
			return false;
		}
	};

	private EditText.OnKeyListener technoRadiusListener
			= new EditText.OnKeyListener() {
		@Override
		public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
			if(keyEvent.getAction() == KeyEvent.ACTION_DOWN &&
					(keyCode == KeyEvent.KEYCODE_ENTER)) {
				settingsSingleton.distanceToShowFrom = Float.parseFloat(technoRadius.getText() + "");
				Log.i("editor", "lol " + settingsSingleton.distanceToShowFrom);
				return true;
			}
			return false;
		}
	};
}
