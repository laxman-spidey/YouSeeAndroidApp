package in.yousee.yousee;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

public class WelcomeActivity extends Activity
{

	Thread t;
	String sessionId = null;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		// WindowManager.LayoutParams.FLAG_FULLSCREEN);
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
				}
				catch (InterruptedException e)
				{
					// do nothing
				}
				finally
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
		intent.setClass(this, in.yousee.yousee.MainActivity.class);
		startActivity(intent);
	}
}
