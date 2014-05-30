package com.example.dogeprototype;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class Navigate extends Activity {
private double lat2,lon2;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location);
		// Show the Up button in the action bar.
		setupActionBar();
		
		//Taking data from intent
		Intent intent = getIntent();
		String coordinates = intent.getStringExtra(Emergency.EXTRA_MESSAGE);
		
		//GPS
				TextView tv=(TextView)findViewById(R.id.tv);
					double lat1=13.036544;
					double lon1=77.593783;
					tv.setText("Navigating to user:-");
					String baseURL="http://maps.google.com/maps?s&saddr="+lat1+","+lon1;
					String mapURL = baseURL+"&daddr="+coordinates+"&dirflg=d";
						Intent intent2 = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(mapURL));
						startActivity(intent2);
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.location, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
