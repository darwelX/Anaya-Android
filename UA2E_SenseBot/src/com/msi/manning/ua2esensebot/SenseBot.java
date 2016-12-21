/*
 * 
 * UA2E_SenseBot application
 * written for Unlocking Android, Second Edition
 * http://manning.com/ableson2
 * Author: Frank Ableson
 * 
 * Use at own risk.
 * Note that this application could possibly damage the motors in your Lego NXT Robot.
 * Please use caution.
 * Don't use while driving a vehicle or any other dangerous activity.
 * Use your common sense.  If you don't have any, borrow some.
 * 
 */

package com.msi.manning.ua2esensebot;


import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.util.Log;


import java.io.*;

import android.hardware.SensorManager;
import android.hardware.SensorEventListener;
import android.hardware.SensorEvent;
import android.hardware.Sensor;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.util.Iterator;
import java.util.Set;


public class SenseBot extends Activity implements SensorEventListener{
	 
	
	final String tag = "SenseBot";
	final String ROBOTNAME = "8aPi";
	
	// BT Variables
	private BluetoothAdapter btInterface;
	private Set<BluetoothDevice> pairedDevices;
	private BluetoothSocket socket;
	private InputStream is = null;
	private OutputStream os = null;
	private boolean bConnected = false;
	// End BT Variables
	
	
	// Navigation Variables
	private int yCenter = 25;
	private int xCenter = 0;
	private int xSensitivity = 40;
	private int ySensitivity = 40;
	final int MOTOR_B = 1;
	final int MOTOR_C = 2;
	int motorPower = 50;
	final int MOTOR_B_FORWARD = 1;
	final int MOTOR_B_BACKWARD = 2;
	final int MOTOR_B_STOP = 4;
	final int MOTOR_C_FORWARD = 8;
	final int MOTOR_C_BACKWARD = 16;
	final int MOTOR_C_STOP = 32;
	private int movementMask = MOTOR_B_STOP + MOTOR_C_STOP;
	// End Navigation Variables
	
	private Button btnConnect = null;
	private Button btnDisconnect = null;
	private TextView readings = null;
	private View motorB = null;
	private View motorC = null;

	// sensor manager
	private SensorManager sManager = null;
	// broadcast receiver to handle bt events
	private BroadcastReceiver btMonitor = null;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.main);
        setupUI();
        setupBTMonitor(); 
        
        sManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    }  
        @Override
    public void onResume()
    {
    	super.onResume();
    	Log.i(tag,"onResume");
    	
    	registerReceiver(btMonitor,new IntentFilter("android.bluetooth.device.action.ACL_CONNECTED"));
    	registerReceiver(btMonitor,new IntentFilter("android.bluetooth.device.action.ACL_DISCONNECTED"));
    }
    @Override
    public void onPause()
    {
    	super.onPause();
    	Log.i(tag,"onPause");
    	unregisterReceiver(btMonitor);
    }
    @Override
    public void onStop() {
    	super.onStop();
    	if (sManager != null) {
    		sManager.unregisterListener(this);
    	}
    }
    
    private void setupUI() {
        readings = (TextView) findViewById(R.id.readings);
        motorB = (View) findViewById(R.id.motorB);
        motorC = (View) findViewById(R.id.motorC);
        btnConnect = (Button) findViewById(R.id.connect);
        btnDisconnect = (Button) findViewById(R.id.disconnect);
        btnDisconnect.setVisibility(View.GONE); 
    }
    
    private void setupBTMonitor() {
        btMonitor = new BroadcastReceiver() {
        	@Override 
        	public void onReceive(Context context,Intent intent) {
        		if (intent.getAction().equals("android.bluetooth.device.action.ACL_CONNECTED")) {
        			handleConnected();
        		}
        		if (intent.getAction().equals("android.bluetooth.device.action.ACL_DISCONNECTED")) {
        			handleDisconnected();
        		}		
        		
        	}
        };

    }
    private void handleConnected() {
		try {
			is = socket.getInputStream();
			os = socket.getOutputStream();
	        if (sManager != null) {
	        	sManager.registerListener(SenseBot.this,sManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_UI);
	        }
			bConnected = true;
			btnConnect.setVisibility(View.GONE);
			btnDisconnect.setVisibility(View.VISIBLE);


		} catch (Exception e) {
			is = null;
			os = null;
			disconnectFromRobot(null);
		}
		
		movementMask = MOTOR_B_STOP + MOTOR_C_STOP;
		motorB.setBackgroundResource(R.drawable.stop);
		motorC.setBackgroundResource(R.drawable.stop);
		updateMotors();
    	
    }
    private void handleDisconnected() {
		bConnected = false;
		btnConnect.setVisibility(View.VISIBLE);
		btnDisconnect.setVisibility(View.GONE);
		motorB.setBackgroundDrawable(null);
		motorC.setBackgroundDrawable(null);
		if (sManager != null) {
			sManager.unregisterListener(SenseBot.this);
		}
		readings.setText("Sensors Disabled.  Please connect to Robot to resume.");
    }
    
    public void onSensorChanged(SensorEvent event) {
    	try {
    		
    		if (bConnected == false) return;
    		
    	
	    	StringBuilder sb = new StringBuilder();
	    	sb.append("[" + event.values[0] + "]");
	    	sb.append("[" + event.values[1] + "]");
	    	sb.append("[" + event.values[2] + "]");

	    	readings.setText(sb.toString());
	    	// process this sensor data
	    	movementMask = MOTOR_B_STOP + MOTOR_C_STOP;
	    	
	      	if (event.values[2] < (yCenter - ySensitivity)) {
	    		movementMask = MOTOR_B_FORWARD + MOTOR_C_FORWARD;
	    		motorPower = 75;
	    	} else if (event.values[2] > (yCenter + ySensitivity)) {
	    		movementMask = MOTOR_B_BACKWARD + MOTOR_C_BACKWARD;
	    		motorPower = 75;
	    	} else if (event.values[1] >(xCenter + xSensitivity)) {
	    		movementMask = MOTOR_B_BACKWARD + MOTOR_C_FORWARD;
	    		motorPower = 50;
	    	} else if (event.values[1] < (xCenter - xSensitivity)) {
	    		movementMask = MOTOR_B_FORWARD + MOTOR_C_BACKWARD;
	    		motorPower = 50;
	    	} 
	      	updateMotors();
    	} catch (Exception e) {
    		Log.e(tag,"onSensorChanged Error::" + e.getMessage());
    	}
    }
    public void onAccuracyChanged(Sensor s, int accuracy) {
    }
    private void updateMotors() {
    	try {
    		if ((movementMask & MOTOR_B_FORWARD) == MOTOR_B_FORWARD) {
    			motorB.setBackgroundResource(R.drawable.uparrow);
    			MoveMotor(MOTOR_B,motorPower);
    		} else if ((movementMask & MOTOR_B_BACKWARD) == MOTOR_B_BACKWARD) {
    			motorB.setBackgroundResource(R.drawable.downarrow);
    			MoveMotor(MOTOR_B,-motorPower);
    		} else {
    			motorB.setBackgroundResource(R.drawable.stop);
    			MoveMotor(MOTOR_B,0);
    		}
    		
    		if ((movementMask & MOTOR_C_FORWARD) == MOTOR_C_FORWARD) {
    			motorC.setBackgroundResource(R.drawable.uparrow);
    			MoveMotor(MOTOR_C,motorPower);
    		} else if ((movementMask & MOTOR_C_BACKWARD) == MOTOR_C_BACKWARD) {
    			motorC.setBackgroundResource(R.drawable.downarrow);
    			MoveMotor(MOTOR_C,-motorPower);
    		} else {
    			motorC.setBackgroundResource(R.drawable.stop);
    			MoveMotor(MOTOR_C,0);
    		}

    	} catch (Exception e) {
    		Log.e(tag,"updateMotors error::" + e.getMessage());
    	}
    }
      
	public void findRobot(View v)
	{
		try
		{
    		btInterface = BluetoothAdapter.getDefaultAdapter();
    		Log.i(tag,"Local BT Interface name is [" + btInterface.getName() + "]");
    		pairedDevices = btInterface.getBondedDevices();
    		Log.i(tag,"Found [" + pairedDevices.size() + "] devices.");
    		Iterator<BluetoothDevice> it = pairedDevices.iterator();
    		while (it.hasNext())
    		{
    			BluetoothDevice bd = it.next();
    			Log.i(tag,"Name of peer is [" + bd.getName() + "]");
    			if (bd.getName().equalsIgnoreCase(ROBOTNAME)) {
    				Log.i(tag,"Found Robot!");
    				Log.i(tag,bd.getAddress());
    				Log.i(tag,bd.getBluetoothClass().toString());
    				connectToRobot(bd);
    				return;
    			}
    		}
		} 
		catch (Exception e)
		{
			Log.e(tag,"Failed in findRobot() " + e.getMessage());
		}
	}

    
    private void connectToRobot(BluetoothDevice bd)
	{
		try
		{
			socket = bd.createRfcommSocketToServiceRecord(java.util.UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
			socket.connect();
		}
		catch (Exception e)
		{
			Log.e(tag,"Error interacting with remote device [" + e.getMessage() + "]"); 
		}
	}
	
	public void disconnectFromRobot(View v)
	{
		try
		{
			Log.i(tag,"Attempting to break BT connection");
			socket.close();
		}
		catch (Exception e)
		{
			Log.e(tag,"Error in DoDisconnect [" + e.getMessage() + "]");
		}
	}
    
    
    

	private void MoveMotor(int motor,int speed)
	{
		try
		{
			Log.i(tag,"MoveMotor");
			Log.i(tag,"Attempting to move [" + motor + "[]" + speed + "]");
			
			byte[] buffer = new byte[14];
			
			buffer[0] = (byte) (14-2);			//length lsb
			buffer[1] = 0;						// length msb
			buffer[2] =  0;						// direct command (with response)
			buffer[3] = 0x04;					// set output state
			buffer[4] = (byte) motor;			// output 1 (motor B)
			buffer[5] = (byte) speed;			// power
			buffer[6] = 1 + 2;					// motor on + brake between PWM
			buffer[7] = 0;						// regulation
			buffer[8] = 0;						// turn ration??
			buffer[9] = 0x20;					// run state
			buffer[10] = 0;
			buffer[11] = 0;
			buffer[12] = 0;
			buffer[13] = 0;

			os.write(buffer);
			os.flush();
			byte response [] = ReadResponse(4);
			if (response == null) {
				Log.e(tag,"No response??");
			} else {
				for (int i=0;i<response.length;i++) {
					Log.i(tag,"Byte[" + i + "][" + response[i] + "]");
				}
			}
			
		}
		catch (Exception e)
		{
			Log.e(tag,"Error in MoveForward(" + e.getMessage() + ")");
		}		
	}

	
	private byte [] ReadResponse(int expectedCommand)
	{
		try {
			
			// attempt to read two bytes
			int attempts = 0;
			int bytesReady = 0;
			byte [] sizeBuffer = new byte[2];
			while (attempts < 5) {
				bytesReady = is.available();
				if (bytesReady == 0) {
					attempts++;
					Thread.sleep(50);
					Log.i(tag,"Nothing there, let's try again");
				} else {
					Log.i(tag,"There are [" + bytesReady + "] waiting for us!");
					break;
				}
			}
			if (bytesReady < 2) { 
				return null;
			}
			int bytesRead = is.read(sizeBuffer,0,2);
			if (bytesRead != 2) {
				return null;
			}
			// calculate response size
			bytesReady = 0;
			bytesReady = sizeBuffer[0] + (sizeBuffer[1] << 8);
			Log.i(tag,"Bytes to read is [" + bytesReady + "]");
			byte [] retBuf = new byte[bytesReady];
			bytesRead = is.read(retBuf);
			if (bytesReady != bytesRead) {
				Log.e(tag,"Unexpected data returned!?");
				return null;
			}
			if (retBuf[1] != expectedCommand) {
				Log.e(tag,"This was an unexpected response");
				return null;
			}
			return retBuf;
		} catch (Exception e) {
			Log.e(tag,"Error in Read Response [" + e.getMessage() + "]");
			return null;
		}
	}
	
    
	 @Override
	    public boolean onCreateOptionsMenu(Menu menu) {
	        super.onCreateOptionsMenu(menu);

	        // add out menu options
	        menu.add(0, 0, 0, "About SenseBot");
	        return true; 
	    }

	    @Override
	    public boolean onOptionsItemSelected(MenuItem item) {

	        switch (item.getItemId()) {
	            case 0:
	            	Intent aboutIntent = new Intent(this,AboutSenseBot.class);
	            	startActivity(aboutIntent);
	            	return true;
	        }
	        return false;
	    }
    
}