package in.yousee.yousee;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class SessionHandler
{
	private Activity activity;
	private String username;
	private String password;
	private String sessionID;

	public SessionHandler(Activity activity)
	{
		this.activity = activity;
	}

	private boolean getLoginCredentials(String username, String password)
	{
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

	private boolean isLoginCredentialsExists()
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
		
		if (getLoginCredentials(username, password))
			return loginExec(username, password);
		else
			return -1;
	}

	public int loginExec(String username, String password)
	{
		int statusCode = 0;

		
		setSessionId(sessionID);
		setLoginCredentials(username, password);
		return statusCode;
	}
}
