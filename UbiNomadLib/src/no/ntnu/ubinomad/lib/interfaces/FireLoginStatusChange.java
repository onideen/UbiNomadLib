package no.ntnu.ubinomad.lib.interfaces;

import no.ntnu.ubinomad.lib.Provider;

public interface FireLoginStatusChange{

	public void onLoggedIn(Provider provider);
	public void onLoggedOut(Provider provider);
	
}
