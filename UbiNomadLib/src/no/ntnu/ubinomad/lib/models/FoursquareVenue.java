package no.ntnu.ubinomad.lib.models;

import java.io.Serializable;
import java.util.List;

import no.ntnu.ubinomad.lib.Provider;
import android.location.Location;
import android.util.Log;

import com.google.api.client.util.Key;

public class FoursquareVenue extends GenericRawPlace implements Serializable {

	@Key
	private String id;
	
	@Key
	private String name;
	
	@Key
	private FSLocation location;

	@Key
	private List<Category> categories;
	
	public FoursquareVenue(){
		super(null);
	}
	
	public FoursquareVenue(String name, Provider provider, String providerId, String iconUrl, UbiLocation location) {
    	this(name, provider, providerId, iconUrl, location, null);
    }

    public FoursquareVenue(String name, Provider provider, String providerId, String iconUrl, UbiLocation location, UbiLocation currentLocation) {
    	super(name, provider, providerId, iconUrl, location, currentLocation);
    }
    
    @Override
    public String getName(){
    	return name;
    }
    
    @Override
    public String getRawReference() {
    	return id;
    }
    
	@Override
	public UbiLocation getLocation() {
		return location.getLocation();
	}

	private double getLongitude() {
		return location.lng;
	}

	private double getLatitude() {
		return location.lat;
	}
	
	@Override
	public int getDistance() {
		return location.getDistance();
	}

	@Override
	public Provider getProvider() {
		return Provider.FOURSQUARE;
	}
	
	@Override
	public String getIconUrl() {
		if (categories.size() > 0 && categories.get(0) != null) {
			return categories.get(0).getIconPath();    		
		}
		return null;
	}
	
    public static class FSLocation implements Serializable {
    	@Key
    	private double lat;
    	
    	@Key
    	private double lng;
    	
    	@Key
    	private int distance;
    	
    	public UbiLocation getLocation(){
    		return new UbiLocation(lat, lng);
    	}
    	
    	public int getDistance() {
    		return distance;
    	}
    }
    
    
    public static class Category implements Serializable{
    	
    	@Key
    	private Icon icon;
    	
    	public String getIconPath(){
    		return icon.getIconPath(88);
    	}; 
    	
    	public static class Icon implements Serializable {
    		@Key 
    		private String prefix;
    		
    		@Key
    		private String suffix;
    		
    		public String getIconPath(int size) {
    			Log.i(TAG, prefix+suffix);
    			return prefix + "bg_"+ size + suffix;
    		}
    	}
    }
    
    
}
