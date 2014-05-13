package no.ntnu.ubinomad.postdemo;


import no.ntnu.ubinomad.lib.Provider;
import no.ntnu.ubinomad.lib.externalproviders.ProviderRegister;
import no.ntnu.ubinomad.lib.interfaces.AggregatorPlace;
import no.ntnu.ubinomad.lib.models.User;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;

public class MainFragment extends Fragment{

	protected static final String TAG = "MainFragment";
	private EditText messageView;
	private Button postButton;
	private TextView placeHolder;
	
	private UiLifecycleHelper uiHelper;
	
	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(final Session session, final SessionState state, final Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		
		View view = inflater.inflate(R.layout.main_fragment, container, false);
		
		messageView = (EditText) view.findViewById(R.id.postContent);
		
		placeHolder = (TextView) view.findViewById(R.id.checkInPlace);
		
		AggregatorPlace checkinPlace = User.getMe(getActivity()).getCheckin();
		
		if (checkinPlace == null){
			placeHolder.setText("Your location is unknown");
		} else {
			placeHolder.setText(checkinPlace.getName().toString());
		}
		
		postButton = (Button) view.findViewById(R.id.postButton);
		
		postButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				String message = messageView.getText().toString();
				AggregatorPlace checkin = User.getMe(getActivity()).getCheckin();
				
				for (Provider provider : ProviderRegister.getInstance().getProviders()) {
					ProviderRegister.getInstance().getExternalProvider(provider).postStatus(getActivity(), message, checkin);
				}
			}
		});
		
		return view;

	}
	
	private void onSessionStateChange(final Session session, SessionState state, Exception exception) {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		uiHelper = new UiLifecycleHelper(getActivity(), callback);
		uiHelper.onCreate(savedInstanceState);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onResume() {
		super.onResume();
		uiHelper.onResume();
	}

	@Override
	public void onSaveInstanceState(Bundle bundle) {
		super.onSaveInstanceState(bundle);
		uiHelper.onSaveInstanceState(bundle);
	}

	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}

}
