package com.example.dogeprototype;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.R.anim;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Ready extends Activity {
	double lat1,lon1;
	boolean nextStarted = false;
	boolean pulseLocation = true;
	int id=2;//TODO!
	final static String PULSE = "PULSE";
	final static String LOC_PULSE = "LOC_PULSE";
	final static String INVOKE_EMERGENCY = "INVOKE_EMERGENCY";
	final static int APPROBATION_DIALOG_ID=1;
	final static int BULWARK_RECRUITMENT_ID=2;
	static String[] bulwarkNames={"MainActivity.bulwark_name"};
	private BluetoothAdapter BA;
	Handler handlerDevice;
	boolean running=true;
	String website_url="http://saviour.comli.com/saviour/index.php/manager";
	Runnable pulse_r = new Runnable(){
		@Override
		public void run()
		{
			pulse();
		}
	};
	Runnable loc_pulse_r = new Runnable(){
		@Override
		public void run()
		{
			locationPulse();
		}
	};
	Handler handler;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ready);
		// Show the Up button in the action bar.
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg)
			{
				Bundle b = msg.getData();
				String function = b.getString("function");
				if(function.equals(PULSE))
				{
					handlePulse(b);
				}
				else if(function.equals(LOC_PULSE))
				{
					Log.d("LOCPULSE","HANDLER");
					handleLocPulse(b);
				}
				else if(function.equals(INVOKE_EMERGENCY))
				{
					//TODO
				}
			}
		};
		location();
		locationPulse();
		pulse();
		setupActionBar();
		BA = BluetoothAdapter.getDefaultAdapter();
		handlerDevice=new Handler();
	      final Runnable runnable=new Runnable(){

				@Override
				public void run() 
				{
					// TODO Auto-generated method stub
				while(running)
				{
					try
					{
						Thread.sleep(15000);
					}
					catch(InterruptedException e)
					{
						e.printStackTrace();
					}
					handlerDevice.post(new Runnable(){

						@Override
						public void run() 
						{
							// TODO Auto-generated method stub
							if(BA.startDiscovery())
							{
								Toast.makeText(Ready.this, "Discovery in progress", Toast.LENGTH_LONG).show();
								registerReceiver(discoveryResult, new IntentFilter(BluetoothDevice.ACTION_FOUND));
							}
						}
					});
				}
				}
				
			};
			new Thread(runnable).start();
	}
	BroadcastReceiver discoveryResult=new BroadcastReceiver()
	   {
		   
		@Override
		public void onReceive(Context context, Intent intent) 
		{
			// TODO Auto-generated method stub
			String remoteDeviceName=intent.getStringExtra(BluetoothDevice.EXTRA_NAME);
			BluetoothDevice remoteDevice;
			remoteDevice=intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			Toast.makeText(Ready.this, remoteDeviceName, Toast.LENGTH_LONG).show();
			if(remoteDeviceName.equals("beaglebone-0"))
				invokeEmergency();
		}
		public void invokeEmergency()
		{
			id = 1;
			HttpPost hp = new HttpPost(website_url);
			List<NameValuePair> p = new ArrayList<NameValuePair>();
			p.add(new BasicNameValuePair("controller","save"));
			p.add(new BasicNameValuePair("action","initiateEmergency"));
			p.add(new BasicNameValuePair("id",""+id));//TODO
			p.add(new BasicNameValuePair("lx",""+13.037903));
			p.add(new BasicNameValuePair("ly",""+77.593922));
			pulseLocation=false;
			try {
				hp.setEntity(new UrlEncodedFormEntity(p));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			new PublishingAsyncTask(INVOKE_EMERGENCY, new DefaultHttpClient(), hp, handler, null).execute();
		}
	   };
	public void invokeEmergency()
	{
		id = 1;
		HttpPost hp = new HttpPost(website_url);
		List<NameValuePair> p = new ArrayList<NameValuePair>();
		p.add(new BasicNameValuePair("controller","save"));
		p.add(new BasicNameValuePair("action","initiateEmergency"));
		p.add(new BasicNameValuePair("id",""+id));//TODO
		p.add(new BasicNameValuePair("lx",""+12.977));
		p.add(new BasicNameValuePair("ly",""+77.715));
		pulseLocation=false;
		try {
			hp.setEntity(new UrlEncodedFormEntity(p));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		new PublishingAsyncTask(INVOKE_EMERGENCY, new DefaultHttpClient(), hp, handler, null).execute();
	}
	public void pulse()
	{
		HttpPost hp = new HttpPost(website_url);
		List<NameValuePair> p = new ArrayList<NameValuePair>();
		p.add(new BasicNameValuePair("controller","pulse"));
		p.add(new BasicNameValuePair("action","pulse"));
		p.add(new BasicNameValuePair("id",""+id));//TODO
		try {
			hp.setEntity(new UrlEncodedFormEntity(p));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		new PublishingAsyncTask(PULSE, new DefaultHttpClient(), hp, handler, null).execute();
	}
	public void handlePulse(Bundle b)
	{
		String json = b.getString("json");
		//Toast.makeText(this, json, Toast.LENGTH_SHORT).show();
		try{
			JSONObject obj = new JSONObject(json);
			JSONObject result = obj.getJSONObject("result");
			Log.d("JSONPULSE","resultreached");
			boolean emergency = result.getBoolean("emergency");
			Log.d("JSONPULSE","emergency reached");
			if(emergency&&!nextStarted)
			{
				JSONObject user = result.getJSONObject("user");	
				double lx = user.getDouble("lx");
				double ly = user.getDouble("ly");
				String name = user.getString("name");
				double distance = distance(lx,ly,lat1,lon1);
				Intent i = new Intent(this,Emergency.class);
				i.putExtra("lx",lx);
				i.putExtra("ly", ly);
				i.putExtra("name", name);
				i.putExtra("distance", distance);
				startActivity(i);
				nextStarted=true;
			}
		}catch(Exception e){
			Log.e("JSONFAIL","handlePulse");
		}
		handler.postDelayed(pulse_r, 5000);
	}
	public void locationPulse()
	{
		if(!pulseLocation)
			return;
		HttpPost hp = new HttpPost(website_url);
		List<NameValuePair> p = new ArrayList<NameValuePair>();
		p.add(new BasicNameValuePair("controller","pulse"));
		p.add(new BasicNameValuePair("action","locationPulse"));
		p.add(new BasicNameValuePair("id",""+id));//TODO
		p.add(new BasicNameValuePair("lx",""+lat1));
		p.add(new BasicNameValuePair("ly",""+lon1));
		try {
			hp.setEntity(new UrlEncodedFormEntity(p));
		} catch (UnsupportedEncodingException e) {
			Log.e("locPULSE","ENTITY SETTING FAILED!");
			e.printStackTrace();
		}
		new PublishingAsyncTask(LOC_PULSE, new DefaultHttpClient(), hp, handler, null).execute();
	}
	public void handleLocPulse(Bundle b)
	{
		Log.d("LOCPLSE","function");
		//Toast.makeText(this, b.getString("json"), Toast.LENGTH_SHORT).show();
		handler.postDelayed(loc_pulse_r, 20000);
	}
	public void Save(View view){
		TextView t=(TextView)findViewById(R.id.tt);
		t.setText("You're location has been broadcast. Help is on the way");
		invokeEmergency();
	}
	
	public void location(){
		GPSTracker gps = new GPSTracker(this);
		if(gps.canGetLocation()){
			lat1=gps.getLatitude();
			lon1=gps.getLongitude();
		}
	}
	private double distance(double lat1, double lon1, double lat2, double lon2) {
		  double theta = lon1 - lon2;
		  double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
		  dist = Math.acos(dist);
		  dist = rad2deg(dist);
		  dist = dist * 60 * 1.1515;
		  dist = dist * 1.609344;
		  return (dist);
		}
	private double deg2rad(double deg) {
		return (deg * Math.PI / 180.0);
	}
	private double rad2deg(double rad) {
		  return (rad * 180 / Math.PI);
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
		getMenuInflater().inflate(R.menu.ready, menu);
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

	@Override
	@Deprecated
	protected Dialog onCreateDialog(int id) 
	{
		// TODO Auto-generated method stub
		switch(id)
		{
		case APPROBATION_DIALOG_ID:
			LayoutInflater inflater=(LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			final View layout=inflater.inflate(R.layout.approbation_dialog,(ViewGroup)findViewById(R.id.root));
			//Modifications
			AlertDialog.Builder builder=new AlertDialog.Builder(this);
			builder.setView(layout);
			builder.setTitle("Do you concur?");
			builder.setPositiveButton("Affirmative", new DialogInterface.OnClickListener() 
			{
				
				@Override
				public void onClick(DialogInterface dialog, int which) 
				{
					// TODO Auto-generated method stub
					Ready.this.removeDialog(APPROBATION_DIALOG_ID);
				}
			});
			builder.setNegativeButton("Rescind", new DialogInterface.OnClickListener() 
			{	
				@Override
				public void onClick(DialogInterface dialog, int which) 
				{
					// TODO Auto-generated method stub
					Ready.this.removeDialog(APPROBATION_DIALOG_ID);
				}
			});
			AlertDialog approbationDialog=builder.create();
			return approbationDialog;
		case BULWARK_RECRUITMENT_ID:
			ListView bulwarkList=(ListView)findViewById(R.id.bulwark_list);
			ArrayAdapter<String> adapt=new ArrayAdapter<String>(this,R.layout.bulwark_listview,bulwarkNames);
			bulwarkList.setAdapter(adapt);
			LayoutInflater bulwarkInflater=(LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			final View bulwarkLayout=bulwarkInflater.inflate(R.layout.bulwark_dialog,(ViewGroup)findViewById(R.id.root_bulwark));
			AlertDialog.Builder bulwarkBuilder=new AlertDialog.Builder(this);
			bulwarkBuilder.setView(bulwarkLayout);
			bulwarkBuilder.setTitle("Device selected:");
			bulwarkBuilder.setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener() 
			{
				
				@Override
				public void onClick(DialogInterface dialog, int which) 
				{
					// TODO Auto-generated method stub
					Ready.this.removeDialog(BULWARK_RECRUITMENT_ID);
				}
			});
			bulwarkBuilder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() 
			{
				
				@Override
				public void onClick(DialogInterface dialog, int which) 
				{
					// TODO Auto-generated method stub
					Ready.this.removeDialog(BULWARK_RECRUITMENT_ID);
				}
			});
			AlertDialog bulwarkDialog=bulwarkBuilder.create();
			return bulwarkDialog;
		}
		return super.onCreateDialog(id);
	}

}
