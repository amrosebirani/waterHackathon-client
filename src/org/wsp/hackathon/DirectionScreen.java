/**
 * 
 */
package org.wsp.hackathon;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.wsp.hackathon.model.Road;
import org.wsp.hackathon.provider.RoadProvider;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

/**
 * @author neel
 *
 */
public class DirectionScreen extends MapActivity {
	
	private MapView mMapView;
	private Road mRoad;
	private ProgressDialog mProgressDialog;
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.direction_screen);
		
		mMapView = (MapView)findViewById(R.id.mapview);
		mMapView.setBuiltInZoomControls(true);
		
		mProgressDialog = ProgressDialog.show(DirectionScreen.this, "", "Loading..");
		mProgressDialog.setCancelable(true);
		
		new Thread() {
			@Override
			public void run() {
				double fromLat = getIntent().getDoubleExtra("srcLatitude", 0.0), fromLon = getIntent().getDoubleExtra("srcLongitude", 0.0), toLat = getIntent().getDoubleExtra("dstLatitude", 0.0), toLon = getIntent().getDoubleExtra("dstLongitude", 0.0);
				String url = RoadProvider
						.getUrl(fromLat, fromLon, toLat, toLon);
				InputStream is = getConnection(url);
				mRoad = RoadProvider.getRoute(is);
				mHandler.sendEmptyMessage(0);
			}
		}.start();
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			/*TextView textView = (TextView) findViewById(R.id.description);
			textView.setText(mRoad.mName + " " + mRoad.mDescription);*/
			MapOverlay mapOverlay = new MapOverlay(mRoad, mMapView);
			List<Overlay> listOfOverlays = mMapView.getOverlays();
			listOfOverlays.clear();
			listOfOverlays.add(mapOverlay);
			mProgressDialog.cancel();
			mMapView.invalidate();
		};
	};
	
	private InputStream getConnection(String url) {
		InputStream is = null;
		try {
			URLConnection conn = new URL(url).openConnection();
			is = conn.getInputStream();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return is;
	}

}

class MapOverlay extends com.google.android.maps.Overlay {
	Road mRoad;
	ArrayList<GeoPoint> mPoints = new ArrayList<GeoPoint>();

	public MapOverlay(Road road, MapView mv) {
		mRoad = road;
		if (road.mRoute.length > 0) {
			mPoints.clear();
			for (int i = 0; i < road.mRoute.length; i++) {
				mPoints.add(new GeoPoint((int) (road.mRoute[i][1] * 1000000),
						(int) (road.mRoute[i][0] * 1000000)));
			}
			int moveToLat = (mPoints.get(0).getLatitudeE6() + (mPoints.get(
					mPoints.size() - 1).getLatitudeE6() - mPoints.get(0)
					.getLatitudeE6()) / 2);
			int moveToLong = (mPoints.get(0).getLongitudeE6() + (mPoints.get(
					mPoints.size() - 1).getLongitudeE6() - mPoints.get(0)
					.getLongitudeE6()) / 2);
			GeoPoint moveTo = new GeoPoint(moveToLat, moveToLong);

			MapController mapController = mv.getController();
			mapController.animateTo(moveTo);
			mapController.setZoom(7);
		}
	}

	@Override
	public boolean draw(Canvas canvas, MapView mv, boolean shadow, long when) {
		super.draw(canvas, mv, shadow);
		drawPath(mv, canvas);
		return true;
	}

	public void drawPath(MapView mv, Canvas canvas) {
		int x1 = -1, y1 = -1, x2 = -1, y2 = -1;
		Paint paint = new Paint();
		paint.setColor(Color.GREEN);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(3);
		for (int i = 0; i < mPoints.size(); i++) {
			Point point = new Point();
			mv.getProjection().toPixels(mPoints.get(i), point);
			x2 = point.x;
			y2 = point.y;
			if (i > 0) {
				canvas.drawLine(x1, y1, x2, y2, paint);
			}
			x1 = x2;
			y1 = y2;
		}
	}
}
