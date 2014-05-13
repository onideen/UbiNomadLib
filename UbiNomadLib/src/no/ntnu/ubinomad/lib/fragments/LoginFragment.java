package no.ntnu.ubinomad.lib.fragments;

import java.util.Arrays;

import no.ntnu.ubinomad.lib.Provider;
import no.ntnu.ubinomad.lib.R;
import no.ntnu.ubinomad.lib.activities.UbiNomadFragmentActivity;
import no.ntnu.ubinomad.lib.connectors.FourSquareTokenStore;
import no.ntnu.ubinomad.lib.connectors.GoogleConnector;
import no.ntnu.ubinomad.lib.externalproviders.ProviderRegister;
import no.ntnu.ubinomad.lib.interfaces.OnConnectionChange;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.widget.LoginButton;
import com.foursquare.android.nativeoauth.FoursquareOAuth;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.plus.Plus;

public class LoginFragment extends Fragment implements OnClickListener, OnConnectionChange {

	/* TODO: Move to strings */
	 private static final String CLIENT_ID = "PUQS5VGRWK5JR5KJEGMKA1TRPRQ2L5VYFGUNDGPMZLUKOSPM";
	private static final String CLIENT_SECRET = "WMX3XC2LWYWPMK3ABQMO3DCXY3HMAN22KG1PSGT5WJHM3LG2";
	
	
	private static final String TAG ="LoginFragment";

	private SignInButton googleSignInButton;
	private Button googleSignOutButton;
	private Button foursquareSignInButton;
	private TextView foursquareConnectedView;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "OnCreate");
		
		((UbiNomadFragmentActivity) getActivity()).addListener(this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.login, container, false);

		googleSignInButton = (SignInButton) view.findViewById(R.id.google_sign_in_button);
		googleSignOutButton = (Button) view.findViewById(R.id.google_sign_out_button);


		if (ProviderRegister.getInstance().isLoaded(Provider.GOOGLE)){
			
			googleSignInButton.setOnClickListener(this);
			googleSignOutButton.setOnClickListener(this);

		} else {
			googleSignInButton.setVisibility(View.GONE);
			googleSignOutButton.setVisibility(View.GONE);
			
		}
		
		if (! ProviderRegister.getInstance().isLoaded(Provider.FACEBOOK)){

			View facebookButtonFrame = view.findViewById(R.id.facebook_button_frame);
			facebookButtonFrame.setVisibility(View.GONE);
		}
		else {
			LoginButton facebookAuthButton = (LoginButton) view.findViewById(R.id.facebook_auth_button);
			facebookAuthButton.setReadPermissions(Arrays.asList("email"));
		}
		
		foursquareSignInButton = (Button) view.findViewById(R.id.foursquare_login_btn);
		foursquareConnectedView = (TextView) view.findViewById(R.id.foursquare_connected);
		
		foursquareSignInButton.setVisibility(ProviderRegister.getInstance().isLoaded(Provider.FOURSQUARE) ? View.VISIBLE : View.GONE);
		foursquareSignInButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ProviderRegister.getInstance().getConnector(Provider.FOURSQUARE).login();
			}
		});
		foursquareConnectedView.setVisibility(FourSquareTokenStore.get().getToken() != null ? View.VISIBLE : View.GONE);
		
		return view;
	}


	@Override
	public void onClick(View v) {
		GoogleConnector googleConnector = (GoogleConnector)ProviderRegister.getInstance().getConnector(Provider.GOOGLE);
		
		
		if (!googleConnector.mGoogleApiClient.isConnecting()) {
			if (v.getId() == R.id.google_sign_in_button) {
				googleConnector.resolveSignInError();		
			}else if (v.getId() == R.id.google_sign_out_button) {
				// We clear the default account on sign out so that Google Play
				// services will not return an onConnected callback without user
				// interaction.
				Plus.AccountApi.revokeAccessAndDisconnect(googleConnector.mGoogleApiClient);
				googleConnector.mGoogleApiClient.disconnect();
				googleConnector.mGoogleApiClient.connect();

			}
		}				
	}	

	@Override
	public void onSignedIn(Provider provider) {
		switch (provider) {
		case GOOGLE:
			googleSignInButton.setVisibility(View.GONE);;
			googleSignOutButton.setVisibility(View.VISIBLE);
			googleSignInButton.setEnabled(false);
			googleSignOutButton.setEnabled(true);
			break;
		case FOURSQUARE:
			foursquareConnectedView.setVisibility(View.VISIBLE);
			foursquareSignInButton.setVisibility(View.GONE);
			foursquareSignInButton.setEnabled(false);
		default:
			break;
		}
	}


	@Override
	public void onSignedOut(Provider provider) {
		switch (provider) {
		case GOOGLE:
			googleSignInButton.setVisibility(View.VISIBLE);
			googleSignOutButton.setVisibility(View.GONE);
			googleSignInButton.setEnabled(true);
			googleSignOutButton.setEnabled(false);
			break;
		case FOURSQUARE:
			foursquareSignInButton.setVisibility(View.VISIBLE);
			foursquareConnectedView.setVisibility(View.GONE);
			foursquareSignInButton.setEnabled(true);
			break;
		default:
			break;
		}
	}

}
