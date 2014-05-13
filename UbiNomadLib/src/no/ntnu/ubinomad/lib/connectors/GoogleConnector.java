package no.ntnu.ubinomad.lib.connectors;

import no.ntnu.ubinomad.lib.Provider;
import no.ntnu.ubinomad.lib.R;
import no.ntnu.ubinomad.lib.activities.UbiNomadFragmentActivity;
import no.ntnu.ubinomad.lib.interfaces.ProviderConnector;
import no.ntnu.ubinomad.lib.models.User;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

public class GoogleConnector implements ProviderConnector, ConnectionCallbacks, OnConnectionFailedListener {

	public static final int STATE_DEFAULT = 0;	
	private static final int STATE_SIGN_IN = 1;
	private static final int STATE_IN_PROGRESS = 2;

	public static final int RC_SIGN_IN = 0;

	public static final int DIALOG_PLAY_SERVICES_ERROR = 0;

	public static final String SAVED_PROGRESS = "sign_in_progress";
	private static final String TAG = "GoogleConnector";

	public int mSignInProgress;
	public GoogleApiClient mGoogleApiClient;
	private PendingIntent mSignInIntent;
	public int mSignInError;
	
	
	
	private UbiNomadFragmentActivity ubiNomadActivity;
	
	@Override
	public void init(UbiNomadFragmentActivity ubiNomadActivity, Bundle savedInstanceState) {
		this.ubiNomadActivity = ubiNomadActivity;
		mGoogleApiClient = buildGoogleApiClient();
		
		if (savedInstanceState != null) {
			mSignInProgress = savedInstanceState
					.getInt(GoogleConnector.SAVED_PROGRESS, GoogleConnector.STATE_DEFAULT);
		}
	}
	
	
	@Override
	public void connect(){
		mGoogleApiClient.connect();

	}
	
	@Override
	public void activityResult(int requestCode, int resultCode, Intent data){

		switch (requestCode) {
		case RC_SIGN_IN:
			if (resultCode == Activity.RESULT_OK) {
				// If the error resolution was successful we should continue
				// processing errors.
				mSignInProgress = STATE_SIGN_IN;
			} else {
				// If the error resolution was not successful or the user canceled,
				// we should stop processing errors.
				mSignInProgress = STATE_DEFAULT;
			}

			if (!mGoogleApiClient.isConnecting()) {
				// If Google Play services resolved the issue with a dialog then
				// onStart is not called so we need to re-attempt connection here.
				mGoogleApiClient.connect();
			}
			break;
		}
	}
	
	private GoogleApiClient buildGoogleApiClient() {
		// When we build the GoogleApiClient we specify where connected and
		// connection failed callbacks should be returned, which Google APIs our
		// app uses and which OAuth 2.0 scopes our app requests.
		return new GoogleApiClient.Builder(ubiNomadActivity)
		.addConnectionCallbacks(this)
		.addOnConnectionFailedListener(this)
		.addApi(Plus.API, null)
		.addScope(Plus.SCOPE_PLUS_LOGIN)
		.addScope(Plus.SCOPE_PLUS_PROFILE)
		.build();
	}




	@Override
	public void onConnected(Bundle connectionHint) {
		Log.i(TAG, "onConnected");

		ubiNomadActivity.onLoggedIn(Provider.GOOGLE);
		
		// Retrieve some profile information to personalize our app for the user.
		Person currentUser = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);

//		Log.i(TAG, String.format("CURRENT USER: %s", currentUser.getName()));

		// Indicate that the sign in process is complete.
		mSignInProgress = STATE_DEFAULT;			
	}	


	
	@Override
	public void onConnectionSuspended(int cause) {
		mGoogleApiClient.connect();		
	}


	@Override
	public void onConnectionFailed(ConnectionResult result) {
		Log.i(TAG, "onConnectionFailed: ConnectionResult.getErrorCode() = "
				+ result.getErrorCode());
		
		if (mSignInProgress != STATE_IN_PROGRESS) {
			Log.i(TAG, "HERE");
			// We do not have an intent in progress so we should store the latest
			// error resolution intent for use when the sign in button is clicked.
			mSignInIntent = result.getResolution();
			mSignInError = result.getErrorCode();
			
			if (mSignInProgress == STATE_SIGN_IN) {
				Log.i(TAG, "But not here");
				// STATE_SIGN_IN indicates the user already clicked the sign in button
				// so we should continue processing errors until the user is signed in
				// or they click cancel.
				resolveSignInError();
			}
		}
		ubiNomadActivity.onLoggedOut(Provider.GOOGLE);		
	}
	
	public void resolveSignInError() {
		if (mSignInIntent != null) {
			try {
				mSignInProgress = STATE_IN_PROGRESS;
				ubiNomadActivity.startIntentSenderForResult(mSignInIntent.getIntentSender(),
						RC_SIGN_IN, null, 0, 0, 0);
			} catch (SendIntentException e) {
				Log.i(TAG, "Sign in intent could not be sent: "
						+ e.getLocalizedMessage());
				// The intent was canceled before it was sent.  Attempt to connect to
				// get an updated ConnectionResult.
				mSignInProgress = STATE_SIGN_IN;
				mGoogleApiClient.connect();
			}
		} else {
			// Google Play services wasn't able to provide an intent for some
			// error types, so we show the default Google Play services error
			// dialog which may still start an intent on our behalf if the
			// user can resolve the issue.
			DialogFragment dialog = new GoogleSignInDialog();
		    Bundle args = new Bundle();
		    args.putInt("id", DIALOG_PLAY_SERVICES_ERROR);
		    dialog.setArguments(args);
		    dialog.show(ubiNomadActivity.getSupportFragmentManager(), "tag");
		}  
	}
	
	
	@SuppressLint("ValidFragment")
	class GoogleSignInDialog extends DialogFragment {
		
		public GoogleSignInDialog()
		{

		}
		
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			 Bundle args = getArguments();
		     int id = args.getInt("id");
		     
		     switch(id) {
				case GoogleConnector.DIALOG_PLAY_SERVICES_ERROR:
					if (GooglePlayServicesUtil.isUserRecoverableError(mSignInError)) {
						return GooglePlayServicesUtil.getErrorDialog(
								mSignInError,
								ubiNomadActivity,
								GoogleConnector.RC_SIGN_IN, 
								new DialogInterface.OnCancelListener() {

									@Override
									public void onCancel(DialogInterface dialog) {
										Log.e(TAG, "Google Play services resolution cancelled");
										mSignInProgress = GoogleConnector.STATE_DEFAULT;
									}
								});
					} else {
						return new AlertDialog.Builder(ubiNomadActivity)
						.setMessage(R.string.play_services_error)
						.setPositiveButton(R.string.close,
								new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								Log.e(TAG, "Google Play services error could not be "
										+ "resolved: " + mSignInError);
								mSignInProgress = GoogleConnector.STATE_DEFAULT;
							}
						}).create();
					}
				default:
					return super.onCreateDialog(savedInstanceState);
				}
		}
	}


	@Override
	public void resume() {}


	@Override
	public void pause() {}


	@Override
	public void destroy() {}


	@Override
	public void resumeFragments() {}


	@Override
	public void saveInstanceState(Bundle outState) {
		outState.putInt(GoogleConnector.SAVED_PROGRESS, mSignInProgress);		
	}
	
	
	public User getCurrentUser(){
		Person currentUser = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);

		User user = new User();
		user.setName(currentUser.getName().getFormatted());
		
		return user;
	}


	@Override
	public void login() {
		// TODO Auto-generated method stub
		
	}
	
}
