package no.ntnu.ubinomad.lib.connectors;

import com.foursquare.android.nativeoauth.FoursquareCancelException;
import com.foursquare.android.nativeoauth.FoursquareDenyException;
import com.foursquare.android.nativeoauth.FoursquareInvalidRequestException;
import com.foursquare.android.nativeoauth.FoursquareOAuth;
import com.foursquare.android.nativeoauth.FoursquareOAuthException;
import com.foursquare.android.nativeoauth.FoursquareUnsupportedVersionException;
import com.foursquare.android.nativeoauth.model.AccessTokenResponse;
import com.foursquare.android.nativeoauth.model.AuthCodeResponse;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import no.ntnu.ubinomad.lib.Provider;
import no.ntnu.ubinomad.lib.R;
import no.ntnu.ubinomad.lib.activities.UbiNomadFragmentActivity;
import no.ntnu.ubinomad.lib.interfaces.ProviderConnector;

public class FourSquareConnector implements ProviderConnector {

    private static final int REQUEST_CODE_FSQ_CONNECT = 200;
    private static final int REQUEST_CODE_FSQ_TOKEN_EXCHANGE = 201;

    private static String CLIENT_ID;
    private static String CLIENT_SECRET;
    
	private static final String TAG = "FS CONNECTOR";
	
	private UbiNomadFragmentActivity activity;
	private Bundle savedInstanceState;
	
	
	public FourSquareConnector(final String clientId, final String clientSecret){
		CLIENT_ID = clientId;
		CLIENT_SECRET = clientSecret;
	}
	
	@Override
	public void init(UbiNomadFragmentActivity ubiNomadActivity, Bundle savedInstanceState) {
		this.activity = ubiNomadActivity;
		this.savedInstanceState = savedInstanceState;
		if (savedInstanceState != null && savedInstanceState.containsKey("fs_token")){
			FourSquareTokenStore.get().setToken(savedInstanceState.getString("fs_token"));
		}
	}

	@Override
	public void connect() {
//		Intent intent = FoursquareOAuth.getConnectIntent(activity, CLIENT_ID);
//		
//		if(!FoursquareOAuth.isPlayStoreIntent(intent)){
//			activity.startActivityForResult(intent, REQUEST_CODE_FSQ_CONNECT);
//		}
	}

	@Override
	public void login(){
	  // Start the native auth flow.
      Intent intent = FoursquareOAuth.getConnectIntent(activity, CLIENT_ID);
      
      // If the device does not have the Foursquare app installed, we'd
      // get an intent back that would open the Play Store for download.
      // Otherwise we start the auth flow.
      if (FoursquareOAuth.isPlayStoreIntent(intent)) {
          toastMessage(activity, activity.getString(R.string.app_not_installed_message));
          activity.startActivity(intent);
      } else {
          activity.startActivityForResult(intent, REQUEST_CODE_FSQ_CONNECT);
      }
      
	}
	
	
	@Override
	public void activityResult(int requestCode, int resultCode, Intent data) {
		 switch (requestCode) {
         case REQUEST_CODE_FSQ_CONNECT:
        	 onCompleteConnect(resultCode, data);
             break;
             
         case REQUEST_CODE_FSQ_TOKEN_EXCHANGE:
             onCompleteTokenExchange(resultCode, data);
             break;
     }

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resumeFragments() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void saveInstanceState(Bundle outState) {
		if(FourSquareTokenStore.get().getToken() != null){
			outState.putString("fs_token", FourSquareTokenStore.get().getToken());
		}
	}
	
	
	 private void onCompleteConnect(int resultCode, Intent data) {
	        AuthCodeResponse codeResponse = FoursquareOAuth.getAuthCodeFromResult(resultCode, data);
	        Exception exception = codeResponse.getException();
	        
	        if (exception == null) {
	            // Success.
	            String code = codeResponse.getCode();
	            performTokenExchange(code);
	            activity.onLoggedIn(Provider.FOURSQUARE);

	        } else {
	            if (exception instanceof FoursquareCancelException) {
	                // Cancel.
	                toastMessage(activity, "Canceled");

	            } else if (exception instanceof FoursquareDenyException) {
	                // Deny.
	            	toastMessage(activity, "Denied");
	                
	            } else if (exception instanceof FoursquareOAuthException) {
	                // OAuth error.
	                String errorMessage = exception.getMessage();
	                String errorCode = ((FoursquareOAuthException) exception).getErrorCode();
	                toastMessage(activity, errorMessage + " [" + errorCode + "]");
	                
	            } else if (exception instanceof FoursquareUnsupportedVersionException) {
	                // Unsupported Fourquare app version on the device.
	            	toastError(activity, exception);
	                
	            } else if (exception instanceof FoursquareInvalidRequestException) {
	                // Invalid request.
	            	toastError(activity, exception);
	                
	            } else {
	                // Error.
	            	toastError(activity, exception);
	            }
	        }
	    }
	    
	    private void onCompleteTokenExchange(int resultCode, Intent data) {
	        AccessTokenResponse tokenResponse = FoursquareOAuth.getTokenFromResult(resultCode, data);
	        Exception exception = tokenResponse.getException();
	        
	        if (exception == null) {
	            String accessToken = tokenResponse.getAccessToken();
	            // Success.
	            toastMessage(activity, "Access token: " + accessToken);
	            
	            // Persist the token for later use. In this example, we save
	            // it to shared prefs.
	            FourSquareTokenStore.get().setToken(accessToken);
	            
	            // Refresh UI.
	            //ensureUi();
	            
	        } else {
	            if (exception instanceof FoursquareOAuthException) {
	                // OAuth error.
	                String errorMessage = ((FoursquareOAuthException) exception).getMessage();
	                String errorCode = ((FoursquareOAuthException) exception).getErrorCode();
	                toastMessage(activity, errorMessage + " [" + errorCode + "]");
	                
	            } else {
	                // Other exception type.
	            	toastError(activity, exception);
	            }
	        }
	    }
	

	 private void performTokenExchange(String code) {
	        Intent intent = FoursquareOAuth.getTokenExchangeIntent(activity, CLIENT_ID, CLIENT_SECRET, code);
	        activity.startActivityForResult(intent, REQUEST_CODE_FSQ_TOKEN_EXCHANGE);
	    }
	
	 public static void toastMessage(Context context, String message) {
	        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	    }

	    public static void toastError(Context context, Throwable t) {
	        Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
	    }	
}
