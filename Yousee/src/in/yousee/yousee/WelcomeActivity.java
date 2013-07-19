package in.yousee.yousee;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class WelcomeActivity extends Activity
{

	Thread t;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.welcome_activity);

		Thread splashThread = new Thread() {
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

	public void checkLoginCredentials()
	{
		SharedPreferences sharedPrefs;
		if (NetworkConnectionHandler.isNetworkConnected(getApplicationContext()))
		{

			String username = null;
			String password = null;
			sharedPrefs = getPreferences(MODE_PRIVATE);
			String sessionID = sharedPrefs.getString("SESSION_ID", null);
			
			if (sessionID == null)
			{

			} 
			//if user is loggedOut
			else if (getLoginCredentials(username, password))
			{
				SharedPreferences.Editor editor = sharedPrefs.edit();
				sessionID = login();
				editor.putString("SESSION_ID", sessionID);

			}
		}
		showMainActivity();
	}

	public boolean getLoginCredentials(String username, String password)
	{
		SharedPreferences sharedPrefs;
		sharedPrefs = getPreferences(MODE_PRIVATE);
		username = sharedPrefs.getString("USERNAME", null);
		password = sharedPrefs.getString("PASSWORD", null);
		if (username == null || password == null)
			return false;
		else
			return true;
	}

	public String login()
	{
		String sessionID = null;
		return sessionID;
	}

	public void showMainActivity()
	{
		Intent intent = new Intent();

		intent.setClass(this, MainActivity.class);
		startActivity(intent);
	}

}
