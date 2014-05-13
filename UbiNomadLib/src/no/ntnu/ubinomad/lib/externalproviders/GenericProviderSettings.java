package no.ntnu.ubinomad.lib.externalproviders;

import android.util.Log;
import no.ntnu.ubinomad.lib.interfaces.ExternalProvider;
import no.ntnu.ubinomad.lib.interfaces.ProviderConnector;
import no.ntnu.ubinomad.lib.interfaces.ProviderSettings;

public class GenericProviderSettings implements ProviderSettings {
	
	private static final String TAG = "ProviderSettings";
	private ExternalProvider externalProvider;
	
	
	private GenericProviderSettings(ExternalProvider externalProvider) {
		this.externalProvider = externalProvider;
	}
	
	public static ProviderSettings create(ExternalProvider externalProvider) {
		return new GenericProviderSettings(externalProvider);
	}

	@Override
	public ExternalProvider getExternalProvider() {
		return externalProvider;
	}

	@Override
	public ProviderConnector getConnector() {
		return externalProvider.getConnector();
	}
	
	
}
