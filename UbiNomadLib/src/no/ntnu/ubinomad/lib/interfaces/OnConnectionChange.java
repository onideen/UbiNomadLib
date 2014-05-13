package no.ntnu.ubinomad.lib.interfaces;

import no.ntnu.ubinomad.lib.Provider;

public interface OnConnectionChange {

	public void onSignedIn(Provider provider);
	public void onSignedOut(Provider provider);
	
}
