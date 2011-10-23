/**
 * 
 */
package org.wsp.hackathon.service;

import org.wsp.hackathon.request.HackathonGetRequest;
import org.wsp.hackathon.request.HackathonPostRequest;
import org.wsp.hackathon.request.HackathonRequest.HackathonRequestListener;
import org.wsp.hackathon.request.HackathonRequestAsyncTask;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

/**
 * @author neel
 *
 */
public class HackathonService extends Service {
	
	private static final String TAG = HackathonService.class.getSimpleName();

	private final IBinder mBinder = new LocalBinder();

	/* (non-Javadoc)
	 * @see android.app.Service#onCreate()
	 */
	@Override
	public void onCreate() {
		super.onCreate();
	}

	/* (non-Javadoc)
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}
	
	public class LocalBinder extends Binder {
		HackathonService getService() {
			return HackathonService.this;
		}
	}
	
	//GET calls
	public void getRestroomList(double lat, double lon, HackathonRequestListener listener) {
		HackathonGetRequest request = HackathonGetRequest.getRestroomList(getBaseContext(), lat, lon);
		new HackathonRequestAsyncTask(request, listener).execute();
	}
	
	public void login(String username, String password, HackathonRequestListener listener) {
		HackathonGetRequest request = HackathonGetRequest.login(getBaseContext(), username, password);
		new HackathonRequestAsyncTask(request, listener).execute();
	}
	
	public void signUp(String username, String password, HackathonRequestListener listener) {
		HackathonGetRequest request = HackathonGetRequest.signUp(getBaseContext(), username, password);
		new HackathonRequestAsyncTask(request, listener).execute();
	}
	
	public void submitRating(String rating, String restroomId, HackathonRequestListener listener) {
		HackathonGetRequest request = HackathonGetRequest.submitRating(getBaseContext(), rating, restroomId);
		new HackathonRequestAsyncTask(request, listener).execute();
	}
	
	//POST calls
	public void uploadComment(String restroomId, String username, String comment, HackathonRequestListener listener) {
		HackathonPostRequest request = HackathonPostRequest.uploadComment(getBaseContext(), restroomId, username, comment);
		new HackathonRequestAsyncTask(request, listener).execute();
	}

}
