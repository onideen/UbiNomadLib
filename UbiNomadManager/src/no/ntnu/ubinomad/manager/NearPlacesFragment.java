package no.ntnu.ubinomad.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import no.ntnu.ubinomad.lib.components.PlacePickerListAdapter;
import no.ntnu.ubinomad.lib.externalproviders.NearPlaces;
import no.ntnu.ubinomad.lib.interfaces.Place;
import no.ntnu.ubinomad.lib.interfaces.RawPlace;
import no.ntnu.ubinomad.lib.models.GenericRawPlace;
import no.ntnu.ubinomad.lib.models.User;
import no.ntnu.ubinomad.lib.observers.PlacesListObserver;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;

import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class NearPlacesFragment extends Fragment implements PlacesListObserver{

	private static final String TAG = "PlacePickerFragment";

	private static final int REAUTH_ACTIVITY_CODE = 100;
		
	private ListView placeList;
	private ListAdapter adapter;
	private User me;
	
	private NearPlaces np;
	
	private List<RawPlace> places;
	
	
	private UiLifecycleHelper uiHelper;
	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(final Session session, final SessionState state, final Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		View view = inflater.inflate(R.layout.place_picker, container, false);
		
		placeList = (ListView) view.findViewById(R.id.placeList);
		
		places = new ArrayList<RawPlace>();
		
		adapter = new PlacePickerListAdapter(getActivity(), places);
		
		placeList.setAdapter(adapter);
		
		
		placeList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// CHECK IN
				RawPlace place = (RawPlace) placeList.getItemAtPosition(position);

		
				
				((MainActivity)getActivity()).showMapPlacesFragment(place);

				

			}
		});
		
		// Check for an open session
		Session session = Session.getActiveSession();
		if (session != null && session.isOpened()) {
			// Get the user's data
			makeMeRequest(session);
			//makePlacesRequest(session);
		}
		getNearPlaces();

		Log.v(TAG, String.format("PLACES IN DATABASE"));
		for (RawPlace place : new GenericRawPlace(getActivity()).getAll()) {
			Log.v(TAG, String.format("Place in database: [%s, %s, %s, %s]", place.getId(), place.getName(), place.getProvider(), place.getRawReference()));
		}
		
		for (User user : new User(getActivity()).getAll()) {
			Log.v(TAG, String.format("User: [%s, %s, %s]", user.getName(), user.getEmail(), user.getCheckin() != null ? user.getCheckin().getName() : "NO CHECKIN"));
		}
		
		return view;

	}
	
	private void makeMeRequest(final Session session){
		
		// Make an API call to get user data and define a new callback to handle the response
		Request request = Request.newMeRequest(session, 
				new Request.GraphUserCallback() {

			@Override
			public void onCompleted(GraphUser user, Response response) {
				// If response is successful
				if (session == Session.getActiveSession()){
					if (user != null) {
						// Set the id for the ProfilePictureView that it will display the profile picture
						//profilePictureView.setProfileId(user.getId());
						// Set the TextView's text to the user's name.
						
						/** TODO: FIX THIS USER
						try {
							User u = new User(getActivity());
							u = u.getByEmail(user.getProperty("email").toString());
							

							if (u == null || u.getEmail() == null) {
								u = new User(getActivity());
								u.setEmail(user.getProperty("email").toString());
								u.setName(user.getName());
								u.setProvider("facebook");
								u.save();
								Log.v(TAG, String.format("User: [%s, %s]", u.getName(), u.getEmail() ));
							}
							
							((MainActivity)getActivity()).setUser(u);
						
						} catch (RemoteException e) {
							e.printStackTrace();
						}
						*/
					}
				}
				if (response.getError() != null){
					// Handle error, will do so later
				}
			}
		});
		request.executeAsync();
	}
	
	private void onSessionStateChange(final Session session, SessionState state, Exception exception) {
		if (session != null && session.isOpened()) {
			// Get the user's data.
			makeMeRequest(session);
			getNearPlaces();
		}
	}

	private void getNearPlaces() {
		if (np == null) {
			np = NearPlaces.getNearPlaces(getActivity());
			np.addListener(this);
		}
		np.getNearPlaces(1000, null);
		
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		uiHelper = new UiLifecycleHelper(getActivity(), callback);
		uiHelper.onCreate(savedInstanceState);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REAUTH_ACTIVITY_CODE) {
			uiHelper.onActivityResult(requestCode, resultCode, data);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		uiHelper.onResume();
	}

	@Override
	public void onSaveInstanceState(Bundle bundle) {
		super.onSaveInstanceState(bundle);
		uiHelper.onSaveInstanceState(bundle);
	}

	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}

	@Override
	public void add(Place place) {
		places.add((RawPlace)place);
		Collections.sort(places);
		((BaseAdapter)placeList.getAdapter()).notifyDataSetChanged();
	}

	@Override
	public void addAll(List<? extends Place> collection) {
		places.addAll((Collection<? extends RawPlace>) collection);
		Collections.sort(places);
		((BaseAdapter)placeList.getAdapter()).notifyDataSetChanged();

	}

}
