package no.ntnu.ubinomad.lib.interfaces;

import no.ntnu.ubinomad.lib.models.UbiLocation;

public interface Place extends Comparable<Place> {
	
	public long getId();
	public String getName();
	public String getIconUrl();
	public UbiLocation getLocation();
	public int getDistance();
	void setDistance(int distance);
	public int distanceTo(UbiLocation location);
}