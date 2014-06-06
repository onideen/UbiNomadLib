package no.ntnu.ubinomad.lib.interfaces;

import no.ntnu.ubinomad.lib.activities.UbiNomadFragmentActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * 
 * Implementations of this interface are handling how to connect to a social 
 * network
 * 
 * @author Vegar Engen <vegaen@vegaen.no> 
 *
 */
public interface ProviderConnector {

	public void init(UbiNomadFragmentActivity ubiNomadActivity, Bundle savedInstanceState);

	public void connect();

	public void activityResult(int requestCode, int resultCode, Intent data);

	public void resume();

	public void pause();

	public void destroy();

	void resumeFragments();

	public void saveInstanceState(Bundle outState);

	public void login();


}
