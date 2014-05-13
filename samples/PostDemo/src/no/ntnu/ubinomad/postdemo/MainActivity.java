package no.ntnu.ubinomad.postdemo;

import no.ntnu.ubinomad.lib.Provider;
import no.ntnu.ubinomad.lib.activities.UbiNomadFragmentActivity;
import no.ntnu.ubinomad.lib.externalproviders.*;
import no.ntnu.ubinomad.lib.fragments.LoginFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

 
public class MainActivity extends UbiNomadFragmentActivity {


	private static final int LOGIN = 0;
	private static final int POST = 1;
	private static final int FRAGMENT_COUNT = POST +1;
	private static final String TAG = "PostMain";


	private Fragment[] fragments = new Fragment[FRAGMENT_COUNT];
	private MenuItem settings;
	private MenuItem main;

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		ProviderRegister.getInstance().addProvider(Provider.FACEBOOK, new FacebookProviders());
		ProviderRegister.getInstance().addProvider(Provider.GOOGLE, new GoogleProvider());
		ProviderRegister.getInstance().addProvider(Provider.FOURSQUARE, new FourSquareProvider("YOUR_CLIENT_KEY", "YOUR_SECRET_KEY"));

		
		super.onCreate(savedInstanceState);
	
		setContentView(R.layout.main_activity);
		
		FragmentManager fm = getSupportFragmentManager();
		LoginFragment splashFragment = (LoginFragment) fm.findFragmentById(R.id.splashFragment);
		fragments[LOGIN] = splashFragment;
		fragments[POST] = fm.findFragmentById(R.id.selectionFragment);

		FragmentTransaction transaction = fm.beginTransaction();
		for(int i = 0; i < fragments.length; i++) {
			transaction.hide(fragments[i]);
		}
		transaction.commit();
		
		showLoginFragment();
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// only add the menu when the selection fragment is showing
		if (menu.size() == 0) {
			main = menu.add(R.string.main);
			settings = menu.add(R.string.settings);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.equals(settings)) {
			showFragment(LOGIN, true);
			return true;
		}
		else if (item.equals(main)){
			showFragment(POST, true);
		}
		
		return false;
	}

	public void showLoginFragment() {
		showFragment(LOGIN, true);
	}

	public void showPlacePickerFragment() {
		showFragment(POST, true);
	} 

	
	private void showFragment(int fragmentIndex, boolean addToBackStack) {
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();
		for (int i = 0; i < fragments.length; i++) {
			if (i == fragmentIndex) {
				transaction.show(fragments[i]);
			} else {
				transaction.hide(fragments[i]);
			}
		}
		if (addToBackStack) {
			transaction.addToBackStack(null);
		}
		transaction.commit();
	}

}
