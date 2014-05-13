package no.ntnu.ubinomad.lib.components;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import no.ntnu.ubinomad.lib.interfaces.Place;
import no.ntnu.ubinomad.lib.interfaces.RawPlace;
import no.ntnu.ubinomad.lib.services.ImageLoader;
import no.ntnu.ubinomad.lib.R;
import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PlacePickerListAdapter extends BaseAdapter {

	
	private static final String TAG = "PlacePickerListAdapter";
	private final List<? extends Place> places;
	private final LayoutInflater inflater;
	private ImageLoader imageLoader;
	
	private Typeface font;
	private final int resource;

	public PlacePickerListAdapter (Context context, List<? extends Place> places){
		this(context, R.layout.place_row, places);
	}
	
	public PlacePickerListAdapter(Context activity, int resource, List<? extends Place> places){
		this.resource = resource;
		this.places = places;
		inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageLoader = new ImageLoader(activity.getApplicationContext());
		//font = getFontFromRes(activity, )
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View placeRow = inflater.inflate(resource, parent, false);
		TextView nameView = (TextView) placeRow.findViewById(R.id.name);
		TextView distanceView = (TextView) placeRow.findViewById(R.id.distance);
		ImageView providerView = (ImageView) placeRow.findViewById(R.id.provider);
		ImageView imageView = (ImageView) placeRow.findViewById(R.id.icon);
		nameView.setText(places.get(position).getName());
		if (places.get(position) instanceof RawPlace ) distanceView.setText(((RawPlace)places.get(position)).getDistance() + " meters");
	
		if (places.get(position) instanceof RawPlace) providerView.setImageResource(((RawPlace)(places.get(position))).getProvider().getIcon());
		imageLoader.DisplayImage(places.get(position).getIconUrl(), imageView);
	
		
		return placeRow;
	}

	@Override
	public int getCount() {
		return places.size();
	}

	@Override
	public Object getItem(int position) {
		return places.get(position);
	}

	@Override
	public long getItemId(int position) {
		return -1;
	}
}
