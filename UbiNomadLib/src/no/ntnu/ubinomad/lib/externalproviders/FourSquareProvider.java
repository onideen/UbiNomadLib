package no.ntnu.ubinomad.lib.externalproviders;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;


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

import android.app.Activity;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import no.ntnu.ubinomad.lib.Provider;
import no.ntnu.ubinomad.lib.connectors.FourSquareConnector;
import no.ntnu.ubinomad.lib.connectors.FourSquareTokenStore;
import no.ntnu.ubinomad.lib.helpers.PlaceHelper;
import no.ntnu.ubinomad.lib.interfaces.AggregatorPlace;
import no.ntnu.ubinomad.lib.interfaces.ExternalDataListener;
import no.ntnu.ubinomad.lib.interfaces.ExternalProvider;
import no.ntnu.ubinomad.lib.interfaces.Place;
import no.ntnu.ubinomad.lib.interfaces.ProviderConnector;
import no.ntnu.ubinomad.lib.interfaces.RawPlace;
import no.ntnu.ubinomad.lib.models.AbstractBaseModel;
import no.ntnu.ubinomad.lib.models.User;
import no.ntnu.ubinomad.lib.utilities.FoursquareVenueDetail;
import no.ntnu.ubinomad.lib.utilities.GooglePlaceDetail;
import no.ntnu.ubinomad.lib.utilities.PlacesList;

public class FourSquareProvider implements ExternalProvider {


	private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

	private static final String PLACES_SEARCH_URL = "https://api.foursquare.com/v2/venues/search";
	private static final String CHECKIN_URL = "https://api.foursquare.com/v2/checkins/add";
	
	private static final String PLACE_DETAIL_URL = "https://api.foursquare.com/v2/venues/";

	private final String clientId;
	private final String clientSecret;
	
	
	private ProviderConnector connector;

	private double _latitude;
	private double _longitude;
	private double _radius;

	private ExternalDataListener externalListener;
	private boolean runningFoursquare;

	
	public FourSquareProvider(String clientId, String clientSecret) {
		this.clientId = clientId;
		this.clientSecret = clientSecret;
	}
	
	public FoursquareSearchResult search(double latitude, double longitude, double radius, String types)
			throws Exception {

		this._latitude = latitude;
		this._longitude = longitude;
		this._radius = radius;

		try {

			HttpRequestFactory httpRequestFactory = createRequestFactory(HTTP_TRANSPORT);
			HttpRequest request = httpRequestFactory
					.buildGetRequest(new GenericUrl(PLACES_SEARCH_URL));
			request.getUrl().put("oauth_token", FourSquareTokenStore.get().getToken());
			request.getUrl().put("ll", _latitude + "," + _longitude);
			request.getUrl().put("v", "20131016");

			FoursquareSearchResult result = request.execute().parseAs(FoursquareSearchResult.class);
			// Check log cat for places response status
			Log.d(TAG, "Places Status: " + result.meta.code);

			return result;

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

		if (!runningFoursquare){
			runningFoursquare = true;
			Log.d(TAG, "GET NEAR PLACES FOURSQUARE");
			new LoadPlaces().execute();
		}
	}

	@Override
	public RawPlace getFromId(final String id) {

		if (FourSquareTokenStore.get().getToken() == null) return null;
		
		try {
			
			Log.i(TAG, "GOT HERE");
			
            HttpRequestFactory httpRequestFactory = createRequestFactory(HTTP_TRANSPORT);
            HttpRequest request = httpRequestFactory
                    .buildGetRequest(new GenericUrl(PLACE_DETAIL_URL + id));
            request.getUrl().put("oauth_token", FourSquareTokenStore.get().getToken());
            request.getUrl().put("v", 20140505);

            Log.i(TAG, "URL: " + request.getUrl());
            
            FoursquareVenueDetail foursquareVenueDetail = request.execute().parseAs(FoursquareVenueDetail.class);
            // Check log cat for places response status
            Log.d("Places Status", "" + foursquareVenueDetail.meta.code);
            
            return foursquareVenueDetail.getVenue();
 
		} catch (IOException e){}
		
		return null;

	}

	@Override
	public ProviderConnector getConnector() {

		if (connector == null) {
			connector = new FourSquareConnector(clientId, clientSecret);
		}
		return connector;
	}

	@Override
	public User getConnectedUser() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void postStatus(Activity activity, final String message, AggregatorPlace place) {

		final RawPlace rawPlace = PlaceHelper.getRawPlaceFromAggregator(activity, place, Provider.FOURSQUARE);


		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					URL url =new URL(CHECKIN_URL);

					String data = URLEncoder.encode("venueId", "UTF-8") + "=" + URLEncoder.encode(rawPlace.getRawReference(), "UTF-8");
					data += "&" + URLEncoder.encode("shout", "UTF-8") + "=" + URLEncoder.encode(message, "UTF-8");
					data += "&" + URLEncoder.encode("oauth_token", "UTF-8") + "=" + URLEncoder.encode(FourSquareTokenStore.get().getToken(), "UTF-8");
					data += "&" + URLEncoder.encode("v", "UTF-8") + "=" + URLEncoder.encode("20140505", "UTF-8");

					URLConnection conn = url.openConnection();
					conn.setDoOutput(true);
					OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
					wr.write(data);
					wr.flush();

					// Get the response
					BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));

					String line;
					while ((line = rd.readLine()) != null) {
						Log.i(TAG, line);
					}
					wr.close();
					rd.close();

				} catch (IOException e){}
			}
		});

		thread.start();		
	}

	/**
	 * 
	 * */
	private class LoadPlaces extends AsyncTask<String, String, FoursquareSearchResult> {

		private FourSquareProvider foursquareProvider;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			Log.v(TAG, "Starting execution Foursquare");
		}

		/**
		 * getting Venues JSON
		 * */
		protected FoursquareSearchResult doInBackground(String... args) {
			// creating Places class object
			Log.v(TAG, "Foursquare Venues");
			foursquareProvider = FourSquareProvider.this;

			try {

				// get nearest places
				return foursquareProvider.search(_latitude, _longitude, _radius, null);


			} catch (Exception e) {
				Log.e(TAG, "EXCEPTION FOURSQUARE PLACES", e);
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
		protected void onPostExecute(FoursquareSearchResult foursquareSearchResults) {
			// dismiss the dialog after getting all products
			// updating UI from Background Thread
			if (foursquareSearchResults == null) return;
			int status = foursquareSearchResults.meta.code;

			Log.v(TAG, "PostExexute");
			if (status == 200){
				Log.d(TAG, "Status OK");
				if (foursquareSearchResults.response.venues != null) {

					//List<Place> places = new ArrayList<Place>();
					Log.v(TAG, "NearPlaces.results != null");

					externalListener.fireCollectionAdded(foursquareSearchResults.response.venues);

				}


			} 
			else {
				Log.d(TAG, "Sorry error occured.");
			}
			//runningGoogle = false;
		}

	}

}
