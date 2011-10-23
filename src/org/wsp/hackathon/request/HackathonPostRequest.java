/**
 * 
 */
package org.wsp.hackathon.request;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.wsp.hackathon.Constants;
import org.wsp.hackathon.request.HackathonRequest.HackathonRequestType;

import android.content.Context;
import android.util.Log;

/**
 * @author neel
 *
 */
public class HackathonPostRequest extends HackathonRequest {
	
	private static final String TAG = HackathonPostRequest.class.getSimpleName();
	
	private String mPostBody = null;
	private boolean mJsonFormat = false;
	
	public HackathonPostRequest(Context cont, String url, String body, HackathonRequestType t) {
		super(cont, t);
		super.mServiceURL = url;
		super.mRequestType = t;
		this.mPostBody = body;
		
	}
	
	public HackathonPostRequest(Context cont, String url, String body, HackathonRequestType t, boolean jsonFormat) {
		this(cont, url, body, t);
		this.mJsonFormat = jsonFormat;
		
	}
	
	public static HackathonPostRequest uploadComment(Context cont, String restroomId, String username, String comment) {
		//http://172.16.1.57:8124/WaterHackathon/resources/comment/uploadcomment
		String url = getWebServiceBaseUrl() + "/WaterHackathon/resources/comment/uploadcomment";
		String body = "{\"restroomId\":"+restroomId+",\"username\":\""+username+"\",\"comment\":\""+comment+"\"}";
		
		return new HackathonPostRequest(cont, url, body, HackathonRequestType.UPLOAD_COMMENT, true);
	}
	
	public static String getWebServiceBaseUrl() {
		return "http://172.16.1.57:8124";
	}

	/* (non-Javadoc)
	 * @see org.wsp.hackathon.request.HackathonRequest#parseXMLResponse(java.io.InputStream, org.wsp.hackathon.request.HackathonRequest.HackathonRequestType)
	 */
	@Override
	protected void parseXMLResponse(InputStream inStream,
			HackathonRequestType requestType) {
		Object request;
		String stream;
		try {
			switch (requestType) {
			case UPLOAD_COMMENT:
				mListener.onRequestComplete("Success");
				break;

			}
			
		}catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
			mListener.onError(HackathonRequestListener.SERVER_ERROR, e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see org.wsp.hackathon.request.HackathonRequest#parseJSONResponse(java.io.InputStream, org.wsp.hackathon.request.HackathonRequest.HackathonRequestType)
	 */
	@Override
	protected void parseJSONResponse(InputStream inStream,
			HackathonRequestType requestType) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.wsp.hackathon.request.HackathonRequest#execute()
	 */
	@Override
	protected void execute() {
		if(mServiceURL == null || mPostBody == null)
			return;
		
		final HttpPost post = new HttpPost(mServiceURL);		
		Log.v(TAG, "URL: " + mServiceURL);
		
		try {
			Log.v(TAG, "Request body:\n" + mPostBody);
			StringEntity entity = new StringEntity(mPostBody);
			if(mJsonFormat) {
				entity.setContentType("application/json");
			} else {
				entity.setContentType("application/xml");
			}
			
			post.setEntity(entity);			
			executeRequest(post);			
		} catch (UnsupportedEncodingException e) {
			Log.e(TAG, e.getMessage(), e);
			mListener.onError(HackathonRequestListener.LOCAL_ERROR, e.getMessage());
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
			mListener.onError(HackathonRequestListener.LOCAL_ERROR, e.getMessage());
		} 
	}

}
