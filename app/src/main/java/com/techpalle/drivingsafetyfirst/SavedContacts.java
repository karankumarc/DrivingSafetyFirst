package com.techpalle.drivingsafetyfirst;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

public class SavedContacts extends Activity {
	ActionBar ab;
	ListView lv;
    ArrayList<String> al1,al2,al3;
    MyAdapter ma;
    Cursor c1;
    MyDatabase mdb;
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.saveddata);
	    ab=getActionBar();
        Drawable d=getResources().getDrawable(R.drawable.shape1);
        ab.setBackgroundDrawable(d);
	    ab.setHomeButtonEnabled(true);
	    ab.setTitle("Saved Contacts List");

        lv=(ListView) findViewById(R.id.listView1);
        al1=new ArrayList<String>();
        al2=new ArrayList<String>();
        al3=new ArrayList<String>();

  
        ma=new MyAdapter();
        lv.setAdapter(ma);
        registerForContextMenu(lv);
        mdb=new MyDatabase(this);
        mdb.open();
        c1=mdb.getAllContacts();
        while(c1.moveToNext())
		{
		al1.add(c1.getString(1));
		al2.add(c1.getString(2));
		al3.add(c1.getString(3));
		
		}
		ma.notifyDataSetChanged();
        
	}
	
	class MyAdapter extends BaseAdapter
	{

		public int getCount() {
			// TODO Auto-generated method stub
			return al1.size();
		}

		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return al1.get(arg0);
		}

		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		public View getView(int arg0, View arg1, ViewGroup arg2) {
			// TODO Auto-generated method stub
			LinearLayout rl=(LinearLayout) getLayoutInflater().inflate(R.layout.customlist,arg2, false);
			TextView t1=(TextView) rl.findViewById(R.id.textView1);
			TextView t2=(TextView) rl.findViewById(R.id.textView2);
			TextView t3=(TextView) rl.findViewById(R.id.textView3);

			t1.setText(al1.get(arg0));
			t2.setText(al2.get(arg0));
			t3.setText(al3.get(arg0));
			return rl;
		}
		
	}
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		MenuInflater mi=getMenuInflater();
		mi.inflate(R.menu.contextmenu, menu);
		super.onCreateContextMenu(menu, v, menuInfo);
	}
	
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		int pos = ((AdapterContextMenuInfo)item.getMenuInfo()).position;
		switch (item.getItemId()) {
		case R.id.item1:
			Cursor c2=mdb.getAllContacts();
			c2.moveToPosition(pos);
			String name=c2.getString(1);
			mdb.deleteContacts(name);
			al1.remove(pos);
			al2.remove(pos);
			ma.notifyDataSetChanged();
			c2.close();
		default:
			break;
		}
		return super.onContextItemSelected(item);
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		c1.close();
		mdb.close();
		super.onDestroy();
	}

}
