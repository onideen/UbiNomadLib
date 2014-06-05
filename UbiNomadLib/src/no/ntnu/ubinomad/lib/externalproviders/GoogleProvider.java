package no.ntnu.ubinomad.lib.externalproviders;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import no.ntnu.ubinomad.lib.Provider;
import no.ntnu.ubinomad.lib.connectors.GoogleConnector;
import no.ntnu.ubinomad.lib.helpers.PlaceHelper;
import no.ntnu.ubinomad.lib.interfaces.ExternalDataListener;
import no.ntnu.ubinomad.lib.interfaces.ExternalProvider;
import no.ntnu.ubinomad.lib.interfaces.AggregatorPlace;
import no.ntnu.ubinomad.lib.interfaces.Place;
import no.ntnu.ubinomad.lib.interfaces.ProviderConnector;
import no.ntnu.ubinomad.lib.interfaces.RawPlace;
import no.ntnu.ubinomad.lib.models.GenericRawPlace;
import no.ntnu.ubinomad.lib.models.GooglePlace;
import no.ntnu.ubinomad.lib.models.AbstractRawPlace;
import no.ntnu.ubinomad.lib.models.UbiLocation;
import no.ntnu.ubinomad.lib.models.User;
import no.ntnu.ubinomad.lib.utilities.GooglePlaceDetail;
import no.ntnu.ubinomad.lib.utilities.PlacesList;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.plus.PlusShare;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson.JacksonFactory;

public class GoogleProvider implements ExternalProvider {
 
	private ProviderConnector connector;
	
	
    /** Global instance of the HTTP transport. */
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
 
    // Google API Key
    private static final String API_KEY = "AIzaSyAJu7JExNRgs_CKGY6sZxlVuhklAF6dT3M";
 
    // Google Places serach url's
    private static final String PLACES_SEARCH_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
    @SuppressWarnings("unused")
	private static final String PLACES_TEXT_SEARCH_URL = "https://maps.googleapis.com/maps/api/place/search/json?";
    private static final String PLACES_DETAILS_URL = "https://maps.googleapis.com/maps/api/place/details/json?";
 
    private static final String ALL_TYPES = "accounting|airport|amusement_park|aquarium|art_gallery|atm|bakery|bank|bar|beauty_salon|bicycle_store|book_store|bowling_alley|bus_station|cafe|campground|car_dealer|car_rental|car_repair|car_wash|casino|cemetery|church|city_hall|clothing_store|convenience_store|courthouse|dentist|department_store|doctor|electrician|electronics_store|embassy|finance|fire_station|florist|food|funeral_home|furniture_store|gas_station|general_contractor|grocery_or_supermarket|gym|hair_care|hardware_store|health|hindu_temple|home_goods_store|hospital|insurance_agency|jewelry_store|laundry|lawyer|library|liquor_store|local_government_office|locksmith|lodging|meal_delivery|meal_takeaway|mosque|movie_rental|movie_theater|moving_company|museum|night_club|painter|park|parking|pet_store|pharmacy|physiotherapist|place_of_worship|plumber|police|post_office|real_estate_agency|restaurant|roofing_contractor|rv_park|school|shoe_store|shopping_mall|spa|stadium|storage|store|subway_station|synagogue|taxi_stand|train_station|travel_agency|university|veterinary_care|zoo";
    
    private double _latitude;
    private double _longitude;
    private double _radius;
 
    private ExternalDataListener externalListener;

	private boolean runningGoogle = false;
	
    
    /**
     * Searching places
     * @param latitude - latitude of place
     * @params longitude - longitude of place
     * @param radius - radius of searchable area
     * @param types - type of place to search
     * @return list of places
     * */
    public PlacesList search(double latitude, double longitude, double radius, String types)
            throws Exception {
 
        this._latitude = latitude;
        this._longitude = longitude;
        this._radius = radius;
 
        try {
 
            HttpRequestFactory httpRequestFactory = createRequestFactory(HTTP_TRANSPORT);
            HttpRequest request = httpRequestFactory
                    .buildGetRequest(new GenericUrl(PLACES_SEARCH_URL));
            request.getUrl().put("key", API_KEY);
            request.getUrl().put("location", _latitude + "," + _longitude);
            request.getUrl().put("rankby", "distance"); // in meters
            request.getUrl().put("sensor", "true");
            if(types == null){
            	request.getUrl().put("types", ALL_TYPES);
            } else {            	
                request.getUrl().put("types", types);
            }
 
            Log.i(TAG, request.getUrl().toString());
            
            PlacesList list = request.execute().parseAs(PlacesList.class);
            // Check log cat for places response status
            Log.d("Places Status", "" + list.status);
            return list;
 
        } catch (HttpResponseException e) {
            Log.e("Error:", e.getMessage());
            return null;
        }
 
    }
 
    /**
     * Creating http request Factory
     * */
    public static HttpRequestFactory createRequestFactory(
            final HttpTransport transport) {
        return transport.createRequestFactory(new HttpRequestInitializer() {
            public void initialize(HttpRequest request) {
                HttpHeaders headers = new HttpHeaders();
                headers.setUserAgent("bamboo-volt-509");
                request.setHeaders(headers);
                JsonObjectParser parser = new JsonObjectParser(new JacksonFactory());
                request.setParser(parser);
            }
        });
    }

	@Override
	public void getNearPlaces(Location location, int radius, ExternalDataListener dataListener) {
		_latitude = location.getLatitude();
		_longitude = location.getLongitude();
		_radius = radius;
		externalListener = dataListener;
		
		if (!runningGoogle){
			runningGoogle = true;
			new LoadPlaces().execute();
		}
	}
 
	@Override
	public RawPlace getPlaceFromReference(String id) {
		GooglePlaceDetail googlePlaceDetail;
		
		try {
			 
            HttpRequestFactory httpRequestFactory = createRequestFactory(HTTP_TRANSPORT);
            HttpRequest request = httpRequestFactory
                    .buildGetRequest(new GenericUrl(PLACES_DETAILS_URL));
            request.getUrl().put("key", API_KEY);
            request.getUrl().put("reference", id);
            request.getUrl().put("sensor", true);
            
            googlePlaceDetail = request.execute().parseAs(GooglePlaceDetail.class);
            // Check log cat for places response status
            Log.d("Places Status", "" + googlePlaceDetail.status);
            
            return googlePlaceDetail.result;
 
        } catch (HttpResponseException e) {
            Log.e("Error:", e.getMessage());
            return null;
        } catch (IOException e) {
			Log.e(TAG, e.getMessage(), e);
		}
			
		return null;
		
	}
	
	/**
     * Background Async Task to Load Google places
     * */
    private class LoadPlaces extends AsyncTask<String, String, PlacesList> {
 
		private GoogleProvider googlePlaces;

		/**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.v(TAG, "Starting execution");
        }
 
        /**
         * getting Places JSON
         * */
        protected PlacesList doInBackground(String... args) {
            // creating Places class object
        	Log.v(TAG, "Google Places");
            googlePlaces = new GoogleProvider();
             
            try {
            	
                // get nearest places
                return googlePlaces.search(_latitude, _longitude, _radius, null);
                 
 
            } catch (Exception e) {
                Log.e(TAG, "EXCEPTION GOOGLE PLACES", e);
            }
            Log.d(TAG, "RETURN NULL");
            return null;
        }
 
        /**
         * After completing background task Dismiss the progress dialog
         * and show the data in UI
         * Always use runOnUiThread(new Runnable()) to update UI from background
         * thread, otherwise you will get error
         * **/
        protected void onPostExecute(PlacesList placesList) {
            // dismiss the dialog after getting all products
            // updating UI from Background Thread
        	
        	String status = placesList.status;
        	
        	Log.v(TAG, "PostExexute");
        	if (status.equals("OK")){
        		Log.d(TAG, "Status OK");
        		if (placesList.results != null) {

        			List<Place> places = new ArrayList<Place>();
                	Log.v(TAG, "NearPlaces.results != null");
                   
                    externalListener.fireCollectionAdded(placesList.results);
                    
                }
        		
        		
        	} 
        	else if (status.equals("ZERO_RESULTS")){
        		Log.d(TAG, "Sorry no places found. Try to change the types of places");
        	}
        	else if (status.equals("UNKNOWN_ERROR")){
        		Log.d(TAG, "Sorry unknown error occured");
        	}
        	else if (status.equals("OVER_QUERY_LIMIT")){
        		Log.d(TAG, "Sorry query limit to google places is reached");
        	}
        	else if (status.equals("REQUEST_DENIED")){
        		Log.d(TAG, "Sorry error occured. Request is denied");
        	}
        	else if (status.equals("INVALID_REQUEST")){
        		Log.d(TAG, "Sorry error occured. Invalid request");
        	}
        	else {
        		Log.d(TAG, "Sorry error occured.");
        	}
        	//runningGoogle = false;
        }

    }

	@Override
	public ProviderConnector getConnector() {
		if (connector == null){
			connector = new GoogleConnector();
		}
		return connector;
	}

	@Override
	public User getConnectedUser() {
		return ((GoogleConnector) connector).getCurrentUser();
	}

	/**
	 * 
	 * Google share services does not currently (2014-05-05) support places in 
	 * interactive posts. Resolved by appending the place in message text 
	 * 
	 */
	@Override
	public void postStatus(Activity activity, String message, AggregatorPlace place) {
		
		final RawPlace rawPlace = PlaceHelper.getRawPlaceFromAggregator(activity, place, Provider.GOOGLE);

		Intent shareIntent = new PlusShare.Builder(activity)
		
		.setType("text/plain")
          .setText(message + (rawPlace != null ? "--- at " + rawPlace.getName() : ""))
          .getIntent();
		
		activity.startActivityForResult(shareIntent, 0);
		
	}


}