package com.techpalle.drivingsafetyfirst;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class RecordVoiceMessage extends Activity {
	ProgressDialog progress_dialog;
	TextToSpeech speak;
	int emergency=0;
	String location="not available";
	ActionBar ab;
	MyDatabase mdb;
    String[]  toArr;

    private static MediaRecorder mediaRecorder;
 	private static MediaPlayer mediaPlayer;
 	private static String audioFilePath;
 	private static ImageButton stopButton;
 	private static ImageButton playButton;
 	private static ImageButton recordButton;
 	
 	private boolean isRecording = false;
 	Mail m;
 	ArrayList<String> emaillist;
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.record);
	    speak1("Record voice message");
	    ab=getActionBar();
	    ab.setHomeButtonEnabled(true);
	    ab.setTitle("Record a Voice Message");
	    mdb=new MyDatabase(this);
	    mdb.open();
	    emaillist=new ArrayList<String>();
	    Intent in=getIntent();
	    Bundle b=in.getExtras();
	    emergency=b.getInt("key");
	    location=b.getString("location");
	    //Toast.makeText(getApplicationContext(),location, 1).show();
        Drawable d=getResources().getDrawable(R.drawable.shape1);
        ab.setBackgroundDrawable(d);
	    recordButton=(ImageButton) findViewById(R.id.imageView2);
	    playButton=(ImageButton) findViewById(R.id.imageView3);
	    stopButton=(ImageButton) findViewById(R.id.imageView4);
	    if (!hasMicrophone())
		{
			stopButton.setEnabled(false);
			playButton.setEnabled(false);
			recordButton.setEnabled(false);
		} else {
			playButton.setEnabled(false);
			stopButton.setEnabled(false);
		}
	    
	    
	    recordButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					recordAudio();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		});
      stopButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				stopClicked();
				
			}
		});
      playButton.setOnClickListener(new OnClickListener() {
	
	public void onClick(View v) {
		// TODO Auto-generated method stub

   try {
	playAudio();
} catch (IOException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}
		
		
	}
});
		
		audioFilePath = 
             Environment.getExternalStorageDirectory().getAbsolutePath() 
                 + "/myaudio.3gp";	
		
		if(emergency==1)
	    {
	    	try {
				recordAudio();
				stopButton.setEnabled(false);
				playButton.setEnabled(false);
				recordButton.setEnabled(false);				new MyTask().execute();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	
	}
	protected boolean hasMicrophone() {
		PackageManager pmanager = this.getPackageManager();
		return pmanager.hasSystemFeature(
               PackageManager.FEATURE_MICROPHONE);
	}
	public void recordAudio () throws IOException
	{
	   isRecording = true;
	   stopButton.setEnabled(true);
	   playButton.setEnabled(false);
	   recordButton.setEnabled(false);
		   
	   try {
	     mediaRecorder = new MediaRecorder();
	     mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
	     mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
	     mediaRecorder.setOutputFile(audioFilePath);
	     mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
	     mediaRecorder.prepare();
	   } catch (Exception e) {
		   e.printStackTrace();
	   }

	   mediaRecorder.start();			
	}
	
	public void stopClicked ()
	{
			
		stopButton.setEnabled(false);
		playButton.setEnabled(true);
			
		if (isRecording)
		{	
			recordButton.setEnabled(false);
			mediaRecorder.stop();
			mediaRecorder.release();
			mediaRecorder = null;
			isRecording = false;
		} else {
			mediaPlayer.release();
		        mediaPlayer = null;
			recordButton.setEnabled(true);
		}
	}
	
	public void playAudio () throws IOException
	{
		playButton.setEnabled(false);
		recordButton.setEnabled(false);
		stopButton.setEnabled(true);

		mediaPlayer = new MediaPlayer();
		mediaPlayer.setDataSource(audioFilePath);
		mediaPlayer.prepare();
		mediaPlayer.start();
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
	
	class MyTask extends AsyncTask<Void, Void, Void>
	{
        @Override
        protected void onPreExecute() {
        	// TODO Auto-generated method stub
        	progress_dialog= ProgressDialog.show(RecordVoiceMessage.this,
                    "Please be patient",
                    "Recording Voice message for sending mail..");
            progress_dialog.setCancelable(true);

            progress_dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                public void onCancel(DialogInterface dialog) {
                    
                    


                }

            });
        	super.onPreExecute();
        }
		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try {
				Thread.sleep(15000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			int size=0;
			stopClicked();
			m = new Mail("saleelkhan99@gmail.com","9704406292");
			Cursor c=mdb.getAllContacts();
			if(c!=null)
			{
				while(c.moveToNext())
				{
					emaillist.add(c.getString(3));
					size++;
				}
			}
            toArr =new String[size];
            for (int j = 0; j < size; j++) 
            {
				toArr[j]=emaillist.get(j);
			}
            m.setTo(toArr);
            m.setFrom("saleelkhan99@gmail.com");
            m.setSubject("Message From Unsafe Driving Detector");
            m.setBody(location);

            
            try {


                m.addAttachment(audioFilePath);//this code is for attaching the any file from your devices
                if (m.send()) 
                {
                	progress_dialog.dismiss();
                	speak1("Email Sent Successfully");
                    Toast.makeText(getApplicationContext(),"Email was sent successfully.", Toast.LENGTH_LONG).show();
                    
                } 
                else 
                {
                	progress_dialog.dismiss();
                	Toast.makeText(getApplication(),"Email was not sent.", Toast.LENGTH_LONG).show();

                }
            } 
            catch (Exception e) 
            {
            	progress_dialog.dismiss();
                Toast.makeText(getApplication(),"There was a problem sending the email.", Toast.LENGTH_LONG).show();

            }
			
			super.onPostExecute(result);
		}
	}

}
