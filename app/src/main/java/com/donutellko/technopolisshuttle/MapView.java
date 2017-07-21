package com.donutellko.technopolisshuttle;

import android.support.v4.app.ActivityCompat;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.app.FragmentManager;
import android.widget.LinearLayout;
import android.location.Criteria;
import android.location.Location;
import android.content.Context;
import android.view.View;
import android.util.Log;
import android.Manifest;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.GoogleMap;

import static android.content.Context.LOCATION_SERVICE;

public class MapView extends SView implements OnMapReadyCallback {

	private FragmentManager fragmentManager;
	private final int
			FRAGMENT_RESOURCE = R.layout.map_fragment,
			LAYOUT_RESOURCE = R.layout.map_layout;

	private LatLng
			coordsTechnopolis,
			coordsUnderground;

	public MapView(Context context, FragmentManager fragmentManager, LatLng coordsTechnopolis, LatLng coordsUnderground) {
		super(context);
		view = new LinearLayout(context);
//		((LinearLayout) view).set
		//new LinearLayout(context, LinearLayout.VERTICAL);
//		new LinearLayout(context, new AttributeSet(LinearLayout.VERTICAL));
		this.coordsTechnopolis = coordsTechnopolis;
		this.coordsUnderground = coordsUnderground;
		this.fragmentManager = fragmentManager;

		view = View.inflate(context, LAYOUT_RESOURCE, null);
//		((LinearLayout) view).addView(mapView); //TODO: не отображается правильно
//		((LinearLayout) view.findViewById(R.id.container)).addView(View.inflate(context, FRAGMENT_RESOURCE, null));
//		((LinearLayout) mapView.findViewById(R.id.container)).addView(View.inflate(context, FRAGMENT_RESOURCE, null));

	}

	@Override
	public void prepareView() {
		MapFragment mapFragment = (MapFragment) fragmentManager.findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);
	}

	@Override
	public void updateView() {
		throw new UnsupportedOperationException("updateView не должно вызываться у MapView");
	}

	@Override
	public void onMapReady(GoogleMap map) {
//		LatLngBounds bounds = new LatLngBounds(coordsTechnopolis, coordsUnderground);
		LatLngBounds bounds = new LatLngBounds(new LatLng(59.8, 30.32), new LatLng(59.87, 30.33));
		CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 0);
//		map.moveCamera(cameraUpdate);

		if (coordsTechnopolis.latitude == 0) {
			coordsTechnopolis = new LatLng(59.818026, 30.327783);
			coordsUnderground = new LatLng(59.854728, 30.320958);
		}

		try { // В случае вылета из-за карты открывается ShortView, чтобы работать при следующем запуске
			map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 0));
		} catch (Exception e) {
			Settings.singleton.currentState = MainActivity.State.SHORT_VIEW;
			Settings.singleton.savePreferences(MainActivity.applicationContext);
		}
//		map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 0));

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
				.title(context.getString(R.string.metro))
				.snippet(context.getString(R.string.address_metro))
				.position(coordsUnderground));


		map.addMarker(new MarkerOptions()
				.title("TECHNOPOLIS")
				.snippet(context.getString(R.string.address_technopolis))
				.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
				.position(coordsTechnopolis));

		map.addMarker(new MarkerOptions()
				.title("Северный полюс")
				.snippet("Тут нет пингвинов")
				.position(new LatLng(80, 30)));

		map.addMarker(new MarkerOptions()
				.title("Южный полюс")
				.snippet("Тут есть пингвины")
				.position(new LatLng(-86, 30)));

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
