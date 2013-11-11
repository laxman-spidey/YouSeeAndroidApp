package in.yousee.main;

import in.yousee.main.constants.RequestCodes;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class SettingsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener, OnResponseRecievedListener
{
	private static final String NOTIFICATIONS_PREF = "notifications_pref";

	GCMHelper gcmHelper;
	CheckBoxPreference notificationPref;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.preferences);
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		notificationPref = (CheckBoxPreference) findPreference(getResources().getString(R.string.notification_pref));
		gcmHelper = new GCMHelper(this, this);
		String regid = gcmHelper.getRegistrationId(this);
		// Log.i("tag", "registration ID" + regid + "gvnsdhfjs");
		if (gcmHelper.isGcmIdEmpty())
		{
			notificationPref.setChecked(true);
		}
		sharedPref.registerOnSharedPreferenceChangeListener(this);
		gcmHelper = new GCMHelper(this, this);

		Log.i("tag", "registration ID" + regid + "gvnsdhfjs");

	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
	{
		Toast.makeText(getApplicationContext(), "Please wait, Processing your request.", Toast.LENGTH_SHORT).show();
		if (key.equals(NOTIFICATIONS_PREF))
		{
			Log.i("tag", "onshared preferences");
			boolean notif = sharedPreferences.getBoolean(NOTIFICATIONS_PREF, false);
			
			gcmHelper.implementGcm(notif);
		}
	}

	@Override
	public void onResponseRecieved(Object response, int requestCode)
	{
		if (requestCode == RequestCodes.NETWORK_REQUEST_SEND_GCM_ID)
		{
			boolean success = (Boolean) response;
			if (success)
			{
				// notificationPref.setChecked(true);
				Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_SHORT).show();

			} else
			{
				// notificationPref.setChecked(false);
				Toast.makeText(getApplicationContext(), "failed", Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	public Context getContext()
	{
		return getApplicationContext();
	}

}