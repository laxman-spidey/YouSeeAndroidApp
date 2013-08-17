package in.yousee.yousee;

import in.yousee.yousee.RequestHandlers.LoginRequestHandler;
import in.yousee.yousee.model.CustomException;
import in.yousee.yousee.model.SessionData;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class SessionHandler extends Chef
{
	private Context context;
	private String username;
	private String password;
	private String sessionID;
	private String userID;
	private String userType;
	private UsesLoginFeature loginFeatureClient;
	
	private static final String LOGIN_DATA = "login_data";
	private static final String KEY_USERNAME = "username";
	private static final String KEY_PASSWORD = "password";
	private static final String KEY_SESSION_ID = "sessionID";

	public SessionHandler(Context context)
	{
		this.context = context;
	}
	public SessionHandler(Context context, UsesLoginFeature usesLoginFeature)
	{
		this.loginFeatureClient = usesLoginFeature;
		this.context = context;
	}
	

	private SharedPreferences getLoginSharedPrefs()
	{

		return context.getSharedPreferences(LOGIN_DATA, Activity.MODE_PRIVATE);
	}

	private boolean getLoginCredentials(String username, String password)
	{
		Log.i("tag", "in getLogin credentials");
		if (isLoginCredentialsExists())
		{
			SharedPreferences sharedPrefs = getLoginSharedPrefs();
			username = sharedPrefs.getString(KEY_USERNAME, null);
			password = sharedPrefs.getString(KEY_PASSWORD, null);
			return true;
		}
		return false;

	}

	private void setLoginCredentials(String username, String password)
	{
		SharedPreferences sharedPrefs = getLoginSharedPrefs();
		SharedPreferences.Editor editor = sharedPrefs.edit();
		editor.putString(KEY_USERNAME, username);
		editor.putString(KEY_PASSWORD, password);
		editor.commit();

	}

	public boolean isLoginCredentialsExists()
	{
		SharedPreferences sharedPrefs = getLoginSharedPrefs();
		if (sharedPrefs.contains(KEY_USERNAME) && sharedPrefs.contains(KEY_PASSWORD))
		{
			return true;
		}
		return false;

	}

	public boolean getSessionId(String sessionId)
	{
		SharedPreferences sharedPrefs = getLoginSharedPrefs();
		if (isSessionIdExists())
		{
			sessionId = sharedPrefs.getString("SESSION_ID", null);
			return true;
		}
		return false;

	}

	private void setSessionId(String sessionId)
	{
		SharedPreferences sharedPrefs = getLoginSharedPrefs();
		SharedPreferences.Editor editor = sharedPrefs.edit();
		editor.putString("SESSION_ID", sessionId);
		this.sessionID = sessionId;
		editor.commit();

	}

	private boolean isSessionIdExists()
	{

		SharedPreferences sharedPrefs = getLoginSharedPrefs();
		if (sharedPrefs.contains(KEY_SESSION_ID))
		{
			return true;
		}
		return false;
	}

	public void loginExec() throws CustomException
	{

		Log.i("tag", "in login exec");
		// if (getLoginCredentials(username, password))
		if (getLoginCredentials(username, password))
			loginExec(username, password);

		// else
		// return -1;
	}

	public void loginExec(String username, String password) throws CustomException
	{
		int statusCode = 0;

		NetworkConnectionHandler networkHandler = new NetworkConnectionHandler(context);

		Log.i("tag", "connection available");
		
		LoginRequestHandler request = new LoginRequestHandler(username, password);
		networkHandler.sendRequest(request.buildRequest(), this);

		setSessionId(sessionID);
		setLoginCredentials(username, password);

	}

	@Override
	public void serveResponse(String result)
	{
		SessionData sessionData = new SessionData(result);
		if (sessionData.isSuccess())
		{
			setLoginCredentials(username, password);
			setSessionId(sessionData.getSessionId());
			loginFeatureClient.onLoginSuccess();
		} else
		{
			loginFeatureClient.onLoginFailed();
			
		}
	}

	@Override
	public void assembleRequest()
	{

	}

	@Override
	public void cook()
	{

	}
}
