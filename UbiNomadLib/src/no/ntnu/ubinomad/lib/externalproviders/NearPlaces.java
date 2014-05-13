package no.ntnu.ubinomad.lib.externalproviders;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import no.ntnu.ubinomad.lib.Provider;
import no.ntnu.ubinomad.lib.interfaces.ExternalDataListener;
import no.ntnu.ubinomad.lib.interfaces.ExternalProvider;
import no.ntnu.ubinomad.lib.interfaces.Place;
import no.ntnu.ubinomad.lib.models.UbiLocation;
import no.ntnu.ubinomad.lib.observers.PlacesListObserver;
import no.ntnu.ubinomad.lib.services.GPSTracker;

public class NearPlaces implements ExternalDataListener {

	private static final String TAG = "NearbyPlaces";

	private Location location;
	private double radius;
	private String types;
	private List<PlacesListObserver> observers;

	private GPSTracker gps;

	private static NearPlaces nearPlaces;

	public static NearPlaces getNearPlaces(Context context){
		return nearPlaces == null ? new NearPlaces(context) : nearPlaces; 
	}

	private NearPlaces(Context context) {
		gps = new GPSTracker(context);
		observers = new ArrayList<PlacesListObserver>();
	}

	public void getNearPlaces(double radius, String types) {
		this.radius = radius;
		this.types = types;

		if(gps.canGetLocation()){

			location = gps.getLocation();
			Log.v(TAG, String.format("[%f, %f]",location.getLatitude(), location.getLongitude()));

		}else{
			// can't get location
			// GPS or Network is not enabled
			// Ask user to enable GPS/network in settings
			gps.showSettingsAlert();
		}

		for (Provider provider : ProviderRegister.getInstance().getProviders()) {
			ExternalProvider externalProvider = ProviderRegister.getInstance().getExternalProvider(provider);
			externalProvider.getNearPlaces(location, 1000, this);
			Log.d(TAG, "EXternal Provider" + externalProvider.toString());
		}
	}

	public void addListener(PlacesListObserver listObserver){
		observers.add(listObserver);
	}

	@Override
	public void fireCollectionAdded(List<? extends Place> places) {
		for (Place place : places) {
			place.distanceTo(new UbiLocation(location.getLatitude(), location.getLongitude()));
		}

		for (PlacesListObserver observer : observers){
			observer.addAll(places);
		}
	}
}
