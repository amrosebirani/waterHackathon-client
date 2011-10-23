/**
 * 
 */
package org.wsp.hackathon.request;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.wsp.hackathon.Constants;
import org.wsp.hackathon.util.HttpManager;

import android.content.Context;
import android.util.Log;

/**
 * @author neel
 *
 */
public abstract class HackathonRequest {
	
	private static final String TAG = HackathonRequest.class.getSimpleName();
	
	private static final int RETRY_LIMIT = 3;
	
	public HackathonRequestType mRequestType;
	protected HackathonRequestListener mListener = new DefaultHackathonRequestListener();
	protected String mServiceURL = null;
	//protected static final String CAMPAIGNID_PARS = "/webCampaignId/" + ServiceHelper.getCampaignID();
	private int mRetryLimitCounter = 0;
	private Context mContext;
	
	public static enum HackathonRequestType {
		// GET Request
		GET_RESTROOM_LIST,
		LOGIN,
		SIGN_UP,
		SUBMIT_RATING,
		
		//POST Request
		UPLOAD_COMMENT
	}
	
	public HackathonRequest(Context cont) {
		mContext = cont;
	}
	
	public HackathonRequest(Context cont, HackathonRequestType t) {
		this(cont);
		mRequestType = t;
	}
	
	public HackathonRequestType getRequestType(){
		return mRequestType;
	}
	
	public void setRequestType(HackathonRequestType t){
		mRequestType = t;
	}
	
	public void addListener(HackathonRequestListener lis){
		mListener = lis;
	}
	
	protected void executeRequest(HttpPost post) {
		HttpEntity entity = null;
    	InputStream is = null; 
	    try {
	    	final HttpResponse response = HttpManager.execute(post);
	    	final int statusCode = response.getStatusLine().getStatusCode();
	    	String reasonPhrase = response.getStatusLine().getReasonPhrase();
	    	Log.v(TAG, "Status Code: " + statusCode + " reasonPhrase:" + reasonPhrase);

	    	entity = response.getEntity();
    		is = entity.getContent();
    		
	    	switch (statusCode) { 
	    		case HttpStatus.SC_CREATED:
	    		case HttpStatus.SC_OK:
	    			//Logger.log(Logger.VERBOSE, TAG, "Response: " + IOUtilities.streamToString(is));
					parseXMLResponse(is, mRequestType);
					break;
	    	 	case HttpStatus.SC_UNAUTHORIZED:
	    	 		mListener.onError(HackathonRequestListener.AUTHENTICATION_ERROR, statusCode + " [" + reasonPhrase + "]" + " : " + streamToString(is));
		    		break;
	    	 	case HttpStatus.SC_BAD_REQUEST:
	    	 		mListener.onError(HackathonRequestListener.BAD_PARAMETER_TYPE_ERROR, statusCode + " [" + reasonPhrase + "]" + " : " + streamToString(is));
	    	 		break;
	    	 	default:
	    	 		mListener.onError(HackathonRequestListener.SERVER_ERROR, "Server Error: " + statusCode + " [" + reasonPhrase + "]" + " : " + streamToString(is));
		    		break;
	    	}
	    } catch (IOException e) {
	    	Log.e(TAG, e.getMessage(), e);
			mListener.onError(HackathonRequestListener.NETWORK_ERROR, "connection time out");
	    } finally {
			if (entity != null) {
	    		try {
	    			if(is != null)
	    				is.close();
					entity.consumeContent();
				} catch (IOException e) {
					Log.e(TAG, e.getMessage(), e);
				}
	    	}
		}
	}
	
	protected void executeRequest(HttpGet get) {
		HttpEntity entity = null;
		InputStream is = null;
		try {
			final HttpResponse response = HttpManager.execute(get);
	    	final int statusCode = response.getStatusLine().getStatusCode();
	    	String reasonPhrase = response.getStatusLine().getReasonPhrase();
	    	Log.v(TAG, "statusCode: " + statusCode + " reasonPhrase:" + reasonPhrase);
	    	entity = response.getEntity();
	    	/*Header[] headers = response.getAllHeaders();
	    	for(int i = 0; i<headers.length; i++)
	    		Logger.i(TAG, headers[i].getName() + ": " + headers[i].getValue());*/

	    	is = entity.getContent();
	    	switch (statusCode) { 
	    	case HttpStatus.SC_CREATED:
	    	case HttpStatus.SC_OK:
	    		if(Constants.JSON_FORMAT)
	    			parseJSONResponse(is, mRequestType);
	    		else
	    			parseXMLResponse(is, mRequestType);
	    		break;
	    	case HttpStatus.SC_BAD_REQUEST:
	    		mListener.onError(HackathonRequestListener.BAD_PARAMETER_TYPE_ERROR, statusCode + " [" + reasonPhrase + "]" + " : " + streamToString(is));
	    		break;
	    	case HttpStatus.SC_UNAUTHORIZED:
	    		mListener.onError(HackathonRequestListener.AUTHENTICATION_ERROR, statusCode + " [" + reasonPhrase + "]" + " : " + streamToString(is));
	    		break;
	    	default:
	    		mListener.onError(HackathonRequestListener.SERVER_ERROR, "Server Error: " + statusCode + " [" + reasonPhrase + "]" + " : " + streamToString(is));
	    		break;
	    	}			

		} catch (ConnectTimeoutException e) {
			Log.e(TAG, e.getMessage(), e);
			mListener.onError(HackathonRequestListener.NETWORK_ERROR, "connection time out");
		}catch (IOException e) {
			Log.e(TAG, e.getMessage(), e);
			mListener.onError(HackathonRequestListener.NETWORK_ERROR, e.getMessage());
		} finally {
			if (entity != null) {
	    		try {
	    			if(is != null)
	    				is.close();
					entity.consumeContent();
				} catch (IOException e) {
					Log.e(TAG, e.getMessage(), e);
				}
	    	}
		}
	}
	
	public static String streamToString(java.io.InputStream is) {		
		java.io.DataInputStream din = new java.io.DataInputStream(is);
		StringBuffer sb = new StringBuffer();
		try {
			String line = null;
			while ((line = din.readLine()) != null) {
				sb.append(line + "\n");
			}
			
		} catch (Exception ex) {
			ex.getMessage();
		} finally {
			try {
				is.close();
			} catch (Exception ex) {
			}
		}
		return sb.toString();
	}
	
	protected abstract void parseXMLResponse(InputStream inStream, HackathonRequest.HackathonRequestType requestType);
	protected abstract void parseJSONResponse(InputStream inStream, HackathonRequest.HackathonRequestType requestType);
	protected abstract void execute();
	
	private class DefaultHackathonRequestListener implements HackathonRequestListener{
		private DefaultHackathonRequestListener(){}
		
		@Override
		public void onRequestComplete(Object obj) {
		}

		@Override
		public void onError(int errorCode, String errorMessage) {
			Log.e(TAG, "error code: " + errorCode + ". " + errorMessage);
		}
	}
	
	public static interface HackathonRequestListener {
		public static final int BAD_PARAMETER_TYPE_ERROR = -1;
		public static final int NETWORK_ERROR = 1;
		public static final int SERVER_ERROR = 2;
		public static final int AUTHENTICATION_ERROR = 3;
		public static final int LOCAL_ERROR = 4;
		public static final int TIMEOUT = 5;
		
		void onRequestComplete(Object obj);
		void onError(int errorCode, String errorMessage);
	}

}
