package no.ntnu.ubinomad.manager;

import java.util.List;

import no.ntnu.ubinomad.lib.Provider;
import no.ntnu.ubinomad.lib.activities.UbiNomadFragmentActivity;
import no.ntnu.ubinomad.lib.externalproviders.*;
import no.ntnu.ubinomad.lib.externdata.ProviderRegister;
import no.ntnu.ubinomad.lib.externdata.SyncData;
import no.ntnu.ubinomad.lib.fragments.LoginFragment;
import no.ntnu.ubinomad.lib.interfaces.AggregatorPlace;
import no.ntnu.ubinomad.lib.interfaces.Place;
import no.ntnu.ubinomad.lib.interfaces.RawPlace;
import no.ntnu.ubinomad.lib.models.AbstractBaseModel;
import no.ntnu.ubinomad.lib.models.GenericAggregatorPlace;
import no.ntnu.ubinomad.lib.models.GenericRawPlace;
import no.ntnu.ubinomad.lib.models.User;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

 
public class MainActivity extends UbiNomadFragmentActivity {


	private static final String TAG = "MainActivity";

	private static final int LOGIN = 0;
	private static final int NEAR_PLACES = 1;
	private static final int MAP_PLACES = 2;
	private static final int ALL_PLACES = 3;
	private static final int FRAGMENT_COUNT = ALL_PLACES +1;

	

	private Fragment[] fragments = new Fragment[FRAGMENT_COUNT];
	private MenuItem settings;
	private MenuItem places;
	private MenuItem sync;

	private User me;

	private static RawPlace lastClickedPlace;
	
	private Tab myPlaces;
	private Tab near;
	private Tab settingsTab;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		ProviderRegister.getInstance().addProvider(Provider.FACEBOOK, new FacebookProviders());
		ProviderRegister.getInstance().addProvider(Provider.GOOGLE, new GoogleProvider());
		ProviderRegister.getInstance().addProvider(Provider.FOURSQUARE, new FourSquareProvider(getResources().getString(R.string.FOURSQUARE_CLIENT_ID), getResources().getString(R.string.FOURSQUARE_CLIENT_SECRET)));

		Log.i(TAG, "SAVEDINSANCESTATE!!!!!: " +savedInstanceState);
		super.onCreate(savedInstanceState);
		

		setContentView(R.layout.main);


		FragmentManager fm = getSupportFragmentManager();
		LoginFragment splashFragment = (LoginFragment) fm.findFragmentById(R.id.splashFragment);
		fragments[LOGIN] = splashFragment;
		fragments[NEAR_PLACES] = fm.findFragmentById(R.id.selectionFragment);
		fragments[MAP_PLACES] = fm.findFragmentById(R.id.mapPlacesFragment);
		fragments[ALL_PLACES] = fm.findFragmentById(R.id.allPlacesFragment);

		FragmentTransaction transaction = fm.beginTransaction();
		for(int i = 0; i < fragments.length; i++) {
			transaction.hide(fragments[i]);
		}
		transaction.commit();

		showLoginFragment();
 
	
		User user = User.getMe(this);
		
		if (user.getCheckin() != null) {
			Toast.makeText(this, "I checked in at " + user.getCheckin().getName(), Toast.LENGTH_LONG).show();	
		}
		
		List<AggregatorPlace> aggregators = new GenericAggregatorPlace(this).getAll();
		
		for(AggregatorPlace aggregator : aggregators) {
			((AbstractBaseModel<AggregatorPlace>)aggregator).setContext(this);
			Log.v(TAG, String.format("MASTER[%d, %s, %d]", aggregator.getId(), aggregator.getName(), aggregator.getRawPlaces().size()));
		}
		
		
		List<RawPlace> rawPlaces = new GenericRawPlace(this).getAll();
		for(RawPlace place : rawPlaces) {
			if (place.getAggregator() != null)
				Log.v(TAG, String.format("PROVIDER[%d, %s, %d]", place.getId(), place.getName(), place.getAggregator().getId()));
		}
		
		initTabs();
		
	}

	private void initTabs() {

		ActionBar myActionBar = getActionBar(); 
		myActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS); 

		near = myActionBar.newTab(); 
		near.setText("Near");
		near.setTabListener(new MyTabListener());
		myActionBar.addTab(near);

		
		myPlaces = myActionBar.newTab(); 
		myPlaces.setText("My Places");
		myPlaces.setTabListener(new MyTabListener());
		myActionBar.addTab(myPlaces);

		settingsTab = myActionBar.newTab(); 
		settingsTab.setText("Settings");
		settingsTab.setTabListener(new MyTabListener());
		myActionBar.addTab(settingsTab);

		
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// only add the menu when the selection fragment is showing
		if (menu.size() == 0) {
			sync = menu.add(R.string.sync_places);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.equals(sync)){
			new SyncData().syncPlaces(this);
		}
		
		return false;
	}
	public void showMapPlacesFragment(RawPlace place) {
		
		lastClickedPlace = place;
		Log.i(TAG, lastClickedPlace.getName());
		
		RawPlace dbPlace = new GenericRawPlace(this).fromProviderPlace(place);
		if (dbPlace!= null) {
			lastClickedPlace = dbPlace;			
			Log.i(TAG, "DBPLACE not NULL");
		}
		Log.i(TAG, lastClickedPlace.getName());
		
		
		((MapPlacesFragment)fragments[MAP_PLACES]).updateList();
		showFragment(MAP_PLACES, true);
	}

	public void showAllPlacesFragment() {
		((AllPlacesFragment)fragments[ALL_PLACES]).updateView();
		showFragment(ALL_PLACES, true);
	} 

	public void showLoginFragment() {
		showFragment(LOGIN, true);
	}

	public void showPlacePickerFragment() {
		showFragment(NEAR_PLACES, true);
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

	@SuppressWarnings("unchecked")
	public AggregatorPlace createAggregatorPlace() {

		Log.i(TAG, "CREATE MASTERPLACE" + lastClickedPlace.getName());
		
		
		AggregatorPlace aggregator = new GenericAggregatorPlace(lastClickedPlace);
		((AbstractBaseModel<AggregatorPlace>)aggregator).setContext(this);
		((AbstractBaseModel<AggregatorPlace>)aggregator).save();


		((RawPlace)lastClickedPlace).setAggregatorPlace(aggregator);

		((AbstractBaseModel<RawPlace>)lastClickedPlace).setContext(this);

		((AbstractBaseModel<RawPlace>)lastClickedPlace).save();


		return aggregator;
	}

	public static Place getLastClickedPlace() {

		return lastClickedPlace;
	}

	@SuppressWarnings("unchecked")
	public void checkIn(AggregatorPlace place) {
		((AbstractBaseModel<AggregatorPlace>) place).setContext(this);
		((AbstractBaseModel<AggregatorPlace>) place).save();

		User user = User.getMe(this);
		user.setCheckin(place);
		
		user.update();
		Toast.makeText(this, "I checked in at " + place.getName(), Toast.LENGTH_LONG).show();

	}

	public void setUser(User u) {
		me = u;
	}

	
	class MyTabListener implements TabListener {

		@Override
		public void onTabSelected(Tab tab, android.app.FragmentTransaction ft) {
			if (near.equals(tab)){
				showPlacePickerFragment();
			}
			else if (myPlaces.equals(tab)){
				Intent intent = new Intent(MainActivity.this, MyPlacesActivity.class);
				startActivity(intent);
			}
			else if (settingsTab.equals(tab)){
				showLoginFragment();
			}
		}

		@Override
		public void onTabUnselected(Tab tab, android.app.FragmentTransaction ft) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onTabReselected(Tab tab, android.app.FragmentTransaction ft) {
			// TODO Auto-generated method stub
			
		}
		
	}
}
