/**
 * 
 */
package org.wsp.hackathon.service;

import org.wsp.hackathon.request.HackathonRequest.HackathonRequestListener;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

/**
 * @author neel
 *
 */
public class ServiceHelper {
	
	private static final String TAG = ServiceHelper.class.getSimpleName();
	
	private static final long WAIT_CONNECTION_TH = 3000;
	private static ServiceHelper _INSTANCE = null;
	private static volatile boolean connected = false;
	
	private Context appContext;
	private HackathonService mService;
	
	private ServiceHelper() {
		throw new AssertionError();
	}
	private ServiceHelper(final Context cont) {
		initInstances(cont);
	}

	private void initInstances(Context cont) {
		cont.bindService(new Intent(cont, HackathonService.class),
				mConnection, Context.BIND_AUTO_CREATE);
		Log.v(TAG, "Start to bind");
		appContext = cont;
	}
	
	public void releaseService(){
		if(_INSTANCE != null && connected){
			Log.v(TAG, "releaseService");
			appContext.unbindService(mConnection);
			connected = false;
			_INSTANCE = null;
		}
	}
	
	/**
	 * @param getApplicationContext()
	 * @return
	 */
	public static ServiceHelper getInstance(Context applicationContext) {
		if (_INSTANCE == null) {
			Log.v(TAG, "Get New Instance");
			_INSTANCE = new ServiceHelper(applicationContext);
		}
		return _INSTANCE;
	}
	
	public Context getContext(){
		return appContext;
	}
	
	
	
	/**
	 * @param lat
	 * @param lon
	 * @param listener
	 * @see org.wsp.hackathon.service.HackathonService#getRestroomList(double, double, org.wsp.hackathon.request.HackathonRequest.HackathonRequestListener)
	 */
	public void getRestroomList(final double lat, final double lon,
			final HackathonRequestListener listener) {
		
		final ServiceBridge bridge = new ServiceBridge(){
			@Override
			public void execute() {
				mService.getRestroomList(lat, lon, listener);
			}
		};
		new WaitBindingThread(bridge).start();
	}



	/**
	 * @param username
	 * @param password
	 * @param listener
	 * @see org.wsp.hackathon.service.HackathonService#login(java.lang.String, java.lang.String, org.wsp.hackathon.request.HackathonRequest.HackathonRequestListener)
	 */
	public void login(final String username, final String password,
			final HackathonRequestListener listener) {
		
		final ServiceBridge bridge = new ServiceBridge(){
			@Override
			public void execute() {
				mService.login(username, password, listener);
			}
		};
		new WaitBindingThread(bridge).start();
	}
	
	/**
	 * @param username
	 * @param password
	 * @param listener
	 * @see org.wsp.hackathon.service.HackathonService#login(java.lang.String, java.lang.String, org.wsp.hackathon.request.HackathonRequest.HackathonRequestListener)
	 */
	public void signUp(final String username, final String password,
			final HackathonRequestListener listener) {
		
		final ServiceBridge bridge = new ServiceBridge(){
			@Override
			public void execute() {
				mService.signUp(username, password, listener);
			}
		};
		new WaitBindingThread(bridge).start();
	}



	/**
	 * @param rating
	 * @param restroomId
	 * @param listener
	 * @see org.wsp.hackathon.service.HackathonService#submitRating(java.lang.String, java.lang.String, org.wsp.hackathon.request.HackathonRequest.HackathonRequestListener)
	 */
	public void submitRating(final String rating, final String restroomId,
			final HackathonRequestListener listener) {
		
		final ServiceBridge bridge = new ServiceBridge(){
			@Override
			public void execute() {
				mService.submitRating(rating, restroomId, listener);
			}
		};
		new WaitBindingThread(bridge).start();
	}



	/**
	 * @param restroomId
	 * @param username
	 * @param comment
	 * @param listener
	 * @see org.wsp.hackathon.service.HackathonService#uploadComment(java.lang.String, java.lang.String, java.lang.String, org.wsp.hackathon.request.HackathonRequest.HackathonRequestListener)
	 */
	public void uploadComment(final String restroomId, final String username,
			final String comment, final HackathonRequestListener listener) {
		
		final ServiceBridge bridge = new ServiceBridge(){
			@Override
			public void execute() {
				mService.uploadComment(restroomId, username, comment, listener);
			}
		};
		new WaitBindingThread(bridge).start();
	}



	private final ServiceConnection mConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName className, IBinder iBinder) {
			mService = ((HackathonService.LocalBinder) iBinder).getService();
			Log.v(TAG, "Service Connected!");
			connected = true;
		}
		@Override
		public void onServiceDisconnected(ComponentName className) {
			Log.v(TAG, "Service Disonnected!");
			connected = false;
		}
	};
	
	private static final Handler connectionHandler = new Handler(){
        public void handleMessage(Message msg) {
        	((ServiceBridge)msg.obj).execute();
        }
    };
    
    private interface ServiceBridge{
		public void execute();
	}
	
	private class WaitBindingThread extends Thread{
		private final ServiceBridge bridgeSer;
		private long startTime;
		
		public WaitBindingThread(ServiceBridge bridge){
			bridgeSer = bridge;
			startTime = System.currentTimeMillis();
		}
		public void run(){
			while(!connected){
				Thread.yield(); 
				if((System.currentTimeMillis()-startTime) > WAIT_CONNECTION_TH){
					Log.e(TAG, "Bind Service Failed!");
					break;
				}
			}	// Wait until the service is connected
			if(connected)
				Message.obtain(connectionHandler, 0, bridgeSer).sendToTarget();
		}
	}

}
