package com.example.dogeprototype;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class PublishingAsyncTask extends AsyncTask<Void,Void,Boolean>{

	HttpClient hc;
	HttpPost hp;
	HttpResponse r;
	ProgressDialog pd;
	Handler mHandler;
	Bundle b;
	Message msg;
	public PublishingAsyncTask(String function,HttpClient hc, HttpPost hp, Handler mHandler, ProgressDialog pd)
	{
		super();
		this.hc=hc;
		this.hp=hp;
		this.pd=pd;
		this.mHandler=mHandler;
		this.msg = mHandler.obtainMessage();
		this.b = new Bundle();
		b.putString("function", function);
	}
	@Override
	protected void onPreExecute()
	{
		if(pd!=null)
			pd.show();
	}
	@Override
	protected Boolean doInBackground(Void... params) {
		try {
			r = hc.execute(hp);
			b.putString("json", EntityUtils.toString(r.getEntity()));
			return true;
		} catch (ClientProtocolException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
	}
	@Override
	protected void onPostExecute(Boolean result)
	{
		if(pd!=null)
		pd.dismiss();
		b.putBoolean("success", result);
		/*if(result)//success
		{
			try {
				b.putString("json", EntityUtils.toString(r.getEntity()));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}*/
		msg.setData(b);
		mHandler.sendMessage(msg);
	}
}
