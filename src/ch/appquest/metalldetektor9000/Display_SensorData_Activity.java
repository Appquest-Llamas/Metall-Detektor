package ch.appquest.metalldetektor9000;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.FloatMath;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class Display_SensorData_Activity extends Activity implements SensorEventListener{

	public TextView tvSensorData;
	public ProgressBar pbSensorData;
	SensorManager sm;
	private static final int SCAN_QR_CODE_REQUEST_CODE = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display__sensor_data_);
		tvSensorData=(TextView)findViewById(R.id.textview_sensorData);
		pbSensorData=(ProgressBar)findViewById(R.id.progressBar_sensorData);
		pbSensorData.setMax(200);
		sm=(SensorManager)getSystemService(SENSOR_SERVICE);
		sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),SensorManager.SENSOR_DELAY_NORMAL);
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		sm.unregisterListener(this);
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),SensorManager.SENSOR_DELAY_NORMAL);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuItem logMenuItem=menu.add(R.string.logText);
		logMenuItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				// TODO Auto-generated method stub
				Intent intent = new Intent("com.google.zxing.client.android.SCAN");
				intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
				if (getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).isEmpty()){
					Toast.makeText(getApplicationContext(), "Zxing-Scanner not installed", Toast.LENGTH_LONG).show();
					return false;
				}
				startActivityForResult(intent, SCAN_QR_CODE_REQUEST_CODE);
				return false;
			}
		});
		return super.onCreateOptionsMenu(menu);
	}
	public void onActivityResult(int RequestCode,int ResultCode,Intent intent) {
		if(RequestCode==SCAN_QR_CODE_REQUEST_CODE){
			if(ResultCode==RESULT_OK){
				log(intent.getStringExtra("SCAN_RESULT"));
			}
		}
	}
	private void log(String qrCode) {
		Intent intent = new Intent("ch.appquest.intent.LOG");
		 
		if (getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).isEmpty()) {
		Toast.makeText(this, "Logbook App not Installed", Toast.LENGTH_LONG).show();
		return;
		}
		 
		intent.putExtra("ch.appquest.taskname", "Metalldetektor");
		intent.putExtra("ch.appquest.logmessage", qrCode);
		 
		startActivity(intent);
		}
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent e) {
		float staerke=FloatMath.sqrt(e.values[0] * e.values[0] + e.values[1] * e.values[1] + e.values[2] * e.values[2]);
		tvSensorData.setText(Float.toString(staerke));
		pbSensorData.setProgress((int)staerke);
	}

}
