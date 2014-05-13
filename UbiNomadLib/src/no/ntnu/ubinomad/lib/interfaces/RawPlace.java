package no.ntnu.ubinomad.lib.interfaces;

import java.util.List;

import android.os.RemoteException;
import no.ntnu.ubinomad.lib.Provider;

public interface RawPlace extends Place {
	
	public Provider getProvider();
	public String getRawReference();
	List<RawPlace> findProviderPlacesByPlaceId(long placeId) throws RemoteException;

	public AggregatorPlace getAggregator();
	public void setAggregatorPlace(AggregatorPlace mPlace);
	public void syncWith(RawPlace externalPlace);
	public void setRawReference(String rawReference);
}
