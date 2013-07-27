package in.yousee.yousee;

import in.yousee.yousee.RequestHandlers.LoginRequestHandler;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class SessionHandler
{
	private Activity activity;
	private Context context;
	private String username;
	private String password;
	private String sessionID;
	private String userID;
	private String userType;

	public SessionHandler(Activity activity)
	{
		this.activity = activity;
		context=activity.getApplicationContext();
	}

	private boolean getLoginCredentials(String username, String password)
	{
		Log.i("tag", "in getLogin credentials");
		SharedPreferences sharedPrefs;
		sharedPrefs = activity.getPreferences(activity.MODE_PRIVATE);
		username = sharedPrefs.getString("USERNAME", null);
		password = sharedPrefs.getString("PASSWORD", null);
		if (username == null || password == null)
			return false;
		return true;
	}

	private void setLoginCredentials(String username, String password)
	{
		SharedPreferences sharedPrefs;
		sharedPrefs = activity.getPreferences(activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPrefs.edit();
		editor.putString("USERNAME", username);
		editor.putString("PASSWORD", password);
		
	}

	public boolean isLoginCredentialsExists()
	{
		SharedPreferences sharedPrefs;
		sharedPrefs = activity.getPreferences(activity.MODE_PRIVATE);
		username = sharedPrefs.getString("USERNAME", null);
		password = sharedPrefs.getString("PASSWORD", null);
		if (username == null || password == null)
			return false;
		return true;
	}

	public boolean getSessionId(String sessionId)
	{
		SharedPreferences sharedPrefs;
		sharedPrefs = activity.getPreferences(activity.MODE_PRIVATE);
		sessionId = sharedPrefs.getString("SESSION_ID", null);
		if (sessionId == null)
			return true;
		return false;
	}
	private void setSessionId(String sessionId)
	{
		SharedPreferences sharedPrefs;
		sharedPrefs = activity.getPreferences(activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPrefs.edit();
		editor.putString("SESSION_ID", sessionId);
		this.sessionID=sessionId;
		
	}
	public int loginExec()
	{
		
		
		Log.i("tag", "in login exec");
		//if (getLoginCredentials(username, password))
			return loginExec(username, password);
		//else
			//return -1;
	}

	public int loginExec(String username, String password)
	{
		int statusCode = 0;
		
		NetworkConnectionHandler networkHandler = new NetworkConnectionHandler(context);
		if(NetworkConnectionHandler.isNetworkConnected(context))
		{
			Log.i("tag", "connection available");
			LoginRequestHandler request = new LoginRequestHandler(username, password);
			
			networkHandler.sendRequest(request.buildRequest());
			Log.i("tag", "response recieved");
		}
		
		setSessionId(sessionID);
		setLoginCredentials(username, password);
		return statusCode;
	}
}
