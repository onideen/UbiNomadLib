package no.ntnu.ubinomad.lib.connectors;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import com.facebook.Request;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;

import no.ntnu.ubinomad.lib.Provider;
import no.ntnu.ubinomad.lib.activities.UbiNomadFragmentActivity;
import no.ntnu.ubinomad.lib.interfaces.ProviderConnector;

public class FacebookConnector implements ProviderConnector {

	private UbiNomadFragmentActivity ubiNomadActivity;
	
	private static final String USER_SKIPPED_LOGIN_KEY = "user_skipped_login";
	
	private UiLifecycleHelper uiHelper;
	private boolean isResumed = false;
	private boolean userSkippedLogin = false;
	
	
	@Override
	public void init(UbiNomadFragmentActivity ubiNomadActivity, Bundle savedInstanceState) {
		this.ubiNomadActivity = ubiNomadActivity;

		uiHelper = new UiLifecycleHelper(ubiNomadActivity, callback);
		uiHelper.onCreate(savedInstanceState);

		if (savedInstanceState != null) {
			userSkippedLogin = savedInstanceState.getBoolean(USER_SKIPPED_LOGIN_KEY);
		}
		
	}

	@Override
	public void connect() {
	}

	@Override
	public void activityResult(int requestCode, int resultCode, Intent data) {
		uiHelper.onActivityResult(requestCode, resultCode, data);
		
	}
	
	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state, Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};
	
	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
		if (isResumed) {
			FragmentManager manager = ubiNomadActivity.getSupportFragmentManager();
			int backStackSize = manager.getBackStackEntryCount();
			for (int i = 0; i < backStackSize; i++) {
				manager.popBackStack();
			}
			// check for the OPENED state instead of session.isOpened() since for the
			// OPENED_TOKEN_UPDATED state, the selection fragment should already be showing.
			if (state.equals(SessionState.OPENED)) {
				ubiNomadActivity.onLoggedIn(Provider.FACEBOOK);
			} else if (state.isClosed()) {
				ubiNomadActivity.onLoggedOut(Provider.FACEBOOK);
			}
		}
	}

	@Override
	public void resume() {
		uiHelper.onResume();
		isResumed = true;
	}
	
	@Override
	public void pause() {
		uiHelper.onPause();
		isResumed = false;		
	}

	@Override
	public void destroy() {
		uiHelper.onDestroy();		
	}
	
	@Override
	public void resumeFragments() {

		Session session = Session.getActiveSession();
		
		
		if (session != null && session.isOpened()) {
			// if the session is already open, try to show the selection fragment
			ubiNomadActivity.onLoggedIn(Provider.FACEBOOK);
			userSkippedLogin = false;
		} else if (userSkippedLogin) {
			ubiNomadActivity.onLoggedIn(Provider.FACEBOOK);
		} else {
			// otherwise present the splash screen and ask the user to login, unless the user explicitly skipped.
			ubiNomadActivity.onLoggedOut(Provider.FACEBOOK);
		}	
	}

	@Override
	public void saveInstanceState(Bundle outState) {
		uiHelper.onSaveInstanceState(outState);
		outState.putBoolean(USER_SKIPPED_LOGIN_KEY, userSkippedLogin);		
	}

	@Override
	public void login() {
		// TODO Auto-generated method stub
		
	}
	
}
