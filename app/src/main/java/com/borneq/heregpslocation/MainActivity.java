package com.borneq.heregpslocation;

import java.util.Calendar;

import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements OnClickListener {

	private LocationManager locationManager;
	private CheckBox checkBox;
	private RadioButton radio0;
	private RadioButton radio1;
	private CheckBox checkBoxRefresh;
	private int refreshCounter;
	private TextView textCounter;
	private TextView textView;

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

		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

		checkBox = (CheckBox) findViewById(R.id.checkBox1);
		radio0 = (RadioButton) findViewById(R.id.radio0);
		radio1 = (RadioButton) findViewById(R.id.radio1);
		checkBoxRefresh = (CheckBox) findViewById(R.id.checkBox2);
		textCounter = (TextView) findViewById(R.id.textView2);
		textView = (TextView) findViewById(R.id.textView1);

		checkBox.setOnClickListener(this);
		radio0.setOnClickListener(this);
		radio1.setOnClickListener(this);
		checkBoxRefresh.setOnClickListener(this);

		updateThread.start();
	}

	Thread updateThread = new Thread() {
		public void run() {
			try {
				while (true) {
					MainActivity.this.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							updateText();
						}
					});
					Thread.sleep(1000);
				}
			} catch (InterruptedException ignore) {
				// Thread was interrupted ==> stop loop
			}
		}
	};

	private void setButtonState() {
		boolean isGps = locationManager.isProviderEnabled("gps");
		checkBox.setChecked(isGps);
	}

	@Override
	protected void onResume() {
		super.onResume();
		setButtonState();
	}

	@Override
	public void onClick(View v) {
		if (v == checkBox)
			clickCheckBox();
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

	private void updateText() {

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
					textView.setText(R.string.no_gps);
				else
					textView.setText(R.string.turn_on);
			} else
				textView.setText(R.string.network_disabled);
		} else {
			long now = Calendar.getInstance().getTimeInMillis();
			long secsAgo = (now - location.getTime()) / 1000;
			if (secsAgo < 0) {
				secsAgo = 0;
			}
			textView.setText(Html.fromHtml(getString(R.string.position_text,
					location.getLatitude(), location.getLongitude(),
					location.getAccuracy(), secsAgo)));
		}
	}
}
