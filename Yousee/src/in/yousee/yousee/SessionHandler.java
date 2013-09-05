package in.yousee.yousee;

import in.yousee.yousee.RequestHandlers.LoginRequestHandler;
import in.yousee.yousee.model.CustomException;
import in.yousee.yousee.model.SessionData;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

public class SessionHandler extends Chef
{
	private Context context;
	private String username;
	private String password;
	private String sessionID;
	private String userID;
	private String userType;
	private UsesLoginFeature loginFeatureClient;
	private static final String DEBUG_TAG = "tag";

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
		Log.i(DEBUG_TAG, "in getLogin credentials----------------------------------");
		if (isLoginCredentialsExists())
		{
			SharedPreferences sharedPrefs = getLoginSharedPrefs();
			username = sharedPrefs.getString(KEY_USERNAME, null);
			Log.i(DEBUG_TAG, "username " + username);
			password = sharedPrefs.getString(KEY_PASSWORD, null);
			Log.i(DEBUG_TAG, "password " + password);
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
			Log.i(DEBUG_TAG, "session id exixsts");
			sessionId = sharedPrefs.getString(KEY_SESSION_ID, "error");
			return true;
		}
		return false;

	}

	private void setSessionId(String sessionId)
	{
		SharedPreferences sharedPrefs = getLoginSharedPrefs();

		SharedPreferences.Editor editor = sharedPrefs.edit();

		editor.putString(KEY_SESSION_ID, sessionId);
		this.sessionID = sessionId;
		editor.commit();

	}

	public boolean isSessionIdExists()
	{

		SharedPreferences sharedPrefs = getLoginSharedPrefs();
		Log.i(DEBUG_TAG, "session id = " + sharedPrefs.contains(KEY_SESSION_ID));
		return sharedPrefs.contains(KEY_SESSION_ID);

	};

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
		super.requestCode = Chef.LOGIN_REQUEST_CODE;

		this.username = username;
		this.password = password;
		NetworkConnectionHandler networkHandler = new NetworkConnectionHandler(context);

		LoginRequestHandler request = new LoginRequestHandler(username, password);
		networkHandler.sendRequest(request.buildRequest(), this);

		setSessionId(sessionID);
		setLoginCredentials(username, password);

	}

	@Override
	public void serveResponse(String result)
	{
		SessionData sessionData = new SessionData(result);
		Log.i(DEBUG_TAG, "serving response");
		if (sessionData.isSuccess())
		{
			Log.i(DEBUG_TAG, "login success");
			setLoginCredentials(username, password);
			Log.i(DEBUG_TAG, "login data set");
			setSessionId(sessionData.getSessionId());
			Log.i(DEBUG_TAG, "setting session id");
			String sessionId = null;
			getLoginCredentials(username, password);
			getSessionId(sessionId);
			if (getSessionId(sessionId))
			{
				Log.i(DEBUG_TAG, "viewing session id");
				Toast.makeText(context, "Successfully logged in " + sessionId, Toast.LENGTH_SHORT).show();
			}
			else
				Toast.makeText(context, "biscuit", Toast.LENGTH_SHORT).show();
			// loginFeatureClient.onLoginSuccess();
		}
		else
		{
			// loginFeatureClient.onLoginFailed();

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
