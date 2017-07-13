package com.donutellko.technopolisshuttle;

import android.content.Context;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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
	Button resetButton, saveButton;
	CheckBox noSnackbar, useToast;


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


		noSnackbar = view.findViewById(R.id.no_snackbar);
		noSnackbar.setOnClickListener(noSnackbarListener);

		useToast = view.findViewById(R.id.use_toast);
		useToast.setOnClickListener(useToastListener);


		resetButton = view.findViewById(R.id.reset);
		resetButton.setText("Сбросить настройки и кэш");
		resetButton.setOnClickListener(resetListener);

		saveButton = view.findViewById(R.id.save);
		saveButton.setText("Сохранить");
		saveButton.setOnClickListener(saveListener);
	}

	@Override
	public void updateView() {

		throw new UnsupportedOperationException("updateView не должно вызываться у SettingsView");
	}

	private EditText.OnKeyListener countOnShortListener
			= new EditText.OnKeyListener() {
		@Override
		public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
			if (keyEvent.getAction() == KeyEvent.ACTION_DOWN &&
					(keyCode == KeyEvent.KEYCODE_ENTER)) {
				settingsSingleton.countToShowOnShort = Integer.parseInt(countOnShort.getText() + "");
				Log.i("editor", "lol " + settingsSingleton.countToShowOnShort);
				MainActivity.viewNotifier("Сохранено!");
				return true;
			}
			return false;
		}
	};

	private EditText.OnKeyListener technoRadiusListener
			= new EditText.OnKeyListener() {
		@Override
		public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
			if (keyEvent.getAction() == KeyEvent.ACTION_DOWN &&
					(keyCode == KeyEvent.KEYCODE_ENTER)) {
				settingsSingleton.distanceToShowFrom = Float.parseFloat(technoRadius.getText() + "");
				Log.i("editor", "lol " + settingsSingleton.distanceToShowFrom);
				MainActivity.viewNotifier("Сохранено!");
				return true;
			}
			return false;
		}
	};


	private Button.OnClickListener resetListener = new Button.OnClickListener() {
		@Override
		public void onClick(View view) {
			settingsSingleton.reset();
		}
	};

	private Button.OnClickListener saveListener = new Button.OnClickListener() { //TODO: нормальная проверка
		@Override
		public void onClick(View view) {
			if (technoRadius.getText().toString().equals("") || technoRadius.getText().toString().equals(""))
				MainActivity.viewNotifier("Введены некорректные значения");
			else {
				settingsSingleton.distanceToShowFrom = Float.parseFloat(technoRadius.getText() + "");
				settingsSingleton.countToShowOnShort = Integer.parseInt(countOnShort.getText() + "");

				settingsSingleton.noSnackbar = noSnackbar.isChecked();
				settingsSingleton.showToast = useToast.isChecked();

				settingsSingleton.savePreferences(MainActivity.applicationContext);
				MainActivity.viewNotifier("Сохранено!");
			}
		}
	};

	private View.OnClickListener noSnackbarListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			DataLoader.SettingsSingleton.singleton.noSnackbar = noSnackbar.isChecked();
		}
	};

	private View.OnClickListener useToastListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			DataLoader.SettingsSingleton.singleton.showToast = useToast.isChecked();
		}
	};
}
