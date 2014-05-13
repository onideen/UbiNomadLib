package no.ntnu.ubinomad.lib.externalproviders;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import no.ntnu.ubinomad.lib.Provider;
import no.ntnu.ubinomad.lib.interfaces.ExternalProvider;
import no.ntnu.ubinomad.lib.interfaces.ProviderConnector;
import no.ntnu.ubinomad.lib.interfaces.ProviderSettings;

public class ProviderRegister {
	

	private static ProviderRegister providerRegister;
	
	private Map<Provider, ProviderSettings> providers;
	
	private Map<Provider, ProviderConnector> connectors;
	
	public static ProviderRegister getInstance() {
		return providerRegister != null ? providerRegister : new ProviderRegister();
	}
	
	private ProviderRegister() {
		providerRegister = this;
		providers = new HashMap<Provider, ProviderSettings>();
		connectors = new HashMap<Provider, ProviderConnector>();
	}
	
	
	public void addProvider(Provider provider, ExternalProvider externalProvider){
		providers.put(provider, GenericProviderSettings.create(externalProvider));
		connectors.put(provider, externalProvider.getConnector());
	}

	public ExternalProvider getExternalProvider(Provider provider) {
		return providers.get(provider).getExternalProvider();
	}
	
	public Collection<ProviderConnector> getConnectors() {
		return connectors.values();
	}
	
	public Set<Provider> getProviders() {
		return providers.keySet();
	}
	
	public boolean isLoaded(Provider provider){
		return providers.containsKey(provider);
	}

	public ProviderConnector getConnector(Provider provider) {
		return connectors.get(provider);
		
	}
	
}
