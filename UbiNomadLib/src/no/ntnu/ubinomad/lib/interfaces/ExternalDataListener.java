package no.ntnu.ubinomad.lib.interfaces;

import java.util.List;

public interface ExternalDataListener {
	public void fireCollectionAdded(List<? extends Place> places);
}
