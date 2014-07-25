package waslny.task.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import waslny.task.dao.SqlHelper;
import waslny.task.foursquareexplorer.FourSquareOper;
import waslny.task.foursquareexplorer.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;
import br.com.condesales.models.Venue;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends Activity implements LocationListener
{
	static LatLng venueLoc;
	private GoogleMap googleMap;
	private SqlHelper dbObj ;
	private FourSquareOper fsObj;
	private String bestProvider;
	LocationManager locationManager;
	 
	   @Override
	   protected void onCreate(Bundle savedInstanceState) 
	   {
	      super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_main);
			dbObj = new SqlHelper(this);
			prepareLocMgr();
			
	      try { 
	            if (googleMap == null) 
	            {
	               googleMap = ((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
	            }
	         googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
	         googleMap.setMyLocationEnabled(true);
	         
	         ArrayList<Venue> venuesObjs = dbObj.getCachedVenues();
	         if(!venuesObjs.get(0).getName().equals("empty"))
	         {
	        	 addMarkers(venuesObjs);
	        	 System.out.println("IN IF COND"+venuesObjs.size());
	         }
	        	         
	         googleMap.setOnMarkerClickListener(new OnMarkerClickListener() 
	         {
				@Override
				public boolean onMarkerClick(Marker arg0) 
				{
					// TODO Auto-generated method stub
					Intent intent = new Intent(MainActivity.this, VenueInfoActivity.class);
					intent.putExtra("venueName", arg0.getTitle());
					startActivity(intent);
					return true;
				}
			});
	      } 
	      catch (Exception e)
	      {
	         e.printStackTrace();
	      }
	   }
	  
	   private void addMarkers(ArrayList<Venue> venuesObjs)
	   {
		   for(Venue venueObj : venuesObjs)
      	 {
			 String categoryName = venueObj.getCategories().get(0).getName().toLowerCase();
			 System.out.println(" CAT NAMEEE: "+categoryName);
			 AssetManager assetManager = getAssets();
		      try 
		      {
				String[] files = assetManager.list("catimages");
				if(! Arrays.asList(files).contains(categoryName+".png"))
				{
					if(categoryName.equals("café"))
						categoryName = "cafe";
					else
						categoryName = "none";
					
					System.out.println("SIZEEE= "+files.length+Arrays.asList(files).get(0));
				}
		      } 
		      catch (IOException e) 
		      {
				// TODO Auto-generated catch block
				e.printStackTrace();
		      }  
			venueLoc = new LatLng( venueObj.getLocation().getLat(),venueObj.getLocation().getLng());
			googleMap.addMarker(new MarkerOptions().
			position(venueLoc).title(venueObj.getName())
			.icon(BitmapDescriptorFactory.fromAsset("catimages/"+categoryName+".png")));
      	 }
	   }
	private void prepareLocMgr()
	{
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Criteria crit = new Criteria();
		crit.setAccuracy(Criteria.ACCURACY_FINE);
		bestProvider = locationManager.getBestProvider(crit, false);
		locationManager.requestLocationUpdates(bestProvider, 0, 1, this);
	    
	}
	
	@Override
	public void onLocationChanged(Location location)
	{
		// TODO Auto-generated method stub
		System.out.println("LAtttt="+location.getLatitude());
		googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 17.0f));
		fsObj = new FourSquareOper(this);
	    ArrayList<Venue> nearByVenues = fsObj.requestVenusNearby(location.getLatitude(), location.getLongitude());
	    googleMap.clear();
	    if(nearByVenues.get(0).getName().equals("NoVenues"))
	    {
	    	Toast.makeText(this, "No nearby venues found!", Toast.LENGTH_LONG).show();
	    	dbObj.deleteCachedVenues();
	    }
	    else
	    {
	    	//googleMap.clear();
	    	addMarkers(nearByVenues);
	    	dbObj.deleteCachedVenues();
	    	dbObj.cacheNewVenues(nearByVenues);
	    }
	}
	
	@Override
	public void onProviderDisabled(String provider) 
	{
		// TODO Auto-generated method stub
	}
	@Override
	public void onProviderEnabled(String provider) 
	{
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onResume() 
	{
	    super.onResume();
	    locationManager.requestLocationUpdates(bestProvider, 1000, 1, this);
	}
	
	@Override
	protected void onPause() 
	{
	    super.onPause();
	    locationManager.removeUpdates(this);
	}
}
