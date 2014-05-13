package no.ntnu.ubinomad.lib.externalproviders;

import java.io.Serializable;
import java.util.List;

import no.ntnu.ubinomad.lib.models.FoursquareVenue;

import com.google.api.client.util.Key;

public class FoursquareSearchResult implements Serializable {
	
	@Key
	public Meta meta;
	
	@Key
	public Response response;	
	
	public static class Meta implements Serializable {
		
		@Key
		public int code;
	}
	
	public static class Response implements Serializable {
		
		@Key
		public List<FoursquareVenue> venues;
	}
}
