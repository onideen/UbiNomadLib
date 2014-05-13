package no.ntnu.ubinomad.lib;

public enum Provider {
	FACEBOOK("Facebook", R.drawable.facebook), 
	UBINOMAD("UbiNomad", R.drawable.house),
	GOOGLE("Google", R.drawable.google), 
	FOURSQUARE("Foursquare", R.drawable.foursquare);
	
	
	private String title;
	private int icon;
	
	Provider(String title, int icon){
		this.title = title;
		this.icon = icon;
	}
	
	public String getTitle(){
		return title;
	}
	
	public int getIcon() {
		return icon;
	}
}
