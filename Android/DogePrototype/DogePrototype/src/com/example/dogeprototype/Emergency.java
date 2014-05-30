package com.example.dogeprototype;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class Emergency extends Activity {
public final static String EXTRA_MESSAGE = "com.example.dogeprototype.MESSAGE";
private double lat,lon,distance;
private String userName;
TextView text;
String website_url="http://saviour.comli.com/saviour/index.php/manager";
Handler handler;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_emergency);
		
		//Get values of lat,lon,distance & userId from server and assign them here in the on create.
		Intent i = getIntent();
		lat = i.getDoubleExtra("lx", 0);
		lon = i.getDoubleExtra("ly", 0);
		distance = i.getDoubleExtra("distance", 0);
		userName = i.getStringExtra("name");
		handler = new Handler(){
			
		};
		text=(TextView)findViewById(R.id.dan);
		text.setText("Alert! A woman named "+userName+" requires your help! She is approximately "+distance+"km from you. Will you aid her?");
	}
	
	public void Navigate(View view){
		accept();
		Intent intent=new Intent(this,Navigate.class);
		String nt=lat+","+lon;
		intent.putExtra(EXTRA_MESSAGE,nt);
		startActivity(intent);
		return;
	}
	public void accept()
	{
		HttpPost hp = new HttpPost(website_url);
		List<NameValuePair> p = new ArrayList<NameValuePair>();
		p.add(new BasicNameValuePair("controller","save"));
		p.add(new BasicNameValuePair("action","acceptEmergency"));
		p.add(new BasicNameValuePair("uid","1"));
		p.add(new BasicNameValuePair("aid","2"));
		try {
			hp.setEntity(new UrlEncodedFormEntity(p));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		new PublishingAsyncTask("accept", new DefaultHttpClient(), hp, handler, null).execute();
	}
	public void Kill(View view){
		decline();
		finish();
	}
	public void decline()
	{
		Toast.makeText(this, "Thankyou for promptly responding with your inability to help today.", Toast.LENGTH_LONG).show();
	}
}
