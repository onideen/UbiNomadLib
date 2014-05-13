package no.ntnu.ubinomad.lib.utilities;

import java.io.Serializable;

import no.ntnu.ubinomad.lib.externalproviders.FoursquareSearchResult.Meta;
import no.ntnu.ubinomad.lib.models.FoursquareVenue;
import no.ntnu.ubinomad.lib.models.GooglePlace;

import com.google.api.client.util.Key;

public class FoursquareVenueDetail implements Serializable {
	
    @Key
    public Meta meta;
 
    @Key
    public Response response;

    public static class Response implements Serializable {
    	@Key
    	public FoursquareVenue venue;
    	
    }

    public FoursquareVenue getVenue() {
		return response.venue;
	}
    
    
}
