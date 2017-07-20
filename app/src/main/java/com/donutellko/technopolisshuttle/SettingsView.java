package com.donutellko.technopolisshuttle;

import android.content.Context;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by donat on 7/13/17.
 */

public class SettingsView extends SView {
	int LAYOUT_RESOURCE = R.layout.settings_layout;
	Settings settings = Settings.singleton;
	EditText countOnShort, technoRadius, serverIpContainerAddress, timeout;
	Button resetButton, saveButton, closeButton;
	CheckBox noSnackbar, useToast;

	LinearLayout mainLayout;

	public SettingsView(Context context) {
		super(context);
		view = View.inflate(context, LAYOUT_RESOURCE, null);
		prepareView();
	}

	@Override
	public void prepareView() {

		serverIpContainerAddress = view.findViewById(R.id.server_ip);
		serverIpContainerAddress.setText(settings.serverIpContainerAddress + "");

		timeout = view.findViewById(R.id.timeout);
		timeout.setText(settings.connection_timeout + "");

		countOnShort = view.findViewById(R.id.count_to_show_on_short);
		countOnShort.setText(settings.countToShowOnShort + "");

		technoRadius = view.findViewById(R.id.techno_radius);
		technoRadius.setText(settings.distanceToShowFrom + "");


		noSnackbar = view.findViewById(R.id.no_snackbar);
		noSnackbar.setOnClickListener(noSnackbarListener);

		useToast = view.findViewById(R.id.use_toast);
		useToast.setOnClickListener(useToastListener);


		resetButton = view.findViewById(R.id.reset);
		resetButton.setText(R.string.reset_settings_and_cache);
		resetButton.setOnClickListener(resetListener);

		saveButton = view.findViewById(R.id.save);
		saveButton.setText(context.getString(R.string.save));
		saveButton.setOnClickListener(saveListener);

		closeButton = view.findViewById(R.id.close);
		closeButton.setText(context.getString(R.string.close));
		closeButton.setOnClickListener(closeListener);

//		mainLayout = view.findViewById(R.id.main_layout);
//		mainLayout.removeViewAt(R.id.navigation);

	}

	@Override
	public void updateView() {

		throw new UnsupportedOperationException("updateView не должно вызываться у SettingsView");
	}

	private Button.OnClickListener resetListener = new Button.OnClickListener() {
		@Override
		public void onClick(View view) {
			settings.reset();
			MainActivity.viewNotifier(context.getString(R.string.settings_reset));
			prepareView();
			MainActivity.setContent(MainActivity.settingsView);
		}
	};

	private Button.OnClickListener saveListener = new Button.OnClickListener() { //TODO: нормальная проверка
		@Override
		public void onClick(View view) {
			if (technoRadius.getText().toString().equals("") || technoRadius.getText().toString().equals(""))
				MainActivity.viewNotifier(context.getString(R.string.incorrect_values));
			else {
				MainActivity.getWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
				settings.distanceToShowFrom = Float.parseFloat(technoRadius.getText() + "");
				settings.connection_timeout = Integer.parseInt(timeout.getText() + "");
				settings.countToShowOnShort = Integer.parseInt(countOnShort.getText() + "");
				settings.serverIpContainerAddress = serverIpContainerAddress.getText().toString();

				settings.noSnackbar = noSnackbar.isChecked();
				settings.showToast = useToast.isChecked();

				settings.savePreferences(MainActivity.applicationContext);
				MainActivity.viewNotifier(context.getString(R.string.saved));
//				MainActivity.navigation.setSelectedItemId(MainActivity.navigation.getSelectedItemId());
			}
		}
	};

	private Button.OnClickListener closeListener = new Button.OnClickListener() { //TODO: нормальная проверка
		@Override
		public void onClick(View view) {
			MainActivity.navigation.setVisibility(View.VISIBLE);
			MainActivity.navigation.setSelectedItemId(MainActivity.navigation.getSelectedItemId());
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
