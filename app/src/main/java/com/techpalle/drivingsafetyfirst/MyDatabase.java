package com.techpalle.drivingsafetyfirst;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;


public class MyDatabase 
{
private MyHelper mh;
private SQLiteDatabase sdb;

public MyDatabase(Context con)
{
	mh=new MyHelper(con,"ContactDetails",null,1);
}
public void open()
{   try{
	sdb=mh.getWritableDatabase();
}
catch(Exception e)
{
	
}
}
public void insertContacts(String name,String mob,String mail) 
{
	ContentValues cv=new ContentValues();
	cv.put("name",name);
	cv.put("mob",mob);
	cv.put("email",mail);
	
    sdb.insert("contacts", null,cv);
}

public Cursor getAllContacts()
{   Cursor c=sdb.query("contacts",null,null,null,null,null,null);
	return c;
	
}


public void deleteContacts(String name) {
	sdb.delete("contacts","name=?",new String[]{name});

}
class MyHelper extends SQLiteOpenHelper
{ 
	

	public MyHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("Create table contacts(_id integer primary key," +
				" name text,mob text,email text);");
		
		
		
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
	
}
public void close(){
	sdb.close();
}
}
