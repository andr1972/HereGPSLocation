package com.example.heregpslocation;

import java.util.Calendar;
import java.util.Date;

import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Button button;
		button = (Button) findViewById(R.id.button1);
		button.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		TextView textView;
		textView = (TextView) findViewById(R.id.textView1);
		RadioButton radio0 = (RadioButton) findViewById(R.id.radio0);
		RadioButton radio1 = (RadioButton) findViewById(R.id.radio1);
		LocationManager locationManager;
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		Location location;
		if (radio0.isChecked())
			location = locationManager.getLastKnownLocation("gps");
		else if (radio1.isChecked())
			location = locationManager.getLastKnownLocation("network");
		else
			location = null;
		if (location == null) {
			if (radio0.isChecked())
				textView.setText("Turn GPS on!");
			else
				textView.setText("Network not enabled!");
		} else {
			Date now = Calendar.getInstance().getTime();
			textView.setText(String.format("Latitude = %s\nLongitude = %s\n"
					+ "Accuracy = %f\n" + "%d seconds ago",
					location.getLatitude(), location.getLongitude(),
					location.getAccuracy(),
					(now.getTime() - location.getTime()) / 1000));
		}
	}
}
