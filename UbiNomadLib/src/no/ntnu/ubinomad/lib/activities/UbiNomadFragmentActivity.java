package no.ntnu.ubinomad.lib.activities;

import java.util.ArrayList;
import java.util.List;

import no.ntnu.ubinomad.lib.Provider;
import no.ntnu.ubinomad.lib.externalproviders.FacebookProviders;
import no.ntnu.ubinomad.lib.externalproviders.GoogleProvider;
import no.ntnu.ubinomad.lib.externalproviders.ProviderRegister;
import no.ntnu.ubinomad.lib.interfaces.FireLoginStatusChange;
import no.ntnu.ubinomad.lib.interfaces.OnConnectionChange;
import no.ntnu.ubinomad.lib.interfaces.ProviderConnector;

import com.facebook.AppEventsLogger;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

public class UbiNomadFragmentActivity extends FragmentActivity implements FireLoginStatusChange {
	
	private List<OnConnectionChange> listeners;
	
	
	private static String TAG = "UbiNomadFragmentActivity";
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		listeners = new ArrayList<OnConnectionChange>();
	
		
		for (ProviderConnector pConnector : ProviderRegister.getInstance().getConnectors()) {
			Log.i(TAG, "ProviderConnector: " + pConnector.toString());
			pConnector.init(this, savedInstanceState);
		}
	}

	
	@Override
	protected void onStart() {
		super.onStart();
		for (ProviderConnector pConnector : ProviderRegister.getInstance().getConnectors()) {
			pConnector.connect();
		}
	}
	
	
	@Override
	protected void onResume() {
		super.onResume();
		// Call the 'activateApp' method to log an app event for use in analytics and advertising reporting.  Do so in
		// the onResume methods of the primary Activities that an app may be launched into.
		AppEventsLogger.activateApp(this);
		for (ProviderConnector pConnector : ProviderRegister.getInstance().getConnectors()) {
			pConnector.resume();
		}
		
	
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		for (ProviderConnector pConnector : ProviderRegister.getInstance().getConnectors()) {
			pConnector.pause();
		}
		
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		for (ProviderConnector pConnector : ProviderRegister.getInstance().getConnectors()) {
			pConnector.activityResult(requestCode, resultCode, data);
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		for (ProviderConnector pConnector : ProviderRegister.getInstance().getConnectors()) {
			pConnector.destroy();
		}
		
	}
	
	@Override
	protected void onResumeFragments() {
		super.onResumeFragments();
		for (ProviderConnector pConnector : ProviderRegister.getInstance().getConnectors()) {
			pConnector.resumeFragments();
		}
	}
	
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		for (ProviderConnector pConnector : ProviderRegister.getInstance().getConnectors()) {
			pConnector.saveInstanceState(outState);
		}
			
	}
		
	public void addListener(OnConnectionChange occ) {
		listeners.add(occ);
	}
	
	public void removeListener(OnConnectionChange occ) {
		listeners.remove(occ);
	}


	@Override
	public void onLoggedIn(Provider provider) {
		for(OnConnectionChange occ : listeners){
			occ.onSignedIn(provider);
		}		
	}


	@Override
	public void onLoggedOut(Provider provider) {
		for(OnConnectionChange occ : listeners){
			occ.onSignedOut(provider);
		}	
	}
	
	
}
