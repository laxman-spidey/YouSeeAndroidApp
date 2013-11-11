package in.yousee.main;

import in.yousee.main.constants.RequestCodes;
import in.yousee.main.constants.ServerFiles;
import in.yousee.main.model.CustomException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GCMHelper extends Middleware
{

	private static final String GCM_PREFERNCES_TAG = "gcmPreferences";
	public static final String EXTRA_MESSAGE = "message";
	public static final String PROPERTY_REG_ID = "registration_id";
	private static final String PROPERTY_APP_VERSION = "appVersion";
	private static final String PROPERTY_DATABASE_IN_SYNC = "databaseInSync";
	public static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

	/**
	 * Substitute you own sender ID here. This is the project number you got
	 * from the API Console, as described in "Getting Started."
	 */
	String SENDER_ID = "797586209317";

	/**
	 * Tag used on log messages.
	 */
	static final String TAG = "GCM Demo";
	GoogleCloudMessaging gcm;
	AtomicInteger msgId = new AtomicInteger();
	Context context;
	Activity activity;
	OnResponseRecievedListener listener;

	String regid;

	public GCMHelper(Activity activity, OnResponseRecievedListener listener)
	{
		this.listener = listener;
		this.activity = activity;
		context = activity.getApplicationContext();

	}

	public void implementGcm(boolean register)
	{

		if (checkPlayServices())
		{
			gcm = GoogleCloudMessaging.getInstance(context);
			regid = getRegistrationId(context);
			if (register)
			{
				if (regid.equals("") || regid == null)
				{
					registerInBackground(true);
				}
				else if (isDatabaseInSync())
				{
					Log.i("tag", "database in not insync");
					sendRegistrationIdToBackend(true);
				}
			} else
			{
				registerInBackground(false);
			}

		} else
		{
			Log.i(TAG, "No valid Google Play Services APK found.");
		}
	}

	/**
	 * Check the device to make sure it has the Google Play Services APK. If
	 * it doesn't, display a dialog that allows users to download the APK
	 * from the Google Play Store or enable it in the device's system
	 * settings.
	 */
	public boolean checkPlayServices()
	{
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
		if (resultCode != ConnectionResult.SUCCESS)
		{
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode))
			{
				GooglePlayServicesUtil.getErrorDialog(resultCode, activity, PLAY_SERVICES_RESOLUTION_REQUEST).show();

			} else
			{
				Log.i(TAG, "This device is not supported.");
				// finish();
			}
			return false;
		}
		return true;
	}

	/**
	 * Gets the current registration ID for application on GCM service, if
	 * there is one.
	 * <p>
	 * If result is empty, the app needs to register.
	 * 
	 * @return registration ID, or empty string if there is no existing
	 *         registration ID.
	 */
	public String getRegistrationId(Context context)
	{
		final SharedPreferences prefs = getGcmPreferences(context);
		String registrationId = prefs.getString(PROPERTY_REG_ID, "");
		if (registrationId == null || registrationId.equals(""))
		{
			Log.i(TAG, "Registration not found.");
			return "";
		}
		// Check if app was updated; if so, it must clear the
		// registration ID
		// since the existing regID is not guaranteed to work with the
		// new
		// app version.
		int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
		int currentVersion = getAppVersion(context);
		if (registeredVersion != currentVersion)
		{
			Log.i(TAG, "App version changed.");
			return "";
		}
		return registrationId;
	}

	/**
	 * Registers the application with GCM servers asynchronously.
	 * <p>
	 * Stores the registration ID and the app versionCode in the
	 * application's shared preferences.
	 */
	private void registerInBackground(boolean register)
	{
		new AsyncTask<Boolean, Void, String>()
		{
			@Override
			protected String doInBackground(Boolean... params)
			{
				String msg = "";
				try
				{
					if (gcm == null)
					{
						gcm = GoogleCloudMessaging.getInstance(context);
					}
					boolean register = params[0];
					if (register)
					{
						regid = gcm.register(SENDER_ID);
						sendRegistrationIdToBackend(true);
						storeRegistrationId(context, regid);
						msg = "Device registered, registration ID=" + regid;
					} else
					{
						gcm.unregister();
						sendRegistrationIdToBackend(false);
						removeRegistrationId(context, getRegistrationId(context));
						msg = "Device unregistered, registration ID=" + regid;
					}

					// You should send the registration ID
					// to your server over HTTP, so it
					// can use GCM/HTTP or CCS to send
					// messages to your app.

					// For this demo: we don't need to send
					// it because the device will send
					// upstream messages to a server that
					// echo back the message using the
					// 'from' address in the message.

					// Persist the regID - no need to
					// register again.

				} catch (IOException ex)
				{
					Log.i("tag", "exception caught");
					ex.printStackTrace();

					// Log.i("tag",);
					msg = "Error :" + ex.getMessage();
					// If there is an error, don't just keep
					// trying to register.
					// Require the user to click a button
					// again, or perform
					// exponential back-off.
				}
				return msg;
			}

			@Override
			protected void onPostExecute(String msg)
			{
				// mDisplay.append(msg + "\n");
			}
		}.execute(register, null, null);
	}

	/**
	 * @return Application's version code from the {@code PackageManager}.
	 */
	private static int getAppVersion(Context context)
	{
		try
		{
			PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e)
		{
			// should never happen
			throw new RuntimeException("Could not get package name: " + e);
		}
	}

	/**
	 * @return Application's {@code SharedPreferences}.
	 */
	private SharedPreferences getGcmPreferences(Context context)
	{
		// This sample app persists the registration ID in shared
		// preferences, but
		// how you store the regID in your app is up to you.
		return activity.getSharedPreferences(GCM_PREFERNCES_TAG, Context.MODE_PRIVATE);
	}

	private boolean register;

	/**
	 * Sends the registration ID to your server over HTTP, so it can use
	 * GCM/HTTP or CCS to send messages to your app. Not needed for this
	 * demo since the device sends upstream messages to a server that echoes
	 * back the message using the 'from' address in the message.
	 */
	private void sendRegistrationIdToBackend(boolean register)
	{
		this.register = register;
		this.assembleRequest();
		Log.i("tag","register = "+register);
		try
		{
			this.sendRequest();
		} catch (CustomException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Stores the registration ID and the app versionCode in the
	 * application's {@code SharedPreferences}.
	 * 
	 * @param context
	 *                application's context.
	 * @param regId
	 *                registration ID
	 */
	private void storeRegistrationId(Context context, String regId)
	{
		final SharedPreferences prefs = getGcmPreferences(context);
		int appVersion = getAppVersion(context);
		Log.i(TAG, "Saving regId on app version " + appVersion);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(PROPERTY_REG_ID, regId);
		editor.putInt(PROPERTY_APP_VERSION, appVersion);
		editor.commit();
	}

	private void removeRegistrationId(Context context, String regId)
	{
		final SharedPreferences prefs = getGcmPreferences(context);
		int appVersion = getAppVersion(context);
		Log.i(TAG, "Saving regId on app version " + appVersion);
		SharedPreferences.Editor editor = prefs.edit();
		editor.remove(PROPERTY_REG_ID);
		editor.remove(PROPERTY_APP_VERSION);
		editor.commit();
	}

	@Override
	public void assembleRequest()
	{

		String TAG_REGISTRATION_ID = "registrationID";
		String TAG_REGISTER_FLAG = "registerFlag";
		postRequest = new HttpPost(NetworkConnectionHandler.DOMAIN + ServerFiles.STORE_GCM_ID);
		nameValuePairs = new ArrayList<NameValuePair>();
		super.setRequestCode(RequestCodes.NETWORK_REQUEST_SEND_GCM_ID);
		nameValuePairs.add(new BasicNameValuePair(TAG_REGISTER_FLAG, "" + this.register));
		if (SessionHandler.isLoggedIn)
		{
			nameValuePairs.add(new BasicNameValuePair(SessionHandler.TAG_USER_ID, "" + SessionHandler.getUserId(context)));
		}
		nameValuePairs.add(new BasicNameValuePair(TAG_REGISTRATION_ID, regid));

		try
		{
			postRequest.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		} catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void serveResponse(String result, int requestCode)
	{
		if (requestCode == RequestCodes.NETWORK_REQUEST_SEND_GCM_ID)
		{
			try
			{
				JSONObject json = new JSONObject(result);
				boolean success = json.getBoolean("successFlag");
				if (success)
				{
					Log.i(TAG, "Registration id successfully uploaded to database");
					setDatabaseInSync();
					listener.onResponseRecieved(success, requestCode);

				}
			} catch (JSONException e)
			{

				Log.i(TAG, "Something went wrong while uploading registration id");
				e.printStackTrace();

			}

		}
	}

	private void setDatabaseInSync()
	{
		final SharedPreferences prefs = getGcmPreferences(context);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean(PROPERTY_DATABASE_IN_SYNC, true);
		editor.commit();
	}

	public boolean isDatabaseInSync()
	{
		final SharedPreferences prefs = getGcmPreferences(context);
		boolean inSync = prefs.getBoolean(PROPERTY_DATABASE_IN_SYNC, false);
		return inSync;

	}

	public boolean isGcmIdEmpty()
	{
		regid = getRegistrationId(context);
		if ((regid.equals("") || regid == null || regid.equals(" ")) && !(isDatabaseInSync()))
		{
			return true;
		}
		return false;
	}

	@Override
	public Context getContext()
	{
		return context;
	}

}
