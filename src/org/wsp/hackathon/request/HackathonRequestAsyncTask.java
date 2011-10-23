/**
 * 
 */
package org.wsp.hackathon.request;

import org.wsp.hackathon.request.HackathonRequest.HackathonRequestListener;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

/**
 * @author neel
 *
 */
public class HackathonRequestAsyncTask extends AsyncTask<Void, Void, Void> {
	
	private static final String TAG = HackathonRequestAsyncTask.class.getSimpleName();
	
	private HackathonRequestListener mListener;
	private HackathonRequest mRequest;
	private Handler mHandle = new Handler();
	
	public HackathonRequestAsyncTask(HackathonRequest req, HackathonRequestListener lis){
		this.mRequest = req;
		this.mListener = lis;
	}
	
	/* (non-Javadoc)
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected Void doInBackground(Void... params) {
		HackathonRequestListener innerlistener = new HackathonRequestListener() {			
			@Override
			public void onRequestComplete(final Object obj) {
				Log.v(TAG, "Request Complete");
				mHandle.post(new Runnable(){
					@Override
					public void run() {
						mListener.onRequestComplete(obj);
					}
				});
			}

			@Override
			public void onError(final int errCode, final String errMessage) {
				mHandle.post(new Runnable(){
					@Override
					public void run() {
						mListener.onError(errCode, errMessage);
					}
				});
			}
		};
		mRequest.addListener(innerlistener);
		mRequest.execute();
		return null;
	}

}
