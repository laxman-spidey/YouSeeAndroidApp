package in.yousee.yousee;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import in.yousee.yousee.RequestHandlers.LoginRequestHandler;
import in.yousee.yousee.constants.RequestCodes;
import in.yousee.yousee.constants.ServerFiles;
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
	private OnResponseRecievedListener logoutListener;
	private static final String SESSION_DEBUG_TAG = "session_tag";
	public static boolean isLoggedIn = false;

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

	private static SharedPreferences getLoginSharedPrefs(Context context)
	{

		return context.getSharedPreferences(LOGIN_DATA, Activity.MODE_PRIVATE);
	}

	private boolean getLoginCredentials(String username, String password)
	{
		Log.i(SESSION_DEBUG_TAG, "in getLogin credentials----------------------------------");
		if (isLoginCredentialsExists(context))
		{
			SharedPreferences sharedPrefs = getLoginSharedPrefs(context);
			username = sharedPrefs.getString(KEY_USERNAME, null);
			Log.i(SESSION_DEBUG_TAG, "username " + username);
			password = sharedPrefs.getString(KEY_PASSWORD, null);
			Log.i(SESSION_DEBUG_TAG, "password " + password);
			return true;
		}
		return false;

	}

	private void setLoginCredentials(String username, String password)
	{
		SharedPreferences sharedPrefs = getLoginSharedPrefs(context);
		SharedPreferences.Editor editor = sharedPrefs.edit();
		editor.putString(KEY_USERNAME, username);
		editor.putString(KEY_PASSWORD, password);
		editor.commit();

	}

	public static boolean isLoginCredentialsExists(Context context)
	{
		Log.i(SESSION_DEBUG_TAG, "is LoginCredentialExists()");
		SharedPreferences sharedPrefs = getLoginSharedPrefs(context);

		if (sharedPrefs.contains(KEY_USERNAME) && sharedPrefs.contains(KEY_PASSWORD))
		{
			if (!(sharedPrefs.getString(KEY_USERNAME, "").equals("")) && !(sharedPrefs.getString(KEY_PASSWORD, "").equals("")))
			{
				Log.i(SESSION_DEBUG_TAG, "returning true");
				return true;
			}
			Log.i(SESSION_DEBUG_TAG, "returning false");
			return false;
		}
		Log.i(SESSION_DEBUG_TAG, "returning false");
		return false;

	}

	public boolean getSessionId(String sessionId)
	{
		Log.i(SESSION_DEBUG_TAG, "getsessionId()");
		SharedPreferences sharedPrefs = getLoginSharedPrefs(context);
		if (isSessionIdExists(context))
		{

			sessionId = sharedPrefs.getString(KEY_SESSION_ID, "error");
			Log.i(SESSION_DEBUG_TAG, "sessiocheppan id exixsts = " + sessionId);
			return true;
		}
		Log.i(SESSION_DEBUG_TAG, "session id false");
		return false;

	}

	private void setSessionId(String sessionId)
	{
		Log.i(SESSION_DEBUG_TAG, "setsessionId()");
		SharedPreferences sharedPrefs = getLoginSharedPrefs(context);

		SharedPreferences.Editor editor = sharedPrefs.edit();

		editor.putString(KEY_SESSION_ID, sessionId);
		this.sessionID = sessionId;
		editor.commit();
		Log.i(SESSION_DEBUG_TAG, "sessionId set to = " + sharedPrefs.getString(KEY_SESSION_ID, ""));

	}

	public static boolean isSessionIdExists(Context context)
	{
		Log.i(SESSION_DEBUG_TAG, "isSessionIdExists()");
		SharedPreferences sharedPrefs = getLoginSharedPrefs(context);
		
		if (sharedPrefs.contains(KEY_SESSION_ID) && sharedPrefs.getString(KEY_SESSION_ID, "").equals(""))
		{
			Log.i(SESSION_DEBUG_TAG, "returning " + true);
			return true;
		}
		Log.i(SESSION_DEBUG_TAG, "returning " + false);
		return false;

	};

	public void loginExec() throws CustomException
	{

		Log.i("tag", "in login exec");
		// if (getLoginCredentials(username, password))
		if (getLoginCredentials(username, password))
		{
			loginExec(username, password);
		}
		// else
		// return -1;
	}

	public void loginExec(String username, String password) throws CustomException
	{
		Log.i("tag", "loginExec(username, password)");

		this.username = username;
		this.password = password;
		NetworkConnectionHandler networkHandler = new NetworkConnectionHandler(context);

		postRequest = new HttpPost(NetworkConnectionHandler.DOMAIN + ServerFiles.LOGIN_EXEC);
		nameValuePairs = new ArrayList<NameValuePair>(2);
		super.setRequestCode(RequestCodes.NETWORK_REQUEST_LOGIN);
		nameValuePairs.add(new BasicNameValuePair("username", username));
		nameValuePairs.add(new BasicNameValuePair("password", password));

		try
		{
			postRequest.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}

		networkHandler.sendRequest(postRequest, this);

	}

	public void logout(OnResponseRecievedListener listener) throws CustomException
	{
		this.logoutListener = listener;
		Log.i("tag", "sendLogoutRequestToServer()");
		postRequest = new HttpPost(NetworkConnectionHandler.DOMAIN + ServerFiles.LOGOUT);
		nameValuePairs = new ArrayList<NameValuePair>();
		super.setRequestCode(RequestCodes.NETWORK_REQUEST_LOGOUT);
		try
		{
			postRequest.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
		NetworkConnectionHandler networkHandler = new NetworkConnectionHandler(context);
		networkHandler.sendRequest(postRequest, this);

	}

	@Override
	public void serveResponse(String result, int requestCode)
	{
		if (requestCode == RequestCodes.NETWORK_REQUEST_LOGIN)
		{
			SessionData sessionData = new SessionData(result);
			Log.i(SESSION_DEBUG_TAG, "serving response");
			if (sessionData.isSuccess())
			{
				Log.i(SESSION_DEBUG_TAG, "login success");
				setLoginCredentials(username, password);
				Log.i(SESSION_DEBUG_TAG, "login data set");
				setSessionId(sessionData.getSessionId());
				Log.i(SESSION_DEBUG_TAG, "setting session id");
				String sessionId = null;
				
				if (getSessionId(sessionId))
				{
					Log.i(SESSION_DEBUG_TAG, "viewing session id");
					Toast.makeText(context, "Successfully logged in " + sessionId, Toast.LENGTH_SHORT).show();
				}
				else
				{
					Toast.makeText(context, "biscuit", Toast.LENGTH_SHORT).show();
				}
				loginFeatureClient.onLoginSuccess();
			}
			else
			{
				loginFeatureClient.onLoginFailed();

			}
		}
		else if (requestCode == RequestCodes.NETWORK_REQUEST_LOGOUT)
		{
			Log.i(SESSION_DEBUG_TAG, "logging out");
			SharedPreferences sharedPrefs = getLoginSharedPrefs(context);
			SharedPreferences.Editor editor = sharedPrefs.edit();
			editor.remove(KEY_SESSION_ID);
			editor.commit();
			logoutListener.onResponseRecieved(null, requestCode);

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
