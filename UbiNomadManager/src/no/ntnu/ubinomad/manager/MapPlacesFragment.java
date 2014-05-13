package no.ntnu.ubinomad.manager;

import java.util.ArrayList;
import java.util.List;

import no.ntnu.ubinomad.lib.components.PlacePickerListAdapter;
import no.ntnu.ubinomad.lib.interfaces.AggregatorPlace;
import no.ntnu.ubinomad.lib.interfaces.Place;
import no.ntnu.ubinomad.lib.interfaces.RawPlace;
import no.ntnu.ubinomad.lib.models.User;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

public class MapPlacesFragment extends Fragment{

	private static final String TAG = "MapPlacesFragment";
	
	private ListView mapPlacesList;
	private List<Place> masterPlaces;
	private ListAdapter adapter;
		
	private Button addNewButton;	
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		View view = inflater.inflate(R.layout.map_places, container, false);
		
		addNewButton = (Button) view.findViewById(R.id.open_allPlaces_button);
		
		addNewButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				((MainActivity)getActivity()).showAllPlacesFragment();
			}
		});
				
		mapPlacesList = (ListView) view.findViewById(R.id.maped_places_list);
				
		masterPlaces = new ArrayList<Place>();

		
		adapter = new PlacePickerListAdapter(getActivity(), masterPlaces);
		
		mapPlacesList.setAdapter(adapter);

		
		mapPlacesList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// CHECK IN
				//Place place = (MasterPlace) masterPlaces.getItemAtPosition(position);
				//checkIn(me, place);
				//checkInPlace.setText("You are checked in at: " + masterPlaces.getName());
				AggregatorPlace place = (AggregatorPlace) mapPlacesList.getItemAtPosition(position);
				
				((MainActivity)getActivity()).checkIn(place);
				((MainActivity)getActivity()).showPlacePickerFragment();
			}
		});
		
		
		
		return view;
	}
	
	public void updateList(){
		
		masterPlaces.clear();
		Log.i(TAG, "updateMAP");
		RawPlace place = ((RawPlace)MainActivity.getLastClickedPlace());
		Log.d(TAG, place != null ? place.getId() + "" : "NEULLLL");

		
		if (place != null && place.getAggregator() != null) {
			Log.d(TAG, "Masterplace Exsits");
			
			masterPlaces.add(place.getAggregator());
			Log.v(TAG, "FINALLY GOT HERE");
		}
		((BaseAdapter)adapter).notifyDataSetChanged();
	}
	
	public void update(Place place){
		masterPlaces.add(place);
		((BaseAdapter)mapPlacesList.getAdapter()).notifyDataSetChanged();

	}
}
