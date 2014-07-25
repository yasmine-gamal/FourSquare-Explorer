package waslny.task.dao;


import java.util.ArrayList;

import br.com.condesales.models.Category;
import br.com.condesales.models.Location;
import br.com.condesales.models.Venue;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SqlHelper extends SQLiteOpenHelper {

	static final String dbName = "MyDB";
	static final String venues = "venues";
	static final String venueID = "venueId";
	static final String venueName = "venueName";
	static final String venueLng = "venueLng";
	static final String venueLtd = "venueLtd";
	static final String venueAddress = "venueAddress";
	static final String venueCategory = "venueCategory";

	public SqlHelper(Context context) 
	{
		// TODO Auto-generated constructor stub
		super(context, dbName, null, 1);
		//SQLiteDatabase database = getWritableDatabase();
	}

	public void dropDB(SQLiteDatabase db) 
	{
		db.execSQL("DROP TABLE IF EXISTS " + venues);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db)
	{
		// TODO Auto-generated method stub
		db.execSQL("CREATE TABLE IF NOT EXISTS " + venues + " (" + venueID
				+ " TEXT NOT NULL, " + venueName + " TEXT, "
				+ venueLng + " TEXT," + venueLtd + " TEXT, " 
				+ venueAddress + " TEXT, " 
				+ venueCategory + " TEXT, "
				+ "PRIMARY KEY(" + venueID +")" + ")");
		System.out.println("DATABASE CREATED");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
	{
		// TODO Auto-generated method stub
		System.out.println("Updateeee done ");
		if (newVersion > oldVersion) 
		{
			db.execSQL("DROP TABLE IF EXISTS " + venues);
			onCreate(db);
		}
	}
	
	public void cacheNewVenues(ArrayList<Venue> venueObjs)
	{
		SQLiteDatabase myDB = this.getWritableDatabase();
		System.out.println("VERSION NUMBER= " + myDB.getVersion());
		for(Venue venueObj : venueObjs)
		{
			ContentValues cv = new ContentValues();
			cv.put(venueID, venueObj.getId());
			cv.put(venueName,venueObj.getName());
			cv.put(venueLng, venueObj.getLocation().getLng());
			cv.put(venueLtd, venueObj.getLocation().getLat());
			cv.put(venueAddress, venueObj.getLocation().getAddress());
			cv.put(venueCategory, venueObj.getCategories().get(0).getName());
			myDB.insert(venues, venueID, cv);
		}
		myDB.close();
	}
	
	public void deleteCachedVenues()
	{
		SQLiteDatabase myDB = this.getWritableDatabase();
		myDB.delete(venues, null, null);
		myDB.close();
	}
	
	public ArrayList<Venue> getCachedVenues()
	{
		ArrayList<Venue> venueObjs=new ArrayList<Venue>();
		Venue venueObj  = new Venue();
		SQLiteDatabase myDB = this.getReadableDatabase();
		Cursor myCursor = myDB.query(venues, null, null, null, null, null, null);
		if (myCursor.getCount() == 0)
		{
			venueObj.setName("empty");
			venueObjs.add(venueObj);
		}
		
		else {
			myCursor.moveToFirst();
			while(!myCursor.isAfterLast())
			{
				int nameIndex = myCursor.getColumnIndex(venueName);
				int lngIndex = myCursor.getColumnIndex(venueLng);
				int ltdIndex = myCursor.getColumnIndex(venueLtd);
				int catIndex = myCursor.getColumnIndex(venueCategory);
				System.out.println("inddexx== " + nameIndex);
				ArrayList<Category> categories = new ArrayList<Category>();
				Category cat = new Category();
				cat.setName(myCursor.getString(catIndex));
				categories.add(cat);
				Location loc=new Location();
				loc.setLat(Double.parseDouble(myCursor.getString(ltdIndex)));
				loc.setLng(Double.parseDouble(myCursor.getString(lngIndex)));
				venueObj.setName(myCursor.getString(nameIndex));
				venueObj.setLocation(loc);
				venueObj.setCategories(categories);
				myCursor.moveToNext();
				venueObjs.add(venueObj);
			}
		}

		myCursor.close();
		myDB.close();
		return venueObjs;
	}

	
	public Venue getVenueByName(String venue_name)
	{
		SQLiteDatabase myDB = this.getReadableDatabase();
		String venue_id = "";
		Venue venueObj = new Venue();
		Cursor myCursor = myDB.rawQuery("SELECT " + venueID + "," + venueLtd 
				+ "," + venueLng + "," + venueAddress + " FROM "
				+ venues + " WHERE " + venueName + "=?", new String[]{venue_name});
		
		if (myCursor.getCount() == 0)
			venue_id = "empty";
		else 
		{
			myCursor.moveToFirst();
			int idIndex = myCursor.getColumnIndex(venueID);
			int lngIndex = myCursor.getColumnIndex(venueLng);
			int ltdIndex = myCursor.getColumnIndex(venueLtd);
			int addressIndex = myCursor.getColumnIndex(venueAddress);
			System.out.println("inddexx== " + idIndex);
			venue_id = myCursor.getString(idIndex);
			Location loc = new Location();
			loc.setLat(Double.parseDouble(myCursor.getString(ltdIndex)));
			loc.setLng(Double.parseDouble(myCursor.getString(lngIndex)));
			String address = myCursor.getString(addressIndex);
			venueObj.setId(venue_id);
			venueObj.setLocation(loc);
			venueObj.setUrl(address);
		}
		myCursor.close();
		myDB.close();
		return venueObj;
	}
}
