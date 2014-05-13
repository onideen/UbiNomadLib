package no.ntnu.ubinomad.lib.externalproviders;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import no.ntnu.ubinomad.lib.Provider;
import no.ntnu.ubinomad.lib.connectors.GoogleConnector;
import no.ntnu.ubinomad.lib.interfaces.ExternalDataListener;
import no.ntnu.ubinomad.lib.interfaces.ExternalProvider;
import no.ntnu.ubinomad.lib.interfaces.AggregatorPlace;
import no.ntnu.ubinomad.lib.interfaces.Place;
import no.ntnu.ubinomad.lib.interfaces.ProviderConnector;
import no.ntnu.ubinomad.lib.interfaces.RawPlace;
import no.ntnu.ubinomad.lib.models.AbstractBaseModel;
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

/**
 * <p>The purpose of this provider is the ability to add and delete custom places to the library.
 * Hence this provider is a bit different, since it is not able to get places or logged on user </p>
 * @author vegaen
 *
 */

public class UbiNomadProvider {
 
	private ProviderConnector connector;
	
	
    /** Global instance of the HTTP transport. */
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
 
    // Google API Key
    private static final String API_KEY = "AIzaSyAJu7JExNRgs_CKGY6sZxlVuhklAF6dT3M";
 
    // Google Places serach url's
    private static final String PLACES_ADD_URL = "https://maps.googleapis.com/maps/api/place/add/json?";
    private static final String PLACES_DELETE_URL = "https://maps.googleapis.com/maps/api/place/delete/json?";


	protected static final String TAG = "UbiNomadData";	
	
	/* TODO: Should probably return reference string */
	public static void addPlace(final RawPlace place, final String type) {
		
		Thread thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					URL object=new URL(PLACES_ADD_URL + "sensor=true&key=" + API_KEY);
					
					HttpURLConnection con = (HttpURLConnection) object.openConnection();
					con.setDoOutput(true);
					con.setDoInput(true);
					con.setRequestProperty("Content-Type", "application/json");
					con.setRequestProperty("Accept", "application/json");
					con.setRequestMethod("POST");
					
					JSONObject jsonPlace = new JSONObject();
					JSONArray types = new JSONArray();
					JSONObject location = new JSONObject();
					location.put("lng", place.getLocation().getLongitude());
					location.put("lat", place.getLocation().getLatitude());
					types.put(type);
					
					jsonPlace.put("location", location);
					jsonPlace.put("name", place.getName());
					jsonPlace.put("types", types);
					
					
					Log.i(TAG, jsonPlace.toString(2));
					OutputStreamWriter wr= new OutputStreamWriter(con.getOutputStream());
					wr.write(jsonPlace.toString());
					
					wr.flush();
					
					//display what returns the POST request
					
					StringBuilder sb = new StringBuilder();  
					
					int HttpResult =con.getResponseCode(); 
					
					if(HttpResult ==HttpURLConnection.HTTP_OK){
						
						BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(),"utf-8"));  
						String line = null;  
						
						while ((line = br.readLine()) != null) {  
							sb.append(line + "\n");  
						}  
						
						br.close();  
						JSONObject json = new JSONObject(sb.toString());
						System.out.println("REFERENCE: " + json.getString("reference"));  
						place.setRawReference(json.getString("reference"));
						((AbstractBaseModel<RawPlace>)place).save();
					}else{
						System.out.println(con.getResponseMessage());  
					}  
				} catch (IOException e){
					
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
		
		thread.start();
	}
	
	public static void delete(final RawPlace place) {
		if (place.getProvider() != Provider.UBINOMAD) {
			return;
		}
		
		Thread thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					URL object=new URL(PLACES_DELETE_URL + "sensor=true&key=" + API_KEY);
					
					HttpURLConnection con = (HttpURLConnection) object.openConnection();
					con.setDoOutput(true);
					con.setDoInput(true);
					con.setRequestProperty("Content-Type", "application/json");
					con.setRequestProperty("Accept", "application/json");
					con.setRequestMethod("POST");
					
					JSONObject jsonPlace = new JSONObject();
					
					jsonPlace.put("reference", place.getRawReference());
//					jsonPlace.put("reference", "CnRsAAAAa3vYGHkfgo5TUxkO4XRhZNKShBY9LRnm6s84idQlEv9QBsYZeF5hKK3g48xGxUn0GV424BTDjUTj5g0tdw_BCi2vwBncASQ_QKIOPr5Ej46iG3ZB3roUrg6T5n7-iTuNBrNtdM76kHDfUPLJfQAo6hIQJ_fVyqHJH9lDB9jgTIQC0hoUBRQosDJSXVPZusXFSd3aJq6CsMg");
					
					
					Log.i(TAG, jsonPlace.toString(2));
					OutputStreamWriter wr= new OutputStreamWriter(con.getOutputStream());
					wr.write(jsonPlace.toString());
					
					wr.flush();
					
					//display what returns the POST request
					
					StringBuilder sb = new StringBuilder();  
					
					int HttpResult =con.getResponseCode(); 
					
					if(HttpResult ==HttpURLConnection.HTTP_OK){
						
						BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(),"utf-8"));  
						String line = null;  
						sb.append("START: \n");
						while ((line = br.readLine()) != null) {  
							sb.append(line + "\n");  
						}  
						
						br.close();  
						System.out.println(sb);
						((AbstractBaseModel<RawPlace>)place).delete();
					}else{
						System.out.println(con.getResponseMessage());  
					}  
				} catch (IOException e){
					
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
		
		thread.start();
	}
}