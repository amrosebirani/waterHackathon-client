/**
 * 
 */
package org.wsp.hackathon;

import java.util.ArrayList;

import org.wsp.hackathon.model.PublicRestroomList;
import org.wsp.hackathon.model.PublicRestroomList.PublicRestroom;
import org.wsp.hackathon.request.HackathonRequest.HackathonRequestListener;
import org.wsp.hackathon.service.ServiceHelper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author neel
 *
 */
public class LocationListScreen extends Activity {
	
	private ListView mLocationsLV;
	private LocationAdapter mAdapter;
	private Spinner mSortSpn;
	private Button mMapBtn;
	
	private double mLatitude;
	private double mLongitude;
	private ArrayList<PublicRestroom> mRestRoomList = new ArrayList<PublicRestroomList.PublicRestroom>();
	
	private String mUsername;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.location_list_screen);
		
		mLocationsLV = (ListView) findViewById(R.id.locationsLV);
		mAdapter = new LocationAdapter(LocationListScreen.this);
		mLocationsLV.setAdapter(mAdapter);
		mSortSpn = (Spinner) findViewById(R.id.sortSpn);
		mMapBtn = (Button) findViewById(R.id.mapBtn);
		
		final ArrayAdapter<CharSequence> adapter = ArrayAdapter
				.createFromResource(this, R.array.sort,
						android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSortSpn.setAdapter(adapter);
		mSortSpn.setSelection(0);
		
		mLatitude = getIntent().getDoubleExtra("latitude", 0.0);
		mLongitude = getIntent().getDoubleExtra("longitude", 0.0);
		mUsername = getIntent().getStringExtra("username");

		setupListeners();
		getResroomsList();
	}

	private void setupListeners() {
		
		mLocationsLV.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(LocationListScreen.this, LocationDetailsScreen.class);
				intent.putExtra("selfLatitude", mLatitude);
				intent.putExtra("selfLongitude", mLongitude);
				intent.putExtra("latitude", mRestRoomList.get(position).getLatitude());
				intent.putExtra("longitude", mRestRoomList.get(position).getLatitude());
				intent.putExtra("name", mRestRoomList.get(position).getTitle());
				intent.putExtra("description", mRestRoomList.get(position).getDescription());
				intent.putExtra("comments", mRestRoomList.get(position).getCommentlist());
				intent.putExtra("rating", mRestRoomList.get(position).getRating());
				intent.putExtra("restroomId", mRestRoomList.get(position).getRestroomId());
				intent.putExtra("username", mUsername);
				startActivity(intent);
			}
		});
		
	}
	
	private void getResroomsList() {
		ServiceHelper.getInstance(getApplicationContext()).getRestroomList(mLatitude, mLongitude, new HackathonRequestListener() {
			
			@Override
			public void onRequestComplete(Object obj) {
				PublicRestroomList restRooms = (PublicRestroomList) obj; 
				
				if(restRooms != null && restRooms.getPublicRestroomList() != null) {
					mRestRoomList.clear();
					mRestRoomList.addAll(restRooms.getPublicRestroomList());
					mAdapter.notifyDataSetChanged();
				}
			}
			
			@Override
			public void onError(int errorCode, String errorMessage) {
				Toast.makeText(LocationListScreen.this, "error fetching Restroom list", Toast.LENGTH_LONG).show();
			}
		});
	}
	
	private class LocationAdapter extends BaseAdapter {
		
		Context mContext;
		LayoutInflater mInflater;
		
		public LocationAdapter(Context cont) {
			mContext = cont;
			mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return mRestRoomList.size();
		}

		@Override
		public PublicRestroom getItem(int position) {
			return mRestRoomList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			convertView = mInflater.inflate(R.layout.location_list_row, null);
			PublicRestroom restRoom = getItem(position);
			
			RatingBar ratingBar = (RatingBar) convertView.findViewById(R.id.locationRatingRB);
			TextView name = (TextView) convertView.findViewById(R.id.locationNameTV);
			TextView distance = (TextView) convertView.findViewById(R.id.locationDistanceTV);
			TextView time = (TextView) convertView.findViewById(R.id.locationTimeTV);
			
			ratingBar.setRating(Float.parseFloat(restRoom.getRating()));
			name.setText(restRoom.getTitle());
			distance.setText(restRoom.getDistance());
			time.setText(restRoom.getDuration());
			
			return convertView;
		}
		
	}

}
