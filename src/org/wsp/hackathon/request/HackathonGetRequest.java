/**
 * 
 */
package org.wsp.hackathon.request;

import java.io.InputStream;

import org.apache.http.client.methods.HttpGet;
import org.simpleframework.xml.core.Persister;
import org.wsp.hackathon.Constants;
import org.wsp.hackathon.model.PublicRestroomList;

import android.content.Context;
import android.util.Log;

/**
 * @author neel
 *
 */
public class HackathonGetRequest extends HackathonRequest {
	
	private static final String TAG = HackathonGetRequest.class.getSimpleName();
	
	public HackathonGetRequest(Context cont, String url, HackathonRequestType t) {
		super(cont, t);
		super.mServiceURL = url;
		super.mRequestType = t;
	}

	public static HackathonGetRequest getRestroomList(Context cont, double lat, double lon) {
		String serviceUrl = getWebServiceBaseUrl() + "/WaterHackathon/resources/restrooms/lat/" 
			+ Double.toString(lat) + "/lon/" + Double.toString(lon);
		return new HackathonGetRequest(cont, serviceUrl, HackathonRequestType.GET_RESTROOM_LIST);
	}
	
	//http://172.16.1.57:8124/WaterHackathon/resources/user/username/{username}/signIn
	public static HackathonGetRequest login(Context cont, String username, String password) {
		String serviceUrl = getWebServiceBaseUrl() + "/WaterHackathon/resources/user/username/" 
			+ username + "/password/" + password + "/signIn";
		return new HackathonGetRequest(cont, serviceUrl, HackathonRequestType.LOGIN);
	}
	
	public static HackathonGetRequest signUp(Context cont, String username, String password) {
		String serviceUrl = getWebServiceBaseUrl() + "/WaterHackathon/resources/user/username/" 
			+ username + "/password/" + password + "/signUp";
		return new HackathonGetRequest(cont, serviceUrl, HackathonRequestType.SIGN_UP);
	}
	
	///WaterHackathon/resources/rating/{rating}/restroomId/{restroomId}
	public static HackathonGetRequest submitRating(Context cont, String rating, String restroomId) {
		String serviceUrl = getWebServiceBaseUrl() + "/WaterHackathon/resources/rating/" 
			+ rating + "/restroomId/" + restroomId;
		return new HackathonGetRequest(cont, serviceUrl, HackathonRequestType.SUBMIT_RATING);
	}
	
	public static String getWebServiceBaseUrl() {
		return "http://172.16.1.57:8124";
	}
	
	/* (non-Javadoc)
	 * @see HackathonRequest#parseXMLResponse(InputStream, HackathonRequest.HackathonRequestType)
	 */
	@Override
	protected void parseXMLResponse(InputStream inStream,
			HackathonRequestType requestType) {
		Object request;
		String stream;
		
		try {
			
			switch (requestType) {
			case GET_RESTROOM_LIST:
				request = new Persister().read(PublicRestroomList.class, inStream);
				mListener.onRequestComplete(request);
				break;
			case LOGIN:
				mListener.onRequestComplete("Sucess");
				break;	
			case SIGN_UP:
				mListener.onRequestComplete("Sucess");
				break;
			case SUBMIT_RATING:
				mListener.onRequestComplete("Sucess");
				break;
			default:
				Log.e(TAG, "Wrong request Type");
				mListener.onError(HackathonRequestListener.BAD_PARAMETER_TYPE_ERROR, "Wrong Parameter at " + this.getClass().getSimpleName());
				return;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, e.getMessage(), e);
			mListener.onError(HackathonRequestListener.SERVER_ERROR, e.toString());
		}
		
	}
	
	/* (non-Javadoc)
	 * @see HackathonRequest#parseJSONResponse(java.io.InputStream, HackathonRequest.HackathonRequestType)
	 */
	@Override
	protected void parseJSONResponse(InputStream inStream,
			HackathonRequestType requestType) {

	}

	/* (non-Javadoc)
	 * @see org.wsp.hackathon.request.HackathonRequest#execute()
	 */
	@Override
	protected void execute() {
		if(mServiceURL == null){
			return;
		}
		Log.v(TAG, "url:\t" + mServiceURL);
		final HttpGet get = new HttpGet(mServiceURL);
		if(Constants.JSON_FORMAT)
			get.setHeader("accept", "application/json");
		executeRequest(get);
	}

}
