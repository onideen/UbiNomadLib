package no.ntnu.ubinomad.lib.models;

import java.io.Serializable;

public class UbiLocation implements Serializable {
	
	private double latitude;
	private double longitude;
	
	public UbiLocation(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	public double getLatitude() {
		return latitude;
	}
	
	public double getLongitude() {
		return longitude;
	}
	
	/**
	 * Returns other distance to another location in meters using Haversine Distance Algorithm
	 * @author vegaen
	 * @return double
	 */
	public double distanceTo(UbiLocation otherLocation) {
		final int R = 6371; // Radius of the earth
		
        final double latDistance = Math.toRadians(otherLocation.getLatitude()-latitude);
        final double lonDistance = Math.toRadians(otherLocation.getLongitude()-longitude);
        final double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + 
                   Math.cos(Math.toRadians(latitude)) * Math.cos(Math.toRadians(otherLocation.getLatitude())) * 
                   Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double distance = R * c;
	         
		return distance*1000;	
	}
	
	@Override
	public boolean equals(Object o) {

		UbiLocation otherLocation = (UbiLocation)o;
		return otherLocation.getLatitude() == getLatitude() && otherLocation.getLongitude() == getLongitude();
	}
	
}
