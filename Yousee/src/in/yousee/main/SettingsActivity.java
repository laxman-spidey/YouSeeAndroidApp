package in.yousee.main;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class SettingsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener
{
	private static final String NOTIFICATIONS_PREF = "notifications_pref";

	GCMHelper gcmHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.preferences);
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		gcmHelper = new GCMHelper(this);
		sharedPref.registerOnSharedPreferenceChangeListener(this);
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

}
