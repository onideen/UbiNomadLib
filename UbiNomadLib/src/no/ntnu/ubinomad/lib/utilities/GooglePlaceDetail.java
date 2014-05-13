package no.ntnu.ubinomad.lib.utilities;

import java.io.Serializable;

import no.ntnu.ubinomad.lib.models.GooglePlace;

import com.google.api.client.util.Key;

public class GooglePlaceDetail implements Serializable {
	
    @Key
    public String status;
 
    @Key
    public GooglePlace result;

}
