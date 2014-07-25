package waslny.task.activity;

import br.com.condesales.models.Venue;

import waslny.task.dao.SqlHelper;
import waslny.task.foursquareexplorer.FourSquareOper;
import waslny.task.foursquareexplorer.R;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class VenueInfoActivity extends Activity 
{

	TextView venueNameTxt , venueAddressTxt;
	Button checkInBtn;
	String venue_id;
	FourSquareOper fsObj;
	Venue venue;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_venue_info);
		fsObj = new FourSquareOper(this);
		Intent intent = getIntent();
		venueNameTxt = (TextView)findViewById(R.id.venuNameTxt);
		venueAddressTxt = (TextView)findViewById(R.id.addressTxt);
		venueNameTxt.setText(intent.getStringExtra("venueName"));
		venue = new SqlHelper(VenueInfoActivity.this).getVenueByName(venueNameTxt.getText().toString());
		venueAddressTxt.setText(venue.getUrl());
		if(venueAddressTxt.getText().length() == 0)
			venueAddressTxt.setText("Not Available");
		
		checkInBtn = (Button) findViewById(R.id.checkinBtn);
		checkInBtn.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				// TODO Auto-generated method stub
				fsObj.login(venue.getId());
			}
		});
	}

}
