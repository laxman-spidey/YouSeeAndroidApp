package in.yousee.main;

import in.yousee.main.constants.RequestCodes;
import in.yousee.main.constants.ServerFiles;
import in.yousee.main.model.CustomException;
import in.yousee.main.model.SessionData;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class SessionHandler extends Middleware
{
	private Context context;

	private String sessionID;
	private String userID;
	private String userType;
	private UsesLoginFeature loginFeatureClient;
	private OnResponseRecievedListener logoutListener;
	private static final String SESSION_DEBUG_TAG = "session_tag";
	public static boolean isLoggedIn = false;
	private String username = "";
	private String password = "";

	private static final String LOGIN_DATA = "login_data";
	private static final String KEY_USERNAME = "username";
	private static final String KEY_PASSWORD = "password";
	private static final String KEY_USER_ID = "userId";
	public static final String KEY_SESSION_ID = "sessionID";

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
			this.username = username;
			Log.i(SESSION_DEBUG_TAG, "username " + this.username);
			password = sharedPrefs.getString(KEY_PASSWORD, null);
			this.password = password;
			Log.i(SESSION_DEBUG_TAG, "password " + this.password);
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
		this.username = username;
		this.password = password;
		editor.commit();

	}

	private void setUserId(int userId)
	{
		SharedPreferences sharedPrefs = getLoginSharedPrefs(context);
		SharedPreferences.Editor editor = sharedPrefs.edit();
		editor.putInt(KEY_USER_ID, userId);
		Log.i(SESSION_DEBUG_TAG, "userid set to : " + userId);
		editor.commit();
	}

	public static boolean isUserIdExists(Context context)
	{

		SharedPreferences sharedPrefs = getLoginSharedPrefs(context);
		if (sharedPrefs.contains(KEY_USER_ID) && sharedPrefs.getInt(KEY_USER_ID, 0) != 0)
		{
			return true;
		}
		return false;

	}

	public static int getUserId(Context context)
	{
		Log.i(SESSION_DEBUG_TAG, "getUserId()");
		SharedPreferences sharedPrefs = getLoginSharedPrefs(context);
		if (isUserIdExists(context))
		{

			int userId = sharedPrefs.getInt(KEY_USER_ID, -1);
			Log.i(SESSION_DEBUG_TAG, "userId = " + userId);
			return userId;
		}
		Log.i(SESSION_DEBUG_TAG, "userId false");
		return 0;

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

	public static String getSessionId(Context context)
	{
		Log.i(SESSION_DEBUG_TAG, "getsessionId()");
		SharedPreferences sharedPrefs = getLoginSharedPrefs(context);
		if (isSessionIdExists(context))
		{

			String sessionId = sharedPrefs.getString(KEY_SESSION_ID, "error");
			Log.i(SESSION_DEBUG_TAG, "sessiocheppan id exixsts = " + sessionId);
			return sessionId;
		}
		Log.i(SESSION_DEBUG_TAG, "session id false");
		return null;

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

		if (sharedPrefs.contains(KEY_SESSION_ID) && sharedPrefs.getString(KEY_SESSION_ID, "").equals("") == false)
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
		if (getLoginCredentials(username, password))
		{
			loginExec(username, password);
		}
	}

	public void loginExec(String username, String password) throws CustomException
	{
		Log.i("tag", "loginExec(" + username + ", " + password + ")");

		this.username = username;
		this.password = password;

		postRequest = new HttpPost(NetworkConnectionHandler.DOMAIN + ServerFiles.LOGIN_EXEC);
		nameValuePairs = new ArrayList<NameValuePair>(2);
		super.setRequestCode(RequestCodes.NETWORK_REQUEST_LOGIN);
		nameValuePairs.add(new BasicNameValuePair("username", username));
		nameValuePairs.add(new BasicNameValuePair("password", password));
		encodePostRequest(nameValuePairs);

		sendRequest();

	}

	public void logout(OnResponseRecievedListener listener) throws CustomException
	{
		this.logoutListener = listener;
		Log.i("tag", "sendLogoutRequestToServer()");
		postRequest = new HttpPost(NetworkConnectionHandler.DOMAIN + ServerFiles.LOGOUT);
		nameValuePairs = new ArrayList<NameValuePair>();
		super.setRequestCode(RequestCodes.NETWORK_REQUEST_LOGOUT);
		encodePostRequest(nameValuePairs);

		sendRequest();

	}

	@Override
	public void serveResponse(String result, int requestCode)
	{
		Log.i(SESSION_DEBUG_TAG, result);

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
				setUserId(sessionData.getUserId());
				String sessionId = null;

				if (isSessionIdExists(context))
				{
					Log.i(SESSION_DEBUG_TAG, "viewing session id");

				}

				getLoginCredentials(username, password);
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
	public Context getContext()
	{
		return context;
	}
}
