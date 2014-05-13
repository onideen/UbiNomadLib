package no.ntnu.ubinomad.lib.observers;

import java.util.List;

import no.ntnu.ubinomad.lib.interfaces.Place;

public interface PlacesListObserver {
	
	public void add(Place place);
	
	public void addAll(List<? extends Place> list);
}
