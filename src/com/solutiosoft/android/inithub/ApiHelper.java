package com.solutiosoft.android.inithub;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import javax.net.ssl.HostnameVerifier;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.util.Log;

import com.solutiosoft.android.inithub.dao.InitHubDatabaseManager;
import com.solutiosoft.android.inithub.entities.Message;
import com.solutiosoft.android.inithub.entities.Subject;

public class ApiHelper {
	private static final String TAG = "ApiHelper";
	private static final String HTTP_MODE = Constants.HTTPS;
	
    /**
     * Shared buffer used by {@link #getUrlContent(String)} when reading results
     * from an API request.
     */
    private static byte[] sBuffer = new byte[512];
    
	private Context mCxt;
    
    private static String sUserAgent = null;
    /**
     * {@link StatusLine} HTTP status code when no server error has occurred.
     */
    private static final int HTTP_STATUS_OK = 200;
    private static final int HTTP_STATUS_CREATED = 201;
    

    private String baseUrl;
    
	private String ApiToken;
	private String subjectList;
	private String messageList;
	private static final String MESSAGE_PATH = "/innohub/api/message/";
	private static final String API_TOKEN_PATH = "/innohub/api/token/auth/";
	private static final String SUBJECT_PATH = "/innohub/api/subject/";
	
	
	//public final static ApiHelper INSTANCE = new ApiHelper();
	
	private static ApiHelper mInstance = null;
	
	protected ApiHelper() {
      // Exists only to defeat instantiation.
	}
	
	public static ApiHelper getInstance(Context context) {
		if(mInstance == null) {
			mInstance = new ApiHelper(context.getApplicationContext());
		}
		return mInstance;
	}

	protected ApiHelper(Context context) {
	      // Exists only to defeat instantiation.
		mCxt = context;
		baseUrl = mCxt.getResources().getString(R.urls.myUrl);
		ApiToken = baseUrl + API_TOKEN_PATH;
		subjectList = baseUrl + SUBJECT_PATH;
		messageList = baseUrl + MESSAGE_PATH;
		sUserAgent = getUserAgent(context);
	}
	
	public String authenticate(String username, String password, Context context){
		if(HTTP_MODE.equals(Constants.HTTPS)){
			return secureAuthenticate(username, password, context);
		}
		else {
			return defaultAuthenticate(username, password, context);
		}
	}
	
    // TODO: Refactor
    private String defaultAuthenticate(String username, String password, Context context){
    	
    	try {
	    	URI uri = new URI(ApiToken);	    	
	    	
	    	DefaultHttpClient client = getDefaultHttpClientWithCredentials(username, password, uri);
	        String content = getUrlContent(client, uri);
	        
	        if(content == null){
	        	return null;
	        }
           
            return getApiKeyFromJsonResponse(content);
	    }
    	catch(URISyntaxException e){
    		//throw new ApiException("Problem communicating with API - URISyntaxException", e);
    		Log.e(TAG, "URISyntaxException");
    		return null;
    	}
    	catch (JSONException e) {
	        //throw new ParseException("Problem parsing API response", e);
    		Log.e(TAG, "JSONException");
    		return null;
	     }
    	catch (ApiException e) {
	        //throw new ParseException("Problem parsing API response", e);
    		Log.e(TAG, "ApiException");
    		e.printStackTrace();
    		return null;
	     }
    }
    
    public ArrayList<Subject> getSubjectList(){
    	if(HTTP_MODE.equals(Constants.HTTPS)){
    		return getSecureSubjectList();
    	}
    	else {
    		return getDefaultSubjectList();
    	}
    	
    }
    
    
    // TODO: Refactor    
    private ArrayList<Subject> getDefaultSubjectList(){
    	
    	try {
    		// RMT - 05/08/2013 : related to 102: message api call only returning 20 records. apply same to subjectlist
	    	String content = getUrlContent(subjectList + "?limit=0");
	        
	        if(content == null){
	        	return null;
	        }
	        
            return getSubjectListFromJsonResponse(content);
	    }
    	catch (JSONException e) {
    		Log.e(TAG, "JSONException");
    		e.printStackTrace();
    		return null;
	     }
    	catch (ApiException e) {
	        Log.e(TAG, "ApiException");
    		e.printStackTrace();
    		return null;
	     }
    }
    
    public ArrayList<Message> getMessageList(long subjectId){
    	if(HTTP_MODE.equals(Constants.HTTPS)){
    		return getSecureMessageList(subjectId);
    	}
    	else {
    		return getDefaultMessageList(subjectId);
    	}
    }
    // TODO: Refactor
    private ArrayList<Message> getDefaultMessageList(long subjectId){
    	
    	try {
    		
    		// RMT - 04/29/2013 : fixed issue 102: message api call only returning 20 records
	    	String content = getUrlContent(messageList + "?subject_id=" + subjectId + "&limit=0");
	        
	        if(content == null){
	        	return null;
	        }

            return getMessageListFromJsonResponse(content);
	    }
    	catch (JSONException e) {
	        //throw new ParseException("Problem parsing API response", e);
    		Log.e(TAG, "JSONException");
    		return null;
	     }
    	catch (ApiException e) {
    		e.printStackTrace();
    		return null;
	     }
    }
    private DefaultHttpClient getDefaultHttpClientWithCredentials(String username, String password, URI uri){
    	DefaultHttpClient  client = new DefaultHttpClient();
    	
		client.getCredentialsProvider().setCredentials(
				new AuthScope(uri.getHost(), uri.getPort(),AuthScope.ANY_SCHEME),
				new UsernamePasswordCredentials(username, password));
		
		return client;
    }
	
    private String getUrlContent(DefaultHttpClient client, URI uri) throws ApiException {
    	checkUserAgent();

        // Create client and set our specific user-agent string
        try {				
			// Set timeout
			client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 5000);
			HttpGet request = new HttpGet(uri);
	        request.setHeader("User-Agent", sUserAgent);
	        HttpResponse response = client.execute(request);

            // Check if server response is valid
            StatusLine status = response.getStatusLine();
            if (status.getStatusCode() != HTTP_STATUS_OK) {
                throw new ApiException("Invalid response from server: " +
                        status.toString());
            }

            // Pull content stream from response
            HttpEntity entity = response.getEntity();
            InputStream inputStream = entity.getContent();

            ByteArrayOutputStream content = new ByteArrayOutputStream();

            // Read response into a buffered stream
            int readBytes = 0;
            while ((readBytes = inputStream.read(sBuffer)) != -1) {
                content.write(sBuffer, 0, readBytes);
            }

            // Return result from buffered stream
            return new String(content.toByteArray());
        } catch (IOException e) {
            throw new ApiException("Problem communicating with API - IOException", e);
        }
    }
    
    private String getUrlContent(String url) throws ApiException {
    	//String sUserAgent = getUserAgent(context);
    	checkUserAgent();

        // Create client and set our specific user-agent string
        try {
			URI uri = new URI(url);
	        DefaultHttpClient  client = new DefaultHttpClient();
	        
			// Set timeout
			client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 5000);
			HttpGet request = new HttpGet(uri);
	        request.setHeader("User-Agent", sUserAgent);
	        
	        InitHubDatabaseManager db = new InitHubDatabaseManager(mCxt);
	        	        
	        String apikey = db.getApiTokenCredentials();
	        
	        request.setHeader("Authorization", apikey);
	        
	        HttpResponse response = client.execute(request);

            // Check if server response is valid
            StatusLine status = response.getStatusLine();
            if (status.getStatusCode() != HTTP_STATUS_OK) {
                throw new ApiException("Invalid response from server: " +
                        status.toString());
            }

            // Return result from buffered stream
            //return new String(content.toByteArray());
            return getHttpResponse(response);
        } catch (IOException e) {
            throw new ApiException("Problem communicating with API - IOException", e);
        } catch (URISyntaxException e) {
			// TODO Auto-generated catch block
        	throw new ApiException("Problem communicating with API - IOException", e);
		}
    }
    
    /**
     * Prepare the internal User-Agent string for use. This requires a
     * {@link Context} to pull the package name and version number for this
     * application.
     */
    private String getUserAgent(Context context) {
        // Read package name and version number from manifest
    	String version = new PackageInfo().versionName;
    	String packageName = new PackageInfo().packageName;
        return String.format(context.getString(R.string.template_user_agent),
                packageName, version);

    }
    
    /**
     * Thrown when there were problems parsing the response to an API call,
     * either because the response was empty, or it was malformed.
     */
    public static class ParseException extends Exception {

		private static final long serialVersionUID = -3229327274930854955L;

		public ParseException(String detailMessage, Throwable throwable) {
            super(detailMessage, throwable);
        }
    }
    
    /**
     * Thrown when there were problems contacting the remote API server, either
     * because of a network error, or the server returned a bad status code.
     */
    public static class ApiException extends Exception {

		private static final long serialVersionUID = -3961286044347578019L;

		public ApiException(String detailMessage, Throwable throwable) {
            super(detailMessage, throwable);
        }

        public ApiException(String detailMessage) {
            super(detailMessage);
        }
    }
    
    public Message addMessage(Message message){
    	if(HTTP_MODE.equals(Constants.HTTPS)){
    		return secureAddMessage(message);
    	}
    	else {
    		return addDefaultsMessage(message);
    	}
    }
    
    // TODO: Refactor
    private Message addDefaultsMessage(Message message){
    	//instantiates httpclient to make request
        DefaultHttpClient httpclient = new DefaultHttpClient();
        
        Message ret_message = null;

        //url with the post data
        URI uri;
		try {
			uri = new URI(messageList);
			HttpPost httpost = new HttpPost(uri);
			
			InitHubDatabaseManager db = new InitHubDatabaseManager(mCxt.getApplicationContext());
			int remoteSubId = db.getRemoteSubjectId(message.getLocalSubjectId());
			String apikey = db.getApiTokenCredentials();

	        JSONObject data = new JSONObject();
			data.put(InitHubDatabaseManager.MESSAGE_COMMENT, message.getComment());
			data.put("subject_id", remoteSubId);
			Log.d(TAG, "subject_id=" + Integer.toString(remoteSubId));
	        	        
	        StringEntity entity = new StringEntity(data.toString(), HTTP.UTF_8);
	        httpost.setEntity(entity);
	        //sets a request header so the page receving the request
	        //will know what to do with it
	        httpost.setHeader("Accept", "application/json");
	        httpost.setHeader("Content-type", "application/json");
	        
	        httpost.setHeader("User-Agent", sUserAgent);
	        
	        httpost.setHeader("Authorization", apikey);
	        
	        HttpResponse response = httpclient.execute(httpost);

            // Check if server response is valid
            StatusLine status = response.getStatusLine();
            if (status.getStatusCode() != HTTP_STATUS_CREATED) {
                throw new ApiException("Invalid response from server: " +
                        status.toString());
            }

            // Pull content stream from response
            HttpEntity respone_entity = response.getEntity();
            InputStream inputStream = respone_entity.getContent();

            ByteArrayOutputStream content = new ByteArrayOutputStream();

            // Read response into a buffered stream
            int readBytes = 0;
            while ((readBytes = inputStream.read(sBuffer)) != -1) {
                content.write(sBuffer, 0, readBytes);
            }
            
            String ret_cont = new String(content.toByteArray());
            
            JSONObject json_response = new JSONObject(ret_cont);
            String id = json_response.getString("id");
            String firstName = json_response.getString("first_name");
            String lastName = json_response.getString("last_name");
            ret_message = new Message();
            ret_message.setFirstName(firstName);
            ret_message.setLastName(lastName);
            ret_message.setRemoteId(Integer.valueOf(id));
            
            Log.d(TAG, "+++++++++++++++++++++++++++++++++++ return_id=" + id);
    	
		} 
		catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (URISyntaxException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return ret_message;
    }
    
    private DefaultHttpClient getSecureHttpClient(){
    	DefaultHttpClient  client = new DefaultHttpClient();
	  	
    	HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
    	SchemeRegistry registry = new SchemeRegistry();
		SSLSocketFactory socketFactory = SSLSocketFactory.getSocketFactory();
		socketFactory.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);
		registry.register(new Scheme("https", socketFactory, 443));
		SingleClientConnManager mgr = new SingleClientConnManager(client.getParams(), registry);
		DefaultHttpClient httpClient = new DefaultHttpClient(mgr, client.getParams());
		return httpClient;
    }
    
    private String secureAuthenticate(String username, String password, Context context) {
    	
    	try {
	    	URI uri = new URI(ApiToken);	    	
	    	
	    	DefaultHttpClient httpClient = getSecureHttpClient();
	    	
			httpClient.getCredentialsProvider().setCredentials(
					new AuthScope(uri.getHost(), uri.getPort(), "https"),
					new UsernamePasswordCredentials(username, password));
	    	
	    	// Set timeout
			httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 5000);
			// Set verifier - rmt : removed, don't think we need this   
			//HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);				
			
			HttpGet request = new HttpGet(uri);
			
			checkUserAgent();
	        request.setHeader("User-Agent", sUserAgent);
	        
	        HttpResponse response = httpClient.execute(request);

            // Return result from buffered stream
            String content =  getHttpResponse(response);	        
	        
            return getApiKeyFromJsonResponse(content);
	    }
    	catch(URISyntaxException e){
    		//throw new ApiException("Problem communicating with API - URISyntaxException", e);
    		Log.e(TAG, "URISyntaxException");
    		e.printStackTrace();
    		return null;
    	}
    	catch (JSONException e) {
	        //throw new ParseException("Problem parsing API response", e);
    		Log.e(TAG, "JSONException");
    		e.printStackTrace();
    		return null;
	     }
    	catch (ApiException e) {
	        //throw new ParseException("Problem parsing API response", e);
    		Log.e(TAG, "ApiException");
    		e.printStackTrace();
    		return null;
	     } catch (IOException e) {
			// TODO Auto-generated catch block
	    	Log.e(TAG, "IOException");
			e.printStackTrace();
			return null;
		}    	
    }
    
    private ArrayList<Subject> getSecureSubjectList(){
        try {
        	checkUserAgent();
			URI uri = new URI(subjectList);
			DefaultHttpClient httpClient = getSecureHttpClient();			
	        
			// Set timeout
			httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 5000);
 			
			HttpGet request = new HttpGet(uri);
	        request.setHeader("User-Agent", sUserAgent);
	        
	        InitHubDatabaseManager db = new InitHubDatabaseManager(mCxt);
	        	        
	        String apikey = db.getApiTokenCredentials();
	        
	        request.setHeader("Authorization", apikey);		        
	        
	        HttpResponse response = httpClient.execute(request);

            // Check if server response is valid
            StatusLine status = response.getStatusLine();
            if (status.getStatusCode() != HTTP_STATUS_OK) {
                throw new ApiException("Invalid response from server: " +
                        status.toString());
            }
            
	        String content = getHttpResponse(response);        
	     
            return getSubjectListFromJsonResponse(content);
	    }
    	catch (JSONException e) {
	        //throw new ParseException("Problem parsing API response", e);
    		Log.e(TAG, "JSONException");
    		return null;
	     }
    	catch (ApiException e) {
	        //throw new ParseException("Problem parsing API response", e);
    		Log.e(TAG, "ApiException");
    		e.printStackTrace();
    		return null;
	     } catch (IllegalStateException e) {
	    	Log.e(TAG, "IllegalStateException");
    		e.printStackTrace();
    		return null;
		} catch (IOException e) {
			Log.e(TAG, "IOException");
    		e.printStackTrace();
    		return null;
		} catch (URISyntaxException e) {
			Log.e(TAG, "URISyntaxException");
    		e.printStackTrace();
    		return null;
		}
    }
    
    private ArrayList<Message> getSecureMessageList(long subjectId){
    	
    	try {
    		// make sure we have user agent - should never hit this
    		checkUserAgent();            
            // prepare uri
    		// RMT - 04/29/2013 : fixed issue 102: message api call only returning 20 records
	    	URI uri = new URI(messageList + "?subject_id=" + subjectId + "&limit=0");
	    	
	    	DefaultHttpClient httpClient = getSecureHttpClient();			
	        
			// Set timeout
			httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 5000);
			
			// prepare request
			HttpGet request = new HttpGet(uri);
	        request.setHeader("User-Agent", sUserAgent);
	        
	        // get api key	        
	        InitHubDatabaseManager db = new InitHubDatabaseManager(mCxt);	        	        
	        String apikey = db.getApiTokenCredentials();
	        request.setHeader("Authorization", apikey);
	        
	        // execute
	        HttpResponse response = httpClient.execute(request);

            // Check if server response is valid
            StatusLine status = response.getStatusLine();
            if (status.getStatusCode() != HTTP_STATUS_OK) {
                throw new ApiException("Invalid response from server: " +
                        status.toString());
            }         
            // Return result from buffered stream
            String content = getHttpResponse(response);
    
            return getMessageListFromJsonResponse(content);
	    }
    	catch (JSONException e) {
    		Log.e(TAG, "JSONException");
    		e.printStackTrace();
    		return null;
	     }
    	catch (ApiException e) {
	        //throw new ParseException("Problem parsing API response", e);
    		Log.e(TAG, "ApiException");
    		e.printStackTrace();
    		return null;
	     } catch (IOException e) {
	    	Log.e(TAG, "IOException");
    		e.printStackTrace();
    		return null;
		} catch (URISyntaxException e) {
			Log.e(TAG, "URISyntaxException");
    		e.printStackTrace();
    		return null;
		}
    }
    
    private Message secureAddMessage(Message message){
    	Message ret_message = null;
		try {
			checkUserAgent();
			
			InitHubDatabaseManager db = new InitHubDatabaseManager(mCxt.getApplicationContext());
			int remoteSubId = db.getRemoteSubjectId(message.getLocalSubjectId());
			String apikey = db.getApiTokenCredentials();
			
			DefaultHttpClient httpClient = getSecureHttpClient();

	        //url with the post data
	        URI uri = new URI(messageList);
			HttpPost httpost = new HttpPost(uri);

	        JSONObject data = new JSONObject();
			data.put(InitHubDatabaseManager.MESSAGE_COMMENT, message.getComment());
			data.put("subject_id", remoteSubId);
			Log.d(TAG, "subject_id=" + Integer.toString(remoteSubId));
	        
	        StringEntity entity = new StringEntity(data.toString(), HTTP.UTF_8);
	        httpost.setEntity(entity);
	        //sets a request header so the page receving the request
	        //will know what to do with it
	        httpost.setHeader("Accept", "application/json");
	        httpost.setHeader("Content-type", "application/json");
	        
	        httpost.setHeader("User-Agent", sUserAgent);

	        httpost.setHeader("Authorization", apikey);
	        
	        HttpResponse response = httpClient.execute(httpost);

            // Check if server response is valid
            StatusLine status = response.getStatusLine();
            if (status.getStatusCode() != HTTP_STATUS_CREATED) {
            	  // Pull content stream from response
                HttpEntity respone_entity = response.getEntity();
                InputStream inputStream = respone_entity.getContent();

                ByteArrayOutputStream content = new ByteArrayOutputStream();

                // Read response into a buffered stream
                int readBytes = 0;
                while ((readBytes = inputStream.read(sBuffer)) != -1) {
                    content.write(sBuffer, 0, readBytes);
                }
                String ret_cont = new String(content.toByteArray());
                Log.d(TAG, ret_cont);
                throw new ApiException("Invalid response from server: " +
                        status.toString());
            }

            // Pull content stream from response
            HttpEntity respone_entity = response.getEntity();
            InputStream inputStream = respone_entity.getContent();

            ByteArrayOutputStream content = new ByteArrayOutputStream();

            // Read response into a buffered stream
            int readBytes = 0;
            while ((readBytes = inputStream.read(sBuffer)) != -1) {
                content.write(sBuffer, 0, readBytes);
            }
            
            String ret_cont = new String(content.toByteArray());
            
            JSONObject json_response = new JSONObject(ret_cont);
            String id = json_response.getString("id");
            String firstName = json_response.getString("first_name");
            String lastName = json_response.getString("last_name");
            ret_message = new Message();
            ret_message.setFirstName(firstName);
            ret_message.setLastName(lastName);
            ret_message.setRemoteId(Integer.valueOf(id));
            ret_message.setRemoteSubjectId(remoteSubId);
    	
		} 
		catch (UnsupportedEncodingException e) {
			Log.e(TAG, "UnsupportedEncodingException");
			e.printStackTrace();
		}
		catch (URISyntaxException e2) {
			Log.e(TAG, "URISyntaxException");
			e2.printStackTrace();
		}
		catch (JSONException e1) {
			Log.e(TAG, "JSONException");
			e1.printStackTrace();
		} catch (ClientProtocolException e) {
			Log.e(TAG, "ClientProtocolException");
			e.printStackTrace();
		} catch (IOException e) {
			Log.e(TAG, "IOException");
			e.printStackTrace();
		} catch (ApiException e) {
			Log.e(TAG, "ApiException");
			e.printStackTrace();
		}
    	return ret_message;
    }
    
    private String getHttpResponse(HttpResponse response) throws IllegalStateException, IOException {
    	
    	// Pull content stream from response
        HttpEntity entity = response.getEntity();
        InputStream inputStream = entity.getContent();

        ByteArrayOutputStream barray = new ByteArrayOutputStream();

        // Read response into a buffered stream
        int readBytes = 0;
        while ((readBytes = inputStream.read(sBuffer)) != -1) {
        	barray.write(sBuffer, 0, readBytes);
        }

        // Return result from buffered stream
        return new String(barray.toByteArray());
    	
    }
    
    private ArrayList<Message> getMessageListFromJsonResponse(String content) throws JSONException{
    	JSONObject response = new JSONObject(content);
        JSONArray recs = response.getJSONArray("objects");
        
        ArrayList<Message> results = new ArrayList<Message>();
        for (int i = 0; i < recs.length(); ++i) {
            JSONObject rec = recs.getJSONObject(i);
            Message message = new Message(rec.getInt("id"),
            		rec.getInt("subject_id"), 
            		rec.getString("comment"),
            		rec.getString("first_name"), 
            		rec.getString("last_name"), 
            		rec.getString("create_date"));
            results.add(message);
        }
        
        return results;
    }
    
    private ArrayList<Subject> getSubjectListFromJsonResponse(String content) throws JSONException{
    	
        JSONObject jResponse = new JSONObject(content);
        JSONArray recs = jResponse.getJSONArray("objects");
        
        ArrayList<Subject> results = new ArrayList<Subject>();
        for (int i = 0; i < recs.length(); ++i) {
            JSONObject rec = recs.getJSONObject(i);
            Subject subject = new Subject(rec.getString("short_desc"),
            		rec.getString("long_desc"), 
            		rec.getString("initiative_name"),
            		rec.getString("first_name"), 
            		rec.getString("last_name"), 
            		rec.getInt("id"));
            results.add(subject);
        }
        return results;
    }
    
    private String getApiKeyFromJsonResponse(String content) throws JSONException{
    	JSONObject response = new JSONObject(content);
        return response.getString("key");
    }
    
    private void checkUserAgent() throws ApiException {
    	if (sUserAgent == null) {
            throw new ApiException("User-Agent string must be prepared");
        }
    }
    
}
