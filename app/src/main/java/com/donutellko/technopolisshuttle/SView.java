package com.donutellko.technopolisshuttle;

import android.content.Context;
import android.view.View;


/**
 * Created by donat on 7/10/17.
 */


public abstract class SView {
	protected Context context;
	protected View view;

	public SView(Context context) {
		this.context = context;
	}

	public View getView() {
		return view;
	}

	public abstract void prepareView();

	public abstract void updateView();

}
