package waslny.task.foursquareexplorer;

import java.util.ArrayList;
import waslny.task.activity.MainActivity;
import waslny.task.activity.VenueInfoActivity;
import android.location.Location;
import android.widget.Toast;
import br.com.condesales.EasyFoursquare;
import br.com.condesales.criterias.CheckInCriteria;
import br.com.condesales.criterias.VenuesCriteria;
import br.com.condesales.listeners.AccessTokenRequestListener;
import br.com.condesales.models.Checkin;
import br.com.condesales.models.Venue;


public class FourSquareOper implements AccessTokenRequestListener
{
	
	private EasyFoursquare sync;
	MainActivity actv;
	VenueInfoActivity venueActv;
	String venue_id;
	//private EasyFoursquareAsync async;
	
	public FourSquareOper(VenueInfoActivity venueActv)
	{
		this.venueActv = venueActv;
		sync = new EasyFoursquare(venueActv);
	}
	public FourSquareOper(MainActivity actv)
	{
		this.actv = actv;
		sync = new EasyFoursquare(actv);
		//async = new EasyFoursquareAsync(actv);
		
	}

	public void login(String venue_id)
	{
		System.out.println("IN LOGIIIIN");
		this.venue_id = venue_id;
		sync.requestAccess(this);
	}

	@Override
	public void onError(String errorMsg) 
	{
		// TODO Auto-generated method stub
		Toast.makeText(actv, errorMsg, Toast.LENGTH_LONG).show();
    }

	@Override
	public void onAccessGrant(String accessToken) 
	{
		// TODO Auto-generated method stub
		
		//sync.getUserInfo();
		System.out.println("LOGIIN SUCESS, VENUE ID= "+venue_id);
		System.out.println("accessTOKEEEN"+accessToken);
		checkin(venue_id);
	
	}


	 public ArrayList<Venue> requestVenusNearby(double ltd, double lng)
	 {
		 Location loc = new Location("");
	        loc.setLatitude(ltd);
	        loc.setLongitude(lng);
	        ArrayList<Venue> venuesNearBy ;
	        VenuesCriteria criteria = new VenuesCriteria();
	        criteria.setLocation(loc);
	        venuesNearBy = sync.getVenuesNearby(criteria);
	        System.out.println("# Venues= "+venuesNearBy.size());
	        return venuesNearBy;
	 }
	 
	 
	 public void requestVenuDetails(String venue_id)
	 {
		  	sync.getVenueDetail(venue_id);
	 }
	 
	 
	 public void checkin(String venueID) 
	 {

	        CheckInCriteria criteria = new CheckInCriteria();
	        criteria.setBroadcast(CheckInCriteria.BroadCastType.PUBLIC);
	        criteria.setVenueId(venueID);
	        Checkin ch = sync.checkIn(criteria);
	        System.out.println("VID= "+venueID);
	        System.out.println("CHECKIN ID= "+ch.getId());
	        if(ch.getId().equals(""))
	        {
	        	Toast.makeText(venueActv, "Can't Check-in!",Toast.LENGTH_LONG).show();
	        }
	        else 
	        	Toast.makeText(venueActv, "Checked-in successfully!",Toast.LENGTH_LONG).show();
	 }
}
