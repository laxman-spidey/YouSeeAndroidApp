package in.yousee.main;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

public class WelcomeActivity extends Activity implements OnResponseRecievedListener
{

	Thread t;
	String sessionId = null;
	String regid;
	GCMHelper gcmHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		// WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.welcome_activity);

		gcmHelper = new GCMHelper(this);

		regid = gcmHelper.getRegistrationId(this);
		Log.i("tag", regid);
		if ((regid.equals("") || regid == null) && (gcmHelper.isDatabaseInSync()))
		{
			processFirstTime();
		} else
		{
			Log.i("tag", "running normally");
			defaultProcess();
		}

	}

	private void processFirstTime()
	{
		String msg = "App is loading for the first time. Please wait.";
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
		gcmHelper.implementGcm(true);
	}

	public boolean checkPlayServices()
	{
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS)
		{
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode))
			{
				
				GooglePlayServicesUtil.getErrorDialog(resultCode, this, GCMHelper.PLAY_SERVICES_RESOLUTION_REQUEST).show();

			} else
			{
				Log.i("tag", "This device is not supported.");
				// finish();
			}
			return false;
		}
		return true;
	}

	private void defaultProcess()
	{
		Thread splashThread = new Thread()
		{
			@Override
			public void run()
			{
				try
				{
					int waited = 0;
					while (waited < 2000)
					{
						sleep(100);
						waited += 100;
					}
				} catch (InterruptedException e)
				{
					// do nothing
				} finally
				{
					finish();
					showMainActivity();

				}
			}
		};
		splashThread.start();
	}

	public void showMainActivity()
	{

		Log.i("tag", "in Show menu activity");
		Intent intent = new Intent();
		intent.putExtra("sessionId", sessionId);
		intent.setClass(this, in.yousee.main.MainActivity.class);
		startActivity(intent);
	}

	@Override
	public void onResponseRecieved(Object response, int requestCode)
	{
		
	}

	@Override
	public Context getContext()
	{
		return getApplicationContext();
	}
}
