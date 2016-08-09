package com.techpalle.drivingsafetyfirst;


import java.io.IOException;
import java.util.List;
import java.util.Locale;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PowerManager;
import android.provider.Settings;
//import android.speech.tts.TextToSpeech;
//import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class Drive extends Activity 
{
	SharedPreferences sp;
	SharedPreferences.Editor ed;
	MyDatabase mdb;
	Boolean firstTime=true;
	Boolean activated=false;
	String vehicle_no="";
	TextToSpeech speak;
	String str1;
	String text="Welcome to Unsafe Drive Detection Application";
	int straight,left,right,up,down=0;
	
	private static final String TAG = "drive";
	private static final boolean D=true;
	private SensorManager sensorManager;
	private Object sensor;
	private int x, y, z;
	private TextView result;
	private TextView pattern;
    
	private Button bdrive;
    public boolean mQuitting = false;
    private PowerManager.WakeLock wl;
    private  static int drive =0;
    
    LocationManager mlocManager;
	LocationListener mlocListener ;
	
	double c_lat;
	double c_long;
	
	String address;
	
	AlarmManager alarmManager;
	PendingIntent piHeartBeatService;
    ActionBar ab;
   
	    
    
    /** Called when the activity is first created. */
    @SuppressLint("NewApi")
	@Override
    public void onCreate(Bundle savedInstanceState)
    {
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mdb=new MyDatabase(this);
        mdb.open();
        ab=getActionBar();
        Drawable d=getResources().getDrawable(R.drawable.shape1);
        ab.setBackgroundDrawable(d);
	    ab.setHomeButtonEnabled(true);
	    

        speak1(text);  
        get_lat_long_details();
        
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		sensor = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
		
	
        bdrive = (Button)findViewById(R.id.btnstart);
        bdrive.setOnClickListener(new Button.OnClickListener() 
        { public void onClick (View v){ 
        	
        	startdriving();
        	if(activated==false)
        	{
        	speak1("Application Activated");
        	activated=true;
        	}
        	else
        	{
        		activated=false;
            	speak1("Application Deactivated");
        	}
        	}});
        
        result = (TextView) findViewById(R.id.tv1);
		result.setText("No result yet");
		
		pattern = (TextView) findViewById(R.id.tv2);
		
		
		// Getting a WakeLock. This insures that the phone does not sleep
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "My Tag");
        //wl.acquire();
        
       
    }
    
   


    
	private void refreshDisplay() 
    {
		String output = String
				.format("x is: %d / y is: %d / z is: %d", x, y, z);
		result.setText(output);
	}
    
    @Override
    public void onStart() 
    {
        super.onStart();
        if(D) Log.e(TAG, "++ ON START ++");
  
       }
    
    @Override
	protected void onResume() 
    {
		super.onResume();
		wl.acquire();
		sensorManager.registerListener(accelerationListener, (Sensor) sensor,
				SensorManager.SENSOR_MIN);
    }
    @Override
	protected void onStop()
    {
		sensorManager.unregisterListener(accelerationListener);
		super.onStop();
		wl.release();
	}
    @Override
    public void onDestroy() 
    {
    	
    	if (speak != null) {
    		speak.stop();
    		speak.shutdown();
		}

        super.onDestroy();
       
        if(D) Log.e(TAG, "--- ON DESTROY ---");
    }
    
    
    
    
 
    
    
    
    ////////////////////////////////message////////////////////////////////////
    private void sendSMS( String message)
    {        
    	String SENT = "SMS_SENT";
    	String DELIVERED = "SMS_DELIVERED";
    	
        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,
            new Intent(SENT), 0);
        
        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
            new Intent(DELIVERED), 0);
    	
        //---when the SMS has been sent---
        registerReceiver(new BroadcastReceiver()
        {
			@Override
			public void onReceive(Context arg0, Intent arg1) 
			{
				switch (getResultCode())
				{
				    case Activity.RESULT_OK:
					    Toast.makeText(getBaseContext(), "SMS sent", 
					    		Toast.LENGTH_SHORT).show();
					    break;
				    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
					    Toast.makeText(getBaseContext(), "Generic failure", 
					    		Toast.LENGTH_SHORT).show();
					    break;
				    case SmsManager.RESULT_ERROR_NO_SERVICE:
					    Toast.makeText(getBaseContext(), "No service", 
					    		Toast.LENGTH_SHORT).show();
					    break;
				    case SmsManager.RESULT_ERROR_NULL_PDU:
					    Toast.makeText(getBaseContext(), "Null PDU", 
					    		Toast.LENGTH_SHORT).show();
					    break;
				    case SmsManager.RESULT_ERROR_RADIO_OFF:
					    Toast.makeText(getBaseContext(), "Radio off", 
					    		Toast.LENGTH_SHORT).show();
					    break;
				}
			}
        }, new IntentFilter(SENT));
        
        //---when the SMS has been delivered---
        registerReceiver(new BroadcastReceiver()
        {
			@Override
			public void onReceive(Context arg0, Intent arg1) 
			{
				switch (getResultCode())
				{
				    case Activity.RESULT_OK:
					    Toast.makeText(getBaseContext(), "SMS delivered", 
					    		Toast.LENGTH_SHORT).show();
					    startdriving();
					    break;
				    case Activity.RESULT_CANCELED:
					    Toast.makeText(getBaseContext(), "SMS not delivered", 
					    		Toast.LENGTH_SHORT).show();
					    break;					    
				}
			}
        }, new IntentFilter(DELIVERED));    
		sensorManager.unregisterListener(accelerationListener);

    	Cursor c=mdb.getAllContacts();
        speak1("Car Drifting. Activating Emergency Mode");
    	if(c!=null)
    	{
    		while(c.moveToNext())
    		{
    			String num=c.getString(2);
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(num, null,message+address, sentPI, deliveredPI);
        pattern.setText(address);
    		}
    		Intent in=new Intent(Drive.this,RecordVoiceMessage.class);
    		in.putExtra("key", 1);
    		in.putExtra("location",message+address);
    		startActivity(in);
    		
    	}
    }    
    
    /////////////////////gps on//////////////////////////////
    private void turnGPSOn()	
	{   

	    String provider = Settings.Secure.getString(getContentResolver(), 
	    		Settings.Secure.LOCATION_PROVIDERS_ALLOWED);   
	    if(!provider.contains("gps"))
	    {      
	        final Intent poke = new Intent();  
	        poke.setClassName("com.android.settings","com.android.settings.widget.SettingsAppWidgetProvider");        
	        poke.addCategory(Intent.CATEGORY_ALTERNATIVE);   
	        poke.setData(Uri.parse("3"));      
	        sendBroadcast(poke);  
	     }
	   }    
    //////////////////////////GPSOFF/////////////////////////ZS
	private void turnGPSOFF()
	{
		String provider = Settings.Secure.getString(getContentResolver(), 
	    		Settings.Secure.LOCATION_PROVIDERS_ALLOWED);   
	    if(provider.contains("gps"))
	    {      
	        final Intent poke = new Intent();  
	        poke.setClassName("com.android.settings","com.android.settings.widget.SettingsAppWidgetProvider");        
	        poke.addCategory(Intent.CATEGORY_ALTERNATIVE);   
	        poke.setData(Uri.parse("3"));      
	        sendBroadcast(poke);  
	     }
		
	}
	
	
	  

    //////////////////////////accelerometer////////////////////////////////////
    public void startdriving()
	{
    	     	
		if(drive==0)
		{
			turnGPSOn();
			drive = 1;
			
		}
		else
		{
			
			turnGPSOFF();
			drive=0;
		}
	}
    
    
    private SensorEventListener accelerationListener = new SensorEventListener()
	{
		public void onAccuracyChanged(Sensor sensor, int acc)
		{
		}
 
		public void onSensorChanged(SensorEvent event)
		{
		
			x = (int) event.values[0];
            y = (int) event.values[1];
			z = (int) event.values[2];
			
			if(drive==1)
			{
				
				sp=getSharedPreferences("vehicle",MODE_PRIVATE);
			    vehicle_no=sp.getString("vno","not available");
				str1= "Message From Unsafe Driving Detector :\nVehicle No: "+vehicle_no+"\n"+"Location: ";
                String message = str1;
                
                if(y<=-8||y>=8 || x<=-8 || x>=8)
  			  {
  				result.setText("Car Drifting.");		     
  						   sendSMS(message);
  						   drive=0;		   
  			  }

             
                else if(x>1&&y<2&&z>=8)
		      {
		    	  if(left==0)
		    	  {
                 speak1("changing left lane");
                 left=1;
                 right=0;
                 straight=0;
                 up=0;
                 down=0;
		    	  }
		      }
		      else if (x<-1&&y<2&&z>=8)
		      {
		    	  if(right==0)
		    	  {
                 speak1("changing right lane");
                 right=1;
                 left=0;
                 straight=0;
                 up=0;
                 down=0;
		    	  }

			  }
		      else if(x==0&&y==0&&z==9)
		      {
		    	  if(straight==0)
		    	  {
		    		  speak1("moving straight");
		    		  straight=1;
		                 right=0;
		                 left=0;
		                 up=0;
		                 down=0;
		    	  }
		      }
		      else if(y>2&&x==0&&z>6)
		      {
		    	  if(up==0)
		    	  {
		    		  speak1("Going up");
		    		  straight=0;
		                 right=0;
		                 left=0;
		                 down=0;
		                 up=1;
		    	  }
		      }
		      else if(y<-1&&x==0&&z>6)
		      {
		    	  if(down==0)
		    	  {
		    		  speak1("Going down");
		    		  straight=0;
		                 right=0;
		                 left=0;
		                 up=0;
		                 down=1;
		    	  }
		      }
			
			refreshDisplay();
		    }
	  }

	
	};
	///////////////GPS///////////////////////////////////////////////////////
	  public void onProviderDisabled(String provider) 
	    {
	    	//Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			//startActivity(intent);

	    }
	  
		public void onProviderEnabled(String provider) 
		{
			// TODO Auto-generated method stub
			
			Toast.makeText(this, "GPS Enabled", Toast.LENGTH_SHORT).show();

			
		}
	  

	
	

		public void onStatusChanged(String provider, int status, Bundle arg2)
		{
			// TODO Auto-generated method stub
			
			
			switch (status)
			{
			case LocationProvider.OUT_OF_SERVICE:
				
				Toast.makeText(this, "Status Changed: Out of Service",
						Toast.LENGTH_SHORT).show();
				break;
			case LocationProvider.TEMPORARILY_UNAVAILABLE:
				
				Toast.makeText(this, "Status Changed: Temporarily Unavailable",
						Toast.LENGTH_SHORT).show();
				break;
			case LocationProvider.AVAILABLE:
				
				Toast.makeText(this, "Status Changed: Available",
						Toast.LENGTH_SHORT).show();
				break;
			}

			
		}
	// Menu option for Quit .....///////////////////////////////////////////////
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater mi=getMenuInflater();
		mi.inflate(R.menu.optionmenu, menu);
		return super.onCreateOptionsMenu(menu);
	}
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	// TODO Auto-generated method stub
    	switch (item.getItemId()) {
		case android.R.id.home:
			
			break;
        case R.id.item2:
			Intent in=new Intent(Drive.this,Emergency.class);
			startActivity(in);
			break;
        case R.id.item3:
	        break;
        case R.id.item4:
	        break;
        case R.id.item5:
	        finish();
	        break;

		default:
			break;
		}
    	return super.onOptionsItemSelected(item);
    }
    private void get_lat_long_details() {
    	
    	turnGPSOn();
    	
   	 mlocManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		   

		  mlocListener = new MyLocationListener();


   	mlocManager.requestLocationUpdates( LocationManager.NETWORK_PROVIDER, 0, 0, mlocListener);
   	mlocManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0, 0, mlocListener);

    }
	 
	 
	 /* Class My Location Listener */

		public class MyLocationListener implements LocationListener

		{

		public void onLocationChanged(Location loc)

		{
			
			

		loc.getLatitude();

		loc.getLongitude();
		
		
		

		
		
		
		c_lat=loc.getLatitude() ;

		c_long=loc.getLongitude();
		getAddress();
		
		
		 
			
		mlocManager.removeUpdates(mlocListener);
		
		pattern.setText(address);
		
			
			
			
		}

		


		public void onProviderDisabled(String provider)

		{

		Toast.makeText( getApplicationContext(),

		"Gps Disabled",

		Toast.LENGTH_SHORT ).show();

		}


		public void onProviderEnabled(String provider)

		{

		Toast.makeText( getApplicationContext(),

		"Gps Enabled",

		Toast.LENGTH_SHORT).show();

		}


		public void onStatusChanged(String provider, int status, Bundle arg2)
		{
			// TODO Auto-generated method stub
			
			
			switch (status)
			{
			case LocationProvider.OUT_OF_SERVICE:
				
				//Toast.makeText(this, "Status Changed: Out of Service",
				//		Toast.LENGTH_SHORT).show();
				break;
			case LocationProvider.TEMPORARILY_UNAVAILABLE:
				
			//	Toast.makeText(this, "Status Changed: Temporarily Unavailable",
				//		Toast.LENGTH_SHORT).show();
				break;
			case LocationProvider.AVAILABLE:
				
			//	Toast.makeText(this, "Status Changed: Available",
				//		Toast.LENGTH_SHORT).show();
				break;
			}

			
		}
		}/* End of Class MyLocationListener */
		
		void getAddress(){
	        try{
				//Toast.makeText(Drive.this, "latitude:"+c_lat+" longitude : "+c_long, 1).show();
	            Geocoder gcd = new Geocoder(this, Locale.getDefault());
	            List<Address> addresses = 
	                gcd.getFromLocation(c_lat, c_long,1);
	            if (addresses.size() > 0) {
	                StringBuilder result = new StringBuilder();
	                for(int i = 0; i < addresses.size(); i++){
	                    Address address =  addresses.get(i);
	                    int maxIndex = address.getMaxAddressLineIndex();
	                    for (int x = 0; x <= maxIndex; x++ ){
	                        result.append(address.getAddressLine(x));
	                        result.append(",");
	                    }               
	                  
	                }
	                address=result.toString();
	               // tv_get_address.setText(result.toString());
	            }
	        }
	        catch(IOException ex){
	        	//tv_get_address.setText(ex.getMessage().toString());
	        }
	    }
		
		 public void speak1(final String text){
		    	
		   	 speak = new TextToSpeech(this,
		                new TextToSpeech.OnInitListener() {

		                   
		                    public void onInit(int status) {

		                        if (status != TextToSpeech.ERROR) {

		                            speak.setLanguage(Locale.US);
		                          
		                        }
		                        speak.speak(text, TextToSpeech.QUEUE_ADD, null);
		                    }
		                });
		   }
		    
}