package com.example.dogeprototype;

import java.io.File;
import java.io.FileOutputStream;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;
public class MainActivity extends Activity 
{
	String filename="base";
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);	
		//Check for existence of text file before opening Splash Screen
		File check= getBaseContext().getFileStreamPath(filename);
		if(check.exists()){
			Intent in =new Intent(this,Ready.class);
			startActivity(in);
			finish();
		}
		setContentView(R.layout.activity_main);
	}
	//OneClickOfButton
	public void bridge(View view)
	{	//GetIMEI
		TelephonyManager mngr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE); 
		String IMEI=mngr.getDeviceId();
		Toast.makeText(this,IMEI,Toast.LENGTH_LONG).show();
		
		//Save IMEI
		FileOutputStream outputStream;

		try {
		  outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
		  outputStream.write(IMEI.getBytes());
		  outputStream.close();
		} catch (Exception e) {
		  e.printStackTrace();
		}

		//Move On To Next Activity
		Intent intent =new Intent(this,Questions.class);
		startActivity(intent);
		
		return;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
