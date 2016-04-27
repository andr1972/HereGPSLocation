package com.borneq.heregpslocation;

import java.util.Calendar;

import com.borneq.heregpslocation.R;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {

	private LocationManager locationManager;
	private CheckBox checkBox;
	private Button button;
	private RadioButton radio0;
	private RadioButton radio1;
	private CheckBox checkBoxRefresh;
	private int refreshCounter;
	private TextView textCounter;

	private LocationListener locationListener = new LocationListener() {
		@Override
		public void onLocationChanged(Location location) {
			refreshCounter++;
			textCounter.setText(Integer.toString(refreshCounter));
		}

		@Override
		public void onProviderDisabled(String provider) {
			refreshCounter = 0;
			textCounter.setText("0");
		}

		@Override
		public void onProviderEnabled(String provider) {
			refreshCounter = 0;
			textCounter.setText("0");
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		button = (Button) findViewById(R.id.button1);
		button.setOnClickListener(this);
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		checkBox = (CheckBox) findViewById(R.id.checkBox1);
		checkBox.setOnClickListener(this);
		radio0 = (RadioButton) findViewById(R.id.radio0);
		radio1 = (RadioButton) findViewById(R.id.radio1);
		radio0.setOnClickListener(this);
		radio1.setOnClickListener(this);
		checkBoxRefresh = (CheckBox) findViewById(R.id.checkBox2);
		checkBoxRefresh.setOnClickListener(this);
		textCounter = (TextView) findViewById(R.id.textView2);
	}

	private void setButtonState() {
		boolean isGps = locationManager.isProviderEnabled("gps");
		checkBox.setChecked(isGps);
		button.setEnabled(isGps | radio1.isChecked());
	}

	@Override
	protected void onResume() {
		super.onResume();
		setButtonState();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		if (v == checkBox)
			clickCheckBox();
		else if (v == button)
			clickButton();
		else if (v == checkBoxRefresh)
			clickCheckBoxRefresh();
		else
			setButtonState(); // for radio buttons
	}

	private void clickCheckBoxRefresh() {
		refreshCounter = 0;
		textCounter.setText("0");
		if (checkBoxRefresh.isChecked()) {
			String provider;
			if (radio0.isChecked())
				provider = LocationManager.GPS_PROVIDER;
			else
				provider = LocationManager.NETWORK_PROVIDER;
			locationManager.requestLocationUpdates(provider, 1000, 0,
					locationListener);
		} else
			locationManager.removeUpdates(locationListener);
	}

	private void clickCheckBox() {
		Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		startActivity(intent);
		checkBox.setChecked(locationManager.isProviderEnabled("gps"));
	}

	private void clickButton() {
		TextView textView;
		textView = (TextView) findViewById(R.id.textView1);

		Location location;
		if (radio0.isChecked())
			location = locationManager.getLastKnownLocation("gps");
		else if (radio1.isChecked())
			location = locationManager.getLastKnownLocation("network");
		else
			location = null;
		if (location == null) {
			if (radio0.isChecked()) {
				if (locationManager.isProviderEnabled("gps"))
					textView.setText("No GPS signal!");
				else
					textView.setText("Turn GPS on!");
			} else
				textView.setText("Network not enabled!");
		} else {
			long now = Calendar.getInstance().getTimeInMillis();
			textView.setText(String.format("Latitude = %s\nLongitude = %s\n"
					+ "Accuracy = %f\n" + "%d seconds ago",
					location.getLatitude(), location.getLongitude(),
					location.getAccuracy(), (now - location.getTime()) / 1000));
		}
	}
}
