package no.ntnu.ubinomad.manager;

import no.ntnu.ubinomad.lib.models.AbstractRawPlace;
import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

public class MyPlacesActivity extends FragmentActivity{

	private MyPlacesFragment myPlacesFragment;
	private PlaceFormFragment placeFormFragment;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.my_places_main);

		FragmentManager fm = getSupportFragmentManager();
		myPlacesFragment = (MyPlacesFragment) fm.findFragmentById(R.id.my_places_fragment);
		placeFormFragment = (PlaceFormFragment) fm.findFragmentById(R.id.my_place_form);
		
		

		FragmentTransaction transaction = fm.beginTransaction();
		transaction.hide(myPlacesFragment);
		transaction.hide(placeFormFragment);
		transaction.commit();
		
		showFragment(myPlacesFragment, true);
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		
	}

	public void showForm(){
		showFragment(placeFormFragment, false);
	}
	
	public void showPlaces() {
		showFragment(myPlacesFragment, false);
	}
	
	private void showFragment(Fragment fragment, boolean addToBackStack) {
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();
		
		if(fragment == myPlacesFragment) {
			transaction.show(myPlacesFragment);
			transaction.hide(placeFormFragment);
		} 
		else if (fragment == placeFormFragment) {
			transaction.show(placeFormFragment);
			transaction.hide(myPlacesFragment);
		}
		if (addToBackStack){
			transaction.addToBackStack(null);
		}
		transaction.commit();
	}

	public void addToList(AbstractRawPlace providerPlace) {
		myPlacesFragment.addToList(providerPlace);
	}

	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    // Respond to the action bar's Up/Home button
	    case android.R.id.home:
	        NavUtils.navigateUpFromSameTask(this);
	        return true;
	    }
	    return super.onOptionsItemSelected(item);
	}
}
