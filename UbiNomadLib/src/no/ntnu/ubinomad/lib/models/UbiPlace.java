package no.ntnu.ubinomad.lib.models;

import java.util.UUID;

import no.ntnu.ubinomad.lib.Provider;
import no.ntnu.ubinomad.lib.interfaces.RawPlace;
import android.content.Context;

public class UbiPlace extends GenericRawPlace {

	public UbiPlace(Context context) {
		super(context);
	}

	public UbiPlace(Context context, String name, UbiLocation location){
		super(name, Provider.UBINOMAD, UUID.randomUUID().toString(), null, location);	
		setContext(context);
	}
		
}
