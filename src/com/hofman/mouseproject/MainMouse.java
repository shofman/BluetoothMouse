 package com.hofman.mouseproject;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.Toast;

public class MainMouse extends Activity implements SensorEventListener {
	private SensorManager mSensorManager;
    private Sensor mSensor;
    private boolean mInitialized;
    private float mLastX, mLastY, mLastZ;
    private final float NOISE = (float) 2.0;
    private final int  REQUEST_ENABLE_BT = (int) 1;

    private ArrayAdapter<String> mArrayAdapter;// = new ArrayAdapter<String>(this, R.id.);
    /*private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            	Log.d("Found", "Found something");
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // Add the name and address to an array adapter to show in a ListView
                mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        }
    };*/
    
    
    private BroadcastReceiver the_receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
        	Log.d("Found", "Found something");
			int orientation = getBaseContext().getResources().getConfiguration().orientation;
			if (orientation == Configuration.ORIENTATION_PORTRAIT) {
				Toast.makeText(getBaseContext(), "I'm still standing.", Toast.LENGTH_SHORT).show();
			} else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
				Toast.makeText(getBaseContext(), "Help! I've fallen and I can't get up.", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(getBaseContext(), "?!#$%!?", Toast.LENGTH_SHORT).show();
			}
		}
		};
	private IntentFilter filter = new IntentFilter(Intent.ACTION_CONFIGURATION_CHANGED);

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_mouse);
        
        //Create the accelerometer sensor and register it
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        
        //Set flag for first runtime for sensor positions
        mInitialized = false;
        
        //Find bluetooth, and check availability
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
        	//Toast.makeText(this, "Bluetooth not available", Toast.LENGTH_SHORT).show();
        	finish();
        }
        
        //Enable bluetooth if not enabled
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        
        //Use low energy Bluetooth if available
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            //Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
        }
        
        this.registerReceiver(the_receiver, filter);

        /*
        //Discover other bluetooth devices
        mBluetoothAdapter.startDiscovery(); 	
     // Create a BroadcastReceiver for ACTION_FOUND
       
        // Register the BroadcastReceiver
        Intent discoverableIntent = new
        		Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(discoverableIntent);
        //IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        //registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy
    	*/
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_mouse, menu);
        return true;
    }

    public void onSensorChanged(SensorEvent event){
    	float x = event.values[0];
    	float y = event.values[1];
    	float z = event.values[2];
    	if (!mInitialized) {
	    	mLastX = x;
	    	mLastY = y;
	    	mLastZ = z;
	    	mInitialized = true;
    	} else {
	    	float deltaX = Math.abs(mLastX - x);
	    	float deltaY = Math.abs(mLastY - y);
	    	float deltaZ = Math.abs(mLastZ - z);
	    	if (deltaX < NOISE) deltaX = (float)0.0;
	    	if (deltaY < NOISE) deltaY = (float)0.0;
	    	if (deltaZ < NOISE) deltaZ = (float)0.0;
	    	mLastX = x;
	    	mLastY = y;
	    	mLastZ = z;
	    	//Log.d("Shifted", mLastX+" "+mLastY+" "+mLastZ);
    	}

    }


	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}
	
	protected void onResume() {
		super.onResume();
		mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
		this.registerReceiver(the_receiver, filter);
		//IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
	   // registerReceiver(mReceiver, filter);
	}
	
	protected void onPause() {
		super.onPause();
		mSensorManager.unregisterListener(this);
		this.unregisterReceiver(the_receiver);
		//unregisterReceiver(mReceiver);
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode) {
		case REQUEST_ENABLE_BT:
			if (resultCode != RESULT_OK) {
	            Toast.makeText(this, R.string.bluetooth, Toast.LENGTH_SHORT).show();
				finish();
				
			}
		}
	}
	
	protected void onStop() {
		super.onStop();
		mSensorManager.unregisterListener(this);
	}
    
	protected void onDestroy() {
		super.onDestroy();
		//unregisterReceiver(mReceiver);
	}
}
