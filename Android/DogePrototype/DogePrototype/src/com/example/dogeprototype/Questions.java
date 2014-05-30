package com.example.dogeprototype;
import java.io.BufferedWriter;
import java.io.FileWriter;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class Questions extends Activity {
FileWriter fw;
private int i=0;
int responses[]=new int[5];
String questionarray[]={"How ready would you be to aid a woman or child in danger on being contacted?",
		"How physically fit would you rate yourself to be?",
		"Which age group do you fall under\n1- >60\n2- 60 to 50\n3- 50 to 40\n4- 40 to 30\n5- <30",
		"Which vehicle do you own?\n1- Do not own a vehicle\n2- Shared four wheeler\n3- Shared two wheeler\n4- Private four wheeler\n5- Private two wheeler",
		"What slot of the day are you usually free during?\nSlot 1-\nSlot 2-\nSlot 3-\nSlot 4- \nSlot 5-"
		};

TextView tv;	
@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_questions);
		setupActionBar();
		}

	//Updater Function
	public void Updater(View view)
	{
		boolean checked = ((RadioButton) view).isChecked();
		switch(view.getId()) {
        case R.id.rb1:
            if (checked)
                responses[i]=1;
            break;
        case R.id.rb2:
            if (checked)
                responses[i]=2;
            break;
        case R.id.rb3:
            if (checked)
                responses[i]=3;
            break;
        case R.id.rb4:
            if (checked)
                responses[i]=4;
            break;
        case R.id.rb5:
            if (checked)
                responses[i]=5;
            break;
    }

		i++;
		if(i<5){
		tv.setText(questionarray[i]);
		return;
		}
		else{
			
			//Save array value in a text file
			try {
				  fw=new FileWriter(getBaseContext().getFileStreamPath("base"),true);
				  BufferedWriter out = new BufferedWriter(fw);
				  for(int j=0;j<5;j++)out.write("\n"+responses[j]);
				  out.close();
		          fw.close();
				} catch (Exception e) {
				  e.printStackTrace();
				}

			
			RadioGroup rg=(RadioGroup)findViewById(R.id.radio);
			rg.setVisibility(View.GONE);
			tv.setText("Thank You!");
			tv.setTextSize(30);
			//Two second interval before changing functions
			//Creating the interval
			final int interval = 1000; // 1 Seconds
			//Creating the handler
			Handler handler = new Handler();
			//Creating the runnable for handler to run
			Runnable runnable = new Runnable(){
			    public void run() {
			    	Intent inten=new Intent(Questions.this,Ready.class);
					startActivity(inten);
			    }
			};//close runnable function
			handler.postDelayed(runnable, interval);
			return;
		}//close else
	}//close updater
	
	public void swap(View view)
	{
		setContentView(R.layout.prequestion);
		//Set Up The Text View
				tv = (TextView)findViewById(R.id.Qtext);
				tv.setText(questionarray[0]);
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
		getMenuInflater().inflate(R.menu.questions, menu);
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

