/**
 * 
 */
package org.wsp.hackathon;

import java.util.ArrayList;
import java.util.List;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

/**
 * @author neel
 *
 */
public class HomeScreen extends MapActivity {
	
	private static final String TAG = HomeScreen.class.getSimpleName();

	private MapView mMapView;
	private LocationManager mLocationManager;
	private MapItemizedOverlay mMapItemizedOverlay;
	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	private MapItemizedOverlay mItemizedOverlay;
	private List<Overlay> mMapOverlays;
	private OverlayItem mCurrentOverlayItem;
	private Location mCurrentLocation;
	private Button mFindButton;
	private ProgressDialog mProgressDialog;
	private String mUsername;

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_screen);
		
		mUsername = getIntent().getStringExtra("username");
		
		mMapView = (MapView)findViewById(R.id.mapview);
		mMapView.setBuiltInZoomControls(true);
		
		mFindButton = (Button)findViewById(R.id.findBtn);
		
		mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		
		mMapOverlays = mMapView.getOverlays();
		Drawable drawable = this.getResources().getDrawable(R.drawable.androidmarker);
		mItemizedOverlay = new MapItemizedOverlay(drawable, this);
		
		/*GeoPoint point = new GeoPoint(19240000,-99120000);
		OverlayItem overlayitem = new OverlayItem(point, "Hola, Mundo!", "I'm in Mexico City!");
		mItemizedOverlay.addOverlay(overlayitem);*/
		mMapOverlays.add(mItemizedOverlay);
		
		mProgressDialog = ProgressDialog.show(HomeScreen.this, "", "Fetching current location");
		mProgressDialog.setCancelable(true);
		
		setupListeners();
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	/* (non-Javadoc)
	 * @see com.google.android.maps.MapActivity#onPause()
	 */
	@Override
	protected void onPause() {
		mLocationManager.removeUpdates(mLocationListener);
		super.onPause();
	}

	/* (non-Javadoc)
	 * @see com.google.android.maps.MapActivity#onResume()
	 */
	@Override
	protected void onResume() {
		mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);
		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
		super.onResume();
	}

	private void setupListeners() {
		mFindButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mCurrentLocation != null) {
					Intent intent = new Intent(HomeScreen.this, LocationListScreen.class);
					intent.putExtra("latitude", mCurrentLocation.getLatitude());
					intent.putExtra("longitude", mCurrentLocation.getLongitude());
					intent.putExtra("username", mUsername);
					startActivity(intent);
				} else {
					Toast.makeText(HomeScreen.this, "refresh current location", Toast.LENGTH_LONG);
				}
				
			}
		});
		
		if(mProgressDialog != null) {
			mProgressDialog.setOnCancelListener(new OnCancelListener() {
				
				@Override
				public void onCancel(DialogInterface dialog) {
					//finish();
				}
			});
		}
	}
	
	private LocationListener mLocationListener = new LocationListener() {
		
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			
		}
		
		@Override
		public void onProviderEnabled(String provider) {
			
		}
		
		@Override
		public void onProviderDisabled(String provider) {
			
		}
		
		@Override
		public void onLocationChanged(Location location) {
			mProgressDialog.cancel();
			synchronized (mOverlays) {
				mCurrentLocation = location;
				Log.d(TAG, "latitude: " + location.getLatitude() + " longitude:" + location.getLongitude());
				mOverlays.remove(mCurrentOverlayItem);
				GeoPoint point = new GeoPoint((int)(location.getLatitude() * 1e6), (int)(location.getLongitude() * 1e6));
				mCurrentOverlayItem = new OverlayItem(point, "Hello", "Current Location");
				mItemizedOverlay.addOverlay(mCurrentOverlayItem);
				mItemizedOverlay.setFocus(mCurrentOverlayItem);
				mMapView.getController().animateTo(point);
			}
			
		}
	};
	
	/**
	 * @author neel
	 *
	 */
	public class MapItemizedOverlay extends ItemizedOverlay {

		private Context mContext;

		/**
		 * @param defaultMarker
		 */
		public MapItemizedOverlay(Drawable defaultMarker, Context context) {
			super(boundCenterBottom(defaultMarker));
			populate();//Work around for crash when map is dragged when ItemizedOverlay is empty
			mContext = context;
		}

		/* (non-Javadoc)
		 * @see com.google.android.maps.ItemizedOverlay#createItem(int)
		 */
		@Override
		protected OverlayItem createItem(int i) {
			return mOverlays.get(i);
		}

		/* (non-Javadoc)
		 * @see com.google.android.maps.ItemizedOverlay#size()
		 */
		@Override
		public int size() {
			return mOverlays.size();
		}
		
		public void addOverlay(OverlayItem overlay) {
		    mOverlays.add(overlay);
		    populate();
		}

		/* (non-Javadoc)
		 * @see com.google.android.maps.ItemizedOverlay#onTap(int)
		 */
		@Override
		protected boolean onTap(int index) {
			OverlayItem item = mOverlays.get(index);
			AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
			dialog.setTitle(item.getTitle());
			dialog.setMessage(item.getSnippet());
			dialog.show();
			return true;
		}

	}

}
