/**
 * 
 */
package org.wsp.hackathon.util;

import java.io.IOException;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

/**
 * @author neel
 *
 */
public class HttpManager {
	
	private static final DefaultHttpClient mClient;
	static {
        final HttpParams params = new BasicHttpParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, "UTF-8");

        HttpConnectionParams.setStaleCheckingEnabled(params, false);
        HttpConnectionParams.setConnectionTimeout(params, 60 * 1000);
        HttpConnectionParams.setSoTimeout(params, 30 * 1000);
        HttpConnectionParams.setSocketBufferSize(params, 8192);

        HttpClientParams.setRedirecting(params, false);

        HttpProtocolParams.setUserAgent(params, "Hackathon/1.1");
        
        ConnManagerParams.setMaxTotalConnections(params, 20);
        ConnPerRouteBean connPerRoute = new ConnPerRouteBean(20);
        ConnManagerParams.setMaxConnectionsPerRoute(params, connPerRoute);

        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        schemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));

        ClientConnectionManager manager = new ThreadSafeClientConnManager(params, schemeRegistry); 
        mClient = new DefaultHttpClient(manager, params);
    }

	private HttpManager() { 
		
    }

    public static HttpResponse execute(HttpHead head) throws IOException {
        return mClient.execute(head);
    }

    public static HttpResponse execute(HttpHost host, HttpGet get) throws IOException {
        return mClient.execute(host, get);
    }
    
    public static HttpResponse execute(HttpHost host, HttpPost post) throws IOException {
        return mClient.execute(host, post);
    }

    public static HttpResponse execute(HttpGet get) throws IOException {
        return mClient.execute(get);
    }
    
    public static HttpResponse execute(HttpPost post) throws IOException {
        return mClient.execute(post);
    }
    
    public static String execute(HttpGet get, ResponseHandler<String> responseHandler) throws IOException {
        return mClient.execute(get, responseHandler);
    }
    
    public static String execute(HttpPost post, ResponseHandler<String> responseHandler) throws IOException {
        return mClient.execute(post, responseHandler);
    }

}
