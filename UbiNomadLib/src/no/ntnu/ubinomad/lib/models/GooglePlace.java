package no.ntnu.ubinomad.lib.models;
import java.io.Serializable;
import java.util.Currency;
import java.util.Map;

import org.json.JSONObject;

import no.ntnu.ubinomad.lib.Provider;
import no.ntnu.ubinomad.lib.interfaces.Place;
import no.ntnu.ubinomad.lib.interfaces.RawPlace;
import android.content.Context;
import android.util.Log;

import com.facebook.model.GraphLocation;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphObjectList;
import com.facebook.model.GraphPlace;
import com.google.api.client.util.Key;
 
/** Implement this class from "Serializable"
* So that you can pass this class Object to another using Intents
* Otherwise you can't pass to another activity
* */
public class GooglePlace extends GenericRawPlace implements Serializable {
 
	@Key
    private String id;
     
    @Key
    private String name;
     
    @Key
    private String reference;
     
    @Key
    private String icon;
     
    @Key
    private String vicinity;
     
    @Key
    private Geometry geometry;
     
    @Key
    private String formatted_address;
     
    @Key
    private String formatted_phone_number;
 
    @Override
    public String toString() {
        return name + " - " + id + " - " + reference;
    }
     
    public static class Geometry implements Serializable
    {
        @Key
        public Location location;
    }
     
    public static class Location implements Serializable
    {
        @Key
        public double lat;
         
        @Key
        public double lng;
    }
    
    public GooglePlace(){
    	super(null);
    }
    
    public GooglePlace(String name, Provider provider, String providerId, String iconUrl, UbiLocation location) {
    	this(name, provider, providerId, iconUrl, location, null);
    }

    public GooglePlace(String name, Provider provider, String providerId, String iconUrl, UbiLocation location, UbiLocation currentLocation) {
    	super(name, provider, providerId, iconUrl, location, currentLocation);
    }
    
    @Override
    public String getName(){
    	return name;
    }
    
    public double getLatitude() {
    	return geometry.location.lat;
    }
    
    public double getLongitude() {
    	return geometry.location.lng;
    }

    @Override
	public String getIconUrl() {
		return icon;
	}

	@Override
	public Provider getProvider() {
		return Provider.GOOGLE;
	}

	@Override
	public String getRawReference() {
		Log.i(TAG, "ProviderID: " + reference);
		return reference;
	}

	@Override
	public UbiLocation getLocation() {
		return new UbiLocation(getLatitude(), getLongitude());
	}

	@Override
	public long getId() {
		return 0;
	}
	
	@Override
	protected RawPlace createProviderPlace(long id, String name, Provider provider, String providerId, String iconUrl, UbiLocation location) {
		GooglePlace place = new GooglePlace(name, Provider.GOOGLE, providerId, iconUrl, location);
		place.setId(id);
		return place;
	}
	
}