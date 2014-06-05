package no.ntnu.ubinomad.manager;

import java.util.ArrayList;
import java.util.List;

import no.ntnu.ubinomad.lib.components.PlacePickerListAdapter;
import no.ntnu.ubinomad.lib.helpers.PlaceHelper;
import no.ntnu.ubinomad.lib.interfaces.AggregatorPlace;
import no.ntnu.ubinomad.lib.interfaces.Place;
import no.ntnu.ubinomad.lib.interfaces.RawPlace;
import no.ntnu.ubinomad.lib.models.AbstractBaseModel;
import no.ntnu.ubinomad.lib.models.AbstractRawPlace;
import no.ntnu.ubinomad.lib.models.GenericAggregatorPlace;
import no.ntnu.ubinomad.lib.models.User;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class AllPlacesFragment extends Fragment{
	
	private static final String TAG = "AllPlacesFragment";
	
	private ListView allPlacesView;
	private List<Place> allAggregatorPlaces;
	private ListAdapter adapter;

	
	private Button createAggregatorPlaceButton;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		View view = inflater.inflate(R.layout.all_places, container, false);
		
		createAggregatorPlaceButton = (Button) view.findViewById(R.id.add_new_mapping_button);
	
		createAggregatorPlaceButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				AggregatorPlace place = ((MainActivity)getActivity()).createAggregatorPlace();
				
				((MainActivity)getActivity()).checkIn(place);
				//checkInPlace.setText("You are checked in at: " + place.getName());
				((MainActivity)getActivity()).showPlacePickerFragment();
			}
		});
		
		
		allPlacesView = (ListView) view.findViewById(R.id.all_places_list);
				
		allAggregatorPlaces = new ArrayList<Place>();
		
		adapter = new PlacePickerListAdapter(getActivity(), allAggregatorPlaces);
		
		allPlacesView.setAdapter(adapter);
		
		allPlacesView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO add to providerPlaces List
				
				AggregatorPlace aggregatorPlace = (AggregatorPlace) allPlacesView.getItemAtPosition(position);
				RawPlace rawPlace = (RawPlace)((MainActivity)getActivity()).getLastClickedPlace();
								
				PlaceHelper.mapPlaceToAggregatorPlace(getActivity(), rawPlace, aggregatorPlace);
				
				
				((MainActivity)getActivity()).checkIn(aggregatorPlace);
				((MainActivity)getActivity()).showPlacePickerFragment();

			}
		});
				
		return view;
	}
	
	public void updateView(){
		List<AggregatorPlace> masterPlaces = new GenericAggregatorPlace(getActivity()).getAll();
		
		allAggregatorPlaces.clear();
		allAggregatorPlaces.addAll(masterPlaces);
		
		((BaseAdapter)adapter).notifyDataSetChanged();
	}

	
}
