package com.techpalle.drivingsafetyfirst;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddVehicle extends Activity {
	Button b;
	EditText et;
	SharedPreferences sp;
	SharedPreferences.Editor ed;

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.addvechicle);
	    et=(EditText) findViewById(R.id.editText1);
	    b=(Button) findViewById(R.id.button1);
	    sp=getSharedPreferences("vehicle",MODE_PRIVATE);
	    ed=sp.edit();
	    b.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ed.putString("vno", et.getText().toString());
				ed.commit();
				Toast.makeText(getApplicationContext(),"Saved Successfully",0).show();
				finish();
				
			}
		});
	
	}

}
