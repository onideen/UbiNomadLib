package no.ntnu.ubinomad.lib.externalproviders;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import no.ntnu.ubinomad.lib.Provider;
import no.ntnu.ubinomad.lib.helpers.PlaceHelper;
import no.ntnu.ubinomad.lib.interfaces.ExternalProvider;
import no.ntnu.ubinomad.lib.interfaces.Place;
import no.ntnu.ubinomad.lib.interfaces.RawPlace;
import no.ntnu.ubinomad.lib.models.AbstractBaseModel;
import no.ntnu.ubinomad.lib.models.GenericRawPlace;

public class SyncData {

	
	private List<RawPlace> rawPlaces;
	
	private Context context;
	
	
	public void syncPlaces(Context context){
		this.context = context;
		
		Toast.makeText(context, "Place sync started", Toast.LENGTH_LONG).show();
		rawPlaces = new GenericRawPlace(context).getAll();
		
		Thread thread = new Thread(new Sync());
		thread.start();
		
	}
	
	private class Sync implements Runnable {

		private static final String TAG = "SyncData";

		@Override
		public void run() {

			for (RawPlace localPlace : rawPlaces) {
				
				RawPlace externalPlace = PlaceHelper.getSingleRawPlace(localPlace.getProvider(), localPlace.getRawReference());
				
				Log.i(TAG, localPlace.getName());
				
				if (externalPlace != null){
					Log.i(TAG, String.format("The local place [%s] is equals to the external place [%s] : %b", localPlace.getName(), externalPlace.getName(), localPlace.equals(externalPlace) ));

					if (!localPlace.equals(externalPlace)){
						localPlace.syncWith(externalPlace);
						((AbstractBaseModel<Place>)localPlace).setContext(context);
						((AbstractBaseModel<Place>)localPlace).update();

						Log.i(TAG, String.format("Update: [%s, %s, %s]", localPlace.getId(), localPlace.getName(), localPlace.getProvider().toString()));
					}					
				} else if (externalPlace == null) {
					Log.i(TAG, String.format("Place [%s, %s, %s] is null", localPlace.getId(), localPlace.getName(), localPlace.getProvider()));
				}
			}			
		}
		
	}
	
}
