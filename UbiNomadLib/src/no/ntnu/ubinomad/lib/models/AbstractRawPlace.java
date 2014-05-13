package no.ntnu.ubinomad.lib.models;


import java.util.ArrayList;
import java.util.List;

import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.util.Log;
import no.ntnu.ubinomad.lib.Provider;
import no.ntnu.ubinomad.lib.contentprovider.UbiNomadContract;
import no.ntnu.ubinomad.lib.contentprovider.UbiNomadContract.AggregatorPlaces;
import no.ntnu.ubinomad.lib.contentprovider.UbiNomadContract.RawPlaces;
import no.ntnu.ubinomad.lib.contentprovider.UbiNomadContract.Users;
import no.ntnu.ubinomad.lib.exceptions.NoContextException;
import no.ntnu.ubinomad.lib.interfaces.AggregatorPlace;
import no.ntnu.ubinomad.lib.interfaces.Place;
import no.ntnu.ubinomad.lib.interfaces.RawPlace;

public abstract class AbstractRawPlace extends AbstractBaseModel<RawPlace>  implements RawPlace {

	private final Uri uri = UbiNomadContract.RawPlaces.CONTENT_URI;
	private final String[] PROJECTION_ALL = UbiNomadContract.RawPlaces.PROJECTION_ALL;
	
	private String name;
	private Provider provider;
	private String rawReference;
	private UbiLocation location;
	private String iconUrl;
	private int distance;
	private AggregatorPlace aggregator;
	
	public AbstractRawPlace(String name, Provider provider, String rawReference, String iconUrl, UbiLocation location, UbiLocation currentLocation){
		this.name = name;
		this.provider = provider;
		this.rawReference = rawReference;
		this.iconUrl = iconUrl;
		this.location = location;
		
		if (currentLocation != null){
			distance = (int)currentLocation.distanceTo(location);
		}
	}
	
	public AbstractRawPlace(String name, Provider provider, String rawReference, String iconUrl, UbiLocation location) {
		this(name, provider, rawReference, iconUrl, location, null);
	}
	
	public AbstractRawPlace(){}
	
	@Override
	public Provider getProvider() {
		return provider;
	}

	@Override
	public String getRawReference() {
		return rawReference;
	}
	
	@Override
	public void setRawReference(String rawReference){
		this.rawReference = rawReference;
	}


	@Override
	public String getName() {
		return name;
	}

	@Override
	public UbiLocation getLocation() {
		return location;
	}

	@Override
	public String getIconUrl() {
		return iconUrl;
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
	protected ContentValues toContentValues() {
		ContentValues values = new ContentValues();
		if (getId() > 0){
			values.put(AggregatorPlaces._ID, getId());
		}
		values.put(RawPlaces.KEY_NAME, getName());
		values.put(RawPlaces.KEY_ICON_URL, getIconUrl());
		values.put(RawPlaces.KEY_PROVIDER, getProvider().toString());
		values.put(RawPlaces.KEY_RAW_REFERENCE, getRawReference());
		values.put(RawPlaces.KEY_LATITUDE, getLocation().getLatitude());
		values.put(RawPlaces.KEY_LONGITUDE, getLocation().getLongitude());
		if (aggregator != null) values.put(RawPlaces.KEY_AGGREGATOR_PLACE_ID, aggregator.getId());
		return values;
	}

	@Override
	protected RawPlace fromCursor(Cursor cursor) {
		
		RawPlace place = createProviderPlace(
			cursor.getLong(RawPlaces._ID_COL),
			cursor.getString(RawPlaces.NAME_COL),
			Provider.valueOf(cursor.getString(RawPlaces.PROVIDER_COL)),
			cursor.getString(RawPlaces.RAW_REFERENCE_COL),
			cursor.getString(RawPlaces.ICON_URL_COL),
			new UbiLocation(cursor.getDouble(RawPlaces.LATITUDE_COL), cursor.getDouble(RawPlaces.LONGITUDE_COL))
		);
		
		long masterplaceId;
		if ((masterplaceId = cursor.getLong(RawPlaces.AGGREGATOR_PLACE_ID_COL)) > 0){
			AggregatorPlace mPlace = (AggregatorPlace) (new GenericAggregatorPlace(getContext())).getById(masterplaceId);
			if (mPlace != null){
				place.setAggregatorPlace(mPlace);
			}
		}
		return place;
	}

	@Override
	public int getDistance() {
		return distance;
	}
	
	@Override
	public void setDistance(int distance){
		this.distance = distance;
	}
	
	@Override
	public int distanceTo(UbiLocation location){
		distance = (int) location.distanceTo(getLocation());
		return distance;
	}

	@Override
	public int compareTo(Place another) {
		return getDistance() - another.getDistance();
	}
	
	@Override
	public AggregatorPlace getAggregator(){
		return aggregator;
	}
	
	@Override
	public void setAggregatorPlace(AggregatorPlace aggregatorPlace) {
		aggregator = aggregatorPlace;
	}

	@Override
	public boolean equals(Object o) {

		RawPlace otherPlace = (RawPlace) o;
		
		if (otherPlace.getName().equals(getName())){
			if(otherPlace.getIconUrl().equals(getIconUrl())){
				if (getProvider() == otherPlace.getProvider()){
					if (otherPlace.getLocation().equals(getLocation())) {
						return true;
					}
				}
			}	
		}
		
		return false;
	}
	
	@Override
	public void syncWith(RawPlace externalPlace) {

		name = externalPlace.getName();
		location = externalPlace.getLocation();
		iconUrl = externalPlace.getIconUrl();
	}
	
	protected abstract RawPlace createProviderPlace(long id, String name, Provider provider, String providerReference, String iconUrl, UbiLocation location);

}
