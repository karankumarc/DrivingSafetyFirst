package com.techpalle.drivingsafetyfirst;

import java.util.ArrayList;
import java.util.Vector;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class Emergency extends Activity {
    ActionBar ab;
    ListView lv;
    ArrayList<String> al;
    ArrayAdapter<String> aa;
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.emergency);
	    ab=getActionBar();
        Drawable d=getResources().getDrawable(R.drawable.shape1);
        ab.setBackgroundDrawable(d);
	    ab.setHomeButtonEnabled(true);
	    ab.setTitle("Emergency");
        lv=(ListView) findViewById(R.id.listView1);
        al=new ArrayList<String>();
        aa=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,al);
        lv.setAdapter(aa);
        al.add("Add Friends and Family contacts");
        al.add("Record a message");
        al.add("Saved Contacts List");
        al.add("Add Vehicles Details");
        aa.notifyDataSetChanged();
        lv.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				if(arg2==0)
				{
					Intent in=new Intent(Emergency.this,AddFriends.class);
					startActivity(in);
				}
				if(arg2==1)
				{
					Intent in=new Intent(Emergency.this,RecordVoiceMessage.class);
					in.putExtra("key",0);
					in.putExtra("location","not available");
					startActivity(in);
				}
				if(arg2==2)
				{
					Intent in=new Intent(Emergency.this,SavedContacts.class);
					startActivity(in);
				}
				if(arg2==3)
				{
					Intent in=new Intent(Emergency.this,AddVehicle.class);
					startActivity(in);
				}
				
				
			}
		});
	}
	

}
