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
	Settings settings = Settings.singleton;
	EditText countOnShort, technoRadius, serverIp, timeout;
	Button resetButton, saveButton, closeButton;
	CheckBox noSnackbar, useToast;


	public SettingsView(Context context) {
		super(context);
		prepareView();
	}

	@Override
	public void prepareView() {
		view = View.inflate(context, LAYOUT_RESOURCE, null);

		serverIp = view.findViewById(R.id.server_ip);
		serverIp.setText(settings.serverIp + "");
		serverIp.setOnKeyListener(serverIpListener);

		timeout = view.findViewById(R.id.timeout);
		timeout.setText(settings.connection_timeout + "");
		timeout.setOnKeyListener(timeoutListener);

		countOnShort = view.findViewById(R.id.count_to_show_on_short);
		countOnShort.setText(settings.countToShowOnShort + "");
		countOnShort.setOnKeyListener(countOnShortListener);

		technoRadius = view.findViewById(R.id.techno_radius);
		technoRadius.setText(settings.distanceToShowFrom + "");
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

		closeButton = view.findViewById(R.id.close);
		closeButton.setText("Закрыть");
		closeButton.setOnClickListener(closeListener);
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
				settings.countToShowOnShort = Integer.parseInt(countOnShort.getText() + "");
				Log.i("editor", "lol " + settings.countToShowOnShort);
				MainActivity.viewNotifier("Сохранено!");
				return true;
			}
			return false;
		}
	};

	private EditText.OnKeyListener serverIpListener
			= new EditText.OnKeyListener() {
		@Override
		public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
			if (keyEvent.getAction() == KeyEvent.ACTION_DOWN &&
					(keyCode == KeyEvent.KEYCODE_ENTER)) {
				settings.serverIp = serverIp.getText().toString();
				Log.i("editor", "lol " + serverIp);
				MainActivity.viewNotifier("Сохранено!");
				return true;
			}
			return false;
		}
	};

	private EditText.OnKeyListener timeoutListener
			= new EditText.OnKeyListener() {
		@Override
		public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
			if (keyEvent.getAction() == KeyEvent.ACTION_DOWN &&
					(keyCode == KeyEvent.KEYCODE_ENTER)) {
				settings.connection_timeout = Integer.parseInt(timeout.getText() + "");
				Log.i("editor", "lol " + settings.connection_timeout);
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
				settings.distanceToShowFrom = Float.parseFloat(technoRadius.getText() + "");
				Log.i("editor", "lol " + settings.distanceToShowFrom);
				MainActivity.viewNotifier("Сохранено!");
				return true;
			}
			return false;
		}
	};

	private Button.OnClickListener resetListener = new Button.OnClickListener() {
		@Override
		public void onClick(View view) {
			settings.reset();
			prepareView();
		}
	};

	private Button.OnClickListener saveListener = new Button.OnClickListener() { //TODO: нормальная проверка
		@Override
		public void onClick(View view) {
			if (technoRadius.getText().toString().equals("") || technoRadius.getText().toString().equals(""))
				MainActivity.viewNotifier("Введены некорректные значения");
			else {
				settings.distanceToShowFrom = Float.parseFloat(technoRadius.getText() + "");
				settings.connection_timeout = Integer.parseInt(timeout.getText() + "");
				settings.countToShowOnShort = Integer.parseInt(countOnShort.getText() + "");
				settings.serverIp = serverIp.getText().toString();

				settings.noSnackbar = noSnackbar.isChecked();
				settings.showToast = useToast.isChecked();

				settings.savePreferences(MainActivity.applicationContext);
				MainActivity.viewNotifier("Сохранено!");
			}
		}
	};

	private Button.OnClickListener closeListener = new Button.OnClickListener() { //TODO: нормальная проверка
		@Override
		public void onClick(View view) {
			MainActivity.changeView(settings.currentState);
		}
	};

	private View.OnClickListener noSnackbarListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			settings.singleton.noSnackbar = noSnackbar.isChecked();
		}
	};

	private View.OnClickListener useToastListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			settings.singleton.showToast = useToast.isChecked();
		}
	};
}
