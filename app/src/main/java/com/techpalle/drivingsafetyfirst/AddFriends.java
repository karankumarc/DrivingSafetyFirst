package com.techpalle.drivingsafetyfirst;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class AddFriends extends Activity {
	EditText et1,et2,et3;
	Button b;
    MyDatabase mdb;
    ActionBar ab;
	/** Called when the activity is first created. */
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
        setContentView(R.layout.addfriends);
        et1=(EditText) findViewById(R.id.editText1);
	    et2=(EditText) findViewById(R.id.editText2);
	    et3=(EditText) findViewById(R.id.editText3);
	    b=(Button) findViewById(R.id.button1);
	    ab=getActionBar();
	    ab.setHomeButtonEnabled(true);
	    ab.setTitle("Add Friends");
        Drawable d=getResources().getDrawable(R.drawable.shape1);
        ab.setBackgroundDrawable(d);
        mdb=new MyDatabase(this);
        mdb.open();
	    b.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String name=et1.getText().toString();
				String mob=et2.getText().toString();
				String mail=et3.getText().toString();
				et1.setText("");
				et2.setText("");
				et3.setText("");

				et1.requestFocus();
				mdb.insertContacts(name, mob, mail);
			}
		});

	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		mdb.close();
		super.onDestroy();
	}

}
