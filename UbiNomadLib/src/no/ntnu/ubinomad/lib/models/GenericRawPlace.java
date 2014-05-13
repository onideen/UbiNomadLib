package no.ntnu.ubinomad.lib.models;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentProviderClient;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.util.Log;
import no.ntnu.ubinomad.lib.Provider;
import no.ntnu.ubinomad.lib.contentprovider.UbiNomadContract;
import no.ntnu.ubinomad.lib.contentprovider.UbiNomadContract.RawPlaces;
import no.ntnu.ubinomad.lib.contentprovider.UbiNomadContract.Users;
import no.ntnu.ubinomad.lib.exceptions.NoContextException;
import no.ntnu.ubinomad.lib.interfaces.Place;
import no.ntnu.ubinomad.lib.interfaces.RawPlace;

public class GenericRawPlace extends AbstractRawPlace {

	public GenericRawPlace(String name, Provider provider, String providerId, String iconUrl, UbiLocation location) {
		super(name, provider, providerId, iconUrl, location);
	}

	public GenericRawPlace(String name, Provider provider, String providerId, String iconUrl, UbiLocation location, UbiLocation currentLocation){
		super(name, provider, providerId, iconUrl, location, currentLocation);
	}

	public GenericRawPlace(Context context) {
		setContext(context);
	}
	
	public RawPlace fromProviderPlace(RawPlace pPlace) {
		if (getContext() == null) {
			throw new NoContextException("In order to connect to database a context is needed");
		}
		ContentProviderClient contentProvider = getContext().getContentResolver().acquireContentProviderClient(UbiNomadContract.CONTENT_URI);

		String[] projection = allProjection();

		Cursor cursor;
		try {
			cursor = contentProvider.query(getUri(), projection, String.format("%s=\"%s\" AND %s=\"%s\"", RawPlaces.KEY_PROVIDER, pPlace.getProvider(), RawPlaces.KEY_RAW_REFERENCE, pPlace.getRawReference()), null, null);
		} catch (RemoteException e) {
			Log.e(TAG, e.getMessage(), e);
			return null;
		}
		
		if (cursor.moveToFirst()) {
			return fromCursor(cursor);
		}
		
		return null;
		
		
	}
	
	public List<RawPlace> getByProvider(Provider provider) {
		if (getContext() == null) {
			throw new NoContextException("In order to connect to database a context is needed");
		}
		ContentProviderClient contentProvider = getContext().getContentResolver().acquireContentProviderClient(UbiNomadContract.CONTENT_URI);

		String[] projection = UbiNomadContract.RawPlaces.PROJECTION_ALL;

		List<RawPlace> providerPlaces = new ArrayList<RawPlace>();
		Cursor cursor;
		try {

			cursor = contentProvider.query(getUri(), projection,String.format("%s=\"%s\"", RawPlaces.KEY_PROVIDER, provider),null, null);
			if (cursor.moveToFirst()) {
				do {					
					RawPlace providerplace = fromCursor(cursor);
					
					// Add to list 
					providerPlaces.add(providerplace);
				}while(cursor.moveToNext());
			}	
			return providerPlaces;
		} catch (RemoteException e) {
			Log.e(TAG, "REMOTE ERROR", e);
			return null;
		}
	}
	
	@Override
	protected RawPlace createProviderPlace(long id, String name, Provider provider, String providerId, String iconUrl, UbiLocation location) {
		GenericRawPlace place = new GenericRawPlace(name, provider, providerId, iconUrl, location);
		place.setId(id);
		return place;
	}
	

	@Override
	public List<RawPlace> findProviderPlacesByPlaceId(long placeId) throws RemoteException  {
		if (getContext() == null) {
			throw new NoContextException("In order to connect to database a context is needed");
		}
		
		ContentProviderClient contentProvider = getContext().getContentResolver().acquireContentProviderClient(UbiNomadContract.CONTENT_URI);

		List<RawPlace> ts = new ArrayList<RawPlace>();
		String[] projection = allProjection();
		Cursor cursor = contentProvider.query(getUri(), projection, RawPlaces.KEY_AGGREGATOR_PLACE_ID + " = " + placeId, null, null);
		if (cursor.moveToFirst()) {
			do {					
				RawPlace place = fromCursor(cursor);

				// Add to list 
				ts.add(place);
			}while(cursor.moveToNext());
		}	
		return ts;
	}

	

}
