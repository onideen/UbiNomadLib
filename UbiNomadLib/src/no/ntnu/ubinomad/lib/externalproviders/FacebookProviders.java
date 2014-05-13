package no.ntnu.ubinomad.lib.externalproviders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.location.Location;
import android.util.Log;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Request.Callback;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.Request.GraphPlaceListCallback;
import com.facebook.android.Facebook;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphPlace;
import com.facebook.model.GraphUser;

import no.ntnu.ubinomad.lib.Provider;
import no.ntnu.ubinomad.lib.connectors.FacebookConnector;
import no.ntnu.ubinomad.lib.helpers.PlaceHelper;
import no.ntnu.ubinomad.lib.interfaces.ExternalDataListener;
import no.ntnu.ubinomad.lib.interfaces.ExternalProvider;
import no.ntnu.ubinomad.lib.interfaces.AggregatorPlace;
import no.ntnu.ubinomad.lib.interfaces.Place;
import no.ntnu.ubinomad.lib.interfaces.ProviderConnector;
import no.ntnu.ubinomad.lib.interfaces.RawPlace;
import no.ntnu.ubinomad.lib.models.GenericRawPlace;
import no.ntnu.ubinomad.lib.models.UbiLocation;
import no.ntnu.ubinomad.lib.models.User;

public class FacebookProviders implements ExternalProvider {

	private ProviderConnector connector;
	
	
	public static RawPlace createPlace(GraphPlace graphPlace) {
		return new GenericRawPlace(
				graphPlace.getName(), 
				Provider.FACEBOOK,
				graphPlace.getId(),
				"http://graph.facebook.com/" + graphPlace.getId() + "/picture?type=small",
				new UbiLocation(graphPlace.getLocation().getLatitude(), graphPlace.getLocation().getLongitude())
				);
		
	}
	
	@Override
	public void getNearPlaces(Location location, int radius, final ExternalDataListener dataListener) {
		
		
		
		final Session session = Session.getActiveSession();
		if (session != null & session.isOpened()) {
			
			// Make an API call to get nearby places and define a new callback to handle the response
			Request request = Request.newPlacesSearchRequest(session, location, (int)radius, 100, "", 
					new GraphPlaceListCallback() {
	
				@Override
				public void onCompleted(List<GraphPlace> graphPlaces, Response response) {
					List<Place> places =  new ArrayList<Place>();
					for (GraphPlace place : graphPlaces) {
						//Log.v(TAG, place.getInnerJSONObject().toString());
						places.add(createPlace(place));
					}
					dataListener.fireCollectionAdded(places);
				}
			});
			request.executeAsync();
		}
	}

	@Override
	public RawPlace getFromId(String id) {
		final Session session = Session.getActiveSession();
		if (session != null & session.isOpened()) {
			
			// Make an API call to get nearby places and define a new callback to handle the response
			Request request = Request.newGraphPathRequest(session, id, new Callback() {
				
				@Override
				public void onCompleted(Response response) {
					
				}
			});
			
			GraphPlace graphPlace = request.executeAndWait().getGraphObject().cast(GraphPlace.class);
			
			return createPlace(graphPlace);
			
		}
		
		return null;
		
	}

	@Override
	public ProviderConnector getConnector() {
		if (connector == null) {
			connector = new FacebookConnector();
		}
		return connector;
	}
	
	@Override
	public User getConnectedUser() {
		final Session session = Session.getActiveSession();
		
		final User user = new User();
		
		if (session != null & session.isOpened()) {
			
			Request request = Request.newMeRequest(session, new Request.GraphUserCallback() {

				@Override
				public void onCompleted(GraphUser graphUser, Response response) {
					// If response is successful
					
					if (graphUser != null){
						user.setEmail(graphUser.getProperty("email").toString());
						user.setName(graphUser.getName());
						Log.v(TAG, String.format("User: [%s, %s]", user.getName(), user.getEmail() ));
					}

					if (response.getError() != null){
						// Handle error, will do so later
					}
					
					
				}
			});
			request.executeAndWait();	
		}
		
		return null;
	}
	
	@Override
	public void postStatus(Activity activity, String message, AggregatorPlace place) {
		Session session = Session.getActiveSession();
		if(session != null && session.isOpened()){
			
	        // Check for publish permissions
	        List<String> permissions = session.getPermissions();
	        if(!permissions.contains("publish_stream")){
	        	
	            Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(activity, Arrays.asList("publish_stream"));
	            session.requestNewPublishPermissions(newPermissionsRequest);
	            return;
	        }
			
	        GraphPlace gPlace = null;
	        
	        RawPlace providerPlace = PlaceHelper.getRawPlaceFromAggregator(activity, place, Provider.FACEBOOK);
	        
	        if(providerPlace != null){
	        	gPlace = GraphObject.Factory.create(GraphPlace.class);
	        	gPlace.setId(providerPlace.getRawReference());
	        }  	
	        
	        Request request = Request.newStatusUpdateRequest(session, message, gPlace, null, new Request.Callback(){
	        	@Override
	        	public void onCompleted(Response response) {
	        		Log.i(TAG , "POSTED: " + response.toString());
	        		
	        	}
	        	
	        });
	        
	        request.executeAsync();
		}
	}
}
