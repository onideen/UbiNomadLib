package no.ntnu.ubinomad.manager;

import java.util.ArrayList;
import java.util.List;

import no.ntnu.ubinomad.lib.Provider;
import no.ntnu.ubinomad.lib.components.PlacePickerListAdapter;
import no.ntnu.ubinomad.lib.externdata.UbiNomadData;
import no.ntnu.ubinomad.lib.interfaces.RawPlace;
import no.ntnu.ubinomad.lib.models.AbstractBaseModel;
import no.ntnu.ubinomad.lib.models.GenericRawPlace;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class MyPlacesFragment extends Fragment{
	
	private static final String TAG = "MyPlacesFragment";
	
	private ListView myPlacesListView;
	private List<RawPlace> myPlacesList;
	private ListAdapter adapter;
	
	private Button createPlaceButton;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		View view = inflater.inflate(R.layout.my_places, container, false);
				
		myPlacesListView = (ListView) view.findViewById(R.id.my_places_list);
		createPlaceButton = (Button) view.findViewById(R.id.create_new_place_button);
		
		myPlacesList = new ArrayList<RawPlace>();
		
		adapter = new PlacePickerListAdapter(getActivity(), myPlacesList);
		
		myPlacesListView.setAdapter(adapter);
		
		fillListWithMyPlaces();

		createPlaceButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				((MyPlacesActivity)getActivity()).showForm();
			}
		});
		
		
		myPlacesListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// checkIn(me, place);
				// checkInPlace.setText("You are checked in at: " + masterPlaces.getName());
				// ((MainActivity)getActivity()).showPlacePickerFragment();

			}
		});
			
		registerForContextMenu(myPlacesListView);
		return view;
	}

	private void fillListWithMyPlaces() {
		myPlacesList.addAll(new GenericRawPlace(getActivity()).getByProvider(Provider.UBINOMAD));

		for(RawPlace pp : myPlacesList) {
			Log.i(TAG, pp.getName());
		}
		
		
		((BaseAdapter)adapter).notifyDataSetChanged();

	}
	
	public void addToList(RawPlace providerPlace) {
		myPlacesList.add(providerPlace);
		((BaseAdapter)adapter).notifyDataSetChanged();
	}

	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		if (v.getId() == R.id.my_places_list) {
	          MenuInflater inflater = getActivity().getMenuInflater();
	          inflater.inflate(R.menu.my_places_context_menu, menu);
	      }
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
	      switch(item.getItemId()) {
	          case R.id.delete:
	        	  
	        	  Log.i(TAG, info.position + "");
	        	  RawPlace place = myPlacesList.get(info.position);
	        	  UbiNomadData.delete(place);
	        	  ((AbstractBaseModel<RawPlace>)place).setContext(getActivity());
	        	  
	        	  myPlacesList.remove(info.position);
	        	  ((BaseAdapter)adapter).notifyDataSetChanged();
	        	  
	        	  return true;
	          default:
	                return super.onContextItemSelected(item);
	      }
		
		
	}
}
