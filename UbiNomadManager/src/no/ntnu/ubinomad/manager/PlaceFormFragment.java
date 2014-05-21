package no.ntnu.ubinomad.manager;

import no.ntnu.ubinomad.lib.externalproviders.UbiNomadProvider;
import no.ntnu.ubinomad.lib.models.AbstractRawPlace;
import no.ntnu.ubinomad.lib.models.UbiLocation;
import no.ntnu.ubinomad.lib.models.UbiPlace;
import no.ntnu.ubinomad.lib.services.GPSTracker;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class PlaceFormFragment extends Fragment{


	protected static final String TAG = null;

	private MyPlacesActivity myPlacesActivity;
	
	private EditText placeName;
	private EditText latitude;
	private EditText longitude;
	private Spinner typeSpinner;

	private Button getCoordinates;
	private Button save;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		myPlacesActivity = (MyPlacesActivity) getActivity();
				
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.my_place_form, container, false);

		placeName = (EditText) view.findViewById(R.id.place_name);
		latitude = (EditText) view.findViewById(R.id.latitude);
		longitude = (EditText) view.findViewById(R.id.longitude);
		getCoordinates = (Button) view.findViewById(R.id.getCoordinates);
		typeSpinner = (Spinner) view.findViewById(R.id.typeSpinner);
		save = (Button) view.findViewById(R.id.save);


		getCoordinates.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				GPSTracker gps = new GPSTracker(getActivity());
				latitude.setText(gps.getLatitude() + "");
				longitude.setText(gps.getLongitude() + ""	);
			}
		});


		save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String name = placeName.getText().toString();
				String latString = latitude.getText().toString();
				String lonString = longitude.getText().toString();
				String type = typeSpinner.getSelectedItem().toString();

				Log.i(TAG, "TYPE: " + type);
				
				if (!latString.equals("") && !lonString.equals("")) {
					save(name, new UbiLocation(Double.parseDouble(latString), Double.parseDouble(lonString)), type);
				}else{
					save(name, null, type );
				}
			}

		});

		return view;
	}

	private void save(String placeName, UbiLocation location, String type) {
		AbstractRawPlace providerPlace = new UbiPlace(getActivity(), placeName, location);
		providerPlace.save();
		
		UbiNomadProvider.addPlace(providerPlace, type);
		myPlacesActivity.addToList(providerPlace);
		myPlacesActivity.showPlaces();
	}
}
