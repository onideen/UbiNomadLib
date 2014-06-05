package no.ntnu.ubinomad.lib.interfaces;

import no.ntnu.ubinomad.lib.models.User;
import android.app.Activity;
import android.location.Location;

/**
 * @author Vegar Engen <vegaen@vegaen.no>
 *
 */
public interface ExternalProvider {

	static final String TAG = "ExternalProvider";
	
	public void getNearPlaces(Location location, int radius, ExternalDataListener dataListener);
	public RawPlace getPlaceFromReference(String id);
	public ProviderConnector getConnector();
	
	/**
	 * 
	 * @return <p>The profile of the present user on device</p>
	 */
	public User getConnectedUser();

	/**
	 * Post a message to the Social network, containing message and a place	
	 * 
	 * @param activity
	 * @param message
	 * @param place
	 */
	public void postStatus(Activity activity, String message, AggregatorPlace place);
}
