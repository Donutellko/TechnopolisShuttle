package com.donutellko.technopolisshuttle;

import android.Manifest;
import android.app.FragmentManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import static android.content.Context.LOCATION_SERVICE;

public class MapView extends SView implements OnMapReadyCallback {

	private FragmentManager fragmentManager;
	private final int
			LAYOUT_RESOURCE = R.layout.map_layout,
			ADRESSES_LAYOUT = R.layout.map_adresses;

	private LatLng
			coordsTechnopolis,
			coordsUnderground;

	public MapView(Context context, FragmentManager fragmentManager, LatLng coordsTechnopolis, LatLng coordsUnderground) {
		super(context);
		view = new LinearLayout(context);
		this.coordsTechnopolis = coordsTechnopolis;
		this.coordsUnderground = coordsUnderground;
		this.fragmentManager = fragmentManager;
	}

	@Override
	public void prepareView() {
		MapFragment mapFragment = (MapFragment) fragmentManager.findFragmentById(R.id.map);

		mapFragment.getMapAsync(this);

		View map = View.inflate(context, R.layout.map_layout, null);
		LinearLayout content = new LinearLayout(context, null);

		content.addView(View.inflate(context, R.layout.map_adresses, null));
		content.addView(map);
	}

	@Override
	public void updateView() {

	}

	@Override
	public void onMapReady(GoogleMap map) {

		LatLngBounds all = new LatLngBounds(
				coordsTechnopolis, coordsUnderground);

		map.moveCamera(CameraUpdateFactory.newLatLngBounds(all, 0));

		if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			// TODO: Consider calling
			//    ActivityCompat#requestPermissions
			// here to request the missing permissions, and then overriding
			//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
			//                                          int[] grantResults)
			// to handle the case where the user grants the permission. See the documentation
			// for ActivityCompat#requestPermissions for more details.
			return;
		}

		map.setMyLocationEnabled(true);
//        map.moveCamera(CameraUpdateFactory.newLatLngZoom(underground, 13));
//        map.moveCamera(CameraUpdateFactory.newLatLngZoom(technopolis, 13));

		map.addMarker(new MarkerOptions()
				.title("м. Московская")
				.snippet("Московский проспект, 189")
				.position(coordsUnderground));


		map.addMarker(new MarkerOptions()
				.title("TECHNOPOLIS")
				.snippet("Пулковское шоссе, 40к4")
				.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
				.position(coordsTechnopolis));
	}

	public double getDistanceBetween(LatLng first, LatLng second) {
		double MAGIC_CONSTANT = 110.096; // коэфф километры/координаты
		if (first == null || second == null) return -1;
		double dlatt = first.latitude - second.latitude,
				dlong = first.longitude - second.longitude;
		return MAGIC_CONSTANT * Math.sqrt(dlatt * dlatt + dlong * dlong);
	}

	public LatLng getLocation() {
		LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		String provider = locationManager.getBestProvider(criteria, true);
		if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			// ну и ладно
			// ну и пожалста
			// ну и очень-то и хотелось
			return null;
		}
		Location location = locationManager.getLastKnownLocation(provider);
		if (location == null) {
			Log.i("getLocation()", "location is null");
			return null;
		} else {
			double latitude = location.getLatitude();
			double longitude = location.getLongitude();
			Log.i("getLocation()", latitude + " : " + longitude);
			return new LatLng(latitude, longitude);
		}
	}
}
