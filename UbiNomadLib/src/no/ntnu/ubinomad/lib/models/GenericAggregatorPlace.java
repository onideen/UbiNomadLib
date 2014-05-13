package no.ntnu.ubinomad.lib.models;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.util.Log;
import no.ntnu.ubinomad.lib.contentprovider.UbiNomadContract;
import no.ntnu.ubinomad.lib.contentprovider.UbiNomadContract.AggregatorPlaces;
import no.ntnu.ubinomad.lib.externalproviders.ProviderRegister;
import no.ntnu.ubinomad.lib.interfaces.AggregatorPlace;
import no.ntnu.ubinomad.lib.interfaces.Place;
import no.ntnu.ubinomad.lib.interfaces.RawPlace;

public class GenericAggregatorPlace extends AbstractBaseModel<AggregatorPlace> implements AggregatorPlace {

	private final Uri uri = UbiNomadContract.AggregatorPlaces.CONTENT_URI;
	private final String[] PROJECTION_ALL = UbiNomadContract.AggregatorPlaces.PROJECTION_ALL;
	private String name;
	private String iconUrl;
	private List<RawPlace> rawPlaces;
	private UbiLocation location;
	private int distance;
	

	private GenericAggregatorPlace(long id, String name, String iconUrl) {
		this(name, iconUrl, null, null);
		setId(id);
		
	}

	public GenericAggregatorPlace(RawPlace place){
		this(place.getName(), place.getIconUrl(), place.getLocation(), null);
		if (rawPlaces == null) {
			rawPlaces = new ArrayList<RawPlace>();
		}
		rawPlaces.add(place);
	}
	
	public GenericAggregatorPlace(String name, String iconUrl, UbiLocation location, UbiLocation currentLocation){
		this.name = name;
		this.iconUrl = iconUrl;
		this.location = location;
		//this.providerPlaces = new ArrayList<ProviderPlace>();
		
		if (currentLocation != null){
			distance = (int)currentLocation.distanceTo(location);
		}
	}
	
	public GenericAggregatorPlace(Context context) {
		setContext(context);
	}

	@Override
	protected Uri getUri() {
		return uri;
	}

	@Override
	protected String[] allProjection() {
		return PROJECTION_ALL;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public String getIconUrl() {
		return iconUrl;
	}

	@Override
	public List<RawPlace> getRawPlaces() {
		if (rawPlaces == null) {
			rawPlaces = findProviderPlaces();
		}
		return rawPlaces;
	}
	
	@Override
	protected ContentValues toContentValues() {

		ContentValues values = new ContentValues();
		if (getId() > 0){
			values.put(AggregatorPlaces._ID, getId());
		}
		values.put(AggregatorPlaces.KEY_NAME, name);
		values.put(AggregatorPlaces.KEY_ICON_URL, iconUrl);
		return values;
	}
	
	@Override
	protected AggregatorPlace fromCursor(Cursor cursor) {
		AggregatorPlace place = new GenericAggregatorPlace(
			cursor.getLong(AggregatorPlaces._ID_COL),
			cursor.getString(AggregatorPlaces.NAME_COL),
			cursor.getString(AggregatorPlaces.ICON_URL_COL)
		);
		return place;
		
	}

	public List<RawPlace> findProviderPlaces(){
		RawPlace p = new GenericRawPlace(getContext());
		try {
			return p.findProviderPlacesByPlaceId(getId());
		} catch (RemoteException e) {
			Log.e(TAG, "Remote exception in findProviderPlaces", e);
		}
		return null;
	}
	
	
	@Override
	public int compareTo(Place another) {
		return getDistance() - another.getDistance();
	}

	@Override
	public UbiLocation getLocation() {
		return location;
	}

	@Override
	public int getDistance() {
		return distance;
	}

	@Override
	public void setDistance(int distance) {
		this.distance = distance;
	}

	@Override
	public int distanceTo(UbiLocation location) {
		int distance = (int) location.distanceTo(getLocation());
		setDistance(distance);
		return distance;
	}
}
